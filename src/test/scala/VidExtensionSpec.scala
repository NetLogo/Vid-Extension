package org.nlogo.extensions.vid

import org.scalatest.{ FeatureSpec, GivenWhenThen }

import java.io.{ File => JFile }

import org.nlogo.api._

import scala.language.dynamics

class VidExtensionSpec extends FeatureSpec with GivenWhenThen {
  val fileSet = Map[String, JFile](
    "foobar.mp4" -> JFile.createTempFile("foobar", ".mp4"))

  def withLoadedExtension(f: (VidExtension, CommandPrimitiveLoader) => Unit) = {
    val vidExtension = new VidExtension(fileSet)
    val primitiveLoader = new CommandPrimitiveLoader()
    vidExtension.load(primitiveLoader)
    f(vidExtension, primitiveLoader)
  }

  feature("opening and closing") {
    scenario("opens a movie") {
      withLoadedExtension { (vidExtension, primitiveLoader) =>
        When("I have opened a movie")
        primitiveLoader.`movie-open`("foobar.mp4")

        Then("I should see that I have an active video source")
        assert(vidExtension.activeVideoSource != null)
      }
    }

    scenario("fails to open a movie") {
      withLoadedExtension { (vidExtension, primitiveLoader) =>
        var ee: ExtensionException = null

        When("I try to open a movie that doesn't exist")
        try {
          primitiveLoader.`movie-open`("not-real.mp4")
          fail("expected to see an error when opening non-existent movie")
        } catch {
          case e: ExtensionException => ee = e
        }

        Then("I should see an error - vid: no movie found")
        assert(ee != null)
        assert(ee.getMessage.contains("vid: no movie found"))
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
    def attachCurrentDirectory(path: String): String = ???
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
    def getDoubleValue: Double = ???
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
