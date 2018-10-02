package demo

object Pipe {

  implicit class PipeForA[A](a: A) {
    def pipe[B](f: A => B): B = f(a)
    def |>[B](f: A => B): B = f(a) // F#'s |> operator
  }
}

object Demo11PipeApp extends App {

  println("\n===== Piping as in F#")

  import Pipe._
  

  val squared: Int => Int = x => x * x

  println(5.pipe(squared)) // 25
  println(5 pipe squared) // 25
  println(5 |> squared) // 25


  println("-----")

  val s2i: String => Int = _.toInt
  val plus2: Int => Int = _ + 2
  val div10By: Int => Double = 10.0 / _
  val d2s: Double => String = _.toString + " !!!"


  // Using regular Scala function invocation
  val res1 = d2s(div10By(plus2(s2i("3")))) // 2.0 !!!
  println(res1)


  // Using pipe
  val res2a = "3".pipe(s2i).pipe(plus2).pipe(div10By).pipe(d2s) // 2.0 !!!
  println(res2a)

  val res2b = "3" pipe s2i pipe plus2 pipe div10By pipe d2s // 2.0 !!!
  println(res2b)

  // Using F#'s pipe idiom
  val res2c = "3" |> s2i |> plus2 |> div10By |> d2s // 2.0 !!!
  println(res2c)


  // Using Function1#andThen
  val res3 = (s2i andThen plus2 andThen div10By andThen d2s)("3") // 2.0 !!!
  println(res3)

  // Using Function1#compose
  val res4 = (d2s compose div10By compose plus2 compose s2i)("3") // 2.0 !!!
  println(res4)

  println("-----\n")
}
