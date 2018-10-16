package demo

object Demo05ImplicitConversion extends App {

  println("\n----- Implicit view/conversion for Int")

  // Implicit view/conversion: Int => EnrichedInt
  implicit class EnrichedInt(i: Int) {
    def double: Int = 2 * i
    def triple: Int = 3 * i
    def square: Int = i * i
    def cube: Int = i * i * i
  }

  println(s"5.double = ${5.double}")
  println(s"5.triple = ${5.triple}")
  println(s"5.square = ${5.square}")
  println(s"5.cube = ${5.cube}")
  println(s"5.double.square = ${5.double.square}")


  println("\n----- Implicit view/conversion for List[A]")

  implicit class PimpedList[A](xs: List[A]) {
    // Haskell provides a function zipWith for lists, Scala doesn't.
    def zipWith[B, C](ys: List[B])(f: (A, B) => C): List[C] =
      xs zip ys map { case (x, y) => f(x, y) }
  }

  val l1 = List(1, 2, 3)
  val l2 = List(10, 20, 30)

  val result = l1.zipWith(l2)(_ + _)
  println(result) // --> List(11, 22, 33)

  println("-----\n")
}
