package org.jetbrains.plugins.scala.failed.typeInference

import org.jetbrains.plugins.scala.PerfCycleTests
import org.jetbrains.plugins.scala.lang.typeInference.TypeInferenceTestBase
import org.junit.experimental.categories.Category

/**
  * Created by mucianm on 22.03.16.
  */
@Category(Array(classOf[PerfCycleTests]))
class RandomBugs extends TypeInferenceTestBase {
  override def folderPath: String = super.folderPath + "bugs5/"

  def testSCL9929() = doTest()

  def testSCL7333() = doTest()
}
