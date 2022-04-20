## Building

Build with `sbt package`. `sbt test` runs tests.

### Camera Capture Hardware Libraries

THe Vid extension has some confusing dependency issues that require a little more elaboration due to the fact that it needs to access hardware through some kind of Java interop layer.

1.  Why are we using both OpenImaj and JavaCV?  Don't they both do camera capture?

Both libraries do camera capture, it's true.  The issue is that JavaCV depends on [OpenCV](https://github.com/opencv/opencv) for its functionality, and [OpenCV doesn't provide camera discovery on all platforms (Windows only)](https://github.com/bytedeco/javacv/issues/189).  OpenImaj does support camera discovery on all platforms we care about, but [it has lots of trouble running successfully on macOS](https://github.com/openimaj/openimaj/issues/170).  The latest version of OpenImaj I tested crashed randomly but regularly in normal usage of the camera capture functionality.  However the OpenImaj camera *discovery* feature works just fine, no crashes.  Hence we use both.  If ever OpenImaj improves its macOS support or if JavaCV supports camera discovery, we could eliminate one or the other.

2.  What is that `sbt-javacpp` sbt plugin stuff?  Why is the `javacv` dep listed as `"org.bytedeco" % "javacv" % "1.5.7"` when the JavaCV project docs say to use `javacv-platform` like `"org.bytedeco" % "javacv-platform" % "1.5.7"`?

Sigh.  JavaCV uses a custom interop layer to get native hardware libraries loaded and usable in Java code.  That library is JavaCPP.  The way JavaCPP includes the native libraries (dll, so, or dylib) through Java app jars is with the idea of "platforms" for each library that has native dependencies.  So the `opencv` library has a corresponding `opencv-platform` library which depends on `opencv` as well as on all the platform-specific jars.  The "platform" library doesn't really have any code of its own.

So the problem is that JavaCPP and JavaCV build for many platforms, including iOS and Android, across many different architectures.  We only need macOS, Linux 32/64, and Windows 32/64.  The native library bundles are big, 20 megs each.  The full "naive" install of all the native libraries, most of which we don't need, is almost 1 gigabyte.  The JavaCPP devs recognize this problem, and provide the `sbt-javacpp` sbt plugin to manage the specific architectures used by a project.  Those settings are in the `build.sbt` like so:

```scala
javaCppVersion    :=  "1.5.7"
javaCppPresetLibs ++= Seq("opencv" -> "4.5.5", "openblas" -> "0.3.19")
javaCppPlatform   :=  Seq("windows-x86_64", "windows-x86", "macosx-arm64", "macosx-x86_64", "linux-x86", "linux-x86_64")
```

Note that we don't actually 100% need the plugin to do this.  We could do something like:

```scala
, "org.bytedeco" % "javacv-platform" % "1.5.7"
  // other exclusions clipped...
  exclude("org.bytedeco", "opencv-platform")
  exclude("org.bytedeco", "javacpp-platform")
  exclude("org.bytedeco", "ffmpeg-platform")
// now include the specific packages we need for each platform
, "org.bytedeco" % "opencv" % "4.5.5-1.5.7" classifier "windows-x86_64"
, "org.bytedeco" % "javacpp" % "4.5.5-1.5.7" classifier "windows-x86_64"
, "org.bytedeco" % "openblas" % "0.3.19-1.5.7" classifier "windows-x86_64"
```

But we'd have to write code to enumerate all the projects and architecture combinations, and keep them all up to date as things change.  So why bother if we already have an sbt plugin that will do it for us?  (The secret answer is because that sbt plugin might break in the future and we might not want to deal with updating it.)

3.  Why all those excluded deps for OpenImaj?

OpenImaj is a general purpose image processing library, it does much more than video capture.  To that end it includes loads of libraries for purposes not related to what we use it for, enumerating video devices.  There might, indeed, be more we could weed out of this list, but it's a tedious process to do so.
