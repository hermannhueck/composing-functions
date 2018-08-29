package demo

import mycats.{Functor, Monad}
import Functor.ops._
import Monad.ops._

object Demo07Monad extends App { self =>

  def getInput: String = {

    val utils = new app.Utils {}
    import utils.{getUrl, getLines}

    // val url = "https://raw.githubusercontent.com/hermannhueck/composing-functions/master/README.md"
    val url = "file:./README.md"
    getUrl(url).flatMap(getLines).map(_.mkString("\n")).toOption.get
  }

  println("\n===== FlatMapping Functions")

  println("----- Monad[Function1]#flatMap (via implicit conversion)")

  val countLines: String => Int = text => text.split("\n").length
  val countWords: String => Int = text => text.split("\\W+").length
  val countChars: String => Int = text => text.length

  val computeStatistics1: String => (Int, Int, Int) = // define a pure program; this does nothing.
    countLines flatMap { nLines =>
      countWords flatMap { nWords =>
        countChars map { nChars =>
          (nLines, nWords, nChars)
        }
      }
    }

  val stat1: (Int, Int, Int) = computeStatistics1(getInput) // execute the program (impure as it reads the URL)
  println(s"lines: ${stat1._1}, words: ${stat1._2}, chars: ${stat1._3}") // print the result (impure, as it writes to the console)
  // prints something like: lines: 39, words: 288, chars: 1747

  val computeStatistics2: String => (Int, Int, Int) = // define a pure program; this does nothing.
    for {
      nLines <- countLines // uses Function1#flatMap
      nWords <- countWords
      nChars <- countChars
    } yield (nLines, nWords, nChars)

  val stat2: (Int, Int, Int) = computeStatistics2(getInput) // execute the program (impure as it reads the URL)
  println(s"lines: ${stat2._1}, words: ${stat2._2}, chars: ${stat2._3}") // print the result (impure, as it writes to the console)
  // prints something like: lines: 39, words: 288, chars: 1747


  println("----- Monad[Function1] is the Reader Monad")
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

  type DbReader[A] = Db => A    // ^= Function1[Db, A] ^= DB => A

  def findUsername(userId: Int): DbReader[Option[String]] =
    db => db.usernames.get(userId)

  def checkPassword(username: String, password: String): DbReader[Boolean] =
    db => db.passwords.get(username).contains(password)

  def checkLogin(userId: Int, password: String): DbReader[Boolean] =
    for {
      optUsername <- findUsername(userId)
      passwordOk <- optUsername
        .map(name => checkPassword(name, password))
        .getOrElse((_:Db) => false)
    } yield passwordOk

  println(checkLogin(1, "zerocool")(db))
  println(checkLogin(4, "davinci")(db))

  println("-----\n")
}
