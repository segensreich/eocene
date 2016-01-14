name := """eocene"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += Resolver.sonatypeRepo("snapshots")
  
libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "mysql" % "mysql-connector-java" % "5.1.27",
  "ws.securesocial" % "securesocial_2.11" % "3.0-M3",
  "org.scalatestplus" % "play_2.11" % "1.2.0"% "test",
  "org.scalatest" % "scalatest_2.11" % "3.0.0-M1"% "test"
)

