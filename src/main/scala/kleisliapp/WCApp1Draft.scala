package kleisliapp

import java.io.{FileNotFoundException, IOException}
import java.net.{MalformedURLException, URL, UnknownHostException}

import scala.io.{BufferedSource, Source}

/*
  This is the 1st draft of the WC (word count) program.

  It provides three methods (getUrlDef, getLinesDef, wordCountDef).
  wcDef combines these methods in a for-comprehension yielding
  either an Error or a list of pairs: List[(String, Int)].
  Each pair contains a word of the input text and the frequency of occurrences of that word.

  Traditional, java-like exception handling is done in catch blocks. :-(
  The URL Source is closed in the finally block. :-(
 */
object WCApp1Draft extends App {

  import Errors._

  println("\n----- " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  def getUrlDef(urlString: String): Either[Error, URL] = try {
    Right(new URL(urlString))
  } catch {
    case e: MalformedURLException => Left(MalformedUrlError(e.toString))
    case e: Throwable => Left(UnspecificError(e.toString))
  }

  def getLinesDef(url: URL): Either[Error, List[String]] = {
    var src: BufferedSource = null
    try {
      src = Source.fromURL(url)
      Right(src.getLines.toList)
    } catch {
      case e: UnknownHostException => Left(UnknownHostError(e.toString))
      case e: FileNotFoundException => Left(FileNotFoundError(e.toString))
      case e: IOException => Left(IOError(e.toString))
      case e: Throwable => Left(UnspecificError(e.toString))
    } finally {
      if (src != null)
        src.close()
    }
  }

  def wordCountDef(lines: List[String]): List[(String, Int)] =
    lines.mkString
      .toLowerCase
      .split("\\W+")
      .toList
      .map(_.filter(c => c.isLetter))
      .filter(_.length > 3)
      .groupBy(s => s)
      .mapValues(_.length)
      .toList
      .filter(_._2 > 2) // return only words with occurences > 2
      .sortWith(_._2 > _._2)

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  def wcDef(urlString: String): Either[Error, List[(String, Int)]] =
    for {
      url <- getUrlDef(urlString)
      lines <- getLinesDef(url)
      wcList <- Right(wordCountDef(lines))
    } yield wcList

  val wc = wcDef(config.url)

  val result = wc.fold(
    error => error.toString,
    wc => wc.map(_.toString).mkString("\n")
  )

  println(result)

  println("-----\n")
}
