package org.nlogo.extensions.vid

import org.scalatest.{ BeforeAndAfter, FunSuite }

import org.bridj.Platform

class CameraTest extends FunSuite with BeforeAndAfter {

  def hasCameras = Camera.cameraNames.nonEmpty

  if (hasCameras) {
    test("default camera name is the first available camera name") {
      assert(Camera.cameraNames.head == Camera.defaultCameraName.get)
    }

    test("trying to open a camera that doesn't exist should return None") {
      assert(Camera.open("notreal") == None)
    }

    test("trying to open a camera that exists should return a VideoSource") {
      val camera = Camera.open(Camera.defaultCameraName.get)
      assert(camera.isDefined)
    }
  }
}

