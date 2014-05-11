
import sbt._
import Keys._

object SnackerBuild extends Build {
  lazy val snacker = Project(id = "snacker",
    base = file(".")) aggregate(snackerCore, snackerAqs)

  lazy val snackerCore = Project(id = "snacker-core", base = file("snacker-core"))

  lazy val snackerAqs = Project(id = "snacker-aqs", base = file("snacker-aqs")) dependsOn(snackerCore)
}
