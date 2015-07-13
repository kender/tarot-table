lazy val `ui-client` = project
  .enablePlugins(ScalaJSPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.0"
    )
  )

lazy val `http-common` = project
  .settings(
    libraryDependencies ++= Seq(
      Boilerplate.Modules.akka("http-experimental", "1.0-RC4"),
      Boilerplate.Modules.spray_json,
      Boilerplate.Modules.scala_xml
    )
  )


lazy val `ui-server` = project
  .dependsOn(`http-common`, `ui-client`)
  .settings(
    gatherJavaScripts := {
      (org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.fastOptJS in(`ui-client`, Compile)).value
      val outputDir = (classDirectory in Compile).value / "js"
      (Seq.empty[File] /: List("*.js", "*.map")) { (files, pattern) ⇒
        files ++ ((crossTarget in `ui-client`).value ** pattern).get
      } foreach { source ⇒
        streams.value.log.info(s"$source ⇒ ${outputDir / source.name}")
        IO.copyFile(source, outputDir / source.name)
      }
    },
    compile in Compile := {
      gatherJavaScripts.value
      (compile in Compile).value
    }
  )


lazy val `event-server` = project
  .dependsOn(`http-common`)
  .settings(
    libraryDependencies ++= Seq(
      Boilerplate.Modules.akka("http-spray-json-experimental", Boilerplate.Modules.akkaStreamsVersion),
      Boilerplate.Modules.scala_xml,
      Boilerplate.Modules.spray_json,
      Boilerplate.Modules.slf4j_api
    )
  )

lazy val `tarot-table` = project.in(file("."))
  .dependsOn(`event-server`, `ui-server`)
  .settings(Revolver.settings)
  .settings(
    libraryDependencies ++= Seq(
      Boilerplate.Modules.akka("actor"),
      Boilerplate.Modules.akka("http-experimental", Boilerplate.Modules.akkaStreamsVersion)
    ) ++ Boilerplate.Modules.logging
  )

lazy val gatherJavaScripts = taskKey[Unit]("get the output of building js")