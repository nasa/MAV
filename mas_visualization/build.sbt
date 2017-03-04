import no.vedaadata.sbtjavafx.JavaFXPlugin._
import sbt.Keys._
import sbt.{Attributed, _}
import MASVisualizationBuild._

name := "mas-visualization"


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

unmanagedSourceDirectories <<= baseDirectory(base => List("../mas_models/src") map (base / _))


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
lazy val orgOpenIDEUtilLookup = "RELEASE82" // latest.integration" //"RELEASE82"

//lazy val masModelProject = Project("plugins", file("../mas_models/mas_model_code"))

lazy val commonSettings = Seq(
  organization := "gov.nasa",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.8",
  sbtVersion := "0.13.12",
  isSnapshot := true,
  // may be hard on us but as long as it is ok, let's keep it that way
  conflictManager := ConflictManager.strict,
  //Define the java version to use

//  javacOptions in (Compile, doc) = Seq("-source", "1.8"),

  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  javacOptions in doc := Seq("-source", "1.8"),
    ivyScala := ivyScala.value map {
    _.copy(overrideScalaVersion = true)
  },
  resolvers += Resolver.mavenLocal
)


//lazy val raceProject = RootProject(uri(sys.props.getOrElse("race.wwj_uri", "git://github.com/NASARace/race.git#master")))

//lazy val wwjProject = RootProject(uri(sys.props.getOrElse("race.wwj_uri", "git://github.com/pcmehlitz/WorldWindJava.git#pcm")))
//lazy val fxLeak = RootProject(uri(sys.props.getOrElse("leakfx", "git://github.com/sialcasa/leakdetectorFX#master")))

lazy val root = createRootProject("mas-viz").aggregate(vizData, vizCore, vizMetrics, vizTrafficSwing, vizTrafficFx, simuLauncher, scenarioGen, documentation, sandBox).dependsOn(vizCore).settings(
  commonSettings,
  // resolvers += "Main maven repo" at "http://repo1.maven.org/maven2/",
  //resolvers += Resolver.mavenLocal,
  resolvers += "Main netbeans maven repo" at "http://bits.netbeans.org/maven2/",
  //libraryDependencies += "org.controlsfx" % "controlsfx" % "8.40.11",
  libraryDependencies += "org.netbeans.api" % "org-openide-util-lookup" % orgOpenIDEUtilLookup,
  //libraryDependencies += "com.lynden" % "GMapsFX" % "2.0.8-SNAPSHOT",
  unmanagedJars in Compile += Attributed.blank(javaFxLib)
)

lazy val vizData = createProject("mas-viz-data","viz-data").settings(
  commonSettings,
  //unmanagedSourceDirectories := List(file(mas_models_path)),

  //unmanagedSourceDirectories <<= baseDirectory( base => List("../mas_models/src/","../../mas_models/src/") map (base / _ )),
  unmanagedBase := baseDirectory.value / "../lib",
  unmanagedJars in Compile += Attributed.blank(javaFxLib),
  libraryDependencies += "junit" % "junit" % jUnitVersion,
//  libraryDependencies += "gov.nasa" % "mas-model-code_2.11" % "1.0-SNAPSHOT",
  libraryDependencies += "gov.nasa" % "mas-data_2.11" % "1.0-SNAPSHOT",
  libraryDependencies += "gov.nasa" % "mas-atclib_2.11" % "1.0-SNAPSHOT",
  libraryDependencies += "gov.nasa" % "mas-atm-concepts_2.11" % "1.0-SNAPSHOT",
  libraryDependencies += "gov.nasa" % "mas-brahms_2.11" % "1.0-SNAPSHOT",
  libraryDependencies += "gov.nasa" % "mas-hmi_2.11" % "1.0-SNAPSHOT",
  libraryDependencies += "gov.nasa" % "mas-sim-conf_2.11" % "1.0-SNAPSHOT",
  //  libraryDependencies += "gov.nasa" % "race-common_2.11" % "1.0",
  //  libraryDependencies += "gov.nasa" % "race-data_2.11" % "1.0",
  //  libraryDependencies += "gov.nasa" % "race-core_2.11" % "1.0",
  //  libraryDependencies += "gov.nasa" % "race-actors_2.11" % "1.0",
  //  libraryDependencies += "gov.nasa" % "race-swing_2.11" % "1.0",
  //  libraryDependencies += "gov.nasa" % "race-ww_2.11" % "1.0",
  //  libraryDependencies += "gov.nasa" % "race-tools_2.11" % "1.0",
  //  libraryDependencies += "gov.nasa" % "race_2.11" % "1.0",

  publishArtifact in(Compile, packageDoc) := false,
  publishArtifact in packageDoc := false,
  sources in(Compile, doc) := Seq.empty
)


lazy val vizCore = createProject("mas-viz-core","viz-core").dependsOn(vizData).settings(
  commonSettings,
  resolvers += "Main netbeans maven repo" at "http://bits.netbeans.org/maven2/",

  libraryDependencies += "org.netbeans.api" % "org-openide-util-lookup" % orgOpenIDEUtilLookup,
  libraryDependencies += "junit" % "junit" % jUnitVersion,
  libraryDependencies += "junit" % "junit" % jUnitVersion % "test",
  //unmanagedBase := baseDirectory.value / "../lib",
  unmanagedJars in Compile += Attributed.blank(javaFxLib)
)

//libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",

lazy val vizMetrics = createProject("mas-viz-metrics","viz-metrics")
  .dependsOn(vizCore)
  .settings(
    commonSettings,
    libraryDependencies += "org.netbeans.api" % "org-openide-util-lookup" % orgOpenIDEUtilLookup,
    libraryDependencies += "org.controlsfx" % "controlsfx" % controlsFxVersion,
    //    libraryDependencies += "junit" % "junit" % "4.12" % "test",
    unmanagedJars in Compile += Attributed.blank(javaFxLib)
  )

lazy val scenarioGen = createProject("mas-viz-scenario-gen","viz-scenario-gen")
  .dependsOn(vizData, vizCore, vizMetrics)
  .settings(
    commonSettings,
    unmanagedJars in Compile += Attributed.blank(javaFxLib),
    mainClass in(Compile, run) := Some("gov.nasa.arc.atc.scenariogen.hmi.ScenarioGenAppLauncher")
  )


lazy val vizTrafficSwing = createProject("mas-viz-traffic-swing","viz-traffic-swing").dependsOn(vizData).settings(
  commonSettings,
  //unmanagedBase := baseDirectory.value / "../lib",
  mainClass in(Compile, run) := Some("atcGUI.components.ATCVisFrame")
)


//NOTE: order matters
lazy val vizTrafficFx = createProject("mas-viz-traffic-fx","viz-traffic-fx").dependsOn(vizCore, vizData, vizMetrics).settings(
  commonSettings,
  resolvers += "Main netbeans maven repo" at "http://bits.netbeans.org/maven2/",
  libraryDependencies += "org.netbeans.api" % "org-openide-util-lookup" % orgOpenIDEUtilLookup,
  libraryDependencies += "org.controlsfx" % "controlsfx" % controlsFxVersion,
  libraryDependencies += "junit" % "junit" % jUnitVersion, //% "test"
  //unmanagedBase := baseDirectory.value / "../lib",
  unmanagedJars in Compile += Attributed.blank(javaFxLib),
  jfxSettings,
  mainClass in(Compile, run) := Some("gov.nasa.arc.atc.viewer.AppLauncher"),
  JFX.mainClass := Some("gov.nasa.arc.atc.viewer.AppLauncher"),
  JFX.addJfxrtToClasspath := true
)


lazy val sandBox = project.in(file("sandbox")).dependsOn(vizData, vizCore, vizMetrics, vizTrafficFx, simuLauncher, scenarioGen).settings(
  commonSettings
)

lazy val simuLauncher = createProject("mas-viz-simu-launcher","viz-simu-launcher").dependsOn(vizCore, vizData, vizMetrics, vizTrafficFx).settings(
  commonSettings,
  libraryDependencies += "org.netbeans.api" % "org-openide-util-lookup" % orgOpenIDEUtilLookup,
  libraryDependencies += "org.controlsfx" % "controlsfx" % controlsFxVersion,
  libraryDependencies += "junit" % "junit" % jUnitVersion % "test",

  libraryDependencies += "gov.nasa" % "mas-data_2.11" % "1.0-SNAPSHOT",
  libraryDependencies += "gov.nasa" % "mas-atclib_2.11" % "1.0-SNAPSHOT",
  libraryDependencies += "gov.nasa" % "mas-atm-concepts_2.11" % "1.0-SNAPSHOT",
  libraryDependencies += "gov.nasa" % "mas-brahms_2.11" % "1.0-SNAPSHOT",
  libraryDependencies += "gov.nasa" % "mas-hmi_2.11" % "1.0-SNAPSHOT",
  libraryDependencies += "gov.nasa" % "mas-sim-conf_2.11" % "1.0-SNAPSHOT",

  jfxSettings,
  mainClass in(Compile, run) := Some("gov.nasa.arc.atc.parametrized.ConfiguratorLauncher"),
  JFX.mainClass := Some("gov.nasa.arc.atc.parametrized.ConfiguratorLauncher"),
  JFX.addJfxrtToClasspath := true
)

lazy val documentation = createProject("mas-viz-doc","viz-doc").settings(
  commonSettings,
  libraryDependencies += "org.asciidoctor" % "asciidoctorj" % asciiDoctorJVersion,
  mainClass in(Compile, run) := Some("gov.nasa.arc.atc.doc.MainDoc")
)

//val generateDoctask = TaskKey[Unit]("generateDoc", "Generates the documentation")
//
//generateDoctask := {
//  println("Start documentation generation...")
//  //  val asciidoctor:Asciidoctor = create
//  //  val map = new util.HashMap[String, Object]
//  //  val html = asciidoctor.convert("Writing AsciiDoc is _easy_!", map)
//  //  println(html)
//
//  println("... end of documentation generation")
//}

//// only works under viz-doc project
//lazy val execScript = taskKey[Unit]("Execute the shell script")
//
//execScript := {
//  "./viz-doc/gradlew asciidoc" !
//}
