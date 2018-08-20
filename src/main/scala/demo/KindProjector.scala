package demo

import mycats.Functor

object KindProjector {

  // structural type compiles without kind-projector
  implicit def eitherFunctor1[L]: Functor[({type f[x] = Either[L, x]})#f] = new Functor[({type f[x] = Either[L, x]})#f] {
    override def map[R1, R2](fa: Either[L, R1])(f: R1 => R2): Either[L, R2] = fa map f
  }

  // Lambda: Lambda[x => Either[L, x]]: compiles only with kind-projector compiler plugin (see build.sbt
  implicit def eitherFunctor2[L]: Functor[Lambda[x => Either[L, x]]] = new Functor[Lambda[x => Either[L, x]]] {
    override def map[R1, R2](fa: Either[L, R1])(f: R1 => R2): Either[L, R2] = fa map f
  }

  // λ: λ[x => Either[L, x]]: compiles only with kind-projector compiler plugin (see build.sbt
  implicit def eitherFunctor3[L]: Functor[λ[x => Either[L, x]]] = new Functor[λ[x => Either[L, x]]] {
    override def map[R1, R2](fa: Either[L, R1])(f: R1 => R2): Either[L, R2] = fa map f
  }

  // ?: Either[L, ?] compiles only with kind-projector compiler plugin (see build.sbt
  implicit def eitherFunctor4[L]: Functor[Either[L, ?]] = new Functor[Either[L, ?]] {
    override def map[R1, R2](fa: Either[L, R1])(f: R1 => R2): Either[L, R2] = fa map f
  }
}
