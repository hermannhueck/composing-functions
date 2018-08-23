package app

import cats.syntax.either._

/*
  In the 3rd step I built an analogous Function1 for each def.
  Each function impl is now a val, i.e. a Function1 literal.

  - def getUrlDef(urlString: String): Either[Error, URL]         becomes
    val getUrl: String => Either[Error, URL]

  - def getLinesDef(url: URL): Either[Error, List[String]]       becomes
    val getLines: URL => Either[Error, List[String]]

  - def wordCountDef(lines: List[String]): List[(String, Int)]   becomes
    val wordCount: List[String] => List[(String, Int)]

  The finally block disappeared through the invocation of Utils.using.
  The try-catch blocks were replaces by a scala.util.Try

  getUrl and getLines are improved again using the Either syntax of Cats.

  All three functions have been moved to the Utils trait.

  Method wcDef has also changed from a method to a function wc.
 */
object WCApp3Functions extends App with Utils {

  import Errors._

  println("\n----- " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  val wc: String => Either[Error, List[(String, Int)]] = urlString =>
    for {
      url <- getUrl(urlString)
      lines <- getLines(url)
      wcList <- wordCount(lines).asRight
    } yield wcList

  val result = stringResult(wc(config.url)) // run the Function1 'wc' returns the Either result
  println(result)

  println("-----\n")
}
