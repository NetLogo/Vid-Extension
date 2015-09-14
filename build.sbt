organization := "org.nlogo"

scalaVersion := "2.11.7"

name := "vid"

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "com.github.sarxos" % "webcam-capture" % "0.3.10",
  "com.github.sarxos" % "v4l4j" % "0.9.1-r507"
)
