package demo

import mycats.Monoid
import Monoid.ops._

object Demo3ComposingWithMonoid extends App {

  println("\n===== Composition with Monoid[A => A]")

  println("----- compose functionality depends on the Monoid[A => A] instance visible at compile time")

  // import Monoid.function1ComposeMonoid
  // import Monoid.function1AndThenMonoid
  import Monoid.function1Monoid

  val f: Int => Int = _ + 1
  val g: Int => Int = _ * 2
  val h: Int => Int = _ + 100

  println(  (f combine g)(4)  )
  println(  (f |+| g)(4)  )

  println(  (f |+| g|+| h)(4)  )
  println(  Monoid[Int => Int].combineAll(List(f, g, h))(4)  )

  println("-----")
}
