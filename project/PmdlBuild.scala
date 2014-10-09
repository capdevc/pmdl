import sbt._
import sbt.Keys._

object PmdlBuild extends Build {

  lazy val pmdl = Project(
    id = "pmdl",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "pmdl",
      organization := "com.pyaanalytics",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.4",
      // add other settings here
      libraryDependencies += "org.scalaj" %% "scalaj-http" % "0.3.16"
    )
  )
}
