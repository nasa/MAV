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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;

/**
 * A program to retrieve the model files and parse them for XML It searches for
 * files under Applications/Brahms/Brahms/Projects/<modelName> It then gets
 * <modelName>.bmd, gets the package name stored within it, and searches for the
 * bcc files under build/<thePackagePath> (replacing . with /) The following
 * error is normal (because it cannot parse the file .DS_Store) [Fatal Error]
 * .DS_Store:1:1: Content is not allowed in prolog.
 *
 * @author jhunter
 */
public class GetFiles {

	private String packageName;
	public boolean DEBUG = true;

	String destination;
	String nameOfModel;
	String mainPath;
	String brahmsBuildDir;

	String bmdFile;

	static public Config config;

	Map<File, FileType> files;

	public GetFiles(String propFile) {

		config = new Config(propFile);
		destination = config.getStringProperty("destination.path");
		nameOfModel = config.getStringProperty("source.model");
		mainPath = config.getStringProperty("source.path");
		
		this.packageName = config.containsKey("brahms.package.name")? 
				config.getStringProperty("brahms.package.name") : "UNDEF";
				
		this.brahmsBuildDir = config.containsKey("brahms.build.dir")?
				config.getStringProperty("brahms.build.dir") : "UNDEF";

		bmdFile = mainPath.concat(nameOfModel + ".bmd");

		if (DEBUG)
			System.out.println("bmdFile is: " + bmdFile);

		files = new HashMap<File, FileType>();

		if (DEBUG) {
			System.out.println("destination.path: " + destination);
			System.out.println("source.model: " + nameOfModel);
			System.out.println("source.path: " + mainPath);
			System.out.println("brahms.package.name: " + packageName);
			System.out.println("brahms.build.dir: " + brahmsBuildDir);
		}

	}

	@Deprecated
	public void parseBMDFile(File bmdFile) {
		System.out.println("DEPRECATED: add brahms.package.name and brahms.build.dir to config");
		File[] fileList = parseBMD(bmdFile);

		for (int i = 0; i < fileList.length; i++) {
			FileType type = getType(fileList[i]);
			// System.out.println(fileList[i].toString());
			if (type != null)
				files.put(fileList[i], type);
		}
		ParseXML.parseXMLFiles(files, nameOfModel, mainPath, packageName, destination);
	}

	protected void buildFileList() {
		FileSearch fileSearch = new FileSearch();
		List<File> brhamsFileList = fileSearch.initSearch(this.brahmsBuildDir);

		for (File i : brhamsFileList) {
			FileType type = getType(i);
			if (type != null) {
				this.files.put(i, type);
			}
		}
	}

	public void generateJavaFiles() {
		if (this.packageName.equals("UNDEF") || this.brahmsBuildDir.equals("UNDEF")) {
			File bmdFile = new File(this.bmdFile);
			parseBMDFile(bmdFile);	
		}
		else {
			buildFileList();			
		} 
		
		ParseXML.parseXMLFiles(this.files, this.nameOfModel, this.mainPath, 
							   this.packageName, this.destination);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// System.out.println("working");
		String propFile;
		if (args.length == 1) {
			propFile = args[0];
		} else {
			propFile = "global.properties";
		}
		GetFiles gf = new GetFiles(propFile);
		gf.generateJavaFiles();
		System.out.println("Parsing completed");
	}

	/**
	 * Get the type of the bcc file (should be areadef, area, path, agent,
	 * group, class, object, or model) (conceptual class/obj?)
	 * 
	 * @param
	 * @return
	 */
	public FileType getType(File f) {
		Document d = null;
		if (!(f.exists())) {
			System.out.println("Error: file " + f.toString() + "does not exist");
		}
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			d = db.parse(f);
		} catch (java.io.IOException e) {
			// System.out.println("can't find the file \""+ f.toString() +
			// "\"");
			System.out.println(e);
		} catch (Exception e) {
			System.out.println("problem parsing file " + f.getName());
		}
		if (d != null) {
			Element root = d.getDocumentElement();
			String type = root.getNodeName();
			if (type.equals("AREADEF"))
				return FileType.AREADEF;
			else if (type.equals("AREA"))
				return FileType.AREA;
			else if (type.equals("PATH"))
				return FileType.PATH;
			else if (type.equals("GROUP"))
				return FileType.GROUP;
			else if (type.equals("AGENT"))
				return FileType.AGENT;
			else if (type.equals("CLASS"))
				return FileType.CLASS;
			else if (type.equals("OBJECT"))
				return FileType.OBJECT;
			else if (type.equals("CONCEPTUALOBJECT"))
				return FileType.CONCEPTUALOBJECT;
			else if (type.equals("CONCEPTUALCLASS"))
				return FileType.CONCEPTUALCLASS;
		}
		return null;
	}

	/**
	 * Gets the path to the set of xml files for the model
	 * 
	 * @param model
	 * @return
	 */

	// public String getMainPath(String model) {
	public File[] parseBMD(File bmdFile) {
		Document doc = null;
		List<File> files = new ArrayList<File>();
		if (!(bmdFile.exists())) {
			System.out.println("bmd file does not exist");
			System.exit(1);
		}
		try { // get file open and parsed
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(bmdFile);
		} catch (java.io.IOException ex) {
			System.out.println("can't find the file " + bmdFile.getName());
			System.out.println(ex.getMessage());
		} catch (Exception ex) {
			System.out.println("problem parsing file: " + bmdFile.getName());
		}

		String buildDir = "";
		if (doc != null) {
			// int j = 0;
			Element root = doc.getDocumentElement();
			// Element d = (Element)buildDirs.item(i);
			buildDir = root.getAttribute("builddir");
			FileSearch fileSearch = new FileSearch();
			files = fileSearch.initSearch(buildDir);
			packageName = root.getAttribute("package");
		}
		
		if (DEBUG) {
			System.out.println("brahms.build.dir (from" + this.bmdFile + "): " + buildDir);
			System.out.println("brahms.package.name (" + this.bmdFile + "): " + this.packageName);
		}
		
		// return path;
		File[] theFiles = new File[files.size()];
		files.toArray(theFiles);
		return theFiles;
	}
}
