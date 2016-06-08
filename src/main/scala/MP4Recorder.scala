package org.nlogo.extensions.vid

import java.io.{ File, FileNotFoundException }
import java.nio.file.{ Files, Path, StandardCopyOption },
  StandardCopyOption.REPLACE_EXISTING
import java.awt.image.BufferedImage

import org.jcodec.common.model.{ ColorSpace, Picture }
import org.jcodec.api.SequenceEncoder
import org.jcodec.scale.AWTUtil

class MP4Recorder extends Recorder {
  private var activeRecording = Option.empty[SequenceEncoder]
  private var recordingPath = Option.empty[Path]

  def isRecording: Boolean =
    activeRecording.isDefined

  def start(): Unit = {
    if (activeRecording.isDefined)
      throw Recorder.AlreadyStarted
    val path = Files.createTempFile("vid", "mp4")
    recordingPath   = Some(path)
    activeRecording = Some(new SequenceEncoder(path.toFile))
  }

  def save(dest: Path): Unit = {
    if (! activeRecording.isDefined)
      throw Recorder.NotRecording
    if (! Files.exists(dest.toAbsolutePath.getParent))
      throw new FileNotFoundException("no such directory: " + dest.toAbsolutePath.toString)
    activeRecording.foreach(_.finish())
    recordingPath.foreach(src => Files.copy(src, dest, REPLACE_EXISTING))
    activeRecording = None
    recordingPath   = None
  }

  def reset(): Unit = {
    recordingPath   = None
    activeRecording = None
  }

  def recordFrame(image: BufferedImage): Unit = {
    if (activeRecording.isDefined)
      activeRecording.foreach { recording =>
        recording.encodeNativeFrame(AWTUtil.fromBufferedImage(image))
      }
    else
      throw new RuntimeException("No recording has been started!")
  }
}
