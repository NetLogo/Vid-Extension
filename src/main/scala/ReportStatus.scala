package org.nlogo.extensions.vid

import org.nlogo.api._

class ReportStatus(vid: VidExtension) extends DefaultReporter {
  def report(args: Array[Argument], context: Context): AnyRef = {
    vid.videoSource match {
      case Some(source) if source.isPlaying => "playing"
      case Some(source)                     => "stopped"
      case None                             => "inactive"
    }
  }
}
