package demo

import mycats.data.Reader
import mycats.Monad.syntax._

object Demo11aReader extends App {

  println("\n===== Reader Monad")

  val s2i: String => Int = _.toInt
  val plus2: Int => Int = _ + 2
  val div10By: Int => Double = 10.0 / _
  val d2s: Double => String = _.toString + " !!!"

  println("----- type Reader[A, B] = Kleisli[Id, A, B]")
  println("----- Reader#flatMap")

  val reader1: String => String = input => for {
    i1 <- Reader(s2i).run(input)
    i2 <- Reader(plus2).run(i1)
    d <- Reader(div10By).run(i2)
    s <- Reader(d2s).run(d)
  } yield s

  println(reader1("3"))

  println("----- Reader#andThen")

  val reader2 =
    Reader(s2i) andThen Reader(plus2) andThen Reader(div10By) andThen Reader(d2s)

  println(reader2("3"))

  val reader3 =
    Reader(s2i) andThen plus2 andThen div10By andThen d2s

  println(reader3("3"))

  println("-----\n")
}
