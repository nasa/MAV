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
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ModelPieces.CreateGeo;

/**
 * Creates Main.java and Geography.java
 * @author josie
 */
public class ParseXML {
	private static Map<File, FileType> files;
	private static String packageName = "";
	private static CreateJavaFiles createJava;
	private static CreateGeo geo;
	
	public static void parseXMLFiles(Map<File, FileType> fileList, String modelName,
			String mainPath, String packageName, String destination) {
		if(!packageName.equals(""))
			packageName = packageName.concat(".");
		files = fileList;

		//create and set up main, geography files
		createJava = new CreateJavaFiles(modelName, destination);
		geo = new CreateGeo(modelName, destination);
		
		//get all relevant geography info
    	getAreaDefs(); //parse areaDefs
    	geo.initAreas();
    	getAreas(); //parse areas
    	geo.initPath();
    	getPaths(); //parse paths
    	geo.closeStream(); //close geography file
    	
    	getGroups();
    	getAgents();
    	getClasses();
    	getObjects();
    	
    	createJava.close();
	}
	
	public static void parseGroup(File file) {
		String tempFileName = file.getName();
		tempFileName = tempFileName.replace(".bcc", "");
		if(tempFileName.contains("-"))
			tempFileName.replace("-", "_");
		createJava.createNewGroup(tempFileName, file,
				packageName); //creates new file & adds to main
	}
	
	public static void parseAgent(File file) {
		String tempFileName = file.getName();
		tempFileName = tempFileName.replace(".bcc", "");
		if(tempFileName.contains("-"))
			tempFileName.replace("-", "_");
		createJava.createNewAgent(tempFileName, file,
				packageName); //creates new file & adds to main
			//parse other info
	}
	
	public static void parseClass(File file) {
		String tempFileName = file.getName();
		tempFileName = tempFileName.replace(".bcc", "");
		if(tempFileName.contains("-"))
			tempFileName.replace("-", "_");
		createJava.createNewClass(tempFileName, file,
				packageName); //creates new file & adds to main
	}
	
	//TODO: objects
	public static void parseObject(File file, String text) {
		String tempFileName = file.getName();
		tempFileName = tempFileName.replace(".bcc", "");
		if(tempFileName.contains("-"))
			tempFileName.replace("-", "_");
			createJava.createNewObject(tempFileName, 
					file, packageName, text); //creates new file & adds to main
			//parse other info
	}
	
	public static void parseAreaDef(File file) {
		String disp = "";
		String[] extendedAreas = new String[20];

		Document d = startParsingFile(file);
		if (d != null) {
			Element root = d.getDocumentElement();
			disp = root.getAttribute("display"); //get display
			//get 'extends'
			NodeList extendList = root.getElementsByTagName("EXTENDS");
			for (int i = 0; i < extendList.getLength(); i++) {
				Element exElem = (Element)extendList.item(i);
				String extension = exElem.getAttribute("ref");
				extension = removePathFromString(extension);
				extendedAreas[i] = extension;
			}
		}
		String name = file.getName();
		if(!packageName.equals("."))
			name = name.replace(packageName, "");
		name = name.replace(".bcc", "");
		geo.addNewAreaDef(name, disp, extendedAreas);
	}
	
	public static void parseArea(File file) {
		String disp = "";
		String instanceOf = "";
		String partOf = "";

		Document d = startParsingFile(file);
		if (d != null) {
			Element root = d.getDocumentElement();
			disp = root.getAttribute("display"); //get display
			//get instanceOf
			NodeList inst = root.getElementsByTagName("INSTANCEOF");
			Element instElem = (Element) inst.item(0);
			instanceOf = instElem.getAttribute("ref");
			instanceOf = removePathFromString(instanceOf);
			NodeList part = root.getElementsByTagName("PARTOF");
			if (part.getLength() > 0) {
				Element partElem = (Element) part.item(0);
				partOf = partElem.getAttribute("ref");
				partOf = removePathFromString(partOf);
			}
		}
		String name = file.getName();
		if(!packageName.equals("."))
			name = name.replace(packageName, "");
		name = name.replace(".bcc", "");
		geo.addNewArea(name, disp, instanceOf, partOf);

	}
	
