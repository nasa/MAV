import no.vedaadata.sbtjavafx.JavaFXPlugin._
import sbt.Keys._
import sbt.{Attributed, _}
import MASModelBuild._

name := "mas-model-code"


//scalaVersion := "2.11.8"

//autoScalaLibrary := false

//Define the java version to use
//javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

//Add Javafx8 library
unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar"))

// debug tasks and values

val javaHomeVar = System.getenv("JAVA_HOME")
val javafxV1 = file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar")
val javaProperty = scala.util.Properties.javaHome
// important
val javaFxLib = file(scala.util.Properties.javaHome + "/lib/ext/jfxrt.jar")
//
val mas_models_path = file("../mas_models/src").getPath

//unmanagedSourceDirectories <<= baseDirectory(base => List("../mas_models/src") map (base / _))


//resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))

lazy val myTask = TaskKey[Unit]("myConfig")

myTask := {
  println("Debug Config !")
  println(" mas_models path: " + mas_models_path)
  println(" java home: " + javaHomeVar)
  println(" javafxV1: " + javafxV1)
  println(" java property: " + javaProperty)
  println(" javafx Lib: " + javaFxLib)
  println("End debug...")
}

lazy val jUnitVersion = "latest.integration" //"4.12"
lazy val controlsFxVersion = "latest.integration" //"8.40.12"
lazy val asciiDoctorJVersion = "latest.integration" //"1.5.4.1"
lazy val orgOpenIDEUtilLookup = "RELEASE82" //"latest.integration" //"RELEASE82"
lazy val log4JVersion = "2.7"
lazy val jUnitInterfaceVersion = "0.11"
lazy val univocityVersion = "latest.integration" // 2.2.2 ?


lazy val commonSettings = Seq(
  organization := "gov.nasa",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.8",
  sbtVersion := "0.13.12",
  isSnapshot := true,
  // may be hard on us but as long as it is ok, let's keep it that way
  //conflictManager := ConflictManager.strict,


  coverageEnabled := true,
  //Define the java version to use

//  javacOptions in (Compile, doc) = Seq("-source", "1.8"),

  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  javacOptions in doc := Seq("-source", "1.8"),
    ivyScala := ivyScala.value map {
    _.copy(overrideScalaVersion = true)
  },
  resolvers += Resolver.mavenLocal
)




lazy val root = project.in(file(".")).aggregate(modelGen,atcLib,atmConcepts,brahms,data,hmi,simConf).settings(
  commonSettings,
  // resolvers += "Main maven repo" at "http://repo1.maven.org/maven2/",
  //resolvers += Resolver.mavenLocal,
  resolvers += "Main netbeans maven repo" at "http://bits.netbeans.org/maven2/",
  libraryDependencies += "org.netbeans.api" % "org-openide-util-lookup" % orgOpenIDEUtilLookup,
  //libraryDependencies += "com.lynden" % "GMapsFX" % "2.0.8-SNAPSHOT",
  unmanagedJars in Compile += Attributed.blank(javaFxLib)
)


lazy val modelGen = createProject("mas-model-gen").settings(
  commonSettings,
  unmanagedBase := baseDirectory.value / "../lib",
  unmanagedJars in Compile += Attributed.blank(javaFxLib),
  libraryDependencies += "junit" % "junit" % jUnitVersion,
  publishArtifact in(Compile, packageDoc) := false,
  publishArtifact in packageDoc := false,
  sources in(Compile, doc) := Seq.empty
)

lazy val data = createProject("mas-data").settings(
  commonSettings,
  unmanagedBase := baseDirectory.value / "../lib",
  unmanagedJars in Compile += Attributed.blank(javaFxLib),
  libraryDependencies += "junit" % "junit" % jUnitVersion,
  publishArtifact in(Compile, packageDoc) := false,
  publishArtifact in packageDoc := false,
  sources in(Compile, doc) := Seq.empty
)

//% "test->test;compile->compile"

lazy val atcLib = createProject("mas-atclib").dependsOn(data).settings(
  commonSettings,
  //unmanagedBase := baseDirectory.value / "../lib",
  unmanagedJars in Compile += Attributed.blank(javaFxLib),
  libraryDependencies += "com.univocity" % "univocity-parsers" % univocityVersion ,
  libraryDependencies += "junit" % "junit" % jUnitVersion % Test,
  libraryDependencies += "com.novocode" % "junit-interface" % jUnitInterfaceVersion % Test
)



lazy val atmConcepts = createProject("mas-atm-concepts" ).dependsOn(atcLib % "test->test;compile->compile").settings(
  commonSettings,
  //unmanagedBase := baseDirectory.value / "../lib",
  unmanagedJars in Compile += Attributed.blank(javaFxLib),
  libraryDependencies += "org.mockito" % "mockito-core" % "2.2.3",
  libraryDependencies += "org.apache.ant" % "ant" % "1.9.7",
  libraryDependencies += "org.apache.ant" % "ant-launcher" % "1.9.7",
  libraryDependencies += "junit" % "junit" % jUnitVersion
)



lazy val brahms = createProject("mas-brahms").dependsOn(atmConcepts % "test->test;compile->compile").settings(
  commonSettings,
  //unmanagedBase := baseDirectory.value / "../lib",
  unmanagedJars in Compile += Attributed.blank(javaFxLib),
  libraryDependencies += "junit" % "junit" % jUnitVersion
)


lazy val hmi = createProject("mas-hmi").dependsOn(brahms % "test->test;compile->compile" )settings(
  commonSettings,
  //unmanagedBase := baseDirectory.value / "../lib",
  unmanagedJars in Compile += Attributed.blank(javaFxLib),
  libraryDependencies += "junit" % "junit" % jUnitVersion,
  libraryDependencies += "org.netbeans.api" % "org-openide-util-lookup" % orgOpenIDEUtilLookup,
  libraryDependencies += "org.controlsfx" % "controlsfx" % controlsFxVersion
)


lazy val simConf = createProject("mas-sim-conf").dependsOn(hmi)settings(
  commonSettings,
  //unmanagedBase := baseDirectory.value / "../lib",
  unmanagedJars in Compile += Attributed.blank(javaFxLib),
  libraryDependencies += "junit" % "junit" % jUnitVersion
)
