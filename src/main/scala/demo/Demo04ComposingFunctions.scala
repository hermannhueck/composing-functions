package demo

object Demo04ComposingFunctions extends App {

  println("\n===== Composing Functions")

  val s2i: String => Int = _.toInt
  val plus2: Int => Int = _ + 2
  val div10By: Int => Double = 10.0 / _
  val d2s: Double => String = _.toString + " !!!"


  println("----- Function1#apply")

  val fComposed1: String => String = str => d2s(div10By(plus2(s2i(str))))
  val res1 = fComposed1("3") // 2.0 !!!
  println(res1)

  println("----- Function1#compose")

  val fComposed2: String => String = d2s compose div10By compose plus2 compose s2i
  val res2 = fComposed2("3") // 2.0 !!!
  println(res2)

  println("----- Function1#andThen")

  val fComposed3: String => String = s2i andThen plus2 andThen div10By andThen d2s
  val res3 = fComposed3("3") // 2.0 !!!
  println(res3)


  println("----- Folding a List[Function1[Int, Int]]")

  val fs: Seq[Int => Int] = Seq(_*2, _+10, _+100)

  val fFolded1 = fs.foldRight(identity[Int] _) { (f, acc) => f compose acc }
  println(fFolded1(1)) // 222

  val fFolded2 = fs.foldLeft(identity[Int] _) { (acc, f) => acc compose f }
  println(fFolded2(1)) // 222

  val fFolded3 = fs.foldRight(identity[Int] _) { (f, acc) => f andThen acc }
  println(fFolded3(1)) // 112

  val fFolded4 = fs.foldLeft(identity[Int] _) { (acc, f) => acc andThen f }
  println(fFolded4(1)) // 112


  println("----- Chaining functions with Function.chain[a](fs: Seq[a => a]): a => a")

  val fChained01 = Function.chain(fs)
  println(fChained01(1)) // 112

  val fChained2 = Function.chain(Seq[Int => Int](_*2, _+10, _+100))
  println(fChained2(1)) // 112

  println("-----")
}
