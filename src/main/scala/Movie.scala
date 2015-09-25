package org.nlogo.extensions.vid

import java.awt.Dimension
import java.awt.image.BufferedImage
import java.lang.{ Void => JVoid }
import java.io.File

import javafx.embed.swing.SwingFXUtils
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableValue
import javafx.scene.{ Group, Scene, SnapshotResult }
import javafx.scene.image.WritableImage
import javafx.scene.media.{ Media, MediaException, MediaPlayer, MediaView }
import javafx.util.{ Callback, Duration }

import scala.concurrent.Channel

import util.FunctionToCallback.{ function2Callable, function2Runnable }

trait MovieFactory {
  // throws InvalidFormatException when the filePath points to a file
  // whose format cannot be understood
  def open(filePath: String): Option[VideoSource]
}

class InvalidFormatException extends Exception("Invalid file format")

object Movie extends MovieFactory {
  def open(filePath: String): Option[VideoSource] = {
    val file = new File(filePath)
    if (file.exists) {
      try {
        val media = new Media(file.toURI.toString)
        Some(new Movie(media, new MediaPlayer(media)))
      } catch {
        case me: MediaException if me.getMessage == "Unrecognized file signature!" =>
          throw new InvalidFormatException()
      }
    } else
      None
  }
}

class Movie(media: Media, mediaPlayer: MediaPlayer) extends VideoSource {
  mediaPlayer.setMute(true)

  override def play(): Unit =
    mediaPlayer.play()

  override def stop(): Unit =
    mediaPlayer.pause()

  override def close(): Unit = {
    mediaPlayer.stop()
    mediaPlayer.dispose()
  }

  override def isPlaying: Boolean =
    mediaPlayer.getStatus == MediaPlayer.Status.PLAYING

  override def captureImage(): BufferedImage = {
    val chan = new Channel[BufferedImage]

    val callback = new Callback[SnapshotResult, JVoid] {
      def call(res: SnapshotResult): JVoid = {
        chan.write(SwingFXUtils.fromFXImage(res.getImage, null))
        null
      }
    }

    Platform.runLater { () =>
      val node = movieNode(None).node
      val scene = new Scene(new Group(node))
      scene.snapshot(callback, null)
    }

    chan.read
  }

  private def movieNode(bounds: Option[(Double, Double)] = None): BoundedNode = {
    val mediaView = new MediaView(mediaPlayer)
    bounds.foreach {
      case (w, h) =>
        mediaView.setFitWidth(w)
        mediaView.setFitHeight(h)
    }
    val preferredSize: ObservableValue[Dimension] =
      Bindings.createObjectBinding[Dimension](
        () =>
          new Dimension(mediaView.getBoundsInLocal.getWidth.toInt, mediaView.getBoundsInLocal.getHeight.toInt),
          mediaView.boundsInLocalProperty)
    BoundedNode(mediaView, preferredSize, bounds)
  }

  override def setTime(timeInSeconds: Double): Unit = {
    val requestedTime = new Duration(timeInSeconds * 1000)
    if (timeInSeconds < 0 || requestedTime.greaterThan(media.getDuration))
      throw new IllegalArgumentException(s"invalid time $timeInSeconds")
    mediaPlayer.seek(requestedTime)
  }

  def videoNode(bounds: Option[(Double, Double)]): BoundedNode =
    movieNode(bounds)
}
