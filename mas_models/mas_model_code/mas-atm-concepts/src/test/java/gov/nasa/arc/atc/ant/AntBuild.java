/**
Copyright © 2016, United States Government, as represented
by the Administrator of the National Aeronautics and Space
Administration. All rights reserved.
 
The MAV - Modeling, analysis and visualization of ATM concepts
platform is licensed under the Apache License, Version 2.0
(the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the
License at http://www.apache.org/licenses/LICENSE-2.0. 
 
Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific
language governing permissions and limitations under the
License.
**/

package gov.nasa.arc.atc.ant;
 
import java.io.File;
 
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
 
 
public class AntBuild extends Task{
     
    Project project=new Project();
     
    public final void execute() throws BuildException {
        System.out.println("--- Executing custom deploy Task ----");
        callAntBuildTarget();
    }
     
    private void callAntBuildTarget() {
        try{
            String target="brahms-sim";
             
            File buildFile = new File("/Documents/Code/mas_models/exampleModels/DSAS/build.xml");
            System.out.println("buildFile.getAbsolutePath() ->"+ buildFile.getAbsolutePath());
            project.setUserProperty("ant.file", buildFile.getAbsolutePath());
            project.init();
            ProjectHelper helper = ProjectHelper.getProjectHelper();
            helper.parse(project, buildFile);
             
//            project.addReference("ant.projectHelper", helper);
//            project.setProperty("BPEL.HOST", "127.0.0.1");
//            project.setProperty("OSB.HOST", "127.0.0.2");
//            project.setProperty("BPEL.HostPort","7001" );
//            project.setProperty("OSB.HostPort", "8001");
             
            project.addBuildListener(getDefaultLogger());
            project.executeTarget("clean");
            project.executeTarget(target);
            project.log("=== Build Completed Successfully ===",Project.MSG_INFO);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getStackTrace());
        }
    }
     
    private static DefaultLogger getDefaultLogger(){
        DefaultLogger consoleLogger=new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(Project.MSG_VERBOSE);
//      consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
        return consoleLogger;
    }
    
    public static void main(String[] args){
    	try {
			AntBuild toto = new AntBuild();
			toto.execute();
		} catch (Exception e) {
            System.out.println(e.getStackTrace());
		}
    }
     
}
