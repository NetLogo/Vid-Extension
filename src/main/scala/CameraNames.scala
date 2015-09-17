package org.nlogo.extensions.vid

import org.nlogo.api._

class CameraNames(cameras: CameraFactory) extends DefaultReporter {
  override def report(args: Array[Argument], context: Context): AnyRef =
    LogoList(cameras.cameraNames: _*)
}

