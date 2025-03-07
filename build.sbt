import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.linker.interface.ModuleKind
import sbtprojectmatrix.ProjectMatrixPlugin.autoImport._

val Scala3Version = "3.6.3"

ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val commonSettings = Seq(
  organization := "com.xebia",
  scalaVersion := Scala3Version
)

lazy val core = (projectMatrix in file("core"))
  .settings(
    commonSettings,
    name := "florence-spike-core"
  )
  .jvmPlatform(Seq(Scala3Version))
  .jsPlatform(Seq(Scala3Version))
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }
  )

lazy val coreJVM = core.jvm(Scala3Version)
lazy val coreJS  = core.js(Scala3Version)

lazy val rendererJS = (project in file("rendererJS"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(coreJS)
  .settings(
    commonSettings,
    name                            := "florence-spike-renderer-js",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0"
  )

lazy val rendererJVM = (project in file("rendererJVM"))
  .dependsOn(coreJVM)
  .settings(
    commonSettings,
    name := "florence-spike-renderer-jvm"
  )

lazy val root = (project in file("."))
  .aggregate(coreJVM, coreJS, rendererJS, rendererJVM)
  .settings(
    commonSettings,
    name           := "florence-spike",
    publish / skip := true
  )
