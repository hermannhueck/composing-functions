package kleisliapp

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
  Here in step 6 the Kleisli wraps an EitherT which in turn wraps a Future that wraps an Either

  'wcKleisli' returns a Kleisli[EitherT[Future, Error, ?], String, List[(String, Int)]]
  which wraps an EitherT[Future, Error, List[(String, Int)]]
  which wraps a Future[Either[Error, List[(String, Int)]]].

  To make this readable I define three helper functions getUrlET, getLinesET, wordCountET.
  All three have the same type structure: A => EitherT[Future, Error, B].
  As they adhere to the same structure they can be wrapped and composed with Kleisli (see wcKleisli).

  wcKleisli returns the Kleisli wrapping an EitherT
  wcEitherT unwraps the Kleisli and yields the EitherT inside.
  wcFuture unwraps the Future contained in the EitherT.
  wcFuture.onComplete just handles the completion of the Future and prints the Either result contained in it.
 */
object WCApp6EitherTFuture extends App with Utils {

  import Errors._

  println("\n----- " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  // ----- 3 helper functions with the same structure: A => EitherT[Future, Error, B]
  // ----- Without these wcKleisli would look quite messy.

  val getUrlET: String => EitherT[Future, Error, URL] =
    str => EitherT(Future(getUrl(str)))

  val getLinesET: URL => EitherT[Future, Error, List[String]] =
    url => EitherT(Future(getLines(url)))

  val wordCountET: List[String] => EitherT[Future, Error, List[(String, Int)]] =
    lines => EitherT(Future(wordCount(lines).asRight))

  // ----- Kleisli wrapping a function: A => EitherT[Future, Error, B]
  //
  val wcKleisli: Kleisli[EitherT[Future, Error, ?], String, List[(String, Int)]] =
    Kleisli(getUrlET) andThen
      getLinesET andThen
      wordCountET

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  // running the Kleisli returns the EitherT
  val wcEitherT: EitherT[Future, Error, List[(String, Int)]] =
    wcKleisli.run(config.url)

  // unwrapping the EitherT returns the Future
  val wcFuture: Future[Either[Error, List[(String, Int)]]] =
    wcEitherT.value

  wcFuture onComplete completionHandler // show result when Future is complete

  Await.ready(wcFuture, 3 seconds) // wait 3 seconds in order not to terminate the app before the future is complete

  Thread.sleep(500L)
  println("-----\n")
}
