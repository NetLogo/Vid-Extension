package org.nlogo.extensions.vid

import java.awt.image.BufferedImage
import java.awt.Dimension

import javafx.scene.{ Group, Scene }
import javafx.scene.shape.Rectangle
import javafx.beans.value.ObservableValue

import org.scalatest.{ FeatureSpec, GivenWhenThen }

import org.nlogo.api.ExtensionException

trait VidHelpers { suite: FeatureSpec with GivenWhenThen =>
  trait VidSpecHelpers extends WithLoadedVidExtension {

    val dummyImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB)

    class DummySource(val startPlaying: Boolean) extends VideoSource {
      var isPlaying = startPlaying
      var isClosed  = false
      override def play(): Unit = { isPlaying = true }
      override def stop(): Unit = { isPlaying = false }
      def close(): Unit = { isClosed = true }
      def captureImage() = dummyImage
      def setTime(time: Double): Unit =
        if (time < 0)
          throw new IllegalArgumentException("bad time!")

    override def showInPlayer(player: Player) = {
      val g = new Group()
      val scene = new Scene(g) with BoundsPreference {
        def preferredBound: ObservableValue[Dimension] =
          null
      }
      player.show(scene, this)
    }

    override def showInPlayer(player: Player, width: Double, height: Double): Unit = {
      val r = new Rectangle()
      r.setWidth(width)
      r.setHeight(height)
      val g = new Group(r)
      val scene = new Scene(g) with BoundsPreference {
        def preferredBound: ObservableValue[Dimension] =
          null
      }
      player.show(scene, this)
    }
    }

    val dummyMovie = new DummySource(false)

    val dummyCamera = new DummySource(true)

    val movieFactory = new MovieFactory {
      override def open(filePath: String): Option[VideoSource] = {
        filePath match {
          case "/currentdir/foobar.mp4"      => Some(dummyMovie)
          case "/currentdir/unsupported.ogg" => throw new InvalidFormatException
          case _ => None
        }
      }
    }

    val cameraFactory = new CameraFactory {
      val cameraNames = Seq("camera")
      var defaultCameraName: Option[String] = Some("camera")

      override def open(cameraName: String): Option[VideoSource] = {
        cameraName match {
          case "camera" => Some(dummyCamera)
          case _        => None
        }
      }
    }

    override val player = new Player {
      import javafx.scene.Scene

      var videoSource: Option[VideoSource] = None

      var scene: Scene = null

      var isShowing = false

      def hide() = { isShowing = false }

      def showEmpty() = { isShowing = true }

      def show(showThisScene: Scene with BoundsPreference, source: VideoSource) = {
        scene = showThisScene
        isShowing = true
        videoSource = Some(source)
      }
    }

    def givenOpenMovie(started: Boolean = false): Unit = {
      suite.Given("I have opened a movie")
      vid.`movie-open`("foobar.mp4")
      if (started) {
        suite.And("I have stared the movie")
        vid.start()
      }
    }

    def thenStatusShouldBe(status: String): Unit = {
      suite.Then(s"""vid:status should show "$status"""")
      assert(vid.`status`() == status)
    }

    def andStatusShouldBe(status: String): Unit = {
      suite.And(s"""vid:status should show "$status"""")
      assert(vid.`status`() == status)
    }

    def shouldMatchBufferedImage(capturedImage: AnyRef): Unit = {
      suite.Then("I should get a BufferedImage matching the image from the video source")
      assert(capturedImage == dummyImage)
    }
  }

  trait ExpectError {
    this: VidSpecHelpers =>

    var _error = Option.empty[ExtensionException]

    def whenRunForError(errorCondition: String, f: => Unit): Unit = {
      suite.When(s"I run $errorCondition")
      try {
        f
        suite.fail(s"expected $errorCondition to error")
      } catch {
        case e: ExtensionException => _error = Some(e)
      }
    }

    def thenShouldSeeError(errorMessage: String): Unit = {
      suite.Then(s"I should see an error - $errorMessage")
      assert(_error.nonEmpty)
      assert(_error.get.getMessage.contains(errorMessage))
    }
  }
}
