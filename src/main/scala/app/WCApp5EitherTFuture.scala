package app

import java.net.URL

import cats.data.{EitherT, Kleisli}
import cats.syntax.either._
import cats.instances.future._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

/*
  In the previous step I used a wrapped an Either in a Kleisli.
  Here in step 5 I wrap an EitherT inside a Kleisli which in turn wraps a Future that wraps an Either

  wc returns a Kleisli[EitherT[Future, Error, ?], String, List[(String, Int)]]
  which wraps an EitherT[Future, Error, List[(String, Int)]]
  which wraps a Future[Either[Error, List[(String, Int)]]].

  To make this readable I define three helper functions getUrlET, getLinesET, wordCountET.
  All three have the same structure: They transform the input type A => EitherT[Future, Error, B].
  As they adhere to the same structure the can be wrapped and composed with Kleisli: see wcKleisli.

  wcEitherT unwraps the Kleisli and yields the EitherT inside.
  wcFuture unwraps the Future contained in the EitherT.
  wcFuture.onComplete just handles the completion of the Future.
 */
object WCApp5EitherTFuture extends App with Utils {

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

  wcFuture.onComplete (completionHandler) // show result when Future is comnplete

  Await.ready(wcFuture, 3 seconds) // wait 3 seconds in order not to terminate the app before the future is complete

  Thread.sleep(500L)
  println("-----\n")
}
