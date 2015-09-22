package org.nlogo.extensions.vid

import org.scalatest.FunSuite
import org.scalatest.concurrent.AsyncAssertions
import org.scalatest.time.{ Millis, Span }

import java.io.File
import java.nio.file.Files
import javafx.util.Duration

import java.awt.image.BufferedImage
import java.util.Arrays

import javafx.scene.Scene
import javafx.scene.media.{ Media, MediaException, MediaPlayer },
  MediaPlayer.{ Status => MPStatus }

import scala.concurrent.Channel

import util.FunctionToCallback.{ function2Runnable, function2ChangeListener }

class MovieTest extends FunSuite with AsyncAssertions {
  import javafx.embed.swing.JFXPanel
  val _ = new JFXPanel() // init JavaFX

  val ValidMoviePath    = "src/test/resources/small.mp4"
  val NotFoundMoviePath = "/tmp/notreal"
  val InvalidMoviePath  = "src/test/resources/small.ogv"

  trait MovieFixture {
    val isReady = new Channel[Boolean]
    val media = new Media(new File(ValidMoviePath).toURI.toString)
    val mediaPlayer = new MediaPlayer(media)
    mediaPlayer.setOnReady(() => isReady.write(true))
    val movie = new Movie(media, mediaPlayer)

    def expectTransition(status: MPStatus, w: Waiter) =
      mediaPlayer.statusProperty.addListener((_: MPStatus, newStatus: MPStatus) =>
          if (newStatus == status) w.dismiss())

    if (! (mediaPlayer.getStatus == MediaPlayer.Status.READY))
      isReady.read
  }

  test("given movie doesn't exist, open returns None") {
    assert(Movie.open(NotFoundMoviePath).isEmpty)
  }

  test("given movie isn't in a supported format, open throws exception") {
    intercept[InvalidFormatException] {
      Movie.open(InvalidMoviePath)
    }
  }

  test("when a movie exists and is in a supported format, returns a Movie") {
    val m = Movie.open(ValidMoviePath)
    assert(m.nonEmpty)
  }

  test("when an attempt is made to setTime outside the time of the media, it raises IllegalArgumentException") {
    new MovieFixture {
      intercept[IllegalArgumentException]{ movie.setTime(-1) }
      intercept[IllegalArgumentException]{ movie.setTime(media.getDuration.toSeconds + 1.0) }
    }
  }

  test("when an attempt is made to setTime to a time within the movie, that time is set") {
    new MovieFixture {
      // unfortunately, the currentTimeProperty only changes when the movie is playing
      // even though getCurrentTime is updated whether or not the movie is playing
      movie.setTime(0.5)
      Thread.sleep(50)
      assert(mediaPlayer.getCurrentTime.equals(Duration.millis(500)))
    }
  }

  test("play starts playback") {
    new MovieFixture {
      val w = new Waiter()
      assert(! movie.isPlaying)
      expectTransition(MPStatus.PLAYING, w)
      movie.play()
      w.await(timeout(Span(100, Millis)), dismissals(1))
      assert(movie.isPlaying)
    }
  }

  test("stop pauses playback") {
    new MovieFixture {
      val w = new Waiter()
      assert(! movie.isPlaying)
      mediaPlayer.statusProperty.addListener((oldStatus: MediaPlayer.Status, newStatus: MediaPlayer.Status) =>
          if (newStatus == MediaPlayer.Status.PLAYING) movie.stop())
      movie.play()
      expectTransition(MPStatus.PAUSED, w)
      w.await(timeout(Span(100, Millis)), dismissals(1))
      assert(! movie.isPlaying)
    }
  }

  test("close disposes media player") {
    new MovieFixture {
      val w = new Waiter()
      expectTransition(MPStatus.DISPOSED, w)
      movie.close()
      w.await(timeout(Span(100, Millis)), dismissals(1))
    }
  }

  test("captureImage records the image available") {
    new MovieFixture {
      import javax.imageio.ImageIO
      val image = movie.captureImage()

      assert(image.isInstanceOf[BufferedImage])
      assert(image.getWidth()  == 560)
      assert(image.getHeight() == 320)

      val expectedOutput = new java.io.FileInputStream(new File("src/test/resources/captured-image.png"))

      class VerifyingOutputStream extends java.io.OutputStream {
        var position: Int = 0

        val IMAGE_SIZE = 179775 // if the image ever changes, this should also change

        def write(i: Int): Unit = {
          val expectedByte = expectedOutput.read()
          // sometime i is negative (go figure), so we need to make it positive before comparing it
          assert((i + 256) % 256 == expectedByte, s"Difference at position $position")
          position += 1
        }

        def verify(): Unit = {
          assert(position == IMAGE_SIZE)
        }
      }

      try {
        val verifyingStream = new VerifyingOutputStream()
        val written = ImageIO.write(image, "png", verifyingStream)
        assert(written)
        verifyingStream.verify()
      } catch {
        case e: Exception =>
          val outputStream = new java.io.FileOutputStream("target/failed-img.png")
          ImageIO.write(image, "png", outputStream)
          println("non-matching image written to target/failed-img.png")
          throw e
      }
    }
  }

  test("showInPlayer shows the movie in the player") {
    new MovieFixture {
      var shownScene: Scene = null

      movie.showInPlayer(new Player {
        def isShowing = false
        def hide(): Unit = {}
        def show(scene: Scene with BoundsPreference, video: VideoSource): Unit =
          shownScene = scene
        def showEmpty(): Unit = {}
        def videoSource: Option[VideoSource] = None
      })

      assert(shownScene != null)
    }
  }
}
