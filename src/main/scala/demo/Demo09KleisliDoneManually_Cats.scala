package demo

import cats.Monad
import cats.instances.option._

object Demo09KleisliDoneManually_Cats extends App {

  println("\n===== Kleisli composition done manually")

  // Functions: A => F[B], where F is Option in this case
  val s2iOpt: String => Option[Int]     = s => Option(s.toInt)
  val plus2Opt: Int => Option[Int]      = i => Option(i + 2)
  val div10ByOpt: Int => Option[Double] = i => Option(10.0 / i)
  val d2sOpt: Double => Option[String]  = d => Option(d.toString + " !!!")

  val fMapped: String => Option[Option[Option[Option[String]]]] = str =>
    s2iOpt(str) map { i1 =>
      plus2Opt(i1) map { i2 =>
        div10ByOpt(i2) map { d =>
          d2sOpt(d)
        }
      }
    }
  val res0: Option[Option[Option[Option[String]]]] = fMapped("3")

  println("----- FlatMapping on the Option context")

  val flatMappedOnOpt1: String => Option[String] = input =>
    s2iOpt(input) flatMap { i1 =>
      plus2Opt(i1) flatMap { i2 =>
        div10ByOpt(i2) flatMap { d =>
          d2sOpt(d)
        }
      }
    }

  val res1: Option[String] = flatMappedOnOpt1("3") // Some(2.0 !!!)
  res1 foreach println

  val flatMappedOnOpt1a: String => Option[String] =
    s2iOpt(_) flatMap {
      plus2Opt(_) flatMap {
        div10ByOpt(_) flatMap {
          d2sOpt(_)
        }
      }
    }

  val res1a: Option[String] = flatMappedOnOpt1a("3") // Some(2.0 !!!)
  res1a foreach println

  val flatMappedOnOpt2: String => Option[String] = input =>
    for {
      i1 <- s2iOpt(input)
      i2 <- plus2Opt(i1)
      d  <- div10ByOpt(i2)
      s  <- d2sOpt(d)
    } yield s

  val res2: Option[String] = flatMappedOnOpt2("3") // Some(2.0 !!!)
  res2 foreach println

  println("----- Kleisli Composition defined on Function1")

  def kleisli[F[_]: Monad, A, B, C](f: A => F[B], g: B => F[C]): A => F[C] =
    a => Monad[F].flatMap(f(a))(g)

  val kleisliComposed1: String => Option[String] =
    kleisli(kleisli(kleisli(s2iOpt, plus2Opt), div10ByOpt), d2sOpt)

  val resKleisli1 = kleisliComposed1("3") // Some(2.0 !!!)
  resKleisli1 foreach println

  implicit class Function1WithKleisli[F[_]: Monad, A, B](f: A => F[B]) {
    def kleisli[C](g: B => F[C]): A => F[C]  = a => Monad[F].flatMap(f(a))(g)
    def andThenF[C](g: B => F[C]): A => F[C] = f kleisli g
    def >=>[C](g: B => F[C]): A => F[C]      = f kleisli g // Haskell's left-to-right Kleisli operator (fish operator)
  }

  val kleisliComposed2: String => Option[String] =
    s2iOpt kleisli plus2Opt kleisli div10ByOpt kleisli d2sOpt

  kleisliComposed2("3") foreach println

  val kleisliComposed3: String => Option[String] =
    s2iOpt andThenF plus2Opt andThenF div10ByOpt andThenF d2sOpt

  kleisliComposed3("3") foreach println

  val kleisliComposed4: String => Option[String] =
    s2iOpt >=> plus2Opt >=> div10ByOpt >=> d2sOpt // using Haskell's fish operator

  kleisliComposed4("3") foreach println

  println("-----\n")
}
