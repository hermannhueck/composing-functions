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

  val fs: Seq[Int => Int] = Seq(_*2, _+10, _+100)

  val fComposed3 = fs.foldRight(identity[Int] _) { (f, acc) => f compose acc }
  val res3 = fComposed3(1)
  println(res3) // 222

  val fComposed4 = fs.foldLeft(identity[Int] _) { (acc, f) => acc compose f }
  val res4 = fComposed4(1)
  println(res4) // 222

  val fComposed5 = fs.foldRight(identity[Int] _) { (f, acc) => f andThen acc }
  val res5 = fComposed5(1)
  println(res5) // 112

  val fComposed6 = fs.foldLeft(identity[Int] _) { (acc, f) => acc andThen f }
  val res6 = fComposed6(1)
  println(res6) // 112


  println("----- Chaining functions with Function.chain[a](fs: Seq[a => a]): a => a")

  val fChained0 = Function.chain(fs)
  println(fChained0(1)) // 112

  val fChained1 = Function.chain(Seq[Int => Int](_*2, _+10, _+100))
  println(fChained1(1)) // 112

  println("-----")
}
