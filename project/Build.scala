// Copyright (C) 2014 Miquel Sabaté Solà <mikisabate@gmail.com>
// This file is licensed under the MIT license.
// See the LICENSE file.

package snacker

import sbt._
import Keys._

object SnackerBuild extends Build {
  val sharedSettings = Project.defaultSettings ++ Seq(
    organization := "com.mssola",
    version := "0.1",

    scalaVersion := "2.10.4",
    crossScalaVersions := Seq("2.9.3", "2.10.4", "2.11.0"),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-Yresolve-term-conflict:package"
    ),

    libraryDependencies ++= Seq(
      "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
    ),

    resolvers ++= Seq(
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
    snackerAqs
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
      "storm" % "storm" % "0.9.0.1" exclude("junit", "junit")
    )
  )

  lazy val snackerAqs = module("aqs").settings(
    // Storm requires a separate process when executing sbt run.
    fork := true,

    libraryDependencies ++= Seq(
      "storm" % "storm" % "0.9.0.1" exclude("junit", "junit")
    )
  ).dependsOn(
    snackerCore
  )
}
