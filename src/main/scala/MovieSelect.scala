package org.nlogo.extensions.vid

import org.nlogo.api._

class MovieSelect(videoSourceContainer: VideoSourceContainer, movies: MovieFactory, selector: Selector) extends DefaultCommand {
  override def getSyntax = Syntax.commandSyntax(Array[Int]())

  override def perform(args: Array[Argument], context: Context): Unit = {
    selector.selectFile.foreach { path =>
      try {
        videoSourceContainer.videoSource = movies.open(path)
      } catch {
        case e: InvalidFormatException =>
          throw new ExtensionException("vid: format not supported")
      }
    }
  }
}
