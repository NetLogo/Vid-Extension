package org.nlogo.extensions.vid


// these dummies allow opening the vid extension in headless mode
// while large parts of the vid extension are unusable when run headlessly
// sometimes we need to run headlessly to do things like resave or autoconvert models
// RG 6/21/16
object Headless {
  object Movie extends MovieFactory {
    def open(filePath: String): Option[org.nlogo.extensions.vid.VideoSource] = None
    def openRemote(uri: String): Option[org.nlogo.extensions.vid.VideoSource] = None
  }

  object Camera extends CameraFactory {
    override var cameraNames: Seq[String] = Seq()
    override var defaultCameraName: Option[String] = None
    def open(cameraName: String): Option[org.nlogo.extensions.vid.VideoSource] = None
  }

  object HeadlessPlayer extends Player {
    def boundedSize: Option[(Double, Double)] = None
    def emptyNode(bounds: Option[(Double, Double)]): org.nlogo.extensions.vid.BoundedNode = null
    def hide(): Unit = {}
    def isShowing: Boolean = false
    def present(boundedNode: org.nlogo.extensions.vid.BoundedNode): Unit = {}
    def show(): Unit = {}
  }

  object HeadlessSelector extends Selector {
    def selectFile: Option[String] = None
    def selectOneOf(choices: Seq[String]): Option[String] = None
  }
}
