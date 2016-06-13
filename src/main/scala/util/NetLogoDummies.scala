package org.nlogo.extensions.vid.util

import org.nlogo.core.{ LogoList, Token }
import org.nlogo.api._

class CurrentDirContext extends Context {
  def attachCurrentDirectory(path: String): String =
    new java.io.File(path).getCanonicalPath
  def attachModelDir(filePath: String): String = ???
  def getAgent: org.nlogo.api.Agent = ???
  def getDrawing: java.awt.image.BufferedImage = ???
  def getRNG: MersenneTwisterFast = ???
  def importPcolors(image: java.awt.image.BufferedImage,asNetLogoColors: Boolean): Unit = ???
  def logCustomGlobals(nameValuePairs: Seq[(String, String)]): Unit = ???
  def logCustomMessage(msg: String): Unit = ???
  def activation: org.nlogo.api.Activation = ???
  def workspace: org.nlogo.api.Workspace = ???
  def world: org.nlogo.api.World = ???
}

class FakeArgument(val underlying: AnyRef) extends Argument {
  def get: AnyRef = ???
  def getAgent: org.nlogo.api.Agent = ???
  def getAgentSet: org.nlogo.api.AgentSet = ???
  def getBoolean: java.lang.Boolean = ???
  def getBooleanValue: Boolean = ???
  def getCode: java.util.List[Token] = ???
  def getCommandTask: org.nlogo.api.CommandTask = ???
  def getDoubleValue: Double = underlying match {
    case d: java.lang.Double => d.doubleValue
    case _ => throw new ExtensionException(s"expected a double, got $underlying")
  }
  def getIntValue: Int = getDoubleValue.toInt
  def getLink: org.nlogo.api.Link = ???
  def getList: LogoList = ???
  def getPatch: org.nlogo.api.Patch = ???
  def getReporterTask: org.nlogo.api.ReporterTask = ???
  def getString: String =
    underlying match {
      case s: String => s
      case _ => throw new ExtensionException(s"expected a string, but got $underlying")
    }
  def getCodeBlock: java.util.List[org.nlogo.core.Token] = ???
  def getSymbol: org.nlogo.core.Token = ???
  def getTurtle: org.nlogo.api.Turtle = ???
}
