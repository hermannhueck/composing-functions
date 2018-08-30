package kleisliapp

import java.net.URL

import kleisliapp.Errors.{Error, toError}

import scala.io.Source
import scala.util.{Failure, Success, Try}

import scala.language.reflectiveCalls

trait Utils {

  // 'using' saves us from finally blocks to close a resource
  // works with every resource that has a method 'close'
  // CL is a subclass of any type that has a 'close' method (defined with a structural type)
  def using[A, CL <: {def close(): Unit}] (closeable: CL) (f: CL => A): A =
    try {
      f(closeable)
    } finally {
      closeable.close()
    }

  // 'produceA' is a call by name param, i.e. lazy (could also be a Function0)
  def tryToEither[A](produceA: => A): Either[Error, A] = {

    import cats.syntax.either._ // import Either syntax from Cats for Either.fromTry

    Either.fromTry(
      Try { produceA }
    ).leftMap(toError)
  }


  val getUrl: String => Either[Error, URL] = urlString =>
    tryToEither { new URL(urlString) }

  val getLines: URL => Either[Error, List[String]] = url =>
    tryToEither { using(Source.fromURL(url))(src => src.getLines.toList) }

  val wordCount: List[String] => List[(String, Int)] = lines =>
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


  val stringResult: Either[Error, List[(String, Int)]] => String = // pure, without side-effect
    result => result.fold(
      error => error.toString,
      wc => wc.map(_.toString).mkString("\n")
    )

  val showResult: Either[Error, List[(String, Int)]] => Unit = // impure, with side-effect
    result => println(stringResult(result))

  val messageFrom: Try[Either[Error, List[(String, Int)]]] => String = { // pure, without side-effect
    case Failure(ex) => ex.toString
    case Success(result) => stringResult(result)
  }

  val completionHandler: Try[Either[Error, List[(String, Int)]]] => Unit = // impure, with side-effect
    tryy => println(messageFrom(tryy))
}
