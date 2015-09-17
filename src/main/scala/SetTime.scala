package org.nlogo.extensions.vid

import org.nlogo.api._

class SetTime(vid: VidExtension) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    if (vid.videoSource.isEmpty)
      throw new ExtensionException("vid: no selected source")
    try {
      vid.videoSource.foreach(_.setTime(args(0).getDoubleValue.toLong))
    } catch {
      case e: IllegalArgumentException =>
        throw new ExtensionException("vid: invalid time")
    }
  }
}
