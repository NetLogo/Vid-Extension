package org.nlogo.extensions.vid

import java.awt.image.BufferedImage

trait VideoSource {
  def isPlaying: Boolean

  def captureImage(): BufferedImage
}
