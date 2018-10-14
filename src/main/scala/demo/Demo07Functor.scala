package demo

import mycats.Functor
import mycats.Functor.syntax._

object Demo07Functor extends App {

  println("\n===== Mapping Functions")

  val f: Int => Int = _ + 3
  val g: Int => Int = _ * 2
  val h0 = Functor[Function1[Int, ?]].map(f)(g)
  val res0 = h0(2)  // 10
  println(res0)

  println("----- Functor[Function1]#map (via implicit conversion)")

  val h1 = f map g
  val res1 = h1(2)  // 10
  println(res1)

  println("----- Pipeline of functions with map")

  val s2i: String => Int = _.toInt
  val plus2: Int => Int = _ + 2
  val div10By: Int => Double = 10.0 / _
  val d2s: Double => String = _.toString + " !!!"

  val fMapped = s2i map plus2 map div10By map d2s // requires -Ypartial-unification
  val res2 = fMapped("3")
  println(res2) // 2.0 !!!

  println("-----\n")
}
