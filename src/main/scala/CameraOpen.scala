package org.nlogo.extensions.vid

import org.nlogo.api._

class CameraOpen(vid: VideoSourceContainer, cameras: CameraFactory) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    val cameraName =
      if (args.length == 0)
        cameras.defaultCameraName.getOrElse(
          throw new ExtensionException("vid: no cameras found"))
      else
        args(0).getString
    cameras.open(cameraName) match {
      case cam@Some(camera) => vid.videoSource = Some(camera)
      case _                =>
        throw new ExtensionException(s"""vid: camera "$cameraName" not found""")
    }
  }
}
