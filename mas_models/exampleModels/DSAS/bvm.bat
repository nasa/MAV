@ECHO OFF
SET BRAHMS_ROOT=D:\Programmes\Brahms2\AgentEnvironment

REM Java Setup
SET JAVA_MEMORY=-Xincgc -Xmx512m -Xss1024k -Xms32m  
SET BOOTCP=-Xbootclasspath/p:"%BRAHMS_ROOT%\lib\jacorb\jacorb.jar;%BRAHMS_ROOT%\lib\jacorb\logkit-1.2.jar;%BRAHMS_ROOT%\lib\jacorb\avalon-framework-4.1.5.jar"
SET CP=-cp ".;%BRAHMS_ROOT%\config;%BRAHMS_ROOT%\deploy;%BRAHMS_ROOT%\Agents"
SET EXT_JAR_DIRS=-Djava.ext.dirs="%BRAHMS_ROOT%\jre\lib\ext;%BRAHMS_ROOT%\lib;%BRAHMS_ROOT%\lib\apache;%BRAHMS_ROOT%\lib\ci;%BRAHMS_ROOT%\lib\jacorb;%BRAHMS_ROOT%\lib\jidesoft;%BRAHMS_ROOT%\lib\mysql;%BRAHMS_ROOT%\lib\nss;%BRAHMS_ROOT%\deploy;%BRAHMS_ROOT%\Agents;D:\Code\NASA\Brahms\atm\BrahmsModels\DSAS\build"
SET ORB=-Dorg.omg.CORBA.ORBClass=org.jacorb.orb.ORB -Dorg.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton
SET JAVA_PROPERTIES=%JAVA_MEMORY% %BOOTCP% %CP% %EXT_JAR_DIRS% %ORB%

title Brahms Virtual Machine
java %JAVA_PROPERTIES% gov.nasa.arc.brahms.vm.bvm -cf vm_win.cfg -mode sim gov/nasa/arc/atm/atmmodel/scenarios/small2plane2depart/ATC
