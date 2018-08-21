package app

import java.net.URL

import cats.{Applicative, Monad}
import cats.data.{EitherT, Kleisli}
import cats.syntax.either._
import cats.syntax.applicative._
import cats.syntax.flatMap._
import cats.instances.either._

import scala.language.{higherKinds, postfixOps}

object WCApp6 extends App with Utils {

  import Errors._

  println("\n===== " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  // generic function definitions: with a type parameter the vals become defs

  def getUrlET[F[_] : Applicative]: String => EitherT[F, Error, URL] =
    str => EitherT(getUrl(str).pure[F])

  def getLinesET[F[_] : Applicative]: URL => EitherT[F, Error, List[String]] =
    url => EitherT(getLines(url).pure[F])

  def wordCountET[F[_] : Applicative]: List[String] => EitherT[F, Error, List[(String, Int)]] =
    lines => EitherT(wordCount(lines).asRight[Error].pure[F])

  def wcKleisli[F[_] : Monad]: Kleisli[EitherT[F, Error, ?], String, List[(String, Int)]] =
    Kleisli(getUrlET[F]) andThen
      getLinesET[F] andThen
      wordCountET[F]


  object UseIdForF {

    println("\n----- Use Id for F ...")

    import cats.Id

    val wcEitherT: String => EitherT[Id, Error, List[(String, Int)]] =
      wcKleisli[Id].run // unwrapping the Kleisli returns the EitherT

    val wcId: Id[Either[Error, List[(String, Int)]]] =
      wcEitherT(config.url).value // running and unwrapping the EitherT returns a Id

    val wc: Either[Error, List[(String, Int)]] = wcId

    wc.fold(
      error => println(error),
      wc => wc foreach println
    )
  }


  object UseFutureForF {

    println("\n----- Use Future for F ...")

    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._
    import scala.concurrent.{Await, Future}
    import scala.util.{Failure, Success}

    import cats.instances.future._

    val wcEitherT: String => EitherT[Future, Error, List[(String, Int)]] =
      wcKleisli[Future].run // unwrapping the Kleisli returns the EitherT

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


  object UseMonixTaskForF {

    println("\n----- Use monix.Task for F ... is not yet compatible with cats-1.0.0")

    import monix.execution.Scheduler.Implicits.global
    import monix.eval.Task

    import scala.concurrent.duration._
    import scala.concurrent.{Await, Future}
    import scala.util.{Failure, Success}

    import monix.cats._

    /*
        val wcEitherT: String => EitherT[Task, Error, List[(String, Int)]] =
          wcKleisli[Task].run // unwrapping the Kleisli returns the EitherT

        val wcTask: Task[Either[Error, List[(String, Int)]]] =
          wcEitherT(config.url).value // running and unwrapping the EitherT returns a Future

        wcTask.runOnComplete { // show result when Future is comnplete
          case Failure(ex) =>
            println(ex)
          case Success(result) =>
            result.fold(
              error => println(error),
              wc => wc foreach println
            )
        }

        Await.ready(wcTask.runAsync, 3 seconds) // wait 3 seconds in order not to terminate the app before the future is complete
    */
  }


  UseIdForF
  UseMonixTaskForF
  UseFutureForF
}
