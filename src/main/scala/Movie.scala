package org.nlogo.extensions.vid

import java.io.File

trait MovieFactory {
  // throws InvalidFormatException when the filePath points to a file
  // whose format cannot be understood
  def open(filePath: String): Option[File]
}

class InvalidFormatException extends Exception("Invalid file format")
