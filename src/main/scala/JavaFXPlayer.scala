package org.nlogo.extensions.vid

import java.awt.Dimension
import java.awt.event.{ WindowAdapter, WindowEvent }
import java.lang.{ Number => JNumber }

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.value.{ ChangeListener, ObservableValue }
import javafx.embed.swing.JFXPanel
import javafx.geometry.Bounds
import javafx.scene.{ Group, Scene }
import javafx.scene.shape.Rectangle

import javax.swing.{ JFrame, SwingUtilities }

import util.FunctionToCallback.{ function2Callable, function2Runnable, function2ChangeListener }

class JavaFXPlayer extends Player {
  type BoundedScene = Scene with BoundsPreference
  new JFXPanel() // init JavaFX

  var videoSource: Option[VideoSource]   = None

  private var frame: Option[PlayerFrame] = None
  private var currentScene: BoundedScene = _

  private val resizeListener: ChangeListener[Dimension] =
    function2ChangeListener { (oldDim: Dimension, newDim: Dimension) =>
      onSwing { () =>
        withFrame { f =>
          f.jfxPanel.setPreferredSize(newDim)
          f.pack()
        }
      }
    }

  setScene(emptyScene(640, 480), None)

  def emptyScene(width: Double, height: Double) = {
    val rectangle = new Rectangle(width, height)
    new Scene(new Group(rectangle)) with BoundsPreference {
      val preferredBound: ObservableValue[Dimension] =
        Bindings.createObjectBinding[Dimension](
          () =>
            new Dimension(rectangle.getWidth.toInt, rectangle.getHeight.toInt),
            rectangle.widthProperty, rectangle.heightProperty)
    }
  }

  override def show(): Unit =
    onSwing(() => withFrame(f => f.setVisible(true)))

  override def isShowing: Boolean = frame.exists(_.isVisible)

  override def hide(): Unit =
    frame.foreach { f =>
      onSwing { () =>
        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING))
      }
    }

  override def setScene(scene: BoundedScene, video: Option[VideoSource]): Unit = {
    if (scene != currentScene) {
      onJavaFX { () =>
        if (currentScene != null)
          currentScene.preferredBound.removeListener(resizeListener)
        scene.preferredBound.addListener(resizeListener)
        currentScene = scene
        val preferredSize = scene.preferredBound.getValue
        onSwing { () =>
          withFrame { f =>
            f.jfxPanel.setScene(scene)
            f.jfxPanel.setPreferredSize(preferredSize)
            f.pack()
          }
        }
      }
    }
  }

  class PlayerFrame extends JFrame("NetLogo - vid extension") {
    val jfxPanel = new JFXPanel()

    add(jfxPanel)

    addWindowListener(new WindowAdapter() {
      override def windowClosing(windowEvent: WindowEvent): Unit = {
        frame  = None
      }
    })
  }

  private def withFrame(f: PlayerFrame => Unit) = {
    for {
      currentFrame <- frame orElse Some(new PlayerFrame)
    } {
      frame = Some(currentFrame)
      f(currentFrame)
    }
  }

  private def onJavaFX(runnable: Runnable) =
    Platform.runLater(runnable)

  private def onSwing(runnable: Runnable) =
    SwingUtilities.invokeLater(runnable)
}
