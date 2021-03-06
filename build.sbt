val defaultProjectSettings = Defaults.coreDefaultSettings ++ Seq(
  scalaVersion := "2.11.8",
  javacOptions := Seq("-encoding", "UTF-8"), // http://docs.oracle.com/javase/jp/7/technotes/tools/windows/javac.html
  scalacOptions ++= Seq("-feature", "-deprecation"),
  scalacOptions in Test ++= Seq("-Yrangepos"),
  javaOptions ++= sys.process.javaVmArguments.filter(// http://d.hatena.ne.jp/xuwei/20130207/1360203782
    a => Seq("-Xmx", "-Xms", "-XX").exists(a.startsWith)
  )
)

val testDependencies = Seq(
  // https://mvnrepository.com/artifact/org.scalatest/scalatest_2.11
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

val freeTask = Project(
  id = "free-task",
  base = file(""),
  settings = defaultProjectSettings
).settings(libraryDependencies ++= testDependencies ++ Seq(
  // https://mvnrepository.com/artifact/org.scalaz/scalaz-core_2.11
  "org.scalaz" %% "scalaz-core" % "7.2.6"
))

val freeTaskExample = Project(
  id = "free-task-example",
  base = file("example"),
  settings = defaultProjectSettings
).dependsOn(
  freeTask,
  ProjectRef(uri("git://github.com/gakuzzzz/free-scalikejdbc.git#b2622a9ab5aefbda775d3ef3a9ac4f431008523f"), "core")
).settings(libraryDependencies ++= testDependencies ++ Seq(
  // https://mvnrepository.com/artifact/org.scalatest/scalatest_2.11
  "org.scalikejdbc" %% "scalikejdbc" % "2.4.2",
  // https://mvnrepository.com/artifact/com.h2database/h2
  "com.h2database" % "h2" % "1.4.192" % "test"
))
