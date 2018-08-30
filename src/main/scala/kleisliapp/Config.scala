package kleisliapp

final case class Config(baseUrl: String,
                        user: String,
                        repo: String,
                        branch: String,
                        path: String) {

  val url: String = List(baseUrl, user, repo, branch, path).mkString("/")
}
