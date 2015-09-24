package org.nlogo.extensions.vid

import org.nlogo.api._

class FakeContext extends Context {
  def attachCurrentDirectory(path: String): String =
    s"/currentdir/$path"
  def attachModelDir(filePath: String): String = ???
  def getAgent: org.nlogo.api.Agent = ???
  def getDrawing: java.awt.image.BufferedImage = ???
  def getRNG: org.nlogo.util.MersenneTwisterFast = ???
  def importPcolors(image: java.awt.image.BufferedImage,asNetLogoColors: Boolean): Unit = ???
  def logCustomGlobals(nameValuePairs: Seq[(String, String)]): Unit = ???
  def logCustomMessage(msg: String): Unit = ???
}
