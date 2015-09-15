enablePlugins(org.nlogo.build.NetLogoExtension)

organization := "org.nlogo"

scalaVersion := "2.11.7"

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "com.github.sarxos" % "webcam-capture" % "0.3.10",
  "com.github.sarxos" % "v4l4j" % "0.9.1-r507"
)

netLogoExtName := "vid"

netLogoClassManager := "org.nlogo.extensions.VidExtension"

netLogoZipSources := false
