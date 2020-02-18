package org.jetbrains.bsp.protocol

import java.io.File
import java.util.concurrent.atomic.AtomicReference

import ch.epfl.scala.bsp4j.BspConnectionDetails
import com.google.gson.Gson
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.{Project, ProjectUtil}
import com.intellij.openapi.roots.CompilerProjectExtension
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.text.StringUtil.defaultIfEmpty
import com.intellij.openapi.vfs.{VfsUtil, VirtualFileManager}
import com.intellij.util.SystemProperties
import org.jetbrains.bsp.protocol.BspCommunication._
import org.jetbrains.bsp.protocol.BspNotifications.BspNotification
import org.jetbrains.bsp.protocol.session.BspServerConnector._
import org.jetbrains.bsp.protocol.session.BspSession._
import org.jetbrains.bsp.protocol.session._
import org.jetbrains.bsp.protocol.session.jobs.BspSessionJob
import org.jetbrains.bsp.settings.{BspExecutionSettings, BspProjectSettings, BspSettings}
import org.jetbrains.bsp.{BSP, BspBundle, BspError, BspErrorMessage, BspUtil}
import org.jetbrains.plugins.scala.build.BuildTaskReporter

import scala.concurrent.duration._
import scala.io.Source
import scala.util.{Failure, Success, Try}


class BspCommunication(base: File, executionSettings: BspExecutionSettings) extends Disposable {

  private val log = Logger.getInstance(classOf[BspCommunication])

  private val session: AtomicReference[Option[BspSession]] = new AtomicReference[Option[BspSession]](None)

  private def acquireSessionAndRun(job: BspSessionJob[_,_], reporter: BuildTaskReporter):
  Either[BspError, BspSession] = session.synchronized {
    session.get() match {
      case Some(currentSession) =>
        if (currentSession.isAlive) Right(currentSession)
        else openSession(job, reporter)

      case None =>
        openSession(job, reporter)
    }
  }

  private def openSession(job: BspSessionJob[_,_], reporter: BuildTaskReporter): Either[BspError, BspSession] = {
    val sessionBuilder = prepareSession(base, executionSettings, reporter)

    sessionBuilder match {
      case Left(error) =>
        val procLogMsg = BspBundle.message("bsp.connection.failed", error.getMessage)
        job.log(procLogMsg)
        log.warn("BSP connection failed", error)
        Left(error)
      case Right(newSessionBuilder) =>
        newSessionBuilder
          .withInitialJob(job)
          .addNotificationCallback(projectCallback)
          .withTraceLogPredicate(() => BspExecutionSettings.executionSettingsFor(base).traceBsp)
        val newSession = newSessionBuilder.create
        session.updateAndGet(_ => Option(newSession))
        Right(newSession)
    }
  }

  private def findProject: Option[Project] =
    for {
      vfsPath <- Option(VfsUtil.findFileByIoFile(base, false))
      project <- Option(ProjectUtil.guessProjectForFile(vfsPath))
    } yield project

  private def bspSettings(project: Project): Option[BspProjectSettings] =
    Option(BspSettings.getInstance(project).getLinkedProjectSettings(base.getPath))

  private val projectCallback: NotificationCallback = {
    case BspNotifications.DidChangeBuildTarget(didChange) =>
      for {
        project <- findProject
        settings <- bspSettings(project)
      } {
        FileDocumentManager.getInstance.saveAllDocuments()
        ExternalSystemUtil.refreshProjects(new ImportSpecBuilder(project, BSP.ProjectSystemId))
      }
    case _ => // ignore
  }

  private[bsp] def closeSession(): Try[Unit] = session.get() match {
    case None => Success(())
    case Some(s) =>
      session.set(None)
      s.shutdown()
  }


  private[protocol] def isIdle(now: Long, timeout: Duration) = session.get() match {
    case None => false
    case Some(s) =>
      s.isAlive && (now - s.getLastActivity >  timeout.toMillis)
  }

  def alive: Boolean = session.get().exists(_.isAlive)

  def run[T, A](task: BspSessionTask[T],
                default: A,
                aggregator: NotificationAggregator[A],
                reporter: BuildTaskReporter,
                processLogger: ProcessLogger
               ): BspJob[(T, A)] = {
    val job = jobs.create(task, default, aggregator, processLogger)

    acquireSessionAndRun(job, reporter) match {
      case Left(error) => new FailedBspJob(error)
      case Right(currentSession) =>
        currentSession.run(job)
    }
  }

