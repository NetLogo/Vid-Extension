import org.nlogo.build.{ NetLogoExtension, ExtensionDocumentationPlugin}

enablePlugins(NetLogoExtension, ExtensionDocumentationPlugin)

name       := "vid"
version    := "1.2.1"
isSnapshot := true

organization := "org.nlogo"
scalaVersion := "3.7.0"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding", "us-ascii",
  "-Xfatal-warnings",
  "-release", "11"
)

netLogoClassManager := "org.nlogo.extensions.vid.VidExtension"
netLogoVersion      := "7.0.0-beta2-8cd3e65"
netLogoZipExtras   ++= Seq(baseDirectory.value / "README.md")

// settings for the `sbt-javacpp` sbt plugin
javaCppVersion    :=  "1.5.7"
// opencv depends on openblas so get those platform-specific binaries, too
javaCppPresetLibs ++= Seq("opencv" -> "4.5.5", "openblas" -> "0.3.19")
// only include the supported NetLogo platforms
javaCppPlatform := {
  (System.getProperty("os.name"), System.getProperty("os.arch")) match {
    case (name, arch) if name.startsWith("Linux") =>
      if (arch.contains("x86")) {
        Seq("linux-x86")
      } else {
        Seq("linux-x86_64")
      }

    case (name, arch) if name.startsWith("Mac") =>
      if (arch.contains("aarch64")) {
        Seq("macosx-arm64")
      } else {
        Seq("macosx-x86_64")
      }

    case (name, arch) if name.startsWith("Windows") =>
      if (arch.contains("x86")) {
        Seq("windows-x86")
      } else {
        Seq("windows-x86_64")
      }

    case _ => throw new Exception("Unknown platform!")
  }
}

libraryDependencies ++= Seq(
  "org.openimaj" % "core" % "1.4-SNAPSHOT" from "https://ccl.northwestern.edu/devel/openimaj_core-1.4-20220209.101848-153.jar"
, "org.openimaj" % "core-image" % "1.4-SNAPSHOT" from "https://ccl.northwestern.edu/devel/openimaj_core-image-1.4-20220209.101850-153.jar"
, "org.openimaj" % "core-aop-support" % "1.4-SNAPSHOT" from "https://ccl.northwestern.edu/devel/openimaj_core-aop-support-1.4-20220209.101849-153.jar"
, "org.openimaj" % "core-citation" % "1.4-SNAPSHOT" from "https://ccl.northwestern.edu/devel/openimaj_core-citation-1.4-20220209.101849-153.jar"
, "org.openimaj" % "core-video" % "1.4-SNAPSHOT" from "https://ccl.northwestern.edu/devel/openimaj_core-video-1.4-20220209.101851-153.jar"
, "org.openimaj" % "core-video-capture" % "1.4-SNAPSHOT" from "https://ccl.northwestern.edu/devel/openimaj_core-video-capture-1.4-20220209.101851-153.jar"
    exclude("org.openimaj.content", "animation")
    exclude("org.openimaj", "core-audio")
    exclude("org.openimaj", "core-math")
    exclude("com.twelvemonkeys.common", "common-lang")
    exclude("com.sun.media", "jai-codec")
    exclude("javax.media", "jai-core")
    exclude("net.sourceforge.jeuclid", "jeuclid-core")
    exclude("uk.ac.ed.ph.snuggletex", "snuggletex-core")
    exclude("com.googlecode.json-simple", "json-simple")
    exclude("com.flickr4java", "flickr4java")
    exclude("uk.ac.ed.ph.snuggletex", "snuggletex-upconversion")
    exclude("com.caffeineowl", "bezier-utils")
    exclude("com.twelvemonkeys.imageio", "imageio-core")
    exclude("uk.ac.ed.ph.snuggletex", "snuggletex-jeuclid")
    exclude("com.twelvemonkeys.imageio", "imageio-jpeg")
    exclude("org.apache.ant", "ant")
, "com.nativelibs4java" % "bridj" % "0.7-SNAPSHOT" from "https://ccl.northwestern.edu/devel/openimaj_bridj-0.7-20140918-3.jar"
// only include `javacv` and not `javacv-platform` as we manually specify the native libraries
// throught the `sbt-javacpp` sbt plugin
, "org.bytedeco" % "javacv" % "1.5.7"
, "org.jcodec" % "jcodec" % "0.1.9"
, "org.jcodec" % "jcodec-javase" % "0.1.9"
)

// necessary for testing camera functionality.
// See https://groups.google.com/forum/#!topic/nativelibs4java/WNmOZPknRiU
Test / fork := true

// Add JavaFX dependencies
val javaFXVersion = "21"
libraryDependencies ++= {
  // Determine OS version of JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % javaFXVersion classifier osName)
}
