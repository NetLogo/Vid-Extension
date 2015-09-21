package org.nlogo.extensions.vid

import org.nlogo.api._

import javafx.embed.swing.JFXPanel

class ShowPlayer(player: Player, vidExtension: VideoSourceContainer) extends DefaultCommand {
  override def getSyntax =
    Syntax.commandSyntax(Array[Int](Syntax.NumberType | Syntax.RepeatableType))

  override def perform(args: Array[Argument], context: Context): Unit = {

    lazy val width  = args(0).getDoubleValue
    lazy val height = args(1).getDoubleValue

    if (args.length != 0 && args.length != 2)
      throw new ExtensionException("show-player expected 2 arguments")
    else if (args.length == 2 && (width <= 0 || height <= 0))
      throw new ExtensionException("vid: invalid dimensions")

    if (vidExtension.videoSource.nonEmpty && args.length == 2)
      vidExtension.videoSource.foreach(_.showInPlayer(player, width, height))
    else if (vidExtension.videoSource.nonEmpty)
      vidExtension.videoSource.foreach(_.showInPlayer(player))
    else
      player.showEmpty()
  }
}
