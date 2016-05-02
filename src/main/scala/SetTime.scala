package org.nlogo.extensions.vid

import org.nlogo.api._
import org.nlogo.core.Syntax

class SetTime(vid: VideoSourceContainer) extends Command {
  override def getSyntax =
    Syntax.commandSyntax(right = List[Int](Syntax.NumberType))

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
