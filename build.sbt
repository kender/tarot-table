lazy val `http-common` = project
  .settings(
    libraryDependencies ++= Seq(
      Boilerplate.Modules.akka("http-experimental", Boilerplate.Modules.akkaStreamsVersion),
      Boilerplate.Modules.μPickle,
      Boilerplate.Modules.scala_xml
    )
  )
lazy val `data-models` = crossProject
  .settings()
  .jvmSettings(
    libraryDependencies ++= Seq(
      Boilerplate.Modules.μPickle
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "0.3.4"
    )
  )

lazy val `data-models-js` = `data-models`.js
lazy val `data-models-jvm` = `data-models`.jvm

lazy val `ui-client` = project
  .dependsOn(`data-models-js`)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.0"
    )
  )

lazy val `ui-server` = project
  .dependsOn(`http-common`, `ui-client`, `data-models-jvm`)
  .settings(
    gatherJavaScripts := {
      (org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.fullOptJS in(`ui-client`, Compile)).value
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
  .dependsOn(`http-common`, `data-models-jvm`)
  .settings(
    libraryDependencies ++= Seq(
      Boilerplate.Modules.scala_xml,
      Boilerplate.Modules.slf4j_api
    )
  )

lazy val `tarot-table` = project.in(file("."))
  .dependsOn(`event-server`, `ui-server`)
  .aggregate(`event-server`, `ui-server`)
  .settings(Revolver.settings)
  .settings(
    libraryDependencies ++= Seq(
      Boilerplate.Modules.akka("actor"),
      Boilerplate.Modules.akka("http-experimental", Boilerplate.Modules.akkaStreamsVersion)
    ) ++ Boilerplate.Modules.logging
  )

lazy val gatherJavaScripts = taskKey[Unit]("get the output of building js")