package demo

import mycats.Functor.syntax._
import mycats.Monad.syntax._

object Demo08bDbReader extends App {

  println("\n===== DbReader with Monad[Function1[Db, ?]]")
  println("----- The following example is a little bit more realistic. It is stolen from \"Scala with Cats\".")

  val users: Map[Int, String] = Map(
    1 -> "dade",
    2 -> "kate",
    3 -> "margo"
  )

  val passwords: Map[String, String] = Map(
    "dade"  -> "zerocool",
    "kate"  -> "acidburn",
    "margo" -> "secret"
  )

  case class Db(usernames: Map[Int, String], passwords: Map[String, String])
  val db = Db(users, passwords)

  type DbReader[A] = Db => A // ^= Function1[Db, A] ^= DB => A

  def findUsername(userId: Int): DbReader[Option[String]] =
    db => db.usernames.get(userId)

  def checkPassword(optUsername: Option[String], password: String): DbReader[Boolean] = {
    def checkPw(db: Db, username: String): Boolean =
      db.passwords.get(username).contains(password)
    db => optUsername.exists(name => checkPw(db, name))
  }

  def checkLogin(userId: Int, password: String): DbReader[Boolean] =
    for {
      optUsername <- findUsername(userId)
      passwordOk  <- checkPassword(optUsername, password)
    } yield passwordOk

  println(checkLogin(1, "zerocool")(db))
  println(checkLogin(4, "davinci")(db))

  println("-----\n")
}
