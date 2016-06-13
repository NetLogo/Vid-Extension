package org.nlogo.extensions.vid

import java.nio.file.{ Files, Paths }
import java.io.FileNotFoundException

import org.nlogo.core.Syntax
import org.nlogo.api.{ Argument, Context, Command, ExtensionException }

class SaveRecording(recorder: Recorder) extends Command {

  def getSyntax = Syntax.commandSyntax(right = List(Syntax.StringType))

  def perform(args: Array[Argument], context: Context): Unit = {
    try {
      val pathString = args(0).getString.stripSuffix(".mp4") + ".mp4"
      val path = Paths.get(pathString)
      val savePath =
        if (path.isAbsolute) path
        else Paths.get(context.attachCurrentDirectory(pathString))
      recorder.save(savePath)
    } catch {
      case _: FileNotFoundException => throw new ExtensionException("vid: no such directory")
      case Recorder.NotRecording    => throw new ExtensionException("vid: recorder not started")
    }
  }
}

