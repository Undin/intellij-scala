package org.jetbrains.plugins.scala.util

import org.jdom.Element
import org.jetbrains.plugins.scala.extensions.ObjectExt

import scala.collection.JavaConverters.iterableAsScalaIterableConverter

class JdomExternalizerMigrationHelper private (element: org.jdom.Element) {

  def migrateString(key: String)(patcher: String => Unit): Unit =
    readString(key).foreach(patcher)

  def migrateBool(key: String)(patcher: Boolean => Unit): Unit =
    readString(key).foreach(x => patcher(x.toBoolean))

  def migrateInt(key: String)(patcher: Int => Unit): Unit =
    readString(key).foreach(x => patcher(x.toInt))

  def migrateMap(mapKey: String, elemKey: String, map: java.util.Map[String, String]): Unit = {
    element
      .getChild(mapKey)
      .toOption
      .map(_.getChildren(elemKey))
      .map(_.asScala)
      .getOrElse(Nil)
      .foreach { e =>
        map.put(
          e.getAttributeValue(NAME_KEY).toOption.getOrElse(""),
          e.getAttributeValue(VALUE_KEY).toOption.getOrElse("")
        )
      }
  }

  private def needsMigration(): Boolean = element.getChild(SETTING_KEY) != null

  private def readString(key: String): Option[String] = {
    element
      .getChildren(SETTING_KEY)
      .asScala
      .find(key == _.getAttributeValue(NAME_KEY))
      .flatMap(_.getAttributeValue(VALUE_KEY).toOption)
  }


  private final val SETTING_KEY = "setting"
  private final val VALUE_KEY   = "value"
  private final val NAME_KEY    = "name"

}

object JdomExternalizerMigrationHelper {
  def apply(element: Element)(runner: JdomExternalizerMigrationHelper => Unit): Unit = {
    val helper = new JdomExternalizerMigrationHelper(element)
    if (helper.needsMigration())
      runner(helper)
  }
}
