package demo

import cats.Monoid
import cats.syntax.monoid._

object Demo05MonoidalComposition_Cats extends App {

  object MonoidInstances {

    // These instance must be imported into local scope.
    // They are not in implicit scope.

    implicit val intMultiplicationMonoid: Monoid[Int] = new Monoid[Int] {
      override def empty: Int = 1
      override def combine(x: Int, y: Int): Int = x * y
    }

    implicit def function1ComposeMonoid[A]: Monoid[A => A] = new Monoid[A => A] {
      override def empty: A => A = identity
      override def combine(f: A => A, g: A => A): A => A = f compose g
    }

    implicit def function1AndThenMonoid[A]: Monoid[A => A] = new Monoid[A => A] {
      override def empty: A => A = identity
      override def combine(f: A => A, g: A => A): A => A = f andThen g
    }
  }

  println("\n===== Composition with Monoid[A => A]")

  println("----- compose functionality depends on the Monoid[A => A] instance visible at compile time")

  import cats.instances.int._
  import cats.instances.function._

  // import MonoidInstances.function1ComposeMonoid
  // import MonoidInstances.function1AndThenMonoid

  val f: Int => Int = _ + 1
  val g: Int => Int = _ * 2
  val h: Int => Int = _ + 100

  println(  (f combine g)(4)  )
  println(  (f |+| g)(4)  )

  println(  (f |+| g|+| h)(4)  )
  println(  Monoid[Int => Int].combineAll(List(f, g, h))(4)  )

  println("-----")
}
