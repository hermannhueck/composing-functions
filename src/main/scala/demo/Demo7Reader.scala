package demo

import mycats.Monad.ops._
import mycats.{Id, Kleisli, Reader}

object Demo7Reader extends App { self =>

  println("\n===== Reader Monad")

  val s2i: String => Int = _.toInt
  val plus2: Int => Int = _ + 2
  val div10By: Int => Double = 10.0 / _
  val d2s: Double => String = _.toString + " !!!"

  println("----- type Reader[A, B] = Kleisli[Id, A, B]")
  println("----- Reader#flatMap")

  val reader1: String => String = input => for {
    i1 <- Reader(s2i).run(input)
    i2 <- Reader(plus2).run(i1)
    d <- Reader(div10By).run(i2)
    s <- Reader(d2s).run(d)
  } yield s

  println(reader1("3"))

  println("----- Reader#andThen")

  val reader2 =
    Reader(s2i) andThen Reader(plus2) andThen Reader(div10By) andThen Reader(d2s)

  println(reader2("3"))

  val reader3 =
    Reader(s2i) andThen plus2 andThen div10By andThen d2s

  println(reader3("3"))


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
        .getOrElse(Kleisli.pure[Id, Db, Boolean](false))
    } yield passwordOk

  println(checkLogin(1, "zerocool")(db))
  println(checkLogin(4, "davinci")(db))

  println("-----\n")
}
