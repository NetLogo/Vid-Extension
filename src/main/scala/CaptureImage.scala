package org.nlogo.extensions.vid

import org.nlogo.api._
import java.awt.image.BufferedImage
import java.awt.{ Image => JImage }

class CaptureImage(vid: VideoSourceContainer) extends Reporter {
  override def getSyntax = Syntax.reporterSyntax(Array[Int](Syntax.NumberType | Syntax.RepeatableType), Syntax.WildcardType)

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
