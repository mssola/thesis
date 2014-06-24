/*
Copyright (C) 2014 Miquel Sabaté Solà <mikisabate@gmail.com>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package snacker

import sbt._
import Keys._

object SnackerBuild extends Build {
  val sharedSettings = Project.defaultSettings ++ Seq(
    organization := "com.mssola",
    version := "0.1",

    scalaVersion := "2.10.4",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-Yresolve-term-conflict:package"
    ),

    /* TODO: Remove json from the shared settings */
    libraryDependencies ++= Seq(
      "com.newzly"  %% "phantom-dsl" % "0.8.0",
      "net.liftweb" %% "lift-json" % "3.0-M0",
      "storm" % "storm" % "0.9.0.1" exclude("junit", "junit"),
      "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
    ),

    resolvers ++= Seq(
      "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
      "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
      "Clojars Repository" at "http://clojars.org/repo"
    ),

    parallelExecution in Test := true,

    // Set Ivy logging to be at the highest level - for debugging
    ivyLoggingLevel := UpdateLogging.Full,

    // Again this may be useful for debugging
    logLevel := Level.Info
  )

  // The project that contains all the sub-projects.
  lazy val snacker = Project(
    id = "snacker",
    base = file("."),
    settings = sharedSettings
  ).settings(
    test := { }
  ).aggregate(
    snackerCore,
    snackerBenchmark,
    snackerAqs,
    snackerBsp
  ).dependsOn(
    snackerCore,
    snackerBenchmark,
    snackerAqs,
    snackerBsp
  )

  // Helper function that creates a sub-module with the proper format.
  def module(name: String) = {
    val id = "snacker-%s".format(name)
    Project(id = id, base = file(id), settings = sharedSettings ++ Seq(
      Keys.name := id)
    )
  }

  // Configuring each sub-module.

  lazy val snackerCore = module("core").settings(
    // Storm requires a separate process when executing sbt run.
    fork := true,

    libraryDependencies ++= Seq(
      "org.scalaj" %% "scalaj-http" % "0.3.15"
    )
  )

  lazy val snackerAqs = module("aqs").settings(
    // Storm requires a separate process when executing sbt run.
    fork := true,

    libraryDependencies ++= Seq(
      "net.liftweb" %% "lift-json" % "3.0-M0"
    )
  ).dependsOn(
    snackerCore
  )

  lazy val snackerBsp = module("bsp").settings(
    // Storm requires a separate process when executing sbt run.
    fork := true,

    libraryDependencies ++= Seq(
      "net.liftweb" %% "lift-json" % "3.0-M0"
    )
  ).dependsOn(
    snackerCore
  )

  lazy val snackerBenchmark = module("benchmark").settings(
    // Storm requires a separate process when executing sbt run.
    fork := true,

    libraryDependencies ++= Seq(
      "net.liftweb" %% "lift-json" % "3.0-M0"
    )
  ).dependsOn(
    snackerCore
  )

}
