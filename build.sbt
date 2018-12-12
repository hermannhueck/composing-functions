name := "composing-functions"
version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",     // source files are in UTF-8
  "-deprecation",           // warn about use of deprecated APIs
  "-unchecked",             // warn about unchecked type parameters
  "-feature",               // warn about misused language features
  "-Ypartial-unification",  // allow the compiler to unify type constructors of different arities
  //"-Xlint",                 // enable handy linter warnings
  //"-Xfatal-warnings",        // turn compiler warnings into errors
)

libraryDependencies ++= Seq(
  //"org.typelevel" %% "cats-core" % "1.4.0",
  "io.monix" %% "monix-eval" % "3.0.0-RC2", // imports cats and cats-effect
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")
