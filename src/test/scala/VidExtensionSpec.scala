package org.nlogo.extensions.vid

import org.scalatest.{ FeatureSpec, GivenWhenThen }

import java.io.{ File => JFile }
import java.awt.image.BufferedImage

import org.nlogo.core.LogoList
import org.nlogo.api._

// this is for all the features which don't justify their own spec yet
// as features get larger, they should get moved out.
class VidExtensionSpec extends FeatureSpec with GivenWhenThen with VidHelpers {
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
}
