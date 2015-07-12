lazy val `ui-client` = project

lazy val `http-common` = project
  .settings(
    libraryDependencies ++= Seq(
      Boilerplate.Modules.akka("http-experimental", "1.0-RC4")
    )
  )


lazy val `ui-server` = project
  .dependsOn(`http-common`)

lazy val `event-server` = project
  .dependsOn(`http-common`)

lazy val `tarot-table` = project.in(file("."))
  .dependsOn(`event-server`, `ui-server`)
  .settings(Revolver.settings)
  .settings(
    libraryDependencies ++= Seq(
      Boilerplate.Modules.akka("actor"),
      Boilerplate.Modules.akka("http-experimental", "1.0-RC4")
    )
  )