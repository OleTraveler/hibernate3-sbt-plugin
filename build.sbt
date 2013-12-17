name := "hibernate3-sbt-plugin"

version := "1.0"

libraryDependencies ++= Seq(
  "org.hibernate" % "hibernate" % "3.2.6.ga",
  "org.hibernate" % "hibernate-annotations" % "3.3.1.GA"
)


resolvers += "java.net repo" at "http://download.java.net/maven/2/"

sbtPlugin := true
