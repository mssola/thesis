
name := "basic"

version := "0.1"

resolvers ++= Seq("clojars" at "http://clojars.org/repo/")

libraryDependencies += "storm" % "storm" % "0.9.0.1" exclude("junit", "junit")

scalacOptions += "-Yresolve-term-conflict:package"

// When doing sbt run, fork a separate process.  This is apparently needed by storm.
fork := true

// set Ivy logging to be at the highest level - for debugging
ivyLoggingLevel := UpdateLogging.Full

// Aagin this may be useful for debugging
logLevel := Level.Info
