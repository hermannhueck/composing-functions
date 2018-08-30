package kleisliapp

import cats.syntax.either._

/*
  In the 4th step I built an analogous Function1 for each def.
  Each function impl is now a val, i.e. a Function1 literal.

  - def getUrlDef(urlString: String):  Either[Error, URL]                 became
    val getUrl:              String => Either[Error, URL]

  - def getLinesDef(url: URL):  Either[Error, List[String]]               became
    val getLines:        URL => Either[Error, List[String]]

  - def wordCountDef(lines: List[String]):  List[(String, Int)]           became
    val wordCount:          List[String] => List[(String, Int)]

  All three functions as well as 'using' and 'stringResult' have been moved to the Utils trait
  in order to be reused in subsequent steps.

  wcDef has also changed from a method to a function:
  def wcDef(urlString: String):  Either[Error, List[(String, Int)]]       became
  val wc:              String => Either[Error, List[(String, Int)]]
 */
object WCApp4Functions extends App with Utils {

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
