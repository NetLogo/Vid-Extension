package org.nlogo.extensions.vid

import org.scalatest.{ FeatureSpec, GivenWhenThen }

import java.io.{ File => JFile }
import java.awt.image.BufferedImage

import org.nlogo.api._

class VidExtensionSpec extends FeatureSpec with GivenWhenThen {

  feature("opening and closing") {
    scenario("no movie open") {
      new VidSpecHelpers {
        thenStatusShouldBe("inactive")
      }
    }

    scenario("opens a movie") {
      new VidSpecHelpers {
        When("""I run vid:movie-open "foobar.mp4"""")
        vid.`movie-open`("foobar.mp4")

        thenStatusShouldBe("stopped")
      }
    }

    scenario("open a camera") {
      new VidSpecHelpers {
        When("""I run vid:camera-open "camera"""")
        vid.`camera-open`("camera")

        thenStatusShouldBe("playing")
      }
    }

    scenario("open a camera that doesn't exist") {
      new VidSpecHelpers with ExpectError {
        whenRunForError("""vid:camera-open "nocamera"""",
          vid.`camera-open`("nocamera"))
        thenShouldSeeError("""vid: camera "nocamera" not found""")
        andStatusShouldBe("inactive")
      }
    }

    scenario("opens a default camera") {
      new VidSpecHelpers {
        When("I run vid:camera-open")
        vid.`camera-open`()
        thenStatusShouldBe("playing")
      }
    }

    scenario("tries to open a default camera when none available") {
      new VidSpecHelpers with ExpectError {
        import scala.language.reflectiveCalls
        Given("there are no cameras available")
        cameraFactory.defaultCameraName = None
        whenRunForError("vid:camera-open", vid.`camera-open`())
        thenShouldSeeError("vid: no cameras found")
        andStatusShouldBe("inactive")
      }
    }

    scenario("closes an opened movie") {
      new VidSpecHelpers {
        givenOpenMovie()
        When("I run movie:close")
        vid.close()
        thenStatusShouldBe("inactive")
      }
    }

    scenario("cannot find movie") {
      new VidSpecHelpers with ExpectError {
        whenRunForError("""vid:movie-open "not-real.mp4"""",
          vid.`movie-open`("not-real.mp4"))
        thenShouldSeeError("vid: no movie found")
        andStatusShouldBe("inactive")
      }
    }

    scenario("movie has invalid format") {
      new VidSpecHelpers with ExpectError {
        whenRunForError("""vid:movie-open "unsupported.ogg"""",
          vid.`movie-open`("unsupported.ogg"))
        thenShouldSeeError("vid: format not supported")
        andStatusShouldBe("inactive")
      }
    }
  }


  feature("Starting and stopping") {
    scenario("no source selected") {
      new VidSpecHelpers with ExpectError {
        whenRunForError("vid:start", vid.start())
        thenShouldSeeError("vid: no selected source")
      }
    }

    scenario("starts stopped source") {
      new VidSpecHelpers {
        givenOpenMovie()

        When("I start the movie")
        vid.start()

        thenStatusShouldBe("playing")
      }
    }

    scenario("start and stop movie") {
      new VidSpecHelpers {
        givenOpenMovie(started = true)

        When("I run vid:stop")
        vid.stop()

        thenStatusShouldBe("stopped")
      }
    }
  }

  feature("capture-image") {
    scenario("capture-image errors when no movie") {
      new VidSpecHelpers with ExpectError {
        whenRunForError("vid:capture-image 640 480",
          vid.`capture-image`(Double.box(640), Double.box(480)))
        thenShouldSeeError("vid: no selected source")
      }
    }

    scenario("invalid dimensions") {
      new VidSpecHelpers with ExpectError {
        givenOpenMovie()
        whenRunForError("vid:capture-image -1 -1",
          vid.`capture-image`(Double.box(-1), Double.box(-1)))
        thenShouldSeeError("vid: invalid dimensions")
      }
    }

    scenario("capture-image returns a scaled image from the active video source") {
      new VidSpecHelpers {
        givenOpenMovie()

        When("I call vid:capture-image 32 32")
        val capturedImage =
          vid.`capture-image`(Double.box(32), Double.box(32))

        Then("I should have a BufferedImage scaled to fit a 32x32 box")
        capturedImage match {
          case image: BufferedImage =>
            assert(image.getWidth  <= 32)
            assert(image.getHeight <= 32)
          case _ => fail("expected BufferedImage to be returned")
        }
      }
    }

    scenario("capture-image returns native-resolution image from active video source") {
      new VidSpecHelpers {
        givenOpenMovie()

        When("I call vid:capture-image")
        shouldMatchBufferedImage(vid.`capture-image`())
      }
    }

    scenario("capture-image can capture an image while movie is playing") {
      new VidSpecHelpers {
        givenOpenMovie(started = true)

        When("I call vid:capture-image")
        shouldMatchBufferedImage(vid.`capture-image`())
      }
    }

    scenario("capture-image can capture an image after stopping a movie") {
      new VidSpecHelpers {
        givenOpenMovie(started = true)
        And("I have called vid:stop")
        vid.`stop`()
        When("I call vid:capture-image")
        shouldMatchBufferedImage(vid.`capture-image`())
      }
    }
  }

  feature("set-time") {
    scenario("set-time errors when no video source has been selected") {
      new VidSpecHelpers with ExpectError {
        whenRunForError("vid:set-time 100", vid.`set-time`(Double.box(100)))
        thenShouldSeeError("vid: no selected source")
      }
    }

    scenario("set-time errors when a video source cannot be set to a time") {
      new VidSpecHelpers with ExpectError {
        givenOpenMovie()
        whenRunForError("vid:set-time -1", vid.`set-time`(Double.box(-1)))
        thenShouldSeeError("vid: invalid time")
      }
    }
  }

  feature("camera-names") {
    scenario("camera-names displays names of available cameras") {
      new VidSpecHelpers {
        When("I call vid:camera-names")
        val cameras = vid.`camera-names`()
        Then("I get a list of cameras available")
        assert(cameras.isInstanceOf[LogoList])
        assert(cameras.asInstanceOf[LogoList].scalaIterator.toSeq == Seq("camera"))
      }
    }
  }

  def shouldMatchBufferedImage(capturedImage: AnyRef): Unit = {
    Then("I should get a BufferedImage matching the image from the video source")
    assert(capturedImage == dummyImage)
  }

  val dummyImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB)

  trait VidSpecHelpers extends WithLoadedVidExtension {
    val dummyVideoSource = new VideoSource {
      var isPlaying = false
      override def play(): Unit =
        isPlaying = true
      override def stop(): Unit =
        isPlaying = false
      def captureImage() = dummyImage

      def setTime(time: Long): Unit = {
        if (time < 0)
          throw new IllegalArgumentException("bad time!")
      }
    }

    val movieFactory = new MovieFactory {
      override def open(filePath: String): Option[VideoSource] = {
        filePath match {
          case "/currentdir/foobar.mp4"      => Some(dummyVideoSource)
          case "/currentdir/unsupported.ogg" => throw new InvalidFormatException
          case _ => None
        }
      }
    }

    val cameraFactory = new CameraFactory {
      val cameraNames = Seq("camera")
      var defaultCameraName: Option[String] = Some("camera")

      override def open(cameraName: String): Option[AnyRef] = {
        cameraName match {
          case "camera" => Some(this)
          case _        => None
        }
      }
    }

    def givenOpenMovie(started: Boolean = false): Unit = {
      Given("I have opened a movie")
      vid.`movie-open`("foobar.mp4")
      if (started) {
        And("I have stared the movie")
        vid.start()
      }
    }

    def thenStatusShouldBe(status: String): Unit = {
      Then(s"""vid:status should show "$status"""")
      assert(vid.`status`() == status)
    }

    def andStatusShouldBe(status: String): Unit = {
      And(s"""vid:status should show "$status"""")
      assert(vid.`status`() == status)
    }
  }

  trait ExpectError {
    var _error = Option.empty[ExtensionException]

    def whenRunForError(errorCondition: String, f: => Unit): Unit = {
      When(s"I run $errorCondition")
      try {
        f
        fail(s"expected $errorCondition to error")
      } catch {
        case e: ExtensionException => _error = Some(e)
      }
    }

    def thenShouldSeeError(errorMessage: String): Unit = {
      Then(s"I should see an error - $errorMessage")
      assert(_error.nonEmpty)
      assert(_error.get.getMessage.contains(errorMessage))
    }
  }
}
