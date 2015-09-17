package org.nlogo.extensions.vid

import org.nlogo.app.App
import org.nlogo.api._
import java.io.{ File => JFile }

class VidExtension(files: MovieFactory, cameras: CameraFactory) extends DefaultClassManager {

  override def load(manager: PrimitiveManager) = {
    manager.addPrimitive("movie-open",    new MovieOpen(this, files))
    manager.addPrimitive("camera-open",   new CameraOpen(this, cameras))
    manager.addPrimitive("status",        new ReportStatus(this))
    manager.addPrimitive("close",         new CloseVideoSource(this))
    manager.addPrimitive("start",         new StartSource(this))
    manager.addPrimitive("stop",          new StopSource(this))
    manager.addPrimitive("capture-image", new CaptureImage(this))
  }

  var videoSource: Option[VideoSource] = None
}

class MovieOpen(vid: VidExtension, files: MovieFactory) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    val filePath = context.attachCurrentDirectory(args(0).getString)
    try {
      if (files.open(filePath).nonEmpty) {
        vid.videoSource = Some(new VideoSource {})
      } else {
        throw new ExtensionException("vid: no movie found")
      }
    } catch {
      case e: InvalidFormatException =>
        throw new ExtensionException("vid: format not supported")
    }
  }
}

class CameraOpen(vid: VidExtension, cameras: CameraFactory) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    val cameraName = args(0).getString
    cameras.open(cameraName) match {
      case Some(x) =>
        vid.videoSource = Some(new VideoSource {
          override def isPlaying = true
        })
      case None =>
        throw new ExtensionException(s"""vid: camera "$cameraName" not found""")
    }
  }
}

class CloseVideoSource(vid: VidExtension) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    vid.videoSource = None
  }
}

class StartSource(vid: VidExtension) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    if (args(0).getDoubleValue <= 0 || args(1).getDoubleValue <= 0)
      throw new ExtensionException("vid: invalid dimensions")
    else if (vid.videoSource.isEmpty)
      throw new ExtensionException("vid: no selected source")
  }
}

class StopSource(vid: VidExtension) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    vid.videoSource = Some(new VideoSource {
      override def isPlaying = false
    })
  }
}

class CaptureImage(vid: VidExtension) extends DefaultReporter {
  def report(args: Array[Argument], context: Context): AnyRef = {
    throw new ExtensionException("vid: no selected source")
    null
  }
}

class ReportStatus(vid: VidExtension) extends DefaultReporter {
  def report(args: Array[Argument], context: Context): AnyRef = {
    vid.videoSource match {
      case Some(source) if source.isPlaying => "playing"
      case Some(source)                     => "stopped"
      case None                             => "inactive"
    }
  }
}
