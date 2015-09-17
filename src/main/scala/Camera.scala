package org.nlogo.extensions.vid

trait CameraFactory {
  def open(cameraName: String): Option[AnyRef]
}
