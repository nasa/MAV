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

package blocks;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import ModelPieces.CreateClassObject;
import ModelPieces.CreateMain;
import ModelPieces.CreateGroupAgent;

/**
 * 
 * @author josie
 */
public class CreateJavaFiles {
	FileWriter mainFileStream = null;
	String modelName;
	String destination;
	CreateMain main;
	boolean DEBUG = false;
	
	List<String> XMLattributes = new ArrayList<String>();
	List<String> XMLactivities = new ArrayList<String>();
	
	StringBuilder config = new StringBuilder();
	
	public CreateJavaFiles(String modelName, String new_destination) {
		this.modelName = modelName;
		destination = new_destination;
		createDirectory();
		main = new CreateMain(modelName, destination);
	}

	/**
	 * Creates a directory with the same name as the model for all the relevant
	 * files; if the directory already exists, then deletes the directory and
	 * all the files within it before re-creating the directory. (neha)
	 */
	public void createDirectory() {
		try {
			File directory = new File(destination + modelName);
			// neha: 10/09/2015 if the directory already exists, delete it first
			// Java isn't able to delete folders with data in it; so rather than
			// write a recursive method, this is done with FileUtils in
			// the Apache Commons IO library.
			if (directory.exists()) {
				FileUtils.deleteDirectory(directory); // directory.delete();
			}

			// Makes a directory, including any necessary but nonexistent
			// parent directories.
			FileUtils.forceMkdir(directory);
			System.out.println("Directory: " + destination + modelName
					+ " created");

		} catch (Exception e) { // Catch exception if any
			System.err.println("Error creating directory: " + destination
					+ modelName + ": " + e.getMessage());
		}
	}

	public void initGroups() {
		main.initGroups();
	}
	
	public void createNewGroup(String name, File file, String packageName) {
		if(name.contains("-"))
			name = name.replaceAll("-", "_");
		main.addGroup(name);
		if(DEBUG)
			System.out.println("Creating group: " + name);
		CreateGroupAgent newGp = new CreateGroupAgent(modelName, destination, name, file, packageName, "group");
		
	}
	
	public void initAgents() {
		main.initAgents();
	}
	
	public void createNewAgent(String name, File file, String packageName) {
		if(name.contains("-"))
			name = name.replaceAll("-", "_");
		main.addAgent(name);
		if(DEBUG)
			System.out.println("Creating agent: " + name);
		CreateGroupAgent newAg = new CreateGroupAgent(modelName, destination, name, file, packageName, "agent");
	}
	
	public void createNewClass(String name, File file, String packageName) {
		if(name.contains("-"))
			name = name.replaceAll("-", "_");
		main.addClass(name);
		if(DEBUG)
			System.out.println("Creating class: " + name);
		CreateClassObject newCls = new CreateClassObject(modelName, destination, name, file, packageName, "class");
	
	}
	
	public void createNewObject(String name, File file, String packageName, String text) {
		if(name.contains("-"))
			name = name.replaceAll("-", "_");
		CreateClassObject newObj = new CreateClassObject(modelName, destination, name, file, packageName, "object");
		if(DEBUG)
			System.out.println("Creating object: " + name + " with instance of " + newObj.getInstanceOf());
		main.addObject(name, newObj.getInstanceOf(), text);
	}
	
	public void initClassesObjects() {
		main.initClassesObjects();
	}
	
	public void createNewObject(String name) {
		if(name.contains("-"))
			name = name.replaceAll("-", "_");
		main.addObject(name, "?", "");
		try {
			FileWriter newObjStream = new FileWriter(modelName + "/"
					+ name + ".java");
			BufferedWriter out = new BufferedWriter(newObjStream);
			out.write("this is a new obj");
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error creating object: " + name + ": " + e.getMessage());
		}
	}
	
	public void createNewGroup(String name) {
		if(name.contains("-"))
			name = name.replaceAll("-", "_");
		try {
			FileWriter newGroupStream = new FileWriter(modelName + "/Main.java");
			BufferedWriter out = new BufferedWriter(newGroupStream);
			out.write("this is a new group");
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error creating group: " + name + ": " + e.getMessage());
		}
	}
	
	public void createNewClass(String name) {
		if(name.contains("-"))
			name = name.replaceAll("-", "_");
		try {
			FileWriter newClassStream = new FileWriter(modelName + "/Main.java");
			BufferedWriter out = new BufferedWriter(newClassStream);
			out.write("this is a new class");
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error creating class: " + name + ": " + e.getMessage());
		}
	}
	
	public void close() {
		//main.addAgentElems();
		//main.addObjectElems();
		main.closeMain();
	}

}
