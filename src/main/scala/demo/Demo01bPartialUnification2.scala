package demo

import mycats.Functor
import Functor.syntax._

/*
  code compiles only with -Ypartial-unification enabled

  See:
  https://github.com/scala/scala/pull/5102
  https://gist.github.com/djspiewak/7a81a395c461fd3a09a6941d4cd040f2
 */
object Demo01bPartialUnification2 extends App {

  println("\n===== Partial Unification")


  val right: Either[String, Int] = Right(123)
  val left: Either[String, Int] = Left("Error")

  println("\n----- Functor[Either[String, ?]].map")
  val r1 = Functor[Either[String, ?]].map(right)(_ + 1)
  println(r1)
  val l1 = Functor[Either[String, ?]].map(left)(_ + 1)
  println(l1)

  println("----- Either.map")
  val r2 = right map (_ + 1)
  println(r2)
  val l2 = left map (_ + 1)
  println(l2)


  val pair: (String, Int) = ("left", 123)

  println("\n----- Functor[Tuple2[String, ?]].map")
  val p1 = Functor[Tuple2[String, ?]].map(pair)(_ + 1)
  println(p1)

  println("----- Tuple2.map (via implicit conversion) with compile option -Ypartial-unification")
  // requires compiler flag -Ypartial-unification to allow the compiler to unify type constructors of different arities
  // otherwise compile error: value map is not a member of (String, Int)
  val p2 = pair map (_ + 1)
  println(p2)


  val function1: String => Int = s => s.toInt * 2

  println("\n----- Functor[Function1[String, ?]].map")
  val f1 = Functor[Function1[String, ?]].map(function1)(_ + 1)
  val res1 = f1("12")
  println(res1)

  println("----- Function1.map (via implicit conversion) with compile option -Ypartial-unification")
  // requires compiler flag -Ypartial-unification to allow the compiler to unify type constructors of different arities
  // otherwise compile error: value map is not a member of (String, Int)
  val f2 = function1 map (_ + 1)
  val res2 = f2("12")
  println(res2)
}
