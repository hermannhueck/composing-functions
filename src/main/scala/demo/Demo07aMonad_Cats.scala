package demo

import cats.instances.function._
import cats.syntax.functor._
import cats.syntax.flatMap._

object Demo07aMonad_Cats extends App {

  def getInput: String = {

    val utils = new kleisliapp.Utils {}
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

  println("-----\n")
}
