package demo

object Demo03CurriedFunctions extends App {

  println("\n----- Currying and Uncurrying")

  val sum3Ints: (Int, Int, Int) => Int = _ + _ + _
  // sum3Ints: (Int, Int, Int) => Int = $$Lambda$6510/1947502277@3c418454

  val sum3Ints2: Function3[Int, Int, Int, Int] = _ + _ + _
  // sum3Ints: (Int, Int, Int) => Int = $$Lambda$6510/1947502277@3c418454

  val sumCurried: Int => Int => Int => Int = sum3Ints.curried
  // sumCurried: Int => (Int => (Int => Int)) = scala.Function3$$Lambda$4346/99143056@43357c0e

  val sumCurried2: Function1[Int, Function1[Int, Function1[Int, Int]]] = sumCurried
  // sumCurried2: Int => (Int => (Int => Int)) = scala.Function3$$Lambda$4346/99143056@7567ab8

  val sumUncurried: (Int, Int, Int) => Int = Function.uncurried(sumCurried)
  // sumUncurried: (Int, Int, Int) => Int = scala.Function$$$Lambda$6605/301079867@1589d895

  println("\n----- Currying and Partial Application")

  val applied1st = sumCurried(1)
  // applied1st: Int => (Int => Int) = scala.Function3$$Lambda$4348/1531035406@5a231dc1
  println(applied1st)

  val applied2nd = applied1st(2)
  // applied2nd: Int => Int = scala.Function3$$Lambda$4349/402963549@117e96fb
  println(applied2nd)

  val applied3rd = applied2nd(3)
  // applied3rd: Int = 6
  println(applied3rd)

  val appliedAllAtOnce = sumCurried(1)(2)(3)
  // appliedAllAtOnce: Int = 6
  println(appliedAllAtOnce)

  println("\n-----\n")
}
