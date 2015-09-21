package org.nlogo.extensions.vid

import com.github.sarxos.webcam.Webcam

import java.util.concurrent.TimeUnit

import java.awt.image.BufferedImage

import scala.collection.JavaConversions._

trait CameraFactory {
  def cameraNames:              Seq[String]
  def defaultCameraName:        Option[String]
  def open(cameraName: String): Option[VideoSource]
}

object Camera extends CameraFactory {
  def withContextClassLoader[A](f: => A): A = {
    val oldccl = Thread.currentThread.getContextClassLoader
    Thread.currentThread.setContextClassLoader(classOf[Camera].getClassLoader)
    val result = f
    Thread.currentThread.setContextClassLoader(oldccl)
    result
  }

  override def cameraNames: Seq[String] =
    withContextClassLoader {
      Webcam.getWebcams(500, TimeUnit.MILLISECONDS).map(_.getName)
    }

  override def defaultCameraName: Option[String] =
    cameraNames.headOption

  override def open(cameraName: String): Option[VideoSource] =
    withContextClassLoader {
      Webcam.getWebcams.find(_.getName == cameraName).map(new Camera(_))
    }
}

class Camera(val webcam: Webcam) extends VideoSource {
  webcam.open()

  var cachedImage = Option.empty[BufferedImage]

  def isPlaying = cachedImage.isEmpty

  override def setTime(timeInSeconds: Double): Unit = {}

  override def stop() = { cachedImage = Some(captureImage()) }

  override def play() = { cachedImage = None }

  override def close() = webcam.close()

  override def captureImage(): BufferedImage =
    cachedImage.getOrElse(webcam.getImage)

  override def showInPlayer(player: Player) = {}

  override def showInPlayer(player: Player, width: Double, height: Double): Unit = {}
}
