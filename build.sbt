lazy val `ui-client` = project

lazy val `ui-server` = project

lazy val `event-server` = project

lazy val `tarot-table` = project.in(file("."))
  .dependsOn(`event-server`, `ui-server`)
  .settings(Revolver.settings)
  .settings(
    libraryDependencies ++= Seq(
      Boilerplate.Modules.akka("actor"),
      Boilerplate.Modules.akka("http-experimental", "1.0-RC4")
    )
  )