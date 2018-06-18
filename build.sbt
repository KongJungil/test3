import Dependencies._

lazy val commonSettings = Seq(
  organization := "com.sktelecom",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.11",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
  test in assembly := {},
  target in assembly := file("lib"),
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case "reference.conf" => MergeStrategy.concat
    case x => MergeStrategy.first
  },
  libraryDependencies ++= ScalaTest,
  resolvers ++= Seq(
    Resolver.sonatypeRepo("public"),
    Resolver.typesafeRepo("releases"),
    Resolver.typesafeIvyRepo("releases"),
    Resolver.sbtPluginRepo("releases"),
    Resolver.bintrayRepo("hseeberger", "maven")
  )
)

lazy val root = (project in file("."))
  .dependsOn(common, preprocessor)
  .settings(commonSettings: _*)
  .settings(
    name := "oksusu-recommendation"
  )

lazy val common = (project in file("common"))
  .settings(commonSettings: _*)
  .settings(
    name := "oksusu-common",
    libraryDependencies ++= SparkDeps ++ Json4sDeps ++ Seq(
      "joda-time" % "joda-time" % "2.9.9",
      // logging library
      "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
    )
  )

lazy val preprocessor = (project in file("preprocessor"))
  .settings(commonSettings: _*)
  .dependsOn(common)
  .settings(
    name := "oksusu-preprocessor",
    libraryDependencies ++= AkkaHttpDeps ++ HBaseDeps ++ SparkDeps ++ Seq(
      // the following dependency is unmanaged.
      // "com.oracle.jdbc" % "ojdbc6" % "12.1.0.2",
      "org.xerial" % "sqlite-jdbc" % "3.20.0" % "test",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",

      // korean text tokenizer
      "org.bitbucket.eunjeon" %% "seunjeon" % "1.3.0"
        exclude("org.slf4j", "slf4j-jdk14"),

      // time-related packages
      "com.github.nscala-time" %% "nscala-time" % "2.16.0",

      // fastutil (efficient collection library)
      "it.unimi.dsi" % "fastutil" % "8.1.0",

      // config and cmd opts library
      "com.iheart" %% "ficus" % "1.4.3",
      "com.github.scopt" %% "scopt" % "3.7.0",

      // JSON serialization
      "io.circe" %% "circe-core" % "0.8.0",
      "io.circe" %% "circe-generic" % "0.8.0",
      "io.circe" %% "circe-parser" % "0.8.0",
      "io.circe" %% "circe-generic-extras" % "0.8.0",

      // Sugar for serialization and deserialization in akka-http with circe
      "de.heikoseeberger" %% "akka-http-circe" % "1.18.1"
    )
  )

lazy val deprecatedLogCollector = (project in file("log-collector-deprecated"))
  .dependsOn(common, preprocessor)
  .settings(
    commonSettings,
    libraryDependencies ++= HBaseDeps ++ AkkaHttpDeps ++ SparkDeps ++ Seq(
      // time-related packages
      "com.github.nscala-time" %% "nscala-time" % "2.16.0",

      // JSON serialization
      "io.circe" %% "circe-core" % "0.8.0",
      "io.circe" %% "circe-generic" % "0.8.0",
      "io.circe" %% "circe-parser" % "0.8.0",
      "io.circe" %% "circe-generic-extras" % "0.8.0",

      // Sugar for serialization and deserialization in akka-http with circe
      "de.heikoseeberger" %% "akka-http-circe" % "1.18.1"
    ),
    addCompilerPlugin(
      "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
    )
  )
  .enablePlugins(SbtTwirl)
