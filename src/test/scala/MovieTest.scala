package org.nlogo.extensions.vid

import org.scalatest.{ FunSuite, OneInstancePerTest }
import java.io.File
import java.nio.file.Files
import javafx.util.Duration

import java.awt.image.BufferedImage
import java.util.Arrays

import javafx.scene.Scene
import javafx.scene.media.{ Media, MediaException, MediaPlayer }

class MovieTest extends FunSuite {
  import javafx.embed.swing.JFXPanel
  val init = new JFXPanel()

  trait MovieFixture {
    val media = new Media(new File("src/test/resources/small.mp4").toURI.toString)
    var loaded = false
    val mediaPlayer = new MediaPlayer(media)

    val movie = new Movie(media, mediaPlayer)

    mediaPlayer.setOnReady(new Runnable() {
      override def run(): Unit =
        loaded = true
    })

    while(! loaded) {}
  }

  test("given movie doesn't exist, open returns None") {
    assert(Movie.open("/tmp/notreal").isEmpty)
  }

  test("given movie isn't in a supported format, open throws exception") {
    intercept[InvalidFormatException] {
      Movie.open("src/test/resources/small.ogv")
    }
  }

  test("when a movie exists and is in a supported format, returns a Movie") {
    val m = Movie.open("src/test/resources/small.mp4")
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
      movie.setTime(0.5)
      Thread.sleep(50)
      assert(mediaPlayer.getCurrentTime.equals(Duration.millis(500)))
    }
  }

  test("play starts playback") {
    new MovieFixture {
    assert(! movie.isPlaying)
    movie.play()
    Thread.sleep(100)
    assert(mediaPlayer.getStatus == MediaPlayer.Status.PLAYING)
    assert(movie.isPlaying)
    }
  }

  test("stop pauses playback") {
    new MovieFixture {
      assert(! movie.isPlaying)
      movie.play()
      Thread.sleep(100)
      movie.stop()
      Thread.sleep(100)
      assert(mediaPlayer.getStatus == MediaPlayer.Status.PAUSED)
      assert(! movie.isPlaying)
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
        def show(scene: Scene, video: VideoSource): Unit =
          shownScene = scene
        def showEmpty(): Unit = {}
        def videoSource: Option[VideoSource] = None
      })

      assert(shownScene != null)
    }
  }
}
