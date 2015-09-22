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
  new JFXPanel() // init JavaFX

  private var frame                       = Option.empty[PlayerFrame]
  var videoSource                         = Option.empty[VideoSource]
  private var currentScene: Scene with BoundsPreference = null

  private val emptySceneRectangle = new Rectangle(0, 0)

  private val emptyScene =
    new Scene(new Group(emptySceneRectangle)) with BoundsPreference {
      val preferredBound: ObservableValue[Dimension] =
        Bindings.createObjectBinding[Dimension](
          () =>
            new Dimension(emptySceneRectangle.getWidth.toInt, emptySceneRectangle.getHeight.toInt),
          emptySceneRectangle.widthProperty, emptySceneRectangle.heightProperty)
    }

  def isShowing: Boolean = frame.exists(_.isVisible)

  def hide(): Unit = {
    frame.foreach { f =>
      onSwing { () =>
        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING))
        frame = None
      }
    }
  }

  def show(scene: Scene with BoundsPreference, video: VideoSource): Unit = {
    videoSource = Some(video)
    showScene(scene)
  }

  def showEmpty(): Unit = showEmpty(640, 480)

  def showEmpty(width: Double, height: Double): Unit = {
    emptySceneRectangle.setWidth(width)
    emptySceneRectangle.setHeight(height)
    showScene(emptyScene)
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

  private val resizeListener: ChangeListener[Dimension] =
    function2ChangeListener {
      (oldDim: Dimension, newDim: Dimension) =>
        onSwing { () => withFrame(f => f.pack()) }
    }

  private def withFrame(f: PlayerFrame => Unit) = {
    for {
      currentFrame <- frame orElse Some(new PlayerFrame)
    } {
      frame = Some(currentFrame)
      f(currentFrame)
    }
  }

  private def showScene(scene: Scene with BoundsPreference): Unit =
    if (scene != currentScene) {
      withFrame { f =>
        f.setVisible(true)
        onJavaFX { () =>
          if (currentScene != null)
            currentScene.preferredBound.removeListener(resizeListener)
          scene.preferredBound.addListener(resizeListener)
          f.jfxPanel.setScene(scene)
          onSwing { () => f.pack() }
        }
      }
    }

  private def onJavaFX(runnable: Runnable) =
    Platform.runLater(runnable)

  private def onSwing(runnable: Runnable) =
    SwingUtilities.invokeLater(runnable)
}
