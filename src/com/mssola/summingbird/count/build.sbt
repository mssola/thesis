//import com.typesafe.startscript.StartScriptPlugin

//seq(StartScriptPlugin.startScriptForClassesSettings: _*)

name := "hello"

version := "1.0"


//resolvers += "twitter-repo" at "http://maven.twttr.com"

//resolvers ++= Seq("clojars" at "http://clojars.org/repo/", "clojure-releases" at "http://build.clojure.org/releases")

libraryDependencies ++= Seq("com.twitter" %% "summingbird-example" % "0.3.3",
                            "com.twitter" %% "summingbird-storm" % "0.3.3")
//libraryDependencies += "storm" % "storm" % "0.8.1" % "provided" exclude("junit", "junit")
//libraryDependencies += "com.twitter" % "summingbird-core" % "2.9.3"
//libraryDependencies ++= Seq("com.twitter" %% "summingbird-core" % "2.9.3")


