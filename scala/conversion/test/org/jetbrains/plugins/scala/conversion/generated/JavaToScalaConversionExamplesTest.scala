package org.jetbrains.plugins.scala
package conversion
package generated

import com.intellij.pom.java.LanguageLevel
import org.jetbrains.plugins.scala.lang.formatting.settings.ScalaCodeStyleSettings

class JavaToScalaConversionExamplesTest extends JavaToScalaConversionTestBase {

  import util.TypeAnnotationSettings._

  //This class was generated by build script, please don't change this
  override def folderPath: String = super.folderPath + "examples/"

  def testAnnotated(): Unit = doTest()

  def testAnonymousClass(): Unit = doTest()

  def testDeprecated(): Unit = doTest()

  def testEnum(): Unit = doTest()

  def testFinalInObjects(): Unit = doTest()

  def testHelloWorld(): Unit = doTest()

  def testInterface(): Unit = doTest()

  def testRightOrder(): Unit = doTest()

  def testStaticInitializer(): Unit = doTest()

  def testStaticPrefix(): Unit = doTest()

  def testThrows(): Unit = doTest()

  def testTypeParameters(): Unit = doTest()

  def testVarArgs(): Unit = doTest()

  def testSCL3899(): Unit = doTest()

  def testSCL9369(): Unit = doTest()

  def testSCL11463(): Unit = doTest()

  def testNeedConstructorsSorting(): Unit = doTest()

  def testNoOverrideToImplement(): Unit = {
    val projectSettings = settings.ScalaProjectSettings.getInstance(getProjectAdapter)
    val oldValue = projectSettings.isAddOverrideToImplementInConverter

    projectSettings.setAddOverrideToImplementInConverter(false)
    doTest()
    projectSettings.setAddOverrideToImplementInConverter(oldValue)
  }

//  def testSCL9434() = doTest()

  def testSCL9421(): Unit = doTest()

  def testSCL9375(): Unit = doTest()

  def testSCL11313(): Unit = doTest()

  def testSCL11451(): Unit = doTest()

  def testNoReturnTypeForPublic(): Unit =
    doTest(noTypeAnnotationForPublic(
      alwaysAddType(ScalaCodeStyleSettings.getInstance(getProjectAdapter)))
    )

  def testNoRetunTypeForLocal(): Unit =
    doTest(noTypeAnnotationForLocal(
      alwaysAddType(ScalaCodeStyleSettings.getInstance(getProjectAdapter))
    ))

  def testImports(): Unit = doTest()

  def testLambdaExpr(): Unit = doTest()

  def testSwitchExpression(): Unit = doTest()

  def testSwitchRemovableBreak(): Unit = doTest()

  def testSwitchNonRemovableBreak(): Unit = doTest()

  def testBreakWithLabel(): Unit = doTest()

  def testSwitchExpressionYield(): Unit = doTestJava13()

  def testSwitchExpressionYieldNonRemovable(): Unit = doTestJava13()

  def testSwitchMultiLabel(): Unit = doTestJava13()

  def testCountingLoopSimple(): Unit = doTest()

  def testCountingLoopInclusive(): Unit = doTest()

  def testCountingLoopDescending(): Unit = doTest()

  def testCountingLoopDescendingInclusive(): Unit = doTest()

  def testNonCountingIncrementLoop(): Unit = doTest()

  private def doTestJava13(): Unit = {
    import com.intellij.openapi.roots.LanguageLevelProjectExtension
    val projectExtension = LanguageLevelProjectExtension.getInstance(getProject)
    val oldLevel = projectExtension.getLanguageLevel
    try {
      projectExtension.setLanguageLevel(LanguageLevel.JDK_13_PREVIEW)
      doTest()
    } finally {
      projectExtension.setLanguageLevel(oldLevel)
    }
  }
}