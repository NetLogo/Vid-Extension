package org.nlogo.extensions.vid

trait CameraFactory {
  def cameraNames:              Seq[String]
  def defaultCameraName:        Option[String]
  def open(cameraName: String): Option[AnyRef]
}

object Camera extends CameraFactory {
  override def cameraNames: Seq[String] = Seq()

  override def defaultCameraName: Option[String] = None

  override def open(cameraName: String): Option[AnyRef] = None
}
