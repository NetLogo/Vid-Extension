package org.nlogo.extensions.vid

import org.scalatest.{ FeatureSpec, GivenWhenThen }

class StartAndStopSpec extends FeatureSpec with GivenWhenThen with VidHelpers {

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
}
