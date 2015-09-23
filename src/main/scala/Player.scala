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
  def show(): Unit
  def hide(): Unit
  def setScene(scene: Scene with BoundsPreference, video: Option[VideoSource]): Unit
  def emptyScene: Scene with BoundsPreference = emptyScene(640, 480)
  def emptyScene(width: Double, height: Double): Scene with BoundsPreference
}
