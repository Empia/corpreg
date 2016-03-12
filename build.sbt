import scalariform.formatter.preferences._

name := "play-silhouette-seed"

version := "3.0.0"

scalaVersion := "2.11.7"

resolvers := ("Atlassian Releases" at "https://maven.atlassian.com/public/") +: resolvers.value

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "central" at "http://repo1.maven.org/maven2/"

resolvers += "Sonatype" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "geotoolkit" at "http://maven.geotoolkit.org"

resolvers += "thirdparty-releases" at "https://repository.jboss.org/nexus/content/repositories/thirdparty-releases"


resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"


unmanagedBase <<= baseDirectory { base => base / "lib" }

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "3.0.0",
  "org.webjars" %% "webjars-play" % "2.4.0",




"com.typesafe.play" %% "play-mailer" % "3.0.1",



  "net.codingwell" %% "scala-guice" % "4.0.0",
  "com.typesafe.play" %% "play-slick" % "1.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.0",
  "com.h2database" % "h2" % "1.4.187",
  "net.ceedubs" %% "ficus" % "1.1.2",

  "com.adrianhurt" %% "play-bootstrap3" % "0.4.4-P24",
  "com.mohiva" %% "play-silhouette-testkit" % "3.0.0" % "test",
  specs2 % Test,
  cache,
  filters
)
//libraryDependencies += "com.itextpdf" % "itext-xtra" % "5.5.8"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41")
libraryDependencies += "info.folone" %% "poi-scala" % "0.15"
libraryDependencies += "com.norbitltd" % "spoiwo" % "1.0.6"
libraryDependencies += "javax.media" % "jai-core" % "1.1.3" % "compile"

libraryDependencies += "com.sun.media" % "jai-codec" % "1.1.3" % "compile"

libraryDependencies += "com.twelvemonkeys.imageio" % "imageio-core" % "3.2.1" % "compile"

libraryDependencies += "com.lowagie" % "itext" % "2.1.7" % "compile"

libraryDependencies += "org.geotoolkit" % "geotk-coverageio" % "3.17" % "compile"

libraryDependencies += "com.github.jai-imageio" % "jai-imageio-core" % "1.3.0" % "compile"

libraryDependencies += "com.twelvemonkeys.imageio" % "imageio-tiff" % "3.2.1" % "compile"


libraryDependencies += "com.github.dragon66" % "icafe" % "1.1-SNAPSHOT" % "compile"


//libraryDependencies += "com.sandinh" %% "scala-soap" % "1.5.0"


lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)

//********************************************************
// Scalariform settings
//********************************************************

defaultScalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(FormatXml, false)
  .setPreference(DoubleIndentClassDeclaration, false)
  .setPreference(PreserveDanglingCloseParenthesis, true)
