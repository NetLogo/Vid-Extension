package org.nlogo.extensions.vid

import org.scalatest.{ FeatureSpec, GivenWhenThen }

class OpenAndCloseSpec extends FeatureSpec with GivenWhenThen with VidHelpers {
  import scala.language.reflectiveCalls

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

    scenario("opening a new source closes the first source") {
      new VidSpecHelpers {
        givenOpenMovie()
        When("""I run vid:camera-open "camera"""")
        vid.`camera-open`("camera")

        Then("the movie should be closed")
        assert(dummyMovie.isClosed)
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
        assert(dummyMovie.isClosed)
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
}
