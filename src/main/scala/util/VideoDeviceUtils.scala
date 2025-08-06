package org.nlogo.extensions.vid.util

import java.io.BufferedInputStream
import java.nio.file.{ Files, Paths }

object VideoDeviceUtils {
  private val (dir, ext) = {
    val os = System.getProperty("os.name").toLowerCase
    val arch = System.getProperty("os.arch")

    if (os.startsWith("win")) {
      (s"windows-$arch", ".dll")
    } else if (os.startsWith("mac")) {
      (s"macosx-$arch", ".dylib")
    } else {
      (s"linux-$arch", ".so")
    }
  }

  // dynamic libraries must be loaded from the host filesystem, so first unpack the library
  // into a temporary directory before attempting to load it (Isaac B 8/5/25)
  private val tempPath = Files.createTempFile("libvideoDeviceUtils", ext)

  Files.deleteIfExists(tempPath)
  Files.copy(getClass.getResourceAsStream(s"/lib/$dir/libvideoDeviceUtils$ext"), tempPath)

  System.load(tempPath.toString)

  def getDeviceNames: Array[String] = {
    new VideoDeviceUtils().getDeviceNames
  }
}

class VideoDeviceUtils {
  @native def getDeviceNames: Array[String]
}
