package org.nlogo.extensions.vid

import java.awt.Dimension

import javafx.beans.value.ObservableValue
import javafx.scene.Scene

trait BoundsPreference {
  def preferredSize: ObservableValue[Dimension]
  def enforcedBounds: Option[(Double, Double)]
}

case class BoundedScene(scene: Scene,
  preferredSize: ObservableValue[Dimension],
  enforcedBounds: Option[(Double, Double)])

trait Player {
  def videoSource: Option[VideoSource]
  def boundedSize: Option[(Double, Double)]
  def isShowing: Boolean
  def show(): Unit
  def hide(): Unit
  def setScene(scene: Scene with BoundsPreference, video: Option[VideoSource]): Unit
  def emptyScene(bounds: Option[(Double, Double)]): Scene with BoundsPreference
}
