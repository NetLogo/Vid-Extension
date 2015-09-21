package org.nlogo.extensions.vid

import com.github.sarxos.webcam.Webcam

import java.util.concurrent.TimeUnit

import scala.collection.JavaConversions._

trait CameraFactory {
  def cameraNames:              Seq[String]
  def defaultCameraName:        Option[String]
  def open(cameraName: String): Option[VideoSource]
}

object Camera extends CameraFactory {
  override def cameraNames: Seq[String] =
    Webcam.getWebcams(500, TimeUnit.MILLISECONDS).map(_.getName)

  override def defaultCameraName: Option[String] =
    cameraNames.headOption

  override def open(cameraName: String): Option[VideoSource] =
    Webcam.getWebcams.find(_.getName == cameraName).map(_ => new Camera())
}

class Camera extends VideoSource {
  override def setTime(timeInSeconds: Double): Unit = {}
  override def stop() = {}
  override def play() = {}
  override def close() = {}
  override def isPlaying = true
  override def captureImage() = null
  // can't be shown at the moment
  override def showInPlayer(player: Player) = {}

}
