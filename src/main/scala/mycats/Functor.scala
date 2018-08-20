package mycats

import scala.concurrent.Future
import scala.language.higherKinds

// typeclass functor
trait Functor[F[_]] {

  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {

  object ops {

    implicit class FunctorSyntax[F[_]: Functor, A](ctx: F[A]) {
      def map[B](f: A => B): F[B] = Functor[F].map(ctx)(f)
    }
  }

  def apply[F[_]: Functor]: Functor[F] = implicitly

  // default typeclass instances in implicit scope

  implicit def listFunctor: Functor[List] = new Functor[List] {
    override def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
  }

  implicit def optionFunctor: Functor[Option] = new Functor[Option] {
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
  }

  implicit def futureFunctor: Functor[Future] = new Functor[Future] {
    import scala.concurrent.ExecutionContext.Implicits.global
    override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)
  }

  implicit def idFunctor: Functor[Id] = new Functor[Id] {
    override def map[A, B](fa: Id[A])(f: A => B): Id[B] = f(fa)
  }

  implicit def eitherFunctor[L]: Functor[Either[L, ?]] = new Functor[Either[L, ?]] {
    override def map[R1, R2](fa: Either[L, R1])(f: R1 => R2): Either[L, R2] = fa map f
  }

  implicit def tuple2Functor[L]: Functor[Tuple2[L, ?]] = new Functor[Tuple2[L, ?]] {
    override def map[R1, R2](fa: Tuple2[L, R1])(f: R1 => R2): Tuple2[L, R2] = fa match {
      case(x, y) => (x, f(y))
    }
  }

  implicit def function1Functor[P]: Functor[Function1[P, ?]] = new Functor[Function1[P, ?]] {
    override def map[R1, R2](f: Function1[P, R1])(g: R1 => R2): Function1[P, R2] = f andThen g
  }
}
