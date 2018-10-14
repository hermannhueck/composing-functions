package kleisliapp

import java.net.URL

import cats.syntax.either._

import scala.io.Source
import scala.language.reflectiveCalls
import scala.util.Try

/*
  In the 3rd development step I further improved getUrlDef and getLinesDef
  using Either syntax from Cats: Either.fromTry
 */
object WCApp3EitherSyntax extends App {

  import Errors._

  println("\n----- " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  // this saves us from finally blocks to close a resource
  // works with every resource that has a method 'close'
  def using[A, CL <: {def close(): Unit}] (closeable: CL) (f: CL => A): A =
    try {
      f(closeable)
    } finally {
      closeable.close()
    }

  def getUrlDef(urlString: String): Either[Error, URL] =
    Either.fromTry(Try{
      new URL(urlString)
    }).leftMap(toError)

  def getLinesDef(url: URL): Either[Error, List[String]] =
    Either.fromTry(Try{
      using(Source.fromURL(url))(src => src.getLines.toList)
    }).leftMap(toError)

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

  def stringResult(result: Either[Error, List[(String, Int)]]): String =
    result.fold(
      error => error.toString,
      wc => wc.map(_.toString).mkString("\n")
    )

  println(stringResult(wc))

  println("-----\n")
}
