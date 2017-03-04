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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CreateGroupAgent extends groupAgentClassObjectMethods {
	String agentName;
	String gpOrAg;
	/**
	 * Sets the modelName, the agentName
	 * and calls initAgent
	 * @param modelName, agentName
	 * @throws IOException
	 */
	
	public CreateGroupAgent(String modelName, String destination, String agentName, File file,
			String packageName, String gpOrAg) {
		super(modelName, destination, agentName, file, packageName, gpOrAg);
		this.agentName = agentName;
		this.gpOrAg = gpOrAg;
		Document d = startParsingFile(file);
		if (d != null)
			root = d.getDocumentElement();
		String body = initGroupAgent();
		//ag = new agent(agentName);
		super.init(body);
	}


	public String initGroupAgent() {		
		String disp = "";
		String cost = "";
		String time = "";
		String loc = "";
		
		List<String> memberOfs = new ArrayList<String>();
		
		disp = root.getAttribute("display"); //get display
		cost = root.getAttribute("cost");
		time = root.getAttribute("timeUnit");
		loc = root.getAttribute("location");
		
		//get 'memberof'
		NodeList extendList = root.getElementsByTagName("MEMBEROF");
		for (int i = 0; i < extendList.getLength(); i++) {
			Element exElem = (Element)extendList.item(i);
			String extension = exElem.getAttribute("ref");
			int index = extension.lastIndexOf('.');
			extension = extension.substring(index+1);
			if(extension.contains("-"))
				extension = extension.replaceAll("-", "_");
			if (!(extension.equals("BaseGroup")))
				memberOfs.add(extension);
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
		//body += "import gov.nasa.arc.brahms.*; \n\n";
		body += "import gov.nasa.arc.brahms.model.*; \n";
		body += "import gov.nasa.arc.brahms.model.activity.*;\n";
		body += "import gov.nasa.arc.brahms.model.comparison.*;\n";
		body += "import gov.nasa.arc.brahms.model.concept.*; \n";
		body += "import gov.nasa.arc.brahms.model.expression.*;\n";
		body += "import gov.nasa.arc.brahms.simulator.world.*;\n\n";
		body += "@SuppressWarnings(\"unused\")\n\n";
		body += "public class " + agentName + " extends ";
		if (gpOrAg.equals("agent"))
			body += "Agent { \n";
		else if (gpOrAg.equals("group"))
			body += "Group { \n";
		body += "public " + agentName + "() { \n";
		body += "super(\"" + agentName + "\", \"" + disp + "\", " + cost + ", "
				+ time;
		if (gpOrAg.equals("agent"))
			body += ", \"" + loc + "\"";
		body += ");\n }\n";
		
		body += "protected void addMemberOfs() {\n";
		for (int i = 0; i < memberOfs.size(); i++) {
			body += "\t\tmemberOf.add(\"" + memberOfs.get(i) + "\");\n";
		}
		body += "}\n\n";
		
		return body;
	}
	

}
	
