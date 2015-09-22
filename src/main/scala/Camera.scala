package org.nlogo.extensions.vid

import com.github.sarxos.webcam.{ Webcam, WebcamEvent, WebcamListener }

import java.util.concurrent.TimeUnit

import java.awt.image.BufferedImage
import java.awt.Dimension

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableValue
import javafx.geometry.Bounds
import javafx.concurrent.{ Service, Task, WorkerStateEvent }
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.scene.{ Group, Scene }
import javafx.scene.image.{ Image, ImageView }

import scala.collection.JavaConversions._

import util.FunctionToCallback.function2Callable

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

  class UpdateImage extends Service[Image] {
    override protected def createTask(): Task[Image] =
      new Task[Image] {
        override protected def call(): Image =
          SwingFXUtils.toFXImage(captureImage(), null)
      }
  }

  class OnUpdateSuccess(imageView: ImageView, imageUpdate: Service[Image])
    extends EventHandler[WorkerStateEvent] {

    def handle(wse: WorkerStateEvent): Unit = {
      imageView.setImage(imageUpdate.getValue)
      imageUpdate.reset()
      imageUpdate.restart()
    }
  }

  class CameraScene(imageView: ImageView) extends Scene(new Group(imageView)) with BoundsPreference {
    val preferredBound: ObservableValue[Dimension] =
      Bindings.createObjectBinding[Dimension](
        () => new Dimension(imageView.boundsInLocalProperty.get.getWidth.toInt, imageView.boundsInLocalProperty.get.getHeight.toInt),
        imageView.boundsInLocalProperty)
  }

  private def cameraScene(f: ImageView => ImageView = identity): CameraScene = {
    val iv = new ImageView()
    val imageView = f(iv)
    val updateImage = new UpdateImage
    val onUpdate = new OnUpdateSuccess(imageView, updateImage)
    updateImage.setOnSucceeded(onUpdate)
    updateImage.start()
    new CameraScene(imageView)
  }

  override def showInPlayer(player: Player) = {
    player.show(cameraScene(), this)
  }

  override def showInPlayer(player: Player, width: Double, height: Double): Unit = {
    val scene = cameraScene { iv =>
      iv.setFitWidth(width)
      iv.setFitHeight(height)
      iv
    }
    player.show(scene, this)
  }
}
