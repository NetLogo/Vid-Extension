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

    class DummySource(
      val startPlaying: Boolean,
      nodeConstructor: Option[(Double, Double)] => BoundedNode) extends VideoSource {
      var isPlaying = startPlaying
      var isClosed  = false
      override def play(): Unit = { isPlaying = true }
      override def stop(): Unit = { isPlaying = false }
      def close(): Unit = { isClosed = true }
      def captureImage() = dummyImage
      def setTime(time: Double): Unit = {
        if (time < 0)
          throw new IllegalArgumentException("bad time!")
      }

      override def videoNode(bounds: Option[(Double, Double)]) =
        nodeConstructor(bounds)
    }

    val dummyMovie = new DummySource(false, new MovieNode(_))

    val dummyCamera = new DummySource(true, new CameraNode(_))

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
      var cameraNames = Seq("camera")

      var defaultCameraName: Option[String] = cameraNames.headOption

      override def open(cameraName: String): Option[VideoSource] = {
        cameraName match {
          case "camera" => Some(dummyCamera)
          case _        => None
        }
      }
    }

    override val player = new DummyPlayer()

    val selector = new DummySelector()

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

class DummyPlayer extends Player {
  import javafx.scene.Scene

  var activeNode: BoundedNode = null
  def boundedSize = Option(activeNode).flatMap(_.enforcedBounds)
  var isShowing = false
  def hide() = { isShowing = false }
  def show(): Unit = { isShowing = true }
  def emptyNode(bounds: Option[(Double, Double)]): BoundedNode =
    new EmptyNode(bounds)
  def present(theNode: BoundedNode) =
    activeNode = theNode
}

class DummySelector extends Selector {
  var selected: Option[String] = None

  override def selectOneOf(choices: Seq[String]): Option[String] = selected

  override def selectFile: Option[String] = selected

  def select(name: String) = { selected = Some(name) }

  def cancel(): Unit =
    selected = None
}

class DummyNode(bds: Option[(Double, Double)])
  extends BoundedNode(
    new Rectangle(bds.map(_._1).getOrElse(640), bds.map(_._2).getOrElse(480)), null, bds)

class EmptyNode(enforcedBounds: Option[(Double, Double)])
  extends DummyNode(enforcedBounds)

class MovieNode(enforcedBounds: Option[(Double, Double)])
  extends DummyNode(enforcedBounds)

class CameraNode(enforcedBounds: Option[(Double, Double)])
  extends DummyNode(enforcedBounds)
