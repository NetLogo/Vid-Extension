package org.nlogo.extensions.vid

import org.nlogo.core.Syntax
import org.nlogo.api.{ Argument, Context, Reporter }

class RecorderStatus(recorder: Recorder) extends Reporter {

  def getSyntax = Syntax.reporterSyntax(right = List(), ret = Syntax.StringType)

  def report(args: Array[Argument], context: Context): AnyRef = {
    if (recorder.isRecording) "recording"
    else "inactive"
  }
}
