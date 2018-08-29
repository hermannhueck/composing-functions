package mycats

trait Monoid[A] {

  def empty: A
  def combine(x: A, y: A): A

  def combineAll(as: List[A]): A = // combines all functions in a List of functions
    as.foldLeft(empty)(combine)
}

object Monoid {

  object ops {

    implicit class MonoidSyntax[A: Monoid](x: A) {

      def combine(y: A): A = Monoid[A].combine(x, y)
      def |+|(y: A): A = x combine y
    }
  }

  def apply[A: Monoid]: Monoid[A] = implicitly

  // default typeclass instances in implicit scope

  implicit val stringMonoid: Monoid[String] = new Monoid[String] {
    override def empty: String = ""
    override def combine(x: String, y: String): String = x + y
  }

  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    override def empty: Int = 0
    override def combine(x: Int, y: Int): Int = x + y
  }

  implicit def listMonoid[A]: Monoid[List[A]] = new Monoid[List[A]] {
    override def empty: List[A] = List.empty[A]
    override def combine(x: List[A], y: List[A]): List[A] = x ++ y
  }

  // This one is the default Function1-Monoid in Cats
  implicit def function1Monoid[A: Monoid]: Monoid[A => A] = new Monoid[A => A] {
    override def empty: A => A = _ => Monoid[A].empty
    override def combine(f: A => A, g: A => A): A => A = a => Monoid[A].combine(f(a), g(a))
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