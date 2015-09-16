package org.nlogo.extensions.vid

import org.nlogo.app.App
import org.nlogo.api._
import java.io.{ File => JFile }

class VidExtension(files: PartialFunction[String, JFile]) extends DefaultClassManager {

  override def load(manager: PrimitiveManager) = {
    manager.addPrimitive("movie-open", new MovieOpen(this, files))
    /*
    manager.addPrimitive("camera-select", new CameraSelect)
    manager.addPrimitive("camera-start",  new CameraStart)
    manager.addPrimitive("camera-image",  new CameraImage)
    manager.addPrimitive("camera-stop",   new CameraStop)

    manager.addPrimitive("movie-open",  new MovieOpen)
    manager.addPrimitive("movie-start", new MovieStart)
    manager.addPrimitive("movie-image", new MovieImage)
    manager.addPrimitive("movie-stop",  new MovieStop)

    manager.addPrimitive("movie-set-time",    new MovieSetTime)

    manager.addPrimitive("movie-open-player", new MovieOpenPlayer)
    */
  }

  var activeVideoSource: AnyRef = null
}

class MovieOpen(vid: VidExtension, files: PartialFunction[String, JFile]) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    if (files.lift(args(0).getString).nonEmpty) {
      vid.activeVideoSource = true: java.lang.Boolean
    } else {
      throw new ExtensionException("vid: no movie found")
    }
  }
}
