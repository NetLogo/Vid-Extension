enablePlugins(org.nlogo.build.NetLogoExtension)

organization := "org.nlogo"

scalaVersion := "2.11.7"

scalacOptions ++=
  "-deprecation -unchecked -feature -Xcheckinit -encoding us-ascii -Xlint -Xfatal-warnings".split(" ").toSeq

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies +=
  "org.nlogo" % "NetLogo" % "6.0-PREVIEW" changing() from "file:///Users/rgg284/IdeaProjects/NetLogo/netlogo-gui/target/NetLogo.jar"

libraryDependencies ++= Seq(
  "com.github.sarxos" % "webcam-capture" % "0.3.10",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

unmanagedJars in Compile += Attributed.blank(file(System.getProperty("java.home") + "/lib/ext/jfxrt.jar"))

isSnapshot := true

netLogoExtName := "vid"

netLogoClassManager := "org.nlogo.extensions.vid.VidExtension"

netLogoZipSources := false

// necessary for testing camera functionality.
// See https://groups.google.com/forum/#!topic/nativelibs4java/WNmOZPknRiU
fork in Test := true
