package app

object WCApp2 extends App with Utils {

  import Errors._

  println("\n----- " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  def wcDef(urlString: String): Either[Error, List[(String, Int)]] =
    for {
      url <- getUrlDef(urlString)
      lines <- getLinesDef(url)
      wcList <- Right(wordCountDef(lines))
    } yield wcList

  wcDef(config.url).fold(
    error => println(error),
    wc => wc foreach println
  )
}
