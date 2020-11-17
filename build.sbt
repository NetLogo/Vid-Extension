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
  "us-ascii",
  // We're using a deprecated method `ButtonBuilder()` which will be
  // removed in JDK 9, so we need to update the code in RunVid.scala.
  // Aaron B November 2020
  // "-Xfatal-warnings"
  "-Xlint"
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "bridjhack" at "https://maven.ecs.soton.ac.uk/content/repositories/thirdparty/"

// Here is the tracking issue for why we're using `0.3.13-SNAPSHOT` and the unofficial version of `bridj`:
//   https://github.com/sarxos/webcam-capture/issues/723

// The stuff for `webcam-capture-driver-openimaj` and `core-video-capture` was the only way to get capture working
// simultaneously with other applications, like Zoom.  This still pulls in too many jars, I think, but it does work
// so we'll leave it for now:
//   https://github.com/sarxos/webcam-capture/issues/757#issuecomment-609102909

// Even with these changes and fixes, you still cannot run `netlogo/run` and test `vid` from an sbt session.  You need
// to manually test it with a notarized, entitled copy of NetLogo.

// -Jeremy B November 2020

libraryDependencies ++= Seq(
  "com.github.sarxos" % "webcam-capture" % "0.3.13-SNAPSHOT",
  "com.github.sarxos" % "webcam-capture-driver-openimaj" % "0.3.13-SNAPSHOT" exclude("org.openimaj", "core-video-capture"),
  "org.openimaj" % "core-video-capture" % "1.3.10"
    exclude("net.billylieurance.azuresearch", "azure-bing-search-java")
    exclude("uk.ac.ed.ph.snuggletex", "snuggletex-core")
    exclude("uk.ac.ed.ph.snuggletex", "snuggletex-upconversion")
    exclude("uk.ac.ed.ph.snuggletex", "snuggletex-jeuclid")
    exclude("com.aetrion.flickr", "flickrapi")
    exclude("vigna.dsi.unimi.it", "jal")
    exclude("jama", "jama")
    exclude("com.googlecode.matrix-toolkits-java", "mtj")
    exclude("com.googlecode.netlib-java", "netlib-java")
    exclude("net.sf.jafama", "JaFaMa")
    exclude("jgrapht", "jgrapht")
    exclude("ch.akuhn.matrix", "MatrixLib")
    exclude("gov.sandia.foundry", "gov-sandia-cognition-common-core")
    exclude("com.thoughtworks.xstream", "xstream")
    exclude("gov.sandia.foundry", "gov-sandia-cognition-common-data")
    exclude("gov.sandia.foundry", "gov-sandia-cognition-learning-core")
    exclude("gov.sandia.foundry", "gov-sandia-cognition-text-core")
    exclude("gov.sandia.foundry", "gov-sandia-cognition-framework-core")
    exclude("gov.sandia.foundry", "gov-sandia-cognition-framework-learning")
    exclude("org.openimaj", "core-citation")
    exclude("org.jsoup", "jsoup")
    exclude("net.sf.trove4j", "trove4j")
    exclude("colt", "colt")
    exclude("com.esotericsoftware.kryo", "kryo")
    exclude("org.apache.ant", "ant")
    exclude("org.apache.httpcomponents", "httpclient")
    exclude("net.sourceforge.jmatio", "jmatio")
    exclude("com.caffeineowl.graphics", "BezierUtils")
    exclude("javax.media", "jai-core")
    exclude("com.sun.media", "jai-codec")
    ,
  "com.nativelibs4java" % "bridj" % "0.7-20140918-3",
  "org.bytedeco" % "javacv" % "1.4.3",
  "org.jcodec" % "jcodec" % "0.1.9",
  "org.jcodec" % "jcodec-javase" % "0.1.9",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

// necessary for testing camera functionality.
// See https://groups.google.com/forum/#!topic/nativelibs4java/WNmOZPknRiU
fork in Test := true

netLogoVersion := "6.1.1-c82c397"
