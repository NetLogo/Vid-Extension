package org.nlogo.extensions.vid

import org.nlogo.api._

class HidePlayer(player: Player) extends DefaultCommand {
  override def perform(args: Array[Argument], context: Context): Unit = {
    player.hide()
  }
}
