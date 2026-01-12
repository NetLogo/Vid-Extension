import org.nlogo.build.{ NetLogoExtension, ExtensionDocumentationPlugin}

enablePlugins(NetLogoExtension, ExtensionDocumentationPlugin)

name       := "vid"
version    := "1.3.2"
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
netLogoVersion      := "7.0.0-2486d1e"
netLogoZipExtras   ++= Seq(baseDirectory.value / "README.md")

// only include the target platform to reduce the size of the final zip (Isaac B 1/8/26)
val javaCppPlatform: String = {
  (System.getProperty("os.name"), System.getProperty("os.arch")) match {
    case (name, arch) if name.startsWith("Linux") =>
      if (arch.contains("x86")) {
        "linux-x86"
      } else {
        "linux-x86_64"
      }

    case (name, arch) if name.startsWith("Mac") =>
      if (arch.contains("aarch64")) {
        "macosx-arm64"
      } else {
        "macosx-x86_64"
      }

    case (name, arch) if name.startsWith("Windows") =>
      if (arch.contains("x86")) {
        "windows-x86"
      } else {
        "windows-x86_64"
      }

    case _ => throw new Exception("Unknown platform!")
  }
}

libraryDependencies ++= Seq(
  "org.bytedeco" % "opencv" % "4.5.5-1.5.7" classifier javaCppPlatform
, "org.bytedeco" % "javacpp" % "1.5.7" classifier javaCppPlatform
, "org.bytedeco" % "openblas" % "0.3.19-1.5.7" classifier javaCppPlatform
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
  Seq("base", "controls", "media", "swing")
    .map(m => "org.openjfx" % s"javafx-$m" % javaFXVersion classifier osName)
}
