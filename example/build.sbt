name := "free-task-example"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-feature", "-deprecation")

scalacOptions in Test ++= Seq("-Yrangepos")

val `free-task-example` = (project in file(".")).dependsOn(
  RootProject(file(".."))
)

// https://mvnrepository.com/artifact/com.h2database/h2
libraryDependencies += "com.h2database" % "h2" % "1.4.192" % "test"
