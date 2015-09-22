package org.nlogo.extensions.vid

import org.nlogo.api._

import javax.swing.{ JFrame, SwingUtilities }

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.{ Scene, Group }
import javafx.scene.media.{ Media, MediaPlayer, MediaView }

import util.FunctionToCallback.function2Runnable

class MovieOpenPlayer extends DefaultCommand {
  override def perform(args: Array[Argument], context: Context): Unit = {
    SwingUtilities.invokeLater { () =>
      val frame = new JFrame("NetLogo - vid extension")
      initJavaFXFrame(frame)
    }
  }

  val mediaPath = "file:///Users/rgg284/IdeaProjects/vid/testvid.mp4"

  def initJavaFXFrame(frame: JFrame): Unit = {
    val fxPanel = new JFXPanel()

    Platform.runLater { () =>
      val media   = new Media(mediaPath)
      val mediaPlayer = new MediaPlayer(media)
      val mediaView   = new MediaView(mediaPlayer)
      val group   = new Group(mediaView)
      val scene   = new Scene(group)
      fxPanel.setScene(scene)
      mediaPlayer.setOnReady(() => frame.setSize(media.getWidth, media.getHeight))
      frame.setVisible(true)
      frame.add(fxPanel)
    }
  }
}
