package app

import cats.syntax.either._

object WCApp3 extends App with Utils {

  import Errors._

  println("\n----- " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  val wc: String => Either[Error, List[(String, Int)]] = urlString =>
    for {
      url <- getUrl(urlString)
      lines <- getLines(url)
      wcList <- wordCount(lines).asRight
    } yield wcList

  wc(config.url).fold( // run the Function1 and show the Either's result
    error => println(error),
    wc => wc foreach println
  )
}
