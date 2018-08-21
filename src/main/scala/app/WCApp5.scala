package app

import java.net.URL

import cats.data.{EitherT, Kleisli}
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.instances.either._
import cats.instances.future._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import scala.language.postfixOps

object WCApp5 extends App with Utils {

  import Errors._

  println("\n----- " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  val getUrlET: String => EitherT[Future, Error, URL] =
    str => EitherT(Future(getUrl(str)))

  val getLinesET: URL => EitherT[Future, Error, List[String]] =
    url => EitherT(Future(getLines(url)))

  val wordCountET: List[String] => EitherT[Future, Error, List[(String, Int)]] =
    lines => EitherT(Future(wordCount(lines).asRight))

  val wcKleisli: Kleisli[EitherT[Future, Error, ?], String, List[(String, Int)]] =
    Kleisli(getUrlET) andThen
      getLinesET andThen
      wordCountET

  val wcEitherT: String => EitherT[Future, Error, List[(String, Int)]] =
    wcKleisli.run // unwrapping the Kleisli returns the EitherT

  val wcFuture: Future[Either[Error, List[(String, Int)]]] =
    wcEitherT(config.url).value // running and unwrapping the EitherT returns a Future

  wcFuture.onComplete { // show result when Future is comnplete
    case Failure(ex) =>
      println(ex)
    case Success(result) =>
      result.fold(
        error => println(error),
        wc => wc foreach println
      )
  }

  Await.ready(wcFuture, 3 seconds) // wait 3 seconds in order not to terminate the app before the future is complete
}
