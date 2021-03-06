package kleisliapp

import cats.data.Kleisli
import cats.syntax.either._
import cats.instances.either._

/*
  In the 5th dev step I use the fact that all three functions (getUrl, getLines, wordCount)
  return an Either, exactly an Either[Error, ?] where only the right type parameter varies.

  Every function has the structure:    A => F[B]
  It transforms an A into a B within the context F[_] where F is the Either[Error, ?] in this case.

  The for-comprehension of step 3 becomes a program definition with Kleisli composition.
  getUrl is wrapped into a Kleisli and composed via Kleisli#andThen with getLines andThen wordCount.
  wcKleisli.run gives us the Kleisli that results from the composition.

  wcKleisli returns a Kleisli[Either[Error, ?], String, List[(String, Int)]]
  which wraps a Function1: String => Either[Error, List[(String, Int)]]
 */
object WCApp5Kleisli extends App with Utils {

  import Errors._

  println("\n----- " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  /*
    Kleisli wrapping a function: A => Either[Error, B]
   */
  val wcKleisli: Kleisli[Either[Error, ?], String, List[(String, Int)]] =
    Kleisli(getUrl) andThen
      getLines andThen
      (lines => wordCount(lines).asRight)

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  val wc = wcKleisli.run(config.url)

  println(stringResult(wc))

  println("-----\n")
}
