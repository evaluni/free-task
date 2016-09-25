name := "free-task"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-feature", "-deprecation")

scalacOptions in Test ++= Seq("-Yrangepos")

val `free-task` = (project in file(".")).dependsOn(
  ProjectRef(uri("git://github.com/rika-t/free-scalikejdbc.git"), "core")
)

// https://mvnrepository.com/artifact/org.scalatest/scalatest_2.11
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0"

// https://mvnrepository.com/artifact/org.scalaz/scalaz-core_2.11
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.7"

// https://mvnrepository.com/artifact/org.scalikejdbc/scalikejdbc_2.11
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "2.4.2"
