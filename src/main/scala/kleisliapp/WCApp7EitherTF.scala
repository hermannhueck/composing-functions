package kleisliapp

import java.net.URL

import cats.{Applicative, Monad}
import cats.data.{EitherT, Kleisli}
import cats.syntax.either._
import cats.syntax.applicative._

import scala.language.{higherKinds, postfixOps}

/*
  Step 7 generalizes getUrlET, getLinesET, wordCountET and wcKleisli.
  These functions are now parameterized with the generic effect F[_]
  with replaces the Future effect from the previous step.
  Due to the new type parameter of these functions they are now defs instead of vals.
  (A val cannot have a type parameter in Scala.)

  In getUrlET, getLinesET, wordCountET F[_] is constrained to have an Applicative instance: F[_]: Applicative
  With Applicative[F] we are able to lift the results of the functions getUrl, getLines, wordCount into the context F (with pure).

  wcKleisli has a Monad constraint on F:   def wcKleisli[F[_] : Monad]
  The Kleisli#andThen internally uses F.flatMap, hence the Monad constraint on wcKleisli.

  wcEitherT also has a Monad constraint on F[_]:   def wcEitherT[F[_] : Monad]
  It unwraps the Kleisli and yields the EitherT contained in it.

  With this parameterization I am able to provide different reifications of F[_]:

  object UseIdForF reifies F[_] with the Id Monad: This makes the Kleisli synchronous as in step 5.

  object UseFutrueForF reifies F[_] with the Future Monad: This makes the Kleisli asynchronous as in step 6.

  object UseMonixTaskForF reifies F[_] with the monix.eval.Task Monad which is an alternative way of asynchronism.
 */
object WCApp7EitherTF extends App with Utils {

  import Errors._

  println("\n===== " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  // ----- 3 helper functions with the same structure: A => EitherT[F, Error, B]
  // ----- Without these wcKleisli would look quite messy.
  // ----- With the type parameter F[_] the vals become defs.

  def getUrlET[F[_]: Applicative]: String => EitherT[F, Error, URL] =
    str => EitherT(getUrl(str).pure[F])

  def getLinesET[F[_]: Applicative]: URL => EitherT[F, Error, List[String]] =
    url => EitherT(getLines(url).pure[F])

  def wordCountET[F[_]: Applicative]: List[String] => EitherT[F, Error, List[(String, Int)]] =
    lines => EitherT(wordCount(lines).asRight[Error].pure[F])

  // ----- Kleisli wrapping a function: A => EitherT[F, Error, B] where f is constrained to: F[_]: Monad
  //
  def wcKleisli[F[_]: Monad]: Kleisli[EitherT[F, Error, ?], String, List[(String, Int)]] =
    Kleisli(getUrlET[F]) andThen
      getLinesET[F] andThen
      wordCountET[F]

  // unwrapping the Kleisli returns the EitherT
  def wcEitherT[F[_]: Monad]: String => EitherT[F, Error, List[(String, Int)]] =
    wcKleisli[F].run

  // running and unwrapping the EitherT returns the F[_] effect
  def wcF[F[_]: Monad]: F[Either[Error, List[(String, Int)]]] =
    wcEitherT[F].apply(config.url).value


  object UseIdForF {

    println("\n----- Sync: Reify F with cats.Id ...")

    import cats.Id

    val wc: Either[Error, List[(String, Int)]] = wcF[Id] // reify F[_] with Id

    showResult(wc)
  }


  object UseFutureForF {

    println("\n----- Async: Reify F with Future ...")

    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._
    import scala.concurrent.{Await, Future}
    import cats.instances.future._

    val wcFuture: Future[Either[Error, List[(String, Int)]]] = wcF[Future] // reify F[_] with Future

    wcFuture.onComplete (completionHandler) // show result when Future is comnplete

    Await.ready(wcF[Future], 3 seconds) // wait max 3 seconds for the Future to complete
  }


  object UseMonixTaskForF {

    println("\n----- Async: Reify F with monix.eval.Task")

    import monix.execution.Scheduler.Implicits.global
    import monix.eval.Task
    import scala.concurrent.duration._
    import scala.concurrent.Await

    val wcTask: Task[Either[Error, List[(String, Int)]]] = wcF[Task] // reify F[_] with Task

    wcTask runOnComplete completionHandler // show result when Task is comnplete

    Await.ready(wcF[Task].runAsync, 3 seconds) // wait max 3 seconds for the Task to complete
  }


  UseIdForF
  UseMonixTaskForF
  UseFutureForF

  Thread.sleep(500L)
  println("-----\n")
}
