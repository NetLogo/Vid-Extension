package org.nlogo.extensions.vid

import org.nlogo.app.App
import org.nlogo.api._
import java.io.{ File => JFile }
import java.awt.image.BufferedImage
import java.awt.{ Image => JImage }

class VidExtension(files: MovieFactory, cameras: CameraFactory) extends DefaultClassManager {

  override def load(manager: PrimitiveManager) = {
    manager.addPrimitive("movie-open",    new MovieOpen(this, files))

    manager.addPrimitive("camera-open",   new CameraOpen(this, cameras))
    manager.addPrimitive("camera-names",  new CameraNames(cameras))

    manager.addPrimitive("status",        new ReportStatus(this))

    manager.addPrimitive("close",         new CloseVideoSource(this))
    manager.addPrimitive("start",         new StartSource(this))
    manager.addPrimitive("stop",          new StopSource(this))
    manager.addPrimitive("capture-image", new CaptureImage(this))
    manager.addPrimitive("set-time",      new SetTime(this))
  }

  var videoSource: Option[VideoSource] = None
}

class MovieOpen(vid: VidExtension, files: MovieFactory) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    val filePath = context.attachCurrentDirectory(args(0).getString)
    try {
      vid.videoSource = files.open(filePath)
      if (vid.videoSource.isEmpty)
        throw new ExtensionException("vid: no movie found")
    } catch {
      case e: InvalidFormatException =>
        throw new ExtensionException("vid: format not supported")
    }
  }
}

class CameraOpen(vid: VidExtension, cameras: CameraFactory) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    val cameraName =
      if (args.length == 0)
        cameras.defaultCameraName.getOrElse(
          throw new ExtensionException("vid: no cameras found"))
      else
        args(0).getString
    val camera = cameras.open(cameraName).getOrElse(
      throw new ExtensionException(s"""vid: camera "$cameraName" not found"""))
    vid.videoSource = Some(new VideoSource {
      override def setTime(timeInMillis: Long): Unit = {}
      override def stop() = {}
      override def play() = {}
      override def isPlaying = true
      override def captureImage() = null
    })
  }
}

class CameraNames(cameras: CameraFactory) extends DefaultReporter {
  override def report(args: Array[Argument], context: Context): AnyRef =
    LogoList(cameras.cameraNames: _*)
}

class CloseVideoSource(vid: VidExtension) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    vid.videoSource = None
  }
}

class StartSource(vid: VidExtension) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    if (vid.videoSource.isEmpty)
      throw new ExtensionException("vid: no selected source")
    vid.videoSource.foreach(_.play())
  }
}

class StopSource(vid: VidExtension) extends DefaultCommand {
  def perform(args: Array[Argument], context: Context): Unit = {
    vid.videoSource.foreach(_.stop())
  }
}

class CaptureImage(vid: VidExtension) extends DefaultReporter {
  def report(args: Array[Argument], context: Context): AnyRef = {
    vid.videoSource.map { source =>
      val image = source.captureImage()
      args.length match {
        case 0 => image
        case 2 =>
          val (width, height) =
            (args(0).getDoubleValue, args(1).getDoubleValue)
          if (width > 0 && height > 0)
            toBufferedImage(
              image.getScaledInstance(
                width.toInt, height.toInt, JImage.SCALE_DEFAULT))
          else
            invalidDimensions()
        case _ => invalidDimensions()
      }
    }.getOrElse(
      throw new ExtensionException("vid: no selected source"))
  }

  private def toBufferedImage(image: JImage): BufferedImage =
    image match {
      case bi: BufferedImage => bi
      case _ =>
        val bi = new BufferedImage(image.getHeight(null), image.getWidth(null), BufferedImage.TYPE_INT_ARGB)

        val bGr = bi.createGraphics()
        bGr.drawImage(image, 0, 0, null)
        bGr.dispose()

        bi
    }

  private def invalidDimensions() =
    throw new ExtensionException("vid: invalid dimensions")
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

class SetTime(vid: VidExtension) extends DefaultCommand {
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
