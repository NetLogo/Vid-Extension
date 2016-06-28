package org.nlogo.extensions.vid

import java.nio.file.Files
import javax.imageio.ImageIO

import org.nlogo.core.Syntax
import org.nlogo.api.{ Argument, Context, Command, ExtensionException }

class RecordSource(recorder: Recorder, vid: VideoSourceContainer) extends Command {

  def getSyntax = Syntax.commandSyntax(right = List())

  def perform(args: Array[Argument], context: Context): Unit = {
    if (! recorder.isRecording)
      throw new ExtensionException("vid: recorder not started")
    val image = vid.videoSource.map(_.captureImage).getOrElse(throw new ExtensionException("vid: no selected source"))
    recorder.recordFrame(image)
  }
}
