package org.nlogo.extensions.vid

trait CameraFactory {
  def defaultCameraName: Option[String]
  def open(cameraName: String): Option[AnyRef]
}
