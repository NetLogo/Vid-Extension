package org.nlogo.extensions.vid

import org.nlogo.api._

class CameraOpen(vid: VidExtension, cameras: CameraFactory) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    val cameraName =
      if (args.length == 0)
        cameras.defaultCameraName.getOrElse(
          throw new ExtensionException("vid: no cameras found"))
      else
        args(0).getString
    val camera = cameras.open(cameraName).getOrElse(
      throw new ExtensionException(s"""vid: camera "$cameraName" not found"""))
    vid.videoSource = Some(new VideoSource {
      override def setTime(timeInSeconds: Double): Unit = {}
      override def stop() = {}
      override def play() = {}
      override def isPlaying = true
      override def captureImage() = null
    })
  }
}
