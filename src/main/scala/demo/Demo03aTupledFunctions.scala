package demo

object Demo03aTupledFunctions extends App {

  println("\n----- Tupling and Untupling")

  val sum3Ints: (Int, Int, Int) => Int = _ + _ + _
  // sum3Ints: (Int, Int, Int) => Int = $$Lambda$6510/1947502277@3c418454
  println(sum3Ints(1,2,3)) // 6

  val sumTupled: ((Int, Int, Int)) => Int = sum3Ints.tupled
  // sumTupled: ((Int, Int, Int)) => Int = scala.Function3$$Lambda$1018/1492801385@4f8c268b
  println(sumTupled((1,2,3))) // 6

  val sumTupled2: Function1[(Int, Int, Int), Int] = sum3Ints.tupled
  // sumTupled2: ((Int, Int, Int)) => Int = scala.Function3$$Lambda$1018/1492801385@5fff4c64
  println(sumTupled2((1,2,3))) // 6

  val sumUnTupled: (Int, Int, Int) => Int = Function.untupled(sumTupled)
  // sumUnTupled: (Int, Int, Int) => Int = scala.Function$$$Lambda$3583/1573807728@781c2988
  println(sumUnTupled(1,2,3)) // 6

  val sumUnTupled2: (Int, Int, Int) => Int = Function.untupled(sumTupled2)
  // sumUnTupled2: (Int, Int, Int) => Int = scala.Function$$$Lambda$3583/1573807728@45b6b590
  println(sumUnTupled2(1,2,3)) // 6

  println("-----\n")
}
