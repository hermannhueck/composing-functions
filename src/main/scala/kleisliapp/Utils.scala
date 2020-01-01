package kleisliapp

import java.net.URL

import kleisliapp.Errors.{toError, Error}

import scala.io.Source
import scala.util.{Failure, Success, Try}

import scala.util.Using

trait Utils {

  // 'using' saves us from finally blocks to close a resource
  // works with every resource that has a method 'close'
  // CL is a subclass of any type that has a 'close' method (defined with a structural type)
  // scala-2.13 provides scala.util.Using
  import scala.language.reflectiveCalls

  def using[A, CL <: { def close(): Unit }](closeable: CL)(f: CL => A): A =
    try {
      f(closeable)
    } finally {
      closeable.close()
    }

  // 'thunk' is a call by name param, i.e. lazy (could also be a Function0)
  def execToEither[A](thunk: => A): Either[Error, A] = {
    import cats.syntax.either._
    Try(thunk)
      .toEither
      .leftMap(toError)
  }

  val getUrl: String => Either[Error, URL] = urlString =>
    execToEither {
      println("-->> getUrl")
      new URL(urlString)
    }

  val getLines: URL => Either[Error, List[String]] = url =>
    execToEither {
      println("-->> getLines")
      Using.resource(Source.fromURL(url))(src => src.getLines.toList)
    }

  val wordCount: List[String] => List[(String, Int)] = { lines =>
    println("-->> wordCount")
    lines
      .mkString
      .toLowerCase // (ju.Locale.getDefault())
      .split("\\W+")
      .toList
      .map(_.filter(c => c.isLetter))
      .filter(_.length > 3)
      .groupBy(s => s)
      .view
      .mapValues(_.length)
      .toList
      .filter(_._2 > 3) // return only words with occurences > 3
      .sortWith(_._2 > _._2)
  }

  val stringResult: Either[Error, List[(String, Int)]] => String =
    result =>
      result.fold(
        error => error.toString,
        wc => wc.map(_.toString).mkString("\n")
      )

  val showResult: Either[Error, List[(String, Int)]] => Unit =
    result => println(stringResult(result))

  val messageFrom: Try[Either[Error, List[(String, Int)]]] => String = {
    case Failure(ex)     => ex.toString
    case Success(result) => stringResult(result)
  }

  val completionHandler: Try[Either[Error, List[(String, Int)]]] => Unit =
    tryy => println(messageFrom(tryy))
}
