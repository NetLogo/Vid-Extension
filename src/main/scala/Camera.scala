package org.nlogo.extensions.vid

import com.github.sarxos.webcam.Webcam

import java.util.concurrent.TimeUnit

import java.awt.image.BufferedImage
import java.awt.Dimension

import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableValue
import javafx.concurrent.{ Service, Task, WorkerStateEvent }
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.scene.image.{ Image, ImageView }

trait CameraFactory {
  var cameraNames:              Seq[String]
  var defaultCameraName:        Option[String]
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

  override var cameraNames: Seq[String] = {
    import scala.collection.JavaConverters.collectionAsScalaIterableConverter
    withContextClassLoader {
      Webcam.getWebcams(1500, TimeUnit.MILLISECONDS).asScala.toSeq.map(_.getName)
    }
  }

  override var defaultCameraName: Option[String] =
    cameraNames.headOption

  override def open(cameraName: String): Option[VideoSource] = {
    import scala.collection.JavaConverters.collectionAsScalaIterableConverter
    withContextClassLoader {
      Webcam.getWebcams.asScala.toSeq.find(_.getName == cameraName).map(new Camera(_))
    }
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

  private def cameraNode(enforcedBounds: Option[(Double, Double)]): BoundedNode = {
    val imageView = new ImageView()
    enforcedBounds.foreach {
      case (w, h) =>
        imageView.setFitWidth(w)
        imageView.setFitHeight(h)
    }
    val updateImage = new UpdateImage
    val onUpdate = new OnUpdateSuccess(imageView, updateImage)
    updateImage.setOnSucceeded(onUpdate)
    updateImage.start()

    val preferredSize: ObservableValue[Dimension] =
      Bindings.createObjectBinding[Dimension](
        () => new Dimension(imageView.boundsInLocalProperty.get.getWidth.toInt, imageView.boundsInLocalProperty.get.getHeight.toInt),
        imageView.boundsInLocalProperty)

    BoundedNode(imageView, preferredSize, enforcedBounds)
  }

  override def videoNode(bounds: Option[(Double, Double)]): BoundedNode =
    cameraNode(bounds)
}
