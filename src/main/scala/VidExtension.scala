package org.nlogo.extensions.vid

import org.nlogo.app.App
import org.nlogo.api._
import java.io.{ File => JFile }

class VidExtension(files: MovieFactory) extends DefaultClassManager {

  override def load(manager: PrimitiveManager) = {
    manager.addPrimitive("movie-open", new MovieOpen(this, files))
    manager.addPrimitive("close",      new CloseVideoSource(this))
    manager.addPrimitive("start",      new StartSource(this))
  }

  var activeVideoSource: Option[AnyRef] = None
}

class MovieOpen(vid: VidExtension, files: MovieFactory) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    val filePath = context.attachCurrentDirectory(args(0).getString)
    try {
      if (files.open(filePath).nonEmpty) {
        vid.activeVideoSource = Some(true: java.lang.Boolean)
      } else {
        throw new ExtensionException("vid: no movie found")
      }
    } catch {
      case e: InvalidFormatException =>
        throw new ExtensionException("vid: format not supported")
    }
  }
}

class CloseVideoSource(vid: VidExtension) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    vid.activeVideoSource = None
  }
}

class StartSource(vid: VidExtension) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    throw new ExtensionException("vid: no selected source")
  }
}
