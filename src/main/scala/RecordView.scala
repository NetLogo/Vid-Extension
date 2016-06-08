package org.nlogo.extensions.vid

import org.nlogo.core.Syntax
import org.nlogo.api.{ Argument, Context, Command, ExtensionException }

class RecordView(recorder: Recorder) extends Command {

  def getSyntax = Syntax.commandSyntax(right = List())

  def perform(args: Array[Argument], context: Context): Unit = {
    if (! recorder.isRecording)
      throw new ExtensionException("vid: recorder not started")
    recorder.recordFrame(context.workspace.exportView)
  }
}
