package app

/*
  In the 2nd development step I improved getUrlDef, getLinesDef and wordCountDef:

  The finally block disappeared through the invocation of Utils.using.
  The try-catch blocks were replaces by a scala.util.Try

  After improving them I moved these methods to trait Utils.
 */
object WCApp2TryAutoClose extends App with Utils {

  import Errors._

  println("\n----- " + getClass.getSimpleName.filter(_.isLetterOrDigit))

  val config = Config("https://raw.githubusercontent.com", "hermannhueck", "composing-functions", "master", "README.md")

  def wcDef(urlString: String): Either[Error, List[(String, Int)]] =
    for {
      url <- getUrlDef(urlString)
      lines <- getLinesDef(url)
      wcList <- Right(wordCountDef(lines))
    } yield wcList

  val wc = wcDef(config.url)

  val result = wc.fold(
    error => error.toString,
    wc => wc.map(_.toString).mkString("\n")
  )

  println(result)

  println("-----\n")
}
