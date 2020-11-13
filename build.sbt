import org.nlogo.build.{ NetLogoExtension, ExtensionDocumentationPlugin}

enablePlugins(NetLogoExtension, ExtensionDocumentationPlugin)

organization := "org.nlogo"

scalaVersion := "2.12.12"

version := "1.0.1"

netLogoExtName := "vid"

netLogoClassManager := "org.nlogo.extensions.vid.VidExtension"

netLogoZipSources := false

netLogoTarget :=
  NetLogoExtension.directoryTarget(baseDirectory.value)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xcheckinit",
  "-encoding",
  // We're using a deprecated method `ButtonBuilder()` which will be
  // removed in JDK 9, so we need to update the code in RunVid.scala.
  // Aaron B November 2020
  // "-Xfatal-warnings"
  "us-ascii",
  "-Xlint"
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "com.github.sarxos" % "webcam-capture" % "0.3.11",
  "org.jcodec" % "jcodec" % "0.1.9",
  "org.jcodec" % "jcodec-javase" % "0.1.9",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

// necessary for testing camera functionality.
// See https://groups.google.com/forum/#!topic/nativelibs4java/WNmOZPknRiU
fork in Test := true

netLogoVersion := "6.1.1-c82c397"
