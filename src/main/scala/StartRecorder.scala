package org.nlogo.extensions.vid

import org.nlogo.core.Syntax
import org.nlogo.api.{ Argument, Context, Command, ExtensionException }

class StartRecorder(recorder: Recorder) extends Command {

  def getSyntax = Syntax.commandSyntax(right = List())

  def perform(args: Array[Argument], context: Context): Unit = {
    try {
      recorder.start()
    } catch {
      case Recorder.AlreadyStarted => throw new ExtensionException("vid: recorder already started")
    }
  }
}
