
name := "Example"
version := "0.1"
scalaVersion := "2.12.10"

lazy val custom = settingKey[String]("Custom value")
lazy val clear = taskKey[Unit]("Clear std::out")

lazy val clearTask = clear := {
  // new ProcessBuilder("bash", "-c").command("clear").inheritIO.start.waitFor // Java style
  import sys.process._
  "bash -c clear" !
}

lazy val common = Seq(
  clearTask,
  custom := "Custom value HELLO!"
)



lazy val base = (project in file(".")).settings(
  common
).aggregate(core, util)
lazy val core = project.dependsOn(util)
lazy val util = project

