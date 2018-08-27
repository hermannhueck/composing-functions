package demo

import mycats.Kleisli

object Demo6KleisliCaseClass extends App { self =>

  println("\n===== case class Kleisli")

  val s2iOpt: String => Option[Int] = s => Option(s.toInt)
  val plus2Opt: Int => Option[Int] = i => Option(i + 2)
  val div10ByOpt: Int => Option[Double] = i => Option(10.0 / i)
  val d2sOpt: Double => Option[String] = d => Option(d.toString + " !!!")

  println("----- Kleisli#flatMap")
  val kleisli1: String => Option[String] = input =>
    Kleisli(s2iOpt).run(input) flatMap { i1 =>
      Kleisli(plus2Opt).run(i1) flatMap { i2 =>
        Kleisli(div10ByOpt).run(i2) flatMap { d =>
          Kleisli(d2sOpt).run(d)
        }
      }
    }

  kleisli1("3") foreach println

  val kleisli1a: String => Option[String] =
    Kleisli(s2iOpt)(_) flatMap {
      Kleisli(plus2Opt)(_) flatMap {
        Kleisli(div10ByOpt)(_) flatMap {
          Kleisli(d2sOpt)(_)
        }
      }
    }

  kleisli1a("3") foreach println

  println("----- Kleisli#flatMap (for-comprehension)")
  val kleisli2: String => Option[String] = input => for {
    i1 <- Kleisli(s2iOpt).run(input)
    i2 <- Kleisli(plus2Opt).run(i1)
    d <- Kleisli(div10ByOpt).run(i2)
    s <- Kleisli(d2sOpt).run(d)
  } yield s

  kleisli2("3") foreach println

  println("----- Kleisli#flatMapF")
  val kleisli4: Kleisli[Option, String, String] =
    Kleisli(s2iOpt) flatMapF plus2Opt flatMapF div10ByOpt flatMapF d2sOpt

  kleisli4.run("3") foreach println

  println("----- Kleisli#andThen")
  val kleisli5: Kleisli[Option, String, String] =
    Kleisli(s2iOpt) andThen plus2Opt andThen div10ByOpt andThen d2sOpt

  kleisli5.run("3") foreach println

  val kleisli6: Kleisli[Option, String, String] =
    Kleisli(s2iOpt) andThen Kleisli(plus2Opt) andThen Kleisli(div10ByOpt) andThen Kleisli(d2sOpt)

  kleisli6.run("3")

  println("----- Kleisli#compose")
  val kleisli7: Kleisli[Option, String, String] =
    Kleisli(d2sOpt) compose div10ByOpt compose plus2Opt compose s2iOpt

  kleisli7.run("3") foreach println

  val kleisli8: Kleisli[Option, String, String] =
    Kleisli(d2sOpt) compose Kleisli(div10ByOpt) compose Kleisli(plus2Opt) compose Kleisli(s2iOpt)

  kleisli8.run("3") foreach println


  println("-----\n")
}
