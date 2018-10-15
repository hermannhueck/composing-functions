package demo

import cats.Monad
import cats.syntax.functor._
import cats.syntax.flatMap._
import cats.syntax.monad._
import cats.instances.function._
import cats.instances.either._

object Demo08cFlattenCurriedFunctions_Cats extends App {

  println("\n----- Flattening curried functions")

  // Flattening nested Option (has 1 type parameter)
  val oooi: Option[Option[Option[Int]]] = Some(Some(Some(1)))
  val oi: Option[Int] = oooi.flatten.flatten
  println(oi) // Some(1)


  // Flattening nested Either (has 2 type parameters)
  val eeei: Either[String, Either[String, Either[String, Int]]] = Right(Right(Right(1)))
  val ei: Either[String, Int] = eeei.flatten.flatten
  println(ei) // Right(1)


  // Flattening nested Function1 (has 2 type parameters)
  val sum3Ints: (Int, Int, Int) => Int = _ + _ + _
  // sum3Ints: (Int, Int, Int) => Int = $$Lambda$6510/1947502277@3c418454

  val sumCurried: Function1[Int, Function1[Int, Function1[Int, Int]]] = sum3Ints.curried
  // sumCurried: Int => (Int => (Int => Int)) = scala.Function3$$Lambda$4346/99143056@7567ab8

  val fFlattened: Int => Int = sumCurried.flatten.flatten
  println(fFlattened(5)) // => 15

  // Flattening with flatMap
  val fFlatMapped: Int => Int = sumCurried.flatMap(identity).flatMap(identity)
  println(fFlatMapped(5)) // => 15

  // Flattening with a for-comprehension
  val fBuiltWithFor: Int => Int = for {
    f1 <- sumCurried
    f2 <- f1
    int <- f2
  } yield int
  println(fBuiltWithFor(5)) // => 15

  val oiBuiltWithFor: Option[Int] = for {
    ooi <- oooi
    oi <- ooi
    int <- oi
  } yield int
  println(oiBuiltWithFor) // => Some(1)

  println("-----\n")
}
