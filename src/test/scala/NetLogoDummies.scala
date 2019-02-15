package org.nlogo.extensions.vid

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import org.nlogo.api._

class FakeContext extends Context {
  def attachCurrentDirectory(path: String): String =
    s"/currentdir/$path"
  def attachModelDir(filePath: String): String = ???
  def getAgent: org.nlogo.api.Agent = ???
  def getDrawing: java.awt.image.BufferedImage = ???
  def getRNG: org.nlogo.api.MersenneTwisterFast = ???
  def importPcolors(image: java.awt.image.BufferedImage,asNetLogoColors: Boolean): Unit = ???
  def logCustomGlobals(nameValuePairs: Seq[(String, String)]): Unit = ???
  def logCustomMessage(msg: String): Unit = ???
  def activation: org.nlogo.api.Activation = ???
  def world: org.nlogo.api.World = ???
  val workspace = new FakeWorkspace()
}

class FakeWorkspace extends org.nlogo.api.Workspace {
  override def exportView: BufferedImage = {
    new BufferedImage(480, 480, BufferedImage.TYPE_INT_ARGB)
  }
  // Members declared in org.nlogo.api.Controllable
  def command(source: String): Unit = ???
  def evaluateCommands(owner: org.nlogo.api.JobOwner,source: String,waitForCompletion: Boolean): Unit = ???
  def evaluateCommands(owner: org.nlogo.api.JobOwner,source: String): Unit = ???
  def evaluateReporter(owner: org.nlogo.api.JobOwner,source: String): AnyRef = ???
  def report(source: String): AnyRef = ???

  // Members declared in org.nlogo.api.ImporterUser
  def currentPlot(plot: String): Unit = ???
  def getPlot(plot: String): org.nlogo.api.PlotInterface = ???
  def importExtensionData(name: String,data: java.util.List[Array[String]],handler: org.nlogo.api.ImportErrorHandler): Unit = ???
  def isExtensionName(name: String): Boolean = ???
  def setOutputAreaContents(text: String): Unit = ???

  // Members declared in org.nlogo.core.LiteralParser
  def readFromString(s: String): AnyRef = ???
  def readNumberFromString(source: String): AnyRef = ???

  // Members declared in org.nlogo.api.RandomServices
  def auxRNG: org.nlogo.api.MersenneTwisterFast = ???
  def mainRNG: org.nlogo.api.MersenneTwisterFast = ???
  def seedRNGs(seed: Int): Unit = ???

  // Members declared in org.nlogo.api.ViewSettings
  def drawSpotlight: Boolean = ???
  def fontSize: Int = ???
  def isHeadless: Boolean = ???
  def perspective: org.nlogo.api.Perspective = ???
  def renderPerspective: Boolean = ???
  def viewHeight: Double = ???
  def viewOffsetX: Double = ???
  def viewOffsetY: Double = ???
  def viewWidth: Double = ???

  // Members declared in org.nlogo.api.Workspace
  def behaviorSpaceRunNumber(n: Int): Unit = ???
  def behaviorSpaceRunNumber: Int = ???
  def benchmark(minTime: Int,maxTime: Int): Unit = ???
  def changeTopology(wrapX: Boolean,wrapY: Boolean): Unit = ???
  def clearAll(): Unit = ???
  def clearDrawing(): Unit = ???
  def clearLastLogoException(): Unit = ???
  def clearOutput(): Unit = ???
  def clearTicks(): Unit = ???
  def compilerTestingMode: Boolean = ???
  def dispose(): Unit = ???
  def getCompilationEnvironment: org.nlogo.core.CompilationEnvironment = ???
  def exportAllPlots(path: String): Unit = ???
  def exportDrawing(path: String,format: String): Unit = ???
  def exportInterface(path: String): Unit = {
    ImageIO.write(new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB), "png", new File(path))
  }
  def exportOutput(path: String): Unit = ???
  def exportPlot(plotName: String,path: String): Unit = ???
  def exportView(path: String,format: String): Unit = ???
  def exportWorld(writer: java.io.PrintWriter): Unit = ???
  def exportWorld(path: String): Unit = ???
  def getAndCreateDrawing(): java.awt.image.BufferedImage = ???
  def getExtensionManager: org.nlogo.api.ExtensionManager = ???
  def getLibraryManager: org.nlogo.api.LibraryManager = ???
  def getModelDir: String = ???
  def getModelFileName: String = ???
  def getModelPath: String = ???
  def graphicsChecksum: String = ???
  def importDrawing(path: String): Unit = ???
  def importWorld(path: String): Unit = ???
  def importWorld(reader: java.io.Reader): Unit = ???
  def lastLogoException: org.nlogo.api.LogoException = ???
  def open(modelPath: String, shouldAutoInstallLibs: Boolean = false): Unit = ???
  def openModel(model: org.nlogo.core.Model, shouldAutoInstallLibs: Boolean = false): Unit = ???
  def outputObject(obj: AnyRef,owner: AnyRef,addNewline: Boolean,readable: Boolean,destination: org.nlogo.api.OutputDestination): Unit = ???
  def patchSize: Double = ???
  def plotManager: AnyRef = ???
  def previewCommands: org.nlogo.api.PreviewCommands = ???
  def profilingEnabled: Boolean = ???
  def renderer: org.nlogo.api.RendererInterface = ???
  def setModelPath(path: String): Unit = ???
  def waitFor(runnable: org.nlogo.api.CommandRunnable): Unit = ???
  def waitForQueuedEvents(): Unit = ???
  def waitForResult[T](runnable: org.nlogo.api.ReporterRunnable[T]): T = ???
  def warningMessage(message: String): Boolean = ???
  def world: org.nlogo.api.World = ???
  def worldChecksum: String = ???
  // Members declared in org.nlogo.api.WorldResizer
  def patchSize(patchSize: Double): Unit = ???
  def resizeView(): Unit = ???
  def setDimensions(dim: org.nlogo.core.WorldDimensions,patchSize: Double): Unit = ???
  def setDimensions(dim: org.nlogo.core.WorldDimensions): Unit = ???
}
