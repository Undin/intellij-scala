package org.jetbrains.plugins.scala.codeInspection.deprecation

import com.intellij.codeInspection.LocalInspectionTool
import org.jetbrains.plugins.scala.codeInspection.ScalaInspectionTestBase
import com.intellij.testFramework.EditorTestUtil.{SELECTION_END_TAG => END, SELECTION_START_TAG => START}
import org.jetbrains.plugins.scala.project.settings.ScalaCompilerConfiguration

class DeprecatedKindProjectorSyntaxInspectionTest extends ScalaInspectionTestBase {
  override protected val classOfInspection: Class[_ <: LocalInspectionTool] = classOf[DeprecatedKindProjectorSyntaxInspection]
  override protected val description: String = "Usage of `?` placeholder is going to be deprecated. Consider using `*` instead."

  protected override def setUp(): Unit = {
    super.setUp()
    val defaultProfile = ScalaCompilerConfiguration.instanceIn(myFixture.getProject).defaultProfile
    val newSettings = defaultProfile.getSettings.copy(
      plugins = defaultProfile.getSettings.plugins :+ "kind-projector-0.10.1.jar"
    )
    defaultProfile.setSettings(newSettings)
  }

  def testDeprecatedSimple(): Unit = {
    val code =
      s"""
         |object Test {
         |  trait F[A[_]]
         |  val a: F[List[$START?$END]] = ???
         |}
         |""".stripMargin
    checkTextHasError(code)
  }

  def testDeprecatedWithVariance(): Unit = {
    val code =
      s"""
         |object Test {
         |  trait F[A[_]]
         |  val a: F[Either[String, $START+?$END]] = ???
         |}
       """.stripMargin
    checkTextHasError(code)
  }

  def testNonTypePosition(): Unit = checkTextHasNoErrors(
    """
      |object Test {
      |  val ? = 123
      |  println(?)
      |}
      |""".stripMargin
  )

  def testQuickFix(): Unit = {
    val code =
      """
         |object Test {
         |  trait F[A[_]]
         |  val a: F[Either[String, +?]] = ???
         |}
       """.stripMargin

    testQuickFix(code,
      """
        |object Test {
        |  trait F[A[_]]
        |  val a: F[Either[String, +*]] = ???
        |}
        |""".stripMargin, DeprecatedKindProjectorSyntaxInspection.quickFixId)
  }
}

class DeprecatedKindProjectorSyntaxInspectionOutdatedKindProjectorTest extends ScalaInspectionTestBase {
  override protected val classOfInspection: Class[_ <: LocalInspectionTool] = classOf[DeprecatedKindProjectorSyntaxInspection]
  override protected val description: String =
    "Usage of `?` placeholder is going to be deprecated. Consider updating kind-projector plugin and using `*` instead."

  protected override def setUp(): Unit = {
    super.setUp()
    val defaultProfile = ScalaCompilerConfiguration.instanceIn(myFixture.getProject).defaultProfile
    val newSettings = defaultProfile.getSettings.copy(
      plugins = defaultProfile.getSettings.plugins :+ "kind-projector-0.9.3.jar"
    )
    defaultProfile.setSettings(newSettings)
  }

  def testDeprecatedSimple(): Unit = {
    val code =
      s"""
         |object Test {
         |  trait F[A[_]]
         |  val a: F[List[$START?$END]] = ???
         |}
         |""".stripMargin
    checkTextHasError(code)
  }

  def testDeprecatedWithVariance(): Unit = {
    val code =
      s"""
         |object Test {
         |  trait F[A[_]]
         |  val a: F[Either[String, $START+?$END]] = ???
         |}
       """.stripMargin
    checkTextHasError(code)
  }

  def testQuickFix(): Unit = {
    val code =
      s"""
         |object Test {
         |  trait F[A[_]]
         |  val a: F[Either[String, +?]] = ???
         |}
       """.stripMargin

    checkNotFixable(code, DeprecatedKindProjectorSyntaxInspection.quickFixId)
  }
}
