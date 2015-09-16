enablePlugins(org.nlogo.build.NetLogoExtension)

organization := "org.nlogo"

scalaVersion := "2.11.7"

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"


libraryDependencies +=
  "org.nlogo" % "NetLogo" % "5.3" from "http://ccl.northwestern.edu/devel/NetLogo-5.3-LevelSpace-3a6b9b4.jar"

libraryDependencies ++= Seq(
  "com.github.sarxos" % "webcam-capture" % "0.3.10"
)

isSnapshot := true

netLogoExtName := "vid"

netLogoClassManager := "org.nlogo.extensions.vid.VidExtension"

netLogoZipSources := false
