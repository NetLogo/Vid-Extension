package org.nlogo.extensions.vid

import java.awt.image.BufferedImage
import java.nio.file.Path

trait Recorder {
  def isRecording: Boolean
  def start(): Unit
  def reset(): Unit
  def recordFrame(image: BufferedImage): Unit
  def save(dest: Path): Unit
}

object Recorder {
  object AlreadyStarted extends Exception("Recorder is already recording")
  object NotRecording extends Exception("Recorder is not recording")
}
