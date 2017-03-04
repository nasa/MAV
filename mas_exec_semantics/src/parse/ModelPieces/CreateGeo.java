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
import java.io.FileWriter;
import java.io.IOException;

public class CreateGeo {
	String modelName, destination;
	FileWriter geoStream = null;
	BufferedWriter geoOut = null;
	
	//vars for area def
	int areaDefNum = 0;
	int extendsNum = 0;
	
	//vars for area
	int areaNum = 0;
	
	//vars for path
	int pathNum = 0;
	
	/**
	 * Sets the modelName for the Geography file
	 * and calls initGeography
	 * @param modelName
	 * @throws IOException
	 */
	public CreateGeo(String modelName, String destination) {
		this.modelName = modelName;
		this.destination = destination;
		initGeography();
	}
	
	/**
	 * Initializes package with the modelName
	 * imports the necessary packages
	 * starts the class and the 'addAreaDefs' method
	 * @throws IOException
	 */
	public void initGeography() {
		String geoBody = "package " + modelName + ";\n";
		geoBody += "import gov.nasa.arc.brahms.model.concept.*; \n";
		geoBody += "import java.util.*; \n\n";
		geoBody += "public class " + modelName + "Geography { \n";
		geoBody += "public void addAreaDefs(Set<AreaDef> areaDefs) { \n";
		try {
			//File tmp = new File(destination + modelName + "/Main.java");
			//if (!tmp.exists())
			//	tmp.createNewFile();
			geoStream = new FileWriter(destination + modelName + "/" + modelName + "Geography.java");
			geoOut = new BufferedWriter(geoStream);
			geoOut.write(geoBody);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Appends a new areaDef to the file
	 * @param name
	 * @param display
	 * @param extendsArea
	 * @throws IOException
	 */
	public void addNewAreaDef(String name, String display, String[] extendsArea) {
		/*
		 * String areaDef0Name = "House";
		 * String areaDef0Extends = "BaseAreaDef";
		 * Set<String> areaDef0AllExtends = new HashSet<String>();
		 * areaDef0AllExtends.add(areaDef0Extends);
		 * AreaDef areaDef0 = new AreaDef(areaDef0Name, areaDef0Name,
				areaDef0AllExtends);
		 * areaDefs.add(areaDef0);*/
		
		String body = "String areaDefName" + areaDefNum + " = \"" + name
				+ "\"; \n";
		body += "String areaDefDisplay" + areaDefNum + " = \"" + display
				+ "\"; \n";
		body += "Set<String> areaDefAllExtends" + areaDefNum
				+ " = new HashSet<String>(); \n";
		//loop through all extends and add to AllExtends
		for (int i = 0; i < extendsArea.length; i++) {
			if (extendsArea[i] != null) {
				body += "String areaExtends" + extendsNum + " = \""
						+ extendsArea[i] + "\"; \n";
				body += "areaDefAllExtends" + areaDefNum + ".add(areaExtends"
						+ extendsNum++ + "); \n";
			}
		}
		body += "AreaDef areaDef" + areaDefNum + " = new AreaDef(areaDefName"
				+ areaDefNum + ", areaDefDisplay" + areaDefNum
				+ ", areaDefAllExtends" + areaDefNum + "); \n";
		body += "areaDefs.add(areaDef" + areaDefNum++ + "); \n \n";
		try {
			geoOut.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes off AreaDefs and inits areas
	 * @throws IOException
	 */
	public void initAreas() {
		String body = "} \n public void addAreas(Set<Area> areas) { \n";
		try {
			geoOut.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Appends a new area to the file
	 * @param name
	 * @param display
	 * @param instanceOf
	 * @param partOf - null signified by ""
	 * @throws IOException
	 */
	public void addNewArea(String name, String display, String instanceOf,
			String partOf) {
		/*
		 * String area0Name = "Geography";
		 * String area0InstanceOf = "World";
		 * Area area0 = new Area(area0Name, area0Name,
							area0InstanceOf);
		 * area0.setID(0);
		 * areas.add(area0);
		 */
		String body = "String areaName" + areaNum + " = \"" + name
				+ "\"; \n";
		body += "String areaDisplay" + areaNum + " = \"" + display
				+ "\"; \n";
		body += "String areaInstanceOf" + areaNum + " = \"" + instanceOf
				+ "\"; \n";
		if (partOf.equals(""))
			body += "Area area" + areaNum + " = new Area(areaName"
			+ areaNum + ", areaDisplay" + areaNum
			+ ", areaInstanceOf" + areaNum + "); \n";
		else {
			body += "String areaPartOf" + areaNum + " = \"" + partOf
					+ "\"; \n";
			body += "Area area" + areaNum + " = new Area(areaName"
				+ areaNum + ", areaDisplay" + areaNum
				+ ", areaInstanceOf" + areaNum + ", areaPartOf"
				+ areaNum + "); \n";
		}
		body += "area" + areaNum + ".setID(" + areaNum + "); \n";
		body += "areas.add(area" + areaNum++ + "); \n \n";
		try {
			geoOut.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Closes off Areas and inits paths
	 * @throws IOException
	 */
	public void initPath() {
		String body = "} \n public void addPaths(Set<Path> paths) { \n";
		try {
			geoOut.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addNewPath(String name, String display, String area1,
			String area2, String distance) {
		/*String path0Name = "chair_to_from_medCabinet";
		String path0Area1 = "chair";
		String path0Area2 = "medCabinet";
		int path0Distance = 10;
		
		Path path0 = new Path(path0Name, path0Name, path0Area1,
				path0Area2, path0Distance);
		paths.add(path0);*/
		String body = "String pathName" + pathNum + " = \"" + name
				+ "\"; \n";
		body += "String pathDisplay" + pathNum + " = \"" + display
				+ "\"; \n";
		body += "String area1Path" + pathNum + " = \"" + area1 + "\"; \n";
		body += "String area2Path" + pathNum + " = \"" + area2 + "\"; \n";
		body += "int pathDistance" + pathNum + " = " + distance
				+ "; \n";
		body += "Path path" + pathNum + " = new Path(pathName" + pathNum
				+ ", pathDisplay" + pathNum + ", area1Path"
				+ pathNum + ", area2Path" + pathNum + ", pathDistance"
				+ pathNum + "); \n";
		body += "paths.add(path" + pathNum++ + "); \n \n";
		try {
			geoOut.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Closes off paths, closes the class
	 * closes stream and writes the output to the file
	 * @throws IOException
	 */
	public void closeStream() {
		String body = "} \n } \n";
		try {
			geoOut.write(body);
			geoOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
