package myscala

trait Function1[-T1, +R] {

  def apply(a: T1): R
  def compose[A](g: A => T1): A => R = { x => apply(g(x)) }
  def andThen[A](g: R => A): T1 => A = { x => g(apply(x)) }
  override def toString = "<function1>"
}
