package demo

import cats.Monad
import cats.data.Reader

object Demo10bDbReader_Cats extends App {

  println("\n===== DbReader with Monad[Reader[Db, ?]]")
  println("----- The following example is a little bit more realistic. It is stolen from \"Scala with Cats\".")

  val users: Map[Int, String] = Map(
    1 -> "dade",
    2 -> "kate",
    3 -> "margo"
  )
  val passwords: Map[String, String] = Map(
    "dade" -> "zerocool",
    "kate" -> "acidburn",
    "margo" -> "secret"
  )

  case class Db(usernames: Map[Int, String], passwords: Map[String, String])
  val db = Db(users, passwords)

  type DbReader[A] = Reader[Db, A]      // ^= Kleisli[Id, Db, Boolean]

  def findUsername(userId: Int): DbReader[Option[String]] =
    Reader { db => db.usernames.get(userId) }

  def checkPassword(username: String, password: String): DbReader[Boolean] =
    Reader { db => db.passwords.get(username).contains(password) }

  def checkLogin(userId: Int, password: String): DbReader[Boolean] =
    for {
      optUsername <- findUsername(userId)
      passwordOk <- optUsername
        .map(name => checkPassword(name, password))
        .getOrElse(Monad[DbReader].pure(false))
    } yield passwordOk

  println(checkLogin(1, "zerocool")(db))
  println(checkLogin(4, "davinci")(db))

  println("-----\n")
}
