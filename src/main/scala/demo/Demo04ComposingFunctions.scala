package demo

object Demo04ComposingFunctions extends App {

  println("\n===== Composing Functions")

  val s2i: String => Int = _.toInt
  val plus2: Int => Int = _ + 2
  val div10By: Int => Double = 10.0 / _
  val d2s: Double => String = _.toString + " !!!"


  println("----- Function1#compose")

  val fComposed1: String => String = d2s compose div10By compose plus2 compose s2i
  val res1 = fComposed1("3") // 2.0 !!!
  println(res1)


  println("----- Function1#andThen")

  val fComposed2: String => String = s2i andThen plus2 andThen div10By andThen d2s
  val res2 = fComposed2("3") // 2.0 !!!
  println(res2)


  println("----- Folding a List[Function1[Int, Int]]")
  println("----- Folding functions is not commutative! foldRight and foldLeft give different results.")

  val lf: List[Int => Int] = List(_*2, _+10, _+100)

  val lfFoldedRight = lf.foldRight(identity[Int] _) {(f, acc) => f compose acc}
  val resLfFoldedRight = lfFoldedRight(1)
  println(resLfFoldedRight) // 222

  val lfFoldedLeft = lf.foldLeft(identity[Int] _) {(acc, f) => acc andThen f}
  val resLfFoldedLeft = lfFoldedLeft(1)
  println(resLfFoldedLeft) // 112

  println("-----")
}
