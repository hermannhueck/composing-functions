package demo

/*
  code compiles only with -Ypartial-unification enabled

  See:
  https://github.com/scala/scala/pull/5102
  https://gist.github.com/djspiewak/7a81a395c461fd3a09a6941d4cd040f2
 */
object Demo01bPartialUnification extends App {

  def foo[F[_], A](fa: F[A]): String =
    fa.toString

  foo { x: Int =>
    x * 2
  }
}
