package demo

import mycats.Functor

object Demo1aKindProjector {

  // Code compiles without kind-projector.
  // It uses a type alias within a structural type.
  implicit def eitherFunctor1[L]: Functor[({type f[x] = Either[L, x]})#f] = new Functor[({type f[x] = Either[L, x]})#f] {
    override def map[A, B](fa: Either[L, A])(f: A => B): Either[L, B] = fa map f
  }

  // Lambda: Lambda[x => Either[L, x]]: compiles only with kind-projector compiler plugin (see build.sbt)
  implicit def eitherFunctor2[L]: Functor[Lambda[x => Either[L, x]]] = new Functor[Lambda[x => Either[L, x]]] {
    override def map[A, B](fa: Either[L, A])(f: A => B): Either[L, B] = fa map f
  }

  // λ: λ[x => Either[L, x]]: compiles only with kind-projector compiler plugin (see build.sbt)
  implicit def eitherFunctor3[L]: Functor[λ[x => Either[L, x]]] = new Functor[λ[x => Either[L, x]]] {
    override def map[A, B](fa: Either[L, A])(f: A => B): Either[L, B] = fa map f
  }

  // ?: Either[L, ?] compiles only with kind-projector compiler plugin (see build.sbt)
  implicit def eitherFunctor4[L]: Functor[Either[L, ?]] = new Functor[Either[L, ?]] {
    override def map[A, B](fa: Either[L, A])(f: A => B): Either[L, B] = fa map f
  }
}
