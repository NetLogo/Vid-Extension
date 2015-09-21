package org.nlogo.extensions.vid

import org.nlogo.api._

class CloseVideoSource(vid: VidExtension) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    vid.videoSource.foreach(_.close())
    vid.videoSource = None
  }
}
