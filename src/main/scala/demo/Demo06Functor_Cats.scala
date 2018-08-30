package demo

import cats.instances.function._
import cats.syntax.functor._

object Demo06Functor_Cats extends App {

  println("\n===== Mapping Functions")

  println("----- Functor[Function1]#map (via implicit conversion)")

  val s2i: String => Int = _.toInt
  val plus2: Int => Int = _ + 2
  val div10By: Int => Double = 10.0 / _
  val d2s: Double => String = _.toString + " !!!"

  val fMapped = s2i map plus2 map div10By map d2s // requires -Ypartial-unification
  val res0 = fMapped("3")
  println(res0) // 2.0 !!!

  println("-----\n")
}
