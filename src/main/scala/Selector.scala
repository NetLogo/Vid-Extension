package org.nlogo.extensions.vid

trait Selector {
  def selectOneOf(choices: Seq[String]): Option[String]
  def selectFile: Option[String]
}

object NetLogoSelector extends Selector {
  import javax.swing.JFileChooser

  import org.nlogo.api.{ I18N, ReporterRunnable }
  import org.nlogo.app.App
  import org.nlogo.awt.UserCancelException
  import org.nlogo.awt.EventQueue.invokeLater
  import org.nlogo.swing.{ FileDialog, OptionDialog }
  import org.nlogo.window.GUIWorkspace

  import scala.concurrent.SyncVar

  import util.FunctionToCallback.function2Runnable

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
              FileDialog.show(frame, "Select a movie to open", JFileChooser.FILES_ONLY)
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
