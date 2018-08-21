package app

import java.io.{FileNotFoundException, IOException}
import java.net.{MalformedURLException, URL, UnknownHostException}

import scala.io.{BufferedSource, Source}

object WCApp1 extends App {

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
      .filter(_._2 > 1)
      .sortWith(_._2 > _._2)

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  def wcDef(urlString: String): Either[Error, List[(String, Int)]] =
    for {
      url <- getUrlDef(urlString)
      lines <- getLinesDef(url)
      wcList <- Right(wordCountDef(lines))
    } yield wcList

  wcDef(config.url).fold(
    error => println(error),
    wc => wc foreach println
  )
}
