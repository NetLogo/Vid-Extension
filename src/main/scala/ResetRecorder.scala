package org.nlogo.extensions.vid

import org.nlogo.core.Syntax
import org.nlogo.api.{ Argument, Context, Command }

class ResetRecorder(recorder: Recorder) extends Command {

  def getSyntax = Syntax.commandSyntax(right = List())

  def perform(args: Array[Argument], context: Context): Unit = {
    recorder.reset()
  }
}
