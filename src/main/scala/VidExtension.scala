package org.nlogo.extensions.vid

import org.nlogo.app.App
import org.nlogo.api._

class VidExtension extends DefaultClassManager {

  override def load(manager: PrimitiveManager) = {
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
    */

    manager.addPrimitive("movie-open-player", new MovieOpenPlayer)
  }
}