	public static void parsePath(File file) {
		String disp = "";
		String area1Name = "";
		String area2Name = "";
		String distance = "";

		Document d = startParsingFile(file);
		if (d != null) {
			Element root = d.getDocumentElement();
			disp = root.getAttribute("display"); //get display
			//get areas
			NodeList areas = root.getElementsByTagName("AREAREF");
			Element area1 = (Element) areas.item(0);
			area1Name = area1.getAttribute("ref");
			area1Name = removePathFromString(area1Name);
			Element area2 = (Element) areas.item(1);
			area2Name = area2.getAttribute("ref");
			area2Name = removePathFromString(area2Name);
			distance = root.getAttribute("distance");
		}
		String name = file.getName();
		if(!packageName.equals("."))
			name = name.replace(packageName, "");
		name = name.replace(".bcc", "");
		geo.addNewPath(name, disp, area1Name, area2Name, distance);
	}
	
	/**
	 * Returns the document after ensuring it exists and can be parsed
	 * @param f
	 * @return
	 */
	public static Document startParsingFile(File f) {
		Document d = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			d = db.parse(f);
		} catch (java.io.IOException e) {
			System.out.println("can't find the file");
		} catch (Exception e) {
			System.out.println("problem parsing file");
		}
		return d;
	}
	
	/**
	 * Loops through the map of files and grabs those that are areadefs
	 * then calls parseAreaDef on them
	 */
	public static void getAreaDefs() {
		Iterator<Entry<File, FileType>> it = files.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<File,FileType> pair= (Map.Entry<File,FileType>)it.next();
			if (pair.getValue() == FileType.AREADEF) {
				File f = pair.getKey();
				parseAreaDef(f);
			}	
		}
	}
	
	/**
	 * Loops through the map of files and grabs those that are areas
	 * then calls parseArea on them
	 */
	public static void getAreas() {
		Iterator<Entry<File, FileType>> it = files.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<File,FileType> pair= (Map.Entry<File,FileType>)it.next();
			if (pair.getValue() == FileType.AREA) {
				File f = pair.getKey();
				parseArea(f);
			}	
		}
	}
	
	/**
	 * Loops through the map of files and grabs those that are areas
	 * then calls parseArea on them
	 */
	public static void getPaths() {
		Iterator<Entry<File, FileType>> it = files.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<File,FileType> pair= (Map.Entry<File,FileType>)it.next();
			if (pair.getValue() == FileType.PATH) {
				File f = pair.getKey();
				parsePath(f);
			}	
		}
	}

	/**
	 * Loops through the map of files and grabs those that are agents
	 * then calls parseAgent on them
	 */
	public static void getGroups() {
		createJava.initGroups();
		Iterator<Entry<File, FileType>> it = files.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<File,FileType> pair= (Map.Entry<File,FileType>)it.next();
			if (pair.getValue() == FileType.GROUP) {
				File f = pair.getKey();
				parseGroup(f);
			}	
		}
	}
	
	/**
	 * Loops through the map of files and grabs those that are agents
	 * then calls parseAgent on them
	 */
	public static void getAgents() {
		createJava.initAgents();
		Iterator<Entry<File, FileType>> it = files.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<File,FileType> pair= (Map.Entry<File,FileType>)it.next();
			if (pair.getValue() == FileType.AGENT) {
				File f = pair.getKey();
				parseAgent(f);
			}	
		}
	}
	
	/**
	 * Loops through the map of files and grabs those that are agents
	 * then calls parseAgent on them
	 */
	public static void getClasses() {
		createJava.initClassesObjects();
		Iterator<Entry<File, FileType>> it = files.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<File,FileType> pair= (Map.Entry<File,FileType>)it.next();
			if (pair.getValue() == FileType.CLASS) {
				File f = pair.getKey();
				parseClass(f);
			}	
		}
	}
	
	/**
	 * Loops through the map of files and grabs those that are objects
	 * then calls parseAreaDef on them
	 */
	public static void getObjects() {
		Iterator<Entry<File, FileType>> it = files.entrySet().iterator();
		int methodsCreated = 0;
		int objectsCreated = 0;
		String body = "";
		while (it.hasNext()) {
			Map.Entry<File,FileType> pair= (Map.Entry<File,FileType>)it.next();
			if (pair.getValue() == FileType.OBJECT) {
				if(objectsCreated > 100){
					body = "\t\taddMoreObjects" + methodsCreated + "();\n";
					body += "\t}\n\n";
					body += "\tprivate void addMoreObjects"+methodsCreated+"() {\n";
					methodsCreated++;
					objectsCreated = 0;
				}
				File f = pair.getKey();
				parseObject(f, body);
				body = "";
				objectsCreated++;
			}	
		}
	}
	
	public static String removePathFromString(String theString) {
		if (theString.contains(packageName) && !packageName.equals("."))
			theString = theString.replace(packageName, "");
		else if (theString.contains("brahms.base."))
			theString = theString.replace("brahms.base.", "");
		return theString;
	}
}
