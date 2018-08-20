package demo

import mycats._
import Functor.ops._
import Monad.ops._
import Monoid.ops._

import scala.language.higherKinds

object ComposingFunctions extends App {

  println("\n===== Mapping Functions")

  val s2i: String => Int = _.toInt
  val plus2: Int => Int = _ + 2
  val div10By: Int => Double = 10.0 / _
  val d2s: Double => String = _.toString
  val prt: String => Unit = s => println(s"result = $s !!!")

  println("----- Function1.compose")

  val fComposed1 = prt compose d2s compose div10By compose plus2 compose s2i
  fComposed1("3")

  println("----- Function1.andThen")

  val fComposed2 = s2i andThen plus2 andThen div10By andThen d2s andThen prt
  fComposed2("3")

  println("----- Functor[Function1].map (via implicit conversion)")

  val fComposed3 = s2i map plus2 map div10By map d2s map prt // requires -Ypartial-unification
  fComposed3("3")

  println("----- Folding a List[Function1[Int, Int]]")
  println("----- Folding functions are not commutative! foldRight and foldLeft give different results.")

  val lf: List[Int => Int] = List(_*2, _+10, _+100)

  val lfFoldedRight = lf.foldRight(identity[Int] _) {(a, acc) => acc map a}
  val resLfFoldedRight = lfFoldedRight(2)
  println(resLfFoldedRight)

  val lfFoldedLeft = lf.foldLeft(identity[Int] _) {(acc, a) => acc map a}
  val resLfFoldedLeft = lfFoldedLeft(2)
  println(resLfFoldedLeft)


  println("\n===== FlatMapping Functions")

  val s2iOpt: String => Option[Int] = s => Option(s.toInt)
  val plus2Opt: Int => Option[Int] = i => Option(i + 2)
  val div10ByOpt: Int => Option[Double] = i => Option(10.0 / i)
  val d2sOpt: Double => Option[String] = d => Option(d.toString)

  val flatCompose1: String => Option[Unit] = input => for {
    i1 <- s2iOpt(input)
    i2 <- plus2Opt(i1)
    d <- div10ByOpt(i2)
    s <- d2sOpt(d)
  } yield prt(s)

  flatCompose1("3")

  val flatCompose2: String => Option[Unit] = input =>
    s2iOpt(input) flatMap { i1 =>
      plus2Opt(i1) flatMap { i2 =>
        div10ByOpt(i2) flatMap { d =>
          d2sOpt(d) map { s =>
            prt(s)
          }
        }
      }
    }

  flatCompose2("3")

  val flatCompose3: String => Option[Unit] =
    s2iOpt(_) flatMap {
      plus2Opt(_) flatMap {
        div10ByOpt(_) flatMap {
          d2sOpt(_) map {
            prt(_)
          }
        }
      }
    }

  flatCompose3("3")


  println("\n===== Kleisli Composition")

  println("----- Monad.kleisliCompose")
  def kleisli[F[_]: Monad, A, B, C](f: A => F[B], g: B => F[C]): A => F[C] = Monad.kleisliCompose(f, g)

  val kleisliCompose: String => Option[Unit] =
    kleisli(kleisli(kleisli(s2iOpt, plus2Opt), div10ByOpt), d2sOpt) map { _.map(prt(_)) }

  kleisliCompose("3")

  println("----- Kleisli#flatMap (for-comprehension)")
  val kleisli1: String => Option[Unit] = input => for {
    i1 <- Kleisli(s2iOpt).run(input)
    i2 <- Kleisli(plus2Opt).run(i1)
    d <- Kleisli(div10ByOpt).run(i2)
    s <- Kleisli(d2sOpt).run(d)
  } yield prt(s)

  kleisli1("3")

  println("----- Kleisli#flatMap")
  val kleisli2: String => Option[Unit] = input =>
    Kleisli(s2iOpt).run(input) flatMap { i1 =>
      Kleisli(plus2Opt).run(i1) flatMap { i2 =>
        Kleisli(div10ByOpt).run(i2) flatMap { d =>
          Kleisli(d2sOpt).run(d) map { s =>
            prt(s)
          }
        }
      }
    }

  kleisli2("3")

  val kleisli3: String => Option[Unit] =
    Kleisli(s2iOpt)(_) flatMap {
      Kleisli(plus2Opt)(_) flatMap {
        Kleisli(div10ByOpt)(_) flatMap {
          Kleisli(d2sOpt)(_) map {
            prt(_)
          }
        }
      }
    }

  kleisli3("3")

  println("----- Kleisli#flatMapF")
  val kleisli4: Kleisli[Option, String, Unit] =
    Kleisli(s2iOpt) flatMapF plus2Opt flatMapF div10ByOpt flatMapF d2sOpt map prt

  kleisli4.run("3")

  println("----- Kleisli#andThen")
  val kleisli5: Kleisli[Option, String, Unit] =
    Kleisli(s2iOpt) andThen plus2Opt andThen div10ByOpt andThen d2sOpt map prt

  kleisli5.run("3")

  val kleisli6: Kleisli[Option, String, Unit] =
    Kleisli(s2iOpt) andThen Kleisli(plus2Opt) andThen Kleisli(div10ByOpt) andThen Kleisli(d2sOpt) map prt

  kleisli6.run("3")

  println("----- Kleisli#compose")
  val kleisli7: Kleisli[Option, String, Unit] =
    Kleisli(d2sOpt) compose div10ByOpt compose plus2Opt compose s2iOpt map prt

  kleisli7.run("3")

  val kleisli8: Kleisli[Option, String, Unit] =
    Kleisli(d2sOpt) compose Kleisli(div10ByOpt) compose Kleisli(plus2Opt) compose Kleisli(s2iOpt) map prt

  kleisli8.run("3")


  println("\n===== type Reader[A, B] = Kleisli[Id, A, B]")

  val reader1: String => String = input => for {
    i1 <- Reader(s2i).run(input)
    i2 <- Reader(plus2).run(i1)
    d <- Reader(div10By).run(i2)
    s <- Reader(d2s).run(d)
  } yield s

  prt(reader1("3"))

  val reader2 =
    Reader(s2i) andThen plus2 andThen div10By andThen d2s

  prt(reader2("3"))

  val reader3 =
    Reader(s2i) andThen Reader(plus2) andThen Reader(div10By) andThen Reader(d2s)

  prt(reader3("3"))


  println("\n===== Composition with Monoid[A => A]")
  println("----- compose functionality depends on the Monoid[A => A] instance visible at compile time")

  // import Monoid.function1ComposeMonoid
  // import Monoid.function1AndThenMonoid
  import Monoid.function1SumsMonoid

  val f: Int => Int = _ + 1
  val g: Int => Int = _ * 2
  val h: Int => Int = _ + 100

  println(  (f combine g)(4)  )
  println(  (f |+| g)(4)  )

  println(  (f |+| g|+| h)(4)  )
  println(  Monoid[Int => Int].combineAll(List(f, g, h))(4)  )

  println("-----")
}
