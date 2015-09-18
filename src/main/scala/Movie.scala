package org.nlogo.extensions.vid

import java.awt.image.BufferedImage
import java.io.File

import javafx.embed.swing.SwingFXUtils
import javafx.application.Platform
import javafx.scene.{ Group, Scene }
import javafx.scene.image.WritableImage
import javafx.scene.media.{ Media, MediaException, MediaPlayer, MediaView }
import javafx.util.Duration

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

  def play(): Unit =
    mediaPlayer.play()

  def stop(): Unit =
    mediaPlayer.pause()

  def isPlaying: Boolean =
    mediaPlayer.getStatus == MediaPlayer.Status.PLAYING

  def captureImage(): BufferedImage = {
    val chan = new Channel[BufferedImage]

    import javafx.util.Callback
    import javafx.scene.SnapshotResult
    import java.lang.{ Void => JVoid }

    val callback = new Callback[SnapshotResult, JVoid] {
      def call(res: SnapshotResult): JVoid = {
        chan.write(SwingFXUtils.fromFXImage(res.getImage, null))
        null
      }
    }

    Platform.runLater(
      new Runnable() {
        override def run(): Unit = {
          mediaScene.snapshot(callback, null)
        }
      })

    chan.read
  }

  private def mediaScene: Scene = {
    val mv = new MediaView(mediaPlayer)
    val g = new Group(mv)
    new Scene(g, media.getWidth, media.getHeight)
  }

  def setTime(timeInSeconds: Double): Unit = {
    val requestedTime = new Duration(timeInSeconds * 1000)
    if (timeInSeconds < 0 || requestedTime.greaterThan(media.getDuration))
      throw new IllegalArgumentException(s"invalid time $timeInSeconds")
    mediaPlayer.seek(requestedTime)
  }

  def showInPlayer(player: Player): Unit =
    player.show(mediaScene, this)
}

