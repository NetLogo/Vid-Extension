package org.nlogo.extensions.vid

import javafx.scene.Scene

trait Player {
  def videoSource: Option[VideoSource]
  def isShowing: Boolean
  def hide(): Unit
  def show(scene: Scene, video: VideoSource): Unit
  def showEmpty(): Unit
}
