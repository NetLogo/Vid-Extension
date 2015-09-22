package org.nlogo.extensions.vid

import org.scalatest.{ FeatureSpec, GivenWhenThen }

class PlayerSpec extends FeatureSpec with GivenWhenThen with VidHelpers {
  import scala.language.reflectiveCalls

  feature("show-player") {
    scenario("player can be hidden even if it has not been shown") {
      new VidSpecHelpers {
        Given("The player is not showing")
        assert(! player.isShowing)
        When("I call vid:hide-player")
        vid.`hide-player`()
        Then("I should see that there is no player")
        assert(! player.isShowing)
      }
    }

    scenario("player must have valid dimensions") {
      new VidSpecHelpers with ExpectError {
        whenRunForError("vid:show-player -1 -1",
          vid.`show-player`(Double.box(-1), Double.box(-1)))
        thenShouldSeeError("vid: invalid dimensions")
      }
    }

    scenario("player can be started with no source") {
      new VidSpecHelpers {
        When("I run vid:show-player")
        vid.`show-player`()

        Then("I should see a player showing with no video")
        assert(player.isShowing)
        assert(player.videoSource.isEmpty)
      }
    }

    scenario("player can be started with no dimensions") {
      new VidSpecHelpers {
        givenOpenMovie()

        When("I run vid:show-player")
        vid.`show-player`()

        Then("I should see a player running with native dimensions matching the video")
        assert(player.isShowing)
        assert(player.videoSource.get == dummyMovie)
      }
    }

    scenario("player can be started with specific dimensions") {
      new VidSpecHelpers {
        givenOpenMovie()

        When("I run vid:show-player 640 480")
        vid.`show-player`(Double.box(640), Double.box(480))

        Then("I should see a player with the specified dimensions")
        assert(player.scene != null)
        assert(player.scene.getRoot.getBoundsInLocal.getWidth == 640)
        assert(player.scene.getRoot.getBoundsInLocal.getHeight == 480)
      }
    }
  }
}
