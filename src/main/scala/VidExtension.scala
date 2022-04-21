package org.nlogo.extensions.vid

import java.awt.Dialog

import org.nlogo.api.{ DefaultClassManager, ExtensionManager, PrimitiveManager }
import org.nlogo.app.App
import org.nlogo.swing.MessageDialog
import org.nlogo.util.Utils

import scala.language.reflectiveCalls

object VidExtension {
  private def isWindows = {
    System.getProperty("os.name").toLowerCase.startsWith("win")
  }
  // we use two headless variables. The java one forces headless, the other one says NetLogo would like it if you ran headlessly
  def guiOrHeadless[A](gui: => A, headless: => A): A = {
    if (System.getProperty("java.awt.headless") == "true" || System.getProperty("org.nlogo.preferHeadless") == "true") {
      headless
    } else {
      try {
        gui
      } catch {
        case ex: java.lang.ExceptionInInitializerError if isWindows =>
          val dialog = new MessageDialog(App.app.frame) {
            def doShow(stackTrace: String): Unit = {
              val is64 = System.getProperty("os.arch").contains("64")
              // URLs are taken from here: https://docs.microsoft.com/en-us/cpp/windows/latest-supported-vc-redist?view=msvc-170#visual-studio-2010-vc-100-sp1-no-longer-supported
              val url = if (is64) {
                "https://aka.ms/highdpimfc2013x64enu"
              } else {
                "https://aka.ms/highdpimfc2013x86enu"
              }
              doShow("Missing Windows Library", s"It appears that a Windows library needed by the Vid extension is not installed.  Please visit this link to download the Microsoft Visual C++ 10 runtime library installer: $url\n\nAfter installing it try running the Vid extension one more time.  If it still does not work, please report the below error message to bugs@ccl.northwestern.edu or at https://github.com/NetLogo/Vid-Extension/issues\n\n$stackTrace", 15, 60)
            }
          }
          val stackTrace = Utils.getStackTrace(ex)
          dialog.doShow(stackTrace)
          headless
      }
    }
  }

  lazy val movie: MovieFactory   = guiOrHeadless(Movie, Headless.Movie)
  lazy val camera: CameraFactory = guiOrHeadless(Camera, Headless.Camera)
  lazy val player: Player        = guiOrHeadless(new JavaFXPlayer(), Headless.HeadlessPlayer)
  lazy val selector: Selector    = guiOrHeadless(NetLogoSelector, Headless.HeadlessSelector)
}
class VidExtension(movies: MovieFactory, cameras: CameraFactory, player: Player, selector: Selector, recorder: Recorder)
  extends DefaultClassManager with VideoSourceContainer {

  def this() =
    this(VidExtension.movie, VidExtension.camera, VidExtension.player, VidExtension.selector, new MP4Recorder())

  override def runOnce(em: ExtensionManager): Unit = {
  }

  override def load(manager: PrimitiveManager) = {
    manager.addPrimitive("camera-names",      new CameraNames(cameras))
    manager.addPrimitive("camera-open",       new CameraOpen(this, cameras))
    manager.addPrimitive("camera-select",     new CameraSelect(this, cameras, selector))
    manager.addPrimitive("capture-image",     new CaptureImage(this))
    manager.addPrimitive("close",             new CloseVideoSource(this))
    manager.addPrimitive("hide-player",       new HidePlayer(player))
    manager.addPrimitive("movie-open",        new MovieOpen(this, movies))
    manager.addPrimitive("movie-open-remote", new MovieOpenRemote(this, movies))
    manager.addPrimitive("movie-select",      new MovieSelect(this, movies, selector))
    manager.addPrimitive("recorder-status",   new RecorderStatus(recorder))
    manager.addPrimitive("record-interface",  new RecordInterface(recorder))
    manager.addPrimitive("record-source",     new RecordSource(recorder, this))
    manager.addPrimitive("record-view",       new RecordView(recorder))
    manager.addPrimitive("reset-recorder",    new ResetRecorder(recorder))
    manager.addPrimitive("save-recording",    new SaveRecording(recorder))
    manager.addPrimitive("set-time",          new SetTime(this))
    manager.addPrimitive("show-player",       new ShowPlayer(player, this))
    manager.addPrimitive("start",             new StartSource(this))
    manager.addPrimitive("start-recorder",    new StartRecorder(recorder))
    manager.addPrimitive("status",            new ReportStatus(this))
    manager.addPrimitive("stop",              new StopSource(this))
  }

  override def unload(em: ExtensionManager) = {
    closeSource()
    recorder.reset()
  }

  def closeSource(): Unit = {
    _videoSource.foreach(_.close())
  }

  var _videoSource: Option[VideoSource] = None

  def videoSource: Option[VideoSource] =
    _videoSource

  def videoSource_=(source: Option[VideoSource]): Unit = {
    try {
      if (player.isShowing) {
        val boundedNode = source.map(n => b => n.videoNode(b))
          .getOrElse(player.emptyNode(_))
          .apply(player.boundedSize)
        player.present(boundedNode)
      }
      closeSource()
    } catch {
      case e: Exception =>
        println("VID Extension Exception")
        println(e.getMessage)
        e.printStackTrace()
    }
    _videoSource = source
  }
}
