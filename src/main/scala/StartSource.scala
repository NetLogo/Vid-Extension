package org.nlogo.extensions.vid

import org.nlogo.api._

class StartSource(vid: VidExtension) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    if (vid.videoSource.isEmpty)
      throw new ExtensionException("vid: no selected source")
    vid.videoSource.foreach(_.play())
  }
}
