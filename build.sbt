lazy val akkaHttpVersion = "10.1.10"
lazy val akkaVersion    = "2.6.0"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "2.13.1"
    )),
    name := "akka-http-quickstart-scala",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                  % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"       % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"           % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"                % akkaVersion,
      "ch.qos.logback"    % "logback-classic"             % "1.2.3",
      "com.datastax.cassandra" % "cassandra-driver-core"  % "3.5.1",
      "com.chrisomeara" % "pillar_2.10"                   % "2.3.0",

      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"                % "3.0.8"         % Test
    )
  )
