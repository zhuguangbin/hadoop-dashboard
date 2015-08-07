import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "hadoop-dashboard"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "org.springframework" % "spring-beans" % "1.2.6",
    "com.google.inject" % "guice" % "3.0",
    "com.googlecode.lambdaj" % "lambdaj" % "2.3.3",
    "mysql" % "mysql-connector-java" % "5.1.29",
    "com.mchange" % "c3p0" % "0.9.2.1",
    "org.spark-project.hive" % "hive-jdbc" % "0.13.1" excludeAll( ExclusionRule(organization = "org.apache.avro") ),
    "org.apache.hadoop" % "hadoop-common" % "2.0.0-cdh4.6.0"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += "cdh" at "https://repository.cloudera.com/artifactory/cloudera-repos/"
  )

}
