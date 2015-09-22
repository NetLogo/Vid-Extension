package org.nlogo.extensions.vid

import java.awt.image.BufferedImage
import java.lang.{ Void => JVoid }
import java.io.File

import javafx.embed.swing.SwingFXUtils
import javafx.application.Platform
import javafx.scene.{ Group, Scene, SnapshotResult }
import javafx.scene.image.WritableImage
import javafx.scene.media.{ Media, MediaException, MediaPlayer, MediaView }
import javafx.util.{ Callback, Duration }

import scala.concurrent.Channel

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

    Platform.runLater(
      new Runnable() {
        override def run(): Unit =
          movieScene().snapshot(callback, null)
      })

    chan.read
  }

  class MovieScene(mediaView: MediaView) extends Scene(new Group(mediaView)) with BoundsPreference {
    import java.util.concurrent.Callable
    import javafx.beans.binding.Bindings
    import java.awt.Dimension
    import javafx.beans.value.ObservableValue

    val preferredBound: ObservableValue[Dimension] =
      Bindings.createObjectBinding[Dimension](
        new Callable[Dimension] {
          override def call(): Dimension =
            new Dimension(mediaView.getFitWidth.toInt, mediaView.getFitHeight.toInt)
        }, mediaView.fitWidthProperty, mediaView.fitHeightProperty)
  }

  private def movieScene(f: MediaView => MediaView = identity): MovieScene =
    new MovieScene(f(new MediaView(mediaPlayer)))

  override def setTime(timeInSeconds: Double): Unit = {
    val requestedTime = new Duration(timeInSeconds * 1000)
    if (timeInSeconds < 0 || requestedTime.greaterThan(media.getDuration))
      throw new IllegalArgumentException(s"invalid time $timeInSeconds")
    mediaPlayer.seek(requestedTime)
  }

  def showInPlayer(player: Player): Unit =
    player.show(movieScene(), this)

  override def showInPlayer(player: Player, width: Double, height: Double): Unit = {
    val scene = movieScene { view =>
      view.setFitWidth(width)
      view.setFitHeight(height)
      view
    }
    player.show(scene, this)
  }
}

