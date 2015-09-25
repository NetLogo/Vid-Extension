package org.nlogo.extensions.vid

import org.nlogo.api._

class CameraSelect(videoContainer: VideoSourceContainer, cameras: CameraFactory, selector: Selector) extends DefaultCommand {
  override def getSyntax = Syntax.commandSyntax(Array[Int]())

  override def perform(args: Array[Argument], context: Context): Unit = {
    if (cameras.cameraNames.isEmpty)
      throw new ExtensionException("vid: no cameras found")
    selector.selectOneOf(cameras.cameraNames).foreach { cameraName =>
      videoContainer.videoSource = cameras.open(cameraName)
    }
  }
}

