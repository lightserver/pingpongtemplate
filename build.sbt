import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

import sbt.Keys._

name := "basic  scalajs"

version := "0.1"

scalaJSStage in Global := FastOptStage

skip in packageJSDependencies := false

val app = crossProject.settings(
  scalaVersion := "2.11.8",

  unmanagedSourceDirectories in Compile +=
    baseDirectory.value  / "shared" / "main" / "scala",

    libraryDependencies ++= Seq(

    "com.lihaoyi" %%% "upickle" % "0.3.8"
  ),
  testFrameworks += new TestFramework("utest.runner.Framework")

).jsSettings(
    libraryDependencies ++= Seq(

      "com.github.japgolly.scalajs-react" %%% "core" % "0.8.3",
      "com.github.japgolly.scalajs-react" %%% "extra" % "0.8.3"

    ),
    jsDependencies += "org.webjars" % "react" % "0.13.1" / "react-with-addons.js" commonJSName "React",
    skip in packageJSDependencies := true ,
     persistLauncher in Compile := true
  ).jvmSettings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.4.1",
      "com.typesafe.akka" %% "akka-remote" % "2.4.1",
      "org.scalaz" %% "scalaz-core" % "7.1.2",
      "com.typesafe.akka" %% "akka-http-experimental" % "2.0.1",
      "org.scalatest" %% "scalatest" % "2.2.1" % "test"
    )
  )

lazy val appJS = app.js.settings(
)

lazy val appJVM = app.jvm.settings(


  version := "0.31",
  resourceDirectory in Compile <<= baseDirectory(_ / "../shared/src/main/resources"),
 unmanagedResourceDirectories in Compile <+= baseDirectory(_ / "../jvm/src/main/resources"),

  resourceGenerators in Compile <+= Def.task {
    val log = streams.value.log
    //log.info(s"APP: ${((classDirectory in Compile).value / "material-ui-app.html").getCanonicalPath}")
    val mui = baseDirectory(_ / "../../web/.tmp" ).value
    val muiFiles = (mui ** ("*.js" || "*.css" || "*.eot" || "*.svg" || "*.svg" || "*.ttf" || "*.woff" || "*.html")).filter(_.isFile).get
    import Path.rebase
    val mappings = muiFiles pair rebase(Seq(mui), (resourceManaged in Compile).value / "web")
    IO.copy(mappings, true)
    mappings.map(_._2)
  }


).enablePlugins(JavaAppPackaging)