  def run[T](bspSessionTask: BspSessionTask[T],
             notifications: NotificationCallback,
             reporter: BuildTaskReporter,
             processLogger: ProcessLogger): BspJob[T] = {
    val callback = (a: Unit, n: BspNotification) => notifications(n)
    val job = run(bspSessionTask, (), callback, reporter, processLogger)
    new NonAggregatingBspJob(job)
  }

  override def dispose(): Unit = {
    closeSession()
  }
}


object BspCommunication {

  def forWorkspace(baseDir: File): BspCommunication = {
    if (!baseDir.isDirectory)
      throw new IllegalArgumentException(s"Base path for BspCommunication is not a directory: $baseDir")
    else
      BspCommunicationService.getInstance.communicate(baseDir)
  }


  private[protocol] def prepareSession(base: File,
                                       bspExecutionSettings: BspExecutionSettings,
                                       reporter: BuildTaskReporter
                                      ): Either[BspError, Builder] = {

    val supportedLanguages = List("scala","java") // TODO somehow figure this out more generically?
    val capabilities = BspCapabilities(supportedLanguages)
    val connectionDetails = findBspConfigs(base)
    val configuredMethods = connectionDetails.map(ProcessBsp)

    val vfm = VirtualFileManager.getInstance()

    val compilerOutputDirFromConfig = for {
      projectDir <- Option(vfm.findFileByUrl(base.toPath.toUri.toString)) // path.toUri is rendered with :// separator which findFileByUrl needs
      project <- Option(ProjectUtil.guessProjectForFile(projectDir))
      cpe = CompilerProjectExtension.getInstance(project)
      output <- Option(cpe.getCompilerOutput)
    } yield new File(output.getCanonicalPath)

    val compilerOutputDir = compilerOutputDirFromConfig
      .getOrElse(new File(base, "out"))

    // TODO user dialog when multiple valid connectors exist: https://youtrack.jetbrains.com/issue/SCL-14880
    val connector =
      if (connectionDetails.nonEmpty)
        new GenericConnector(base, compilerOutputDir, capabilities, configuredMethods)
      else if (BspUtil.bloopConfigDir(base).isDefined)
        new BloopLauncherConnector(
          base,
          compilerOutputDir,
          capabilities
        )
      else new DummyConnector(base.toURI)

    connector.connect(reporter)
  }

  private def findBspConfigs(projectBase: File): List[BspConnectionDetails] = {

    val workspaceConfigs = BspUtil.bspConfigFiles(projectBase)
    val systemConfigs = systemDependentConnectionFiles
    val potentialConfigs = tryReadingConnectionFiles(workspaceConfigs ++ systemConfigs)

    potentialConfigs.flatMap(_.toOption).toList
  }

  /**
   * Find connection files installed on user's system.
   * https://build-server-protocol.github.io/docs/server-discovery.html#default-locations-for-bsp-connection-files
   */
  private def systemDependentConnectionFiles: List[File] = {
    val basePaths =
      if (SystemInfo.isWindows) windowsBspFiles()
      else if (SystemInfo.isMac) macBspFiles()
      else if (SystemInfo.isUnix) unixBspFiles()
      else Nil

    listFiles(bspDirs(basePaths))
  }

  private def tryReadingConnectionFiles(files: Seq[File]): Seq[Try[BspConnectionDetails]] = {
    val gson = new Gson()
    files.map { file =>
      if (file.canRead) {
        val reader = Source.fromFile(file).bufferedReader()
        Try(gson.fromJson(reader, classOf[BspConnectionDetails]))
      } else Failure(BspErrorMessage(BspBundle.message("file.not.readable", file)))
    }
  }

  private val BspDirName = "bsp"

  private def windowsBspFiles() = {
    val localAppData = System.getenv("LOCALAPPDATA")
    val programData = System.getenv("PROGRAMDATA")
    List(localAppData, programData)
  }

  private def unixBspFiles() = {
    val xdgDataHome = System.getenv("XDG_DATA_HOME")
    val xdgDataDirs = System.getenv("XDG_DATA_DIRS")
    val dataHome = defaultIfEmpty(xdgDataHome, SystemProperties.getUserHome + "/.local/share")
    val dataDirs = defaultIfEmpty(xdgDataDirs, "/usr/local/share:/usr/share").split(":").toList
    dataHome :: dataDirs
  }

  private def macBspFiles() = {
    val userHome = SystemProperties.getUserHome
    val userData = userHome + "/Library/Application Support"
    val systemData = "/Library/Application Support"
    List(userData, systemData)
  }

  private def bspDirs(basePaths: List[String]): List[File] = basePaths.map(new File(_, BspDirName))

  private def listFiles(dirs: List[File]): List[File] = dirs.flatMap { dir =>
    if (dir.isDirectory) dir.listFiles()
    else Array.empty[File]
  }

}
