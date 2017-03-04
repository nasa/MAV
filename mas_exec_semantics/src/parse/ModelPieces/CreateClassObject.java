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

package ModelPieces;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CreateClassObject extends groupAgentClassObjectMethods{
	String objName;
	String clsOrObj;
	FileWriter stream;
	BufferedWriter out;
	
	/**
	 * Sets the modelName, the agentName
	 * and calls initAgent
	 * @param modelName, agentName
	 * @throws IOException
	 */
	public CreateClassObject(String modelName, String destination, String objName, File file,
			String packageName, String clsOrObj) {
		
		super(modelName, destination, objName, file, packageName, clsOrObj);
		this.objName = objName;
		this.clsOrObj = clsOrObj;
		Document d = startParsingFile(file);
		if (d != null)
			root = d.getDocumentElement();
		String body = initClassObject();
		super.init(body);
	}


	public String getInstanceOf() {
		NodeList instList = root.getElementsByTagName("INSTANCEOF");
		String instance = "";
		for (int i = 0; i < instList.getLength(); i++) {
			Element elem = (Element)instList.item(i);
			instance = elem.getAttribute("ref");
			int idx = instance.lastIndexOf(".");
			instance = instance.substring(idx+1);
		}
		return instance;
	}
	
	public String initClassObject() {
		String disp = "";
		String cost = "";
		String time = "";
		String loc = "";
		String instOf = "";
		List<String> extendList = new ArrayList<String>();
		
		disp = root.getAttribute("display"); //get display
		cost = root.getAttribute("cost");
		time = root.getAttribute("timeUnit");
		loc = root.getAttribute("location");
		
		if (clsOrObj.equals("object")) {
			NodeList instOfList = root.getElementsByTagName("INSTANCEOF");
			Element instance = (Element) instOfList.item(0);
			instOf = instance.getAttribute("ref");
			int index = instOf.lastIndexOf('.');
			instOf = instOf.substring(index+1);
			if(instOf.contains("-"))
				instOf = instOf.replaceAll("-", "_");
			if (!instOf.equals("BaseClass") && !instOf.equals("SystemClass")) {
				extendList.add(instOf);
			}
		} else {
			NodeList instOfList = root.getElementsByTagName("EXTENDS");
			for (int i = 0; i < instOfList.getLength(); i++) {
				Element instance = (Element) instOfList.item(i);
				instOf = instance.getAttribute("ref");
				int index = instOf.lastIndexOf('.');
				instOf = instOf.substring(index+1);
				if(instOf.contains("-"))
					instOf = instOf.replaceAll("-", "_");
				if (!instOf.equals("BaseClass") && !instOf.equals("SystemClass")) {
					extendList.add(instOf);
				}
			}
		}
		if (cost.equals(""))
			cost = "0.0";
		if (time.equals(""))
			time = "0";
		if (loc.equals(""))
			loc = "none";
		loc = loc.replace(packageName, "");
		String body = "package " + modelName + ";\n";
		body += "import java.util.*; \n";
		body += "import gov.nasa.arc.brahms.model.*; \n";
		body += "import gov.nasa.arc.brahms.model.activity.*; \n";
		body += "import gov.nasa.arc.brahms.model.comparison.*;\n";
		body += "import gov.nasa.arc.brahms.model.concept.*; \n";
		body += "import gov.nasa.arc.brahms.model.expression.*;\n";
		body += "import gov.nasa.arc.brahms.simulator.world.*;\n";
		body += "import gov.nasa.arc.brahms.utilities.*;\n\n";
		body += "@SuppressWarnings(\"unused\")\n\n";
		body += "public class " + objName + " extends ";
		if(DEBUG)
			System.out.println("Creating object/class :" + objName);
		if (clsOrObj.equals("object")) {
			body += "Object_b { \n";
			body += "public " + objName + "(Class_b classInstance) { \n";
			body += "super(\"" + objName + "\", \"" + disp + "\", " + cost + ", "
					+ time;
			body += ", \"" + loc + "\", classInstance";
		} else if (clsOrObj.equals("class")) {
			body += "Class_b { \n";
			body += "public " + objName + "() { \n";
			body += "super(\"" + objName + "\", \"" + disp + "\", " + cost + ", "
					+ time;
			//body += ", \"" + loc + "\"";
		}
	
		
		body += ");\n }\n\n";
		
		body += "protected void addclassExtended() {\n";
		for (int i = 0; i < extendList.size(); i++) {
			body += "\t\tclassesExtended.add(\"" + extendList.get(i) + "\");\n";
		}
		body += "} \n\n";
		return body;
	}

	
}
	
