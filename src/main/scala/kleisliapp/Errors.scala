package kleisliapp

import java.io.{FileNotFoundException, IOException}
import java.net.{MalformedURLException, UnknownHostException}

object Errors {

  sealed trait Error extends Product with Serializable
  final case class MalformedUrlError(msg: String) extends Error
  final case class UnknownHostError(msg: String) extends Error
  final case class FileNotFoundError(msg: String) extends Error
  final case class IOError(msg: String) extends Error
  final case class UnspecificError(msg: String) extends Error

  def toError(t: Throwable): Error = t match {
    case e: MalformedURLException => MalformedUrlError(e.toString)
    case e: UnknownHostException => UnknownHostError(e.toString)
    case e: FileNotFoundException => FileNotFoundError(e.toString)
    case e: IOException => IOError(e.toString)
    case e: Throwable => UnspecificError(e.toString)
  }
}
