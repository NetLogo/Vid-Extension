package org.nlogo.extensions.vid

import org.nlogo.api._

class VidExtension(files: MovieFactory, cameras: CameraFactory) extends DefaultClassManager {

  def this() = this(Movie, Camera)

  override def runOnce(em: ExtensionManager): Unit = {
    //initialize javafx
    import javafx.embed.swing.JFXPanel
    val init = new JFXPanel()
  }

  override def load(manager: PrimitiveManager) = {

    manager.addPrimitive("camera-names",  new CameraNames(cameras))
    manager.addPrimitive("camera-open",   new CameraOpen(this, cameras))
    manager.addPrimitive("capture-image", new CaptureImage(this))
    manager.addPrimitive("close",         new CloseVideoSource(this))
    manager.addPrimitive("movie-open",    new MovieOpen(this, files))
    manager.addPrimitive("set-time",      new SetTime(this))
    manager.addPrimitive("start",         new StartSource(this))
    manager.addPrimitive("status",        new ReportStatus(this))
    manager.addPrimitive("stop",          new StopSource(this))
  }

  var videoSource: Option[VideoSource] = None
}
