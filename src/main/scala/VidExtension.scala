package org.nlogo.extensions.vid

import org.nlogo.api._

import javafx.embed.swing.JFXPanel

class VidExtension(movies: MovieFactory, cameras: CameraFactory, player: Player)
  extends DefaultClassManager with VideoSourceContainer {

  def this() = this(Movie, Camera, new JavaFXPlayer())

  override def runOnce(em: ExtensionManager): Unit = {
  }

  override def load(manager: PrimitiveManager) = {
    manager.addPrimitive("camera-names",  new CameraNames(cameras))
    manager.addPrimitive("camera-open",   new CameraOpen(this, cameras))
    manager.addPrimitive("capture-image", new CaptureImage(this))
    manager.addPrimitive("close",         new CloseVideoSource(this))
    manager.addPrimitive("hide-player",   new HidePlayer(player))
    manager.addPrimitive("movie-open",    new MovieOpen(this, movies))
    manager.addPrimitive("set-time",      new SetTime(this))
    manager.addPrimitive("show-player",   new ShowPlayer(player, this))
    manager.addPrimitive("start",         new StartSource(this))
    manager.addPrimitive("status",        new ReportStatus(this))
    manager.addPrimitive("stop",          new StopSource(this))
  }

  override def unload(em: ExtensionManager) = {
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
      _videoSource.foreach(_.close())
    } catch {
      case e: Exception =>
        println("VID Extension Exception")
        println(e.getMessage)
        e.printStackTrace()
    }
    _videoSource = source
  }
}
