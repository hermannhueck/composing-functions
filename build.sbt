name := "composing-functions"
version := "0.0.1"

scalaVersion := "2.12.10"

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",                // source files are in UTF-8
  "-deprecation",         // warn about use of deprecated APIs
  "-unchecked",           // warn about unchecked type parameters
  "-feature",             // warn about misused language features
  "-Ypartial-unification" // (only for 2.12) allow the compiler to unify type constructors of different arities
  // "-Xlint",                 // enable handy linter warnings
  // "-Xfatal-warnings",        // turn compiler warnings into errors
)

libraryDependencies ++= Seq(
  "io.monix" %% "monix-eval" % "3.1.0" // imports cats and cats-effect
)

// https://github.com/typelevel/kind-projector
addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full)
// https://github.com/oleg-py/better-monadic-for
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
