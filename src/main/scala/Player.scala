package org.nlogo.extensions.vid

import java.awt.Dimension
import java.awt.event.{ WindowAdapter, WindowEvent }
import java.lang.{ Number => JNumber }

import javafx.application.Platform
import javafx.geometry.Bounds
import javafx.scene.Scene
import javafx.beans.value.ObservableValue
import javafx.beans.value.{ ChangeListener, ObservableValue }
import javafx.embed.swing.JFXPanel

import javax.swing.{ JFrame, SwingUtilities }

trait BoundsPreference {
  def preferredBound: ObservableValue[Dimension]
}

trait Player {
  def videoSource: Option[VideoSource]
  def isShowing: Boolean
  def hide(): Unit
  def show(scene: Scene with BoundsPreference, video: VideoSource): Unit
  def showEmpty(): Unit
}

class JavaFXPlayer extends Player {
  private var _panel: Option[JFXPanel] = Some(new JFXPanel())
  private var frame                    = Option.empty[JFrame]
  var videoSource = Option.empty[VideoSource]

  def isShowing: Boolean = false

  def hide(): Unit = {
    frame.foreach { f =>
      onSwing { () =>
        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING))
      }
    }
  }

  private def withFrame(f: JFrame => Unit) = {
    for {
      currentFrame <- frame orElse Some(new JFrame("NetLogo - vid extension"))
    } {
      frame = Some(currentFrame)
      f(currentFrame)
    }
  }

  def jfxPanel = {
    if (_panel.isEmpty)
      _panel = Some(new JFXPanel())
    _panel.get
  }

  def show(scene: Scene with BoundsPreference, video: VideoSource): Unit = {
    videoSource = Some(video)

    withFrame { f =>
      onJavaFX { () =>
        jfxPanel.setScene(scene)
        scene.preferredBound.addListener(
          new ChangeListener[Dimension] {
            override def changed(obs: ObservableValue[_ <: Dimension], oldDim: Dimension, newDim: Dimension) = {
              jfxPanel.setPreferredSize(newDim)

              onSwing { () =>
                f.pack()
              }
            }
          })

        onSwing { () =>
          f.add(jfxPanel)
          f.pack()
          f.addWindowListener(new WindowAdapter() {
            override def windowClosing(windowEvent: WindowEvent): Unit = {
              frame  = None
              _panel = None
            }
          })
          f.setVisible(true)
        }
      }
    }
  }

  private def onJavaFX(f: () => Unit) =
    Platform.runLater(new Runnable() {
      override def run() = f()
    })

  private def onSwing(f: () => Unit) =
    SwingUtilities.invokeLater(new Runnable() {
      override def run(): Unit = f()
    })


  def showEmpty(): Unit = {}
}
