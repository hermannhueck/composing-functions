package app

import cats.data.Kleisli
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.instances.either._

object WCApp4 extends App with Utils {

  import Errors._

  println("\n----- " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  val wcKleisli: Kleisli[Either[Error, ?], String, List[(String, Int)]] =
    Kleisli(getUrl) andThen
      getLines andThen
      (lines => wordCount(lines).asRight)

  val wc: String => Either[Error, List[(String, Int)]] =
    wcKleisli.run // unwrapping the Kleisli returns the Function1

  wc(config.url).fold( // run the Function1 and show the Either's result
    error => println(error),
    wc => wc foreach println
  )
}
