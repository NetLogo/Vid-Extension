package org.nlogo.extensions.vid

import org.scalatest.{ FeatureSpec, GivenWhenThen }

import java.io.{ File => JFile }
import java.awt.image.BufferedImage

import org.nlogo.api._

import scala.language.dynamics

class VidExtensionSpec extends FeatureSpec with GivenWhenThen {

  val dummyImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB)

  val dummyVideoSource = new VideoSource {
    def isPlaying      = false
    def captureImage() = dummyImage
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
    var defaultCameraName: Option[String] = Some("camera")

    override def open(cameraName: String): Option[AnyRef] = {
      cameraName match {
        case "camera" => Some(this)
        case _        => None
      }
    }
  }

  trait WithLoadedExtension {
    lazy val (vidExtension, vid) = {
      val ve     = new VidExtension(movieFactory, cameraFactory)
      val loader = new CommandPrimitiveLoader()
      ve.load(loader)
      (ve, loader)
    }

    def givenOpenMovie(): Unit = {
      Given("I have opened a movie")
      vid.`movie-open`("foobar.mp4")
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

  feature("opening and closing") {
    scenario("no movie open") {
      new WithLoadedExtension {
        thenStatusShouldBe("inactive")
      }
    }

    scenario("opens a movie") {
      new WithLoadedExtension {
        When("""I run vid:movie-open "foobar.mp4"""")
        vid.`movie-open`("foobar.mp4")

        thenStatusShouldBe("stopped")
      }
    }

    scenario("open a camera") {
      new WithLoadedExtension {
        When("""I run vid:camera-open "camera"""")
        vid.`camera-open`("camera")

        thenStatusShouldBe("playing")
      }
    }

    scenario("open a camera that doesn't exist") {
      new WithLoadedExtension with ExpectError {
        whenRunForError("""vid:camera-open "nocamera"""",
          vid.`camera-open`("nocamera"))
        thenShouldSeeError("""vid: camera "nocamera" not found""")
        andStatusShouldBe("inactive")
      }
    }

    scenario("opens a default camera") {
      new WithLoadedExtension {
        When("I run vid:camera-open")
        vid.`camera-open`()
        thenStatusShouldBe("playing")
      }
    }

    scenario("tries to open a default camera when none available") {
      new WithLoadedExtension with ExpectError {
        import scala.language.reflectiveCalls
        Given("there are no cameras available")
        cameraFactory.defaultCameraName = None
        whenRunForError("vid:camera-open", vid.`camera-open`())
        thenShouldSeeError("vid: no cameras found")
        andStatusShouldBe("inactive")
      }
    }

    scenario("closes an opened movie") {
      new WithLoadedExtension {
        givenOpenMovie()
        When("I run movie:close")
        vid.close()
        thenStatusShouldBe("inactive")
      }
    }

    scenario("cannot find movie") {
      new WithLoadedExtension with ExpectError {
        whenRunForError("""vid:movie-open "not-real.mp4"""",
          vid.`movie-open`("not-real.mp4"))
        thenShouldSeeError("vid: no movie found")
        andStatusShouldBe("inactive")
      }
    }

    scenario("movie has invalid format") {
      new WithLoadedExtension with ExpectError {
        whenRunForError("""vid:movie-open "unsupported.ogg"""",
          vid.`movie-open`("unsupported.ogg"))
        thenShouldSeeError("vid: format not supported")
        andStatusShouldBe("inactive")
      }
    }
  }


  feature("Starting and stopping") {
    scenario("no source selected") {
      new WithLoadedExtension with ExpectError {
        whenRunForError("vid:start", vid.start())
        thenShouldSeeError("vid: no selected source")
      }
    }

    scenario("starts stopped source") {
      new WithLoadedExtension {
        givenOpenMovie()

        When("I start the movie")
        vid.start()

        thenStatusShouldBe("playing")
      }
    }

    scenario("start and stop movie") {
      new WithLoadedExtension {
        givenOpenMovie()
        And("I have started the movie")
        vid.start()

        When("I run vid:stop")
        vid.stop()

        thenStatusShouldBe("stopped")
      }
    }
  }

  feature("capture-image") {
    scenario("capture-image errors when no movie") {
      new WithLoadedExtension with ExpectError {
        whenRunForError("vid:capture-image 640 480",
          vid.`capture-image`(Double.box(640), Double.box(480)))
        thenShouldSeeError("vid: no selected source")
      }
    }

    scenario("invalid dimensions") {
      new WithLoadedExtension with ExpectError {
        givenOpenMovie()
        whenRunForError("vid:capture-image -1 -1",
          vid.`capture-image`(Double.box(-1), Double.box(-1)))
        thenShouldSeeError("vid: invalid dimensions")
      }
    }

    scenario("capture-image returns a scaled image from the active video source") {
      new WithLoadedExtension {
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
      new WithLoadedExtension {
        givenOpenMovie()

        When("I call vid:capture-image")
        val capturedImage =
          vid.`capture-image`()

        Then("I should have a BufferedImage matching the image from the video source")
        assert(capturedImage == dummyImage)
      }
    }
  }

  class CommandPrimitiveLoader extends PrimitiveManager with Dynamic {
    var commands  = Map[String, Command]()
    var reporters = Map[String, Reporter]()

    def addPrimitive(name: String, prim: Primitive) = {
      prim match {
        case c: Command  => commands  += (name -> c)
        case r: Reporter => reporters += (name -> r)
      }
    }

    def applyDynamic(name: String)(args: AnyRef*): AnyRef = {
      val arguments = args.map(v => new FakeArgument(v).asInstanceOf[Argument]).toArray
      val context = new FakeContext()
      commands.get(name).map(cmd => { cmd.perform(arguments, context); null }).orElse(
        reporters.get(name).map(rep => rep.report(arguments, context))).getOrElse(
          throw new Exception(s"could not find command or reporter named $name"))
    }

    def autoImportPrimitives: Boolean = false
    def autoImportPrimitives_=(value: Boolean) = {}
  }

  class FakeContext extends Context {
    def attachCurrentDirectory(path: String): String =
      s"/currentdir/$path"
    def attachModelDir(filePath: String): String = ???
    def getAgent: org.nlogo.api.Agent = ???
    def getDrawing: java.awt.image.BufferedImage = ???
    def getRNG: org.nlogo.util.MersenneTwisterFast = ???
    def importPcolors(image: java.awt.image.BufferedImage,asNetLogoColors: Boolean): Unit = ???
    def logCustomGlobals(nameValuePairs: Seq[(String, String)]): Unit = ???
    def logCustomMessage(msg: String): Unit = ???
  }

  class FakeArgument(val underlying: AnyRef) extends Argument {
    def get: AnyRef = ???
    def getAgent: org.nlogo.api.Agent = ???
    def getAgentSet: org.nlogo.api.AgentSet = ???
    def getBoolean: java.lang.Boolean = ???
    def getBooleanValue: Boolean = ???
    def getCode: java.util.List[org.nlogo.api.Token] = ???
    def getCommandTask: org.nlogo.api.CommandTask = ???
    def getDoubleValue: Double = underlying match {
      case d: java.lang.Double => d.doubleValue
      case _ => throw new ExtensionException(s"expected a double, got $underlying")
    }
    def getIntValue: Int = ???
    def getLink: org.nlogo.api.Link = ???
    def getList: org.nlogo.api.LogoList = ???
    def getPatch: org.nlogo.api.Patch = ???
    def getReporterTask: org.nlogo.api.ReporterTask = ???
    def getString: String =
      underlying match {
        case s: String => s
        case _ => throw new ExtensionException(s"expected a string, but got $underlying")
      }
    def getTurtle: org.nlogo.api.Turtle = ???
  }
}
