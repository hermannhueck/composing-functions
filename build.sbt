name := "composing-functions"
version := "0.1.0"

scalaVersion := "2.13.8"

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",        // source files are in UTF-8
  "-deprecation", // warn about use of deprecated APIs
  "-unchecked",   // warn about unchecked type parameters
  "-feature",     // warn about misused language features
  "-Xlint"        // enable handy linter warnings
  // "-Xfatal-warnings"  // turn compiler warnings into errors
)

libraryDependencies ++= Seq(
  "io.monix"       %% "monix-eval"  % "3.4.1", // imports cats and cats-effect
  "org.typelevel"  %% "cats-effect" % "2.5.5",
  "org.scalatest"  %% "scalatest"   % "3.2.13" % Test,
  "org.scalacheck" %% "scalacheck"  % "1.16.0" % Test,
  compilerPlugin("org.typelevel" % "kind-projector"      % "0.13.2" cross CrossVersion.full),
  compilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
)
