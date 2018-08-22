package app

import java.net.URL

import cats.{Applicative, Monad}
import cats.data.{EitherT, Kleisli}
import cats.syntax.either._
import cats.syntax.applicative._

import scala.language.{higherKinds, postfixOps}

/*
  Step 6 generalizes getUrlET, getLinesET, wordCountET and wcKleisli.
  These functions are now parameterized with the generic type constructor F[_]
  with replaces Future from the previous step.
  Due to the new type parameters of these functions they are now defs instead of vals.

  In getUrlET, getLinesET, wordCountET F is constrained to have an Applicative instance: F[_]: Applicative
  With Applicative[F] we are able to lift teh results of the functions getUrl, getLines, wordCount into the context F.

  wcKleisli has a Monad constraint on F:   def wcKleisli[F[_] : Monad]
  The Kleisli#andThen internally uses flatMap, hence the Monad constraint on wcKleisli.

  wcEitherT also has a Monad constraint on F:   def wcEitherT[F[_] : Monad]
  It unwraps the Kleisli and yields the EitherT contained in it.

  With this parameterization I am able to provide different implementations, each with another representation of F:

  object UseIdForF uses the Id Monad for F: This makes the Kleisli synchronous as in step 4.

  object UseFutrueForF uses the Furure Monad for F: This makes the Kleisli asynchronous as in step 5.
 */
object WCApp6EitherTF extends App with Utils {

  import Errors._

  println("\n===== " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  // generic function definitions: with a type parameter the vals become defs

  def getUrlET[F[_]: Applicative]: String => EitherT[F, Error, URL] =
    str => EitherT(getUrl(str).pure[F])

  def getLinesET[F[_]: Applicative]: URL => EitherT[F, Error, List[String]] =
    url => EitherT(getLines(url).pure[F])

  def wordCountET[F[_]: Applicative]: List[String] => EitherT[F, Error, List[(String, Int)]] =
    lines => EitherT(wordCount(lines).asRight[Error].pure[F])

  def wcKleisli[F[_]: Monad]: Kleisli[EitherT[F, Error, ?], String, List[(String, Int)]] =
    Kleisli(getUrlET[F]) andThen
      getLinesET[F] andThen
      wordCountET[F]

  def wcEitherT[F[_]: Monad]: String => EitherT[F, Error, List[(String, Int)]] =
    wcKleisli[F].run // unwrapping the Kleisli returns the EitherT

  def wcF[F[_]: Monad]: F[Either[Error, List[(String, Int)]]] =
    wcEitherT[F].apply(config.url).value // running and unwrapping the EitherT returns a Id


  object UseIdForF {

    println("\n----- Sync: Reify F with cats.Id ...")

    import cats.Id

    val wc: Either[Error, List[(String, Int)]] = wcF[Id]

    showResult(wc)
  }


  object UseFutureForF {

    println("\n----- Async: Reify F with Future ...")

    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._
    import scala.concurrent.{Await, Future}
    import cats.instances.future._

    wcF[Future].onComplete (completionHandler) // show result when Future is comnplete

    Await.ready(wcF[Future], 3 seconds) // wait 3 seconds in order not to terminate the app before the future is complete
  }


  object UseMonixTaskForF {

    println("\n----- Async: Reify F with monix.eval.Task")

    import monix.execution.Scheduler.Implicits.global
    import monix.eval.Task
    import scala.concurrent.duration._
    import scala.concurrent.Await

    wcF[Task] runOnComplete completionHandler // show result when Future is comnplete

    Await.ready(wcF[Task].runAsync, 3 seconds) // wait 3 seconds in order not to terminate the app before the future is complete
  }


  UseIdForF
  UseMonixTaskForF
  UseFutureForF

  Thread.sleep(500L)
  println("-----\n")
}
