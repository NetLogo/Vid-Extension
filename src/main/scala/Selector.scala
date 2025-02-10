package org.nlogo.extensions.vid

import javax.swing.JFileChooser

import org.nlogo.api.ReporterRunnable
import org.nlogo.app.App
import org.nlogo.awt.UserCancelException
import org.nlogo.swing.{ DropdownOptionPane, FileDialog }

trait Selector {
  def selectOneOf(choices: Seq[String]): Option[String]
  def selectFile: Option[String]
}

object NetLogoSelector extends Selector {

  val frame = App.app.frame
  def workspace = App.app.workspace

  override def selectOneOf(choices: Seq[String]): Option[String] = {
    Option(workspace.waitForResult(
      new ReporterRunnable[String] {
        override def run(): String =
          new DropdownOptionPane(frame, "Select a Camera", "Choose a camera from the list", choices).getSelectedChoice
      }))
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
