package org.nlogo.extensions.vid

import java.awt.image.BufferedImage

trait VideoSource {
  def play(): Unit

  def stop(): Unit

  def close(): Unit

  def isPlaying: Boolean

  def captureImage(): BufferedImage

  def setTime(timeInSeconds: Double): Unit

  def showInPlayer(player: Player): Unit

  def showInPlayer(player: Player, width: Double, height: Double): Unit
}

trait VideoSourceContainer {
  def videoSource: Option[VideoSource]
  def videoSource_=(source: Option[VideoSource]): Unit
}
