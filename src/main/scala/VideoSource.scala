package org.nlogo.extensions.vid

import java.awt.image.BufferedImage

trait VideoSource {
  def play(): Unit

  def stop(): Unit

  def isPlaying: Boolean

  def captureImage(): BufferedImage

  def setTime(timeInSeconds: Double): Unit

  def showInPlayer(player: Player): Unit
}
