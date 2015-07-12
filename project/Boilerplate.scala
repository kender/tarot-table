import sbt._, Keys._

object Boilerplate extends AutoPlugin {
  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.11.7",
    organization := "enkode.me")

  object Modules {
    def akka(name: String, version: String = "2.3.12") = "com.typesafe.akka" %% s"akka-$name" % version

    def slf4j(name: String) = "org.slf4j" % s"slf4j-$name" % "1.7.10"

    lazy val slf4j_api = slf4j("api")
    lazy val logback = "ch.qos.logback" % "logback-classic" % "1.1.2"

    lazy val logging =
      slf4j_api :: logback :: Nil
  }
}