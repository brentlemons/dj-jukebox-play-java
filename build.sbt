name := "dj-jukebox-play-java"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "4.2.1.Final",
  cache,
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "org.webjars" %% "webjars-play" % "2.2.1",
  "org.webjars" % "bootstrap" % "3.1.1"
)     

play.Project.playJavaSettings
