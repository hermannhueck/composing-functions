package app

import java.net.URL

import app.Errors.{Error, toError}

import scala.io.Source
import scala.util.{Failure, Success, Try}

import scala.language.reflectiveCalls

trait Utils {

  // this saves us from finally blocks to close a resourse
  // works with every resource that has a method 'close'
  def using[A, CL <: {def close(): Unit}] (closeable: CL) (f: CL => A): A =
    try {
      f(closeable)
    } finally {
      closeable.close()
    }


  // getUrl as a method (def)
  def getUrlDef(urlString: String): Either[Error, URL] =
    Try {
      new URL(urlString)
    } match {
      case Success(url) => Right(url)
      case Failure(ex) => Left(toError(ex))
    }

  // getUrl as a Function1 (val)
  val getUrl: String => Either[Error, URL] = urlString => {
    import cats.syntax.either._ // import Either syntax from Cats
    Either.fromTry(Try(
      new URL(urlString)
    )).leftMap(toError)
  }


  // getLines as a method (def)
  def getLinesDef(url: URL): Either[Error, List[String]] =
    Try {
      using(Source.fromURL(url))(src => src.getLines.toList)
    } match {
      case Success(lines) => Right(lines)
      case Failure(ex) => Left(toError(ex))
    }

  // getLines as a Function1 (val)
  val getLines: URL => Either[Error, List[String]] = url => {
    import cats.syntax.either._ // import Either syntax from Cats
    Either.fromTry(Try(
      using(Source.fromURL(url))(src => src.getLines.toList)
    )).leftMap(toError)
  }


  // wordCount as a method (def)
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

  // wordCount as a Function1 (val)
  val wordCount: List[String] => List[(String, Int)] = lines =>
    wordCountDef(lines)
}
