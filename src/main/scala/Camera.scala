package org.nlogo.extensions.vid

trait CameraFactory {
  def cameraNames:              Seq[String]
  def defaultCameraName:        Option[String]
  def open(cameraName: String): Option[AnyRef]
}
