package org.nlogo.extensions.vid

import java.awt.image.BufferedImage

import org.scalatest.{ FeatureSpec, GivenWhenThen }

class CaptureImageSpec extends FeatureSpec with GivenWhenThen with VidHelpers {
  import scala.language.reflectiveCalls

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
}
