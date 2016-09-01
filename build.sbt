import org.nlogo.build.{ NetLogoExtension, ExtensionDocumentationPlugin}

enablePlugins(NetLogoExtension, ExtensionDocumentationPlugin)

organization := "org.nlogo"

scalaVersion := "2.11.8"

netLogoExtName := "vid"

netLogoClassManager := "org.nlogo.extensions.vid.VidExtension"

netLogoZipSources := false

netLogoTarget :=
  NetLogoExtension.directoryTarget(baseDirectory.value)

scalacOptions ++=
  "-deprecation -unchecked -feature -Xcheckinit -encoding us-ascii -Xlint -Xfatal-warnings".split(" ").toSeq

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "com.github.sarxos" % "webcam-capture" % "0.3.11",
  "org.jcodec" % "jcodec" % "0.1.9",
  "org.jcodec" % "jcodec-javase" % "0.1.9",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

isSnapshot := true

// necessary for testing camera functionality.
// See https://groups.google.com/forum/#!topic/nativelibs4java/WNmOZPknRiU
fork in Test := true

netLogoVersion := "6.0.0-BETA1"
