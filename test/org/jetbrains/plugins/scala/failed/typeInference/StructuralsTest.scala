package org.jetbrains.plugins.scala.failed.typeInference

import org.jetbrains.plugins.scala.PerfCycleTests
import org.jetbrains.plugins.scala.lang.typeInference.TypeInferenceTestBase
import org.junit.experimental.categories.Category

/**
  * @author Alefas
  * @since 23/03/16
  */
@Category(Array(classOf[PerfCycleTests]))
class StructuralsTest extends TypeInferenceTestBase {
  override def folderPath: String = super.folderPath + "bugs5/"

  def testSCL8689(): Unit = doTest()
  
  def testSCL5423() = doTest {
    """
      |trait Test {
      |  trait SettValue {
      |    type T
      |    def value: T
      |  }
      |  trait Foo { def foo: Int }
      |  type Sett <: SettValue
      |  type BSetting <: Sett { type T = Foo }
      |  def foo(b: BSetting) = /*start*/b.value.foo/*end*/
      |  
      |}
      |
      |//Int
    """.stripMargin
  }

  def testSCL4724(): Unit = doTest {
    """
      |class SCL4724 {
      |  def foo(x: Set[{ val bar: Int }]) = 1
      |  def foo(s: String) = false
      |
      |  /*start*/foo(Set(new { val bar = 1 }) ++ Set(new { val bar = 2 }))/*end*/
      |}
      |//Int
    """.stripMargin.trim
  }
}
