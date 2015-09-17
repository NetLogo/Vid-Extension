package org.nlogo.extensions.vid

import org.nlogo.api._

class StopSource(vid: VidExtension) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    vid.videoSource.foreach(_.stop())
  }
}
