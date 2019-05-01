scalaVersion := "2.12.6"

version := "1.0"

lazy val commonSettings = Seq(
  scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint"),
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "org.mockito" % "mockito-core" % "2.13.0" % "test",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "net.debasishg" %% "redisclient" % "3.9",
  ),
)

lazy val domain = (project in file("domain"))
  .settings(
    commonSettings,
    name := "queue_domain",
  )

lazy val adapter = (project in file("adapter"))
  .settings(
    commonSettings,
    name := "queue_adapter",
  )
  .dependsOn(domain)

lazy val provider = (project in file("provider"))
  .settings(
    commonSettings,
    name := "provider",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.1.8",
      "com.typesafe.akka" %% "akka-stream" % "2.5.19",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8",
    ),
  )
  .dependsOn(domain)
  .dependsOn(adapter)

lazy val consumer = (project in file("consumer"))
  .settings(
    commonSettings,
    name := "consumer",
  )
  .dependsOn(domain)
  .dependsOn(adapter)

lazy val root = (project in file("."))
  .aggregate(domain, provider, consumer)
