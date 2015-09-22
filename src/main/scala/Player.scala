package org.nlogo.extensions.vid

import java.awt.Dimension

import javafx.beans.value.ObservableValue
import javafx.scene.Scene

trait BoundsPreference {
  def preferredBound: ObservableValue[Dimension]
}

trait Player {
  def videoSource: Option[VideoSource]
  def isShowing: Boolean
  def hide(): Unit
  def show(scene: Scene with BoundsPreference, video: VideoSource): Unit
  def showEmpty(): Unit
  def showEmpty(width: Double, height: Double): Unit
}
