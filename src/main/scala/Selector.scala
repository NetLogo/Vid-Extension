package org.nlogo.extensions.vid

import javax.swing.JFileChooser

import org.nlogo.api.ReporterRunnable
import org.nlogo.app.App
import org.nlogo.awt.UserCancelException
import org.nlogo.core.I18N
import org.nlogo.swing.{ FileDialog, OptionDialog }

trait Selector {
  def selectOneOf(choices: Seq[String]): Option[String]
  def selectFile: Option[String]
}

object NetLogoSelector extends Selector {

  val frame = App.app.frame
  def workspace = App.app.workspace

  override def selectOneOf(choices: Seq[String]): Option[String] = {
    val selectedCam = workspace.waitForResult(
      new ReporterRunnable[Object] {
        override def run(): Object = {
          new OptionDialog(frame, "Select a Camera", "Choose a camera from the list", Array(choices: _*), I18N.gui.fn)
            .showOptionDialog
        }
      })

    selectedCam match {
      case i: java.lang.Integer => Some(choices(i.intValue))
      case _ => None
    }
  }

  override def selectFile: Option[String] = {
    val selectedPath = workspace.waitForResult(
      new ReporterRunnable[Option[String]] {
        override def run(): Option[String] =
          try {
            val path =
              FileDialog.showFiles(frame, "Select a movie to open", JFileChooser.FILES_ONLY)
            Some(path)
          } catch {
            case e: UserCancelException => None
            case e: Exception =>
              println(e)
              e.printStackTrace()
              None
          }
      }
    )

    selectedPath
  }
}
