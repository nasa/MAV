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
import java.util.HashMap;
import java.util.Map;

public class CreateMain {
	FileWriter mainFileStream = null;
	BufferedWriter out = null;
	String modelName = "";
	String destination;
	
	int groupNum = 0;
	int agentNum = 0;
	int classNum = 0;
	int objectNum = 0;
	
	Map<String, String> classList;
	
	public CreateMain(String modelName, String destination) {
		classList = new HashMap<String, String>();
		this.modelName = modelName;
		this.destination = destination;
		initDriver();
	}
	
	public void initDriver() {
		String driverBody = "package " + modelName + ";\n";
		driverBody += "import java.io.*;\n";
		driverBody += "import java.util.*;\n";
		driverBody += "import gov.nasa.arc.brahms.model.*;\n";
		driverBody += "import gov.nasa.arc.brahms.model.concept.*; \n";
		driverBody += "import gov.nasa.arc.brahms.simulator.world.*;\n";
		driverBody += "import gov.nasa.arc.brahms.simulator.*; \n";
		driverBody += "import gov.nasa.arc.brahms.model.expression.*;\n\n";
		driverBody += "import gov.nasa.arc.brahms.utilities.*;\n";
		driverBody += "import gov.nasa.arc.brahms.utilities.Calendar;\n";
		driverBody += "import gov.nasa.arc.brahms.simulator.Utils;\n\n";
		driverBody += "@SuppressWarnings(\"unused\")\n\n";

		//class declaration
		driverBody += "public class " + modelName +"Main extends MultiAgentSystem {\n\n";
		//initialize method
		driverBody += "\t\tpublic static String modelName = \""+modelName+"\";\n\n";
		driverBody += "\tpublic void initialize() { \n";
		driverBody += "\t\tSystem.out.println(\"**!**!**!**";
		driverBody += modelName + "**!**!**!**\"); \n";
		driverBody += "\t\tglobalClock = 0;\n\n";
		driverBody += "\t\t/*** Initialize ***/\n";
		driverBody += "\t\tinitGeography();\n";
		driverBody += "\t\tinitGroups();\n";
		driverBody += "\t\tinitAgents();\n";
		driverBody += "\t\tinitClassesObjects();\n\n";
		driverBody += "\t\t/*** Setup ***/\n";
		driverBody += "\t\tsetupGroups();\n";
		driverBody += "\t\tsetupAgents();\n";
		driverBody += "\t\tsetupClasses();\n";
		driverBody += "\t\tsetupObjects();\n\n";
		driverBody += "\t\tupdateTheTypeMap();\n\t";
		driverBody += "\t\tremoveAllParentBeliefs();\n";
		driverBody += "\t\tremoveAllParentFacts();\n\t}\n\n";
		
		//initGeography method
		driverBody += "\tprivate void initGeography() {\n";
		driverBody += "\t\t"+modelName+"Geography g = new "+modelName+"Geography(); \n";
		driverBody += "\t\tg.addAreaDefs(allAreaDefs); \n";
		driverBody += "\t\t//Set up all areadefs\n";
		driverBody += "\t\tIterator<AreaDef> adItr = allAreaDefs.iterator();\n";
		driverBody += "\t\twhile(adItr.hasNext()) {\n";
		driverBody += "\t\t\tAreaDef ad = adItr.next();\n";
		driverBody += "\t\t\tad.addElements();\n\t\t}\n";
		driverBody += "\t\tadItr = allAreaDefs.iterator();\n";
		driverBody += "\t\twhile(adItr.hasNext()) {\n";
		driverBody += "\t\t\tAreaDef ad = adItr.next();\n";
		driverBody += "\t\t\tad.inheritFromAreaDefs(allAreaDefs);\n\t\t}\n";
		driverBody += "\t\tg.addAreas(allAreas); \n";
		driverBody += "\t\tIterator<Area> arItr = allAreas.iterator();\n";
		driverBody += "\t\twhile(arItr.hasNext()) {\n";
		driverBody += "\t\t\tArea ar = arItr.next();\n";
		driverBody += "\t\t\tar.inheritFromInst(allAreaDefs);\n\t\t}\n";
		driverBody += "\t\tg.addPaths(allPaths); \n";
		driverBody += "\t\tif (Simulator.DEBUG) { \n";
		driverBody += "\t\t\tSystem.out.println(allAreaDefs.toString()); \n";
		driverBody += "\t\t\tSystem.out.println(allAreas.toString()); \n";
		driverBody += "\t\t\tSystem.out.println(allPaths.toString()); \n";
		driverBody += "\t\t}\n\t}\n\n";
		try {
			File tmp = new File(destination + modelName + "/" + modelName + "Main.java");
			if (!tmp.exists()) {
				try {
					tmp.createNewFile();
				} catch (IOException ioe) {
				    ioe.printStackTrace(); 
				}
			}
			
			mainFileStream = new FileWriter(destination + modelName + "/" + modelName + "Main.java");
			out = new BufferedWriter(mainFileStream);
			out.write(driverBody);
		}catch (Exception e){//Catch exception if any
			System.err.println("Error in Create Main: " + e.getMessage());
		}
	}
	
	public void initGroups() {
		String body = "\t/**\n\t * Initializes all groups\n\t */ \n";
		body += "\tprivate void initGroups() {\n";
		try {
			out.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addGroup(String name) {
		String body = "\t\tGroup group" + groupNum + " = new " + name + "(); \n";
		body += "\t\tallGroups.add(group" + groupNum++ + "); \n \n";
		try {
			out.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addClass(String name) {
		classList.put(name, "class" + classNum);
		String body = "\t\tClass_b class" + classNum + " = new " + name + "(); \n";
		body += "\t\tallClasses.put(\"" + name + "\", class" + classNum++ + "); \n \n";
		try {
			out.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initAgents() {
		String body = "\t}\n\n\t/**\n\t * Initializes all agents\n\t */\n";
		body += "\tprivate void initAgents() {\n";
		try {
			out.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addAgent(String name) {
		String body = "\t\tAgent agent" + agentNum + " = new " + name + "(); \n";
		body += "\t\t this.addAgents(agent" + agentNum++ + "); \n \n";
		try {
			out.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initClassesObjects() {
		String body = "\t}\n\n\t/**\n\t * Initializes all classes, conceptual" +
				"classes, objects, conceptual objects\n\t * Note: they're " +
				"grouped together because objects rely on classes to be\n" +
				"\t * initialized\n\t */\n";
		body += "\tprivate void initClassesObjects() {\n";
		try {
			out.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
		addClass("BaseConceptualClass");
		addClass("Calendar");
		addClass("CalendarUtilClass");
		addClass("SerializableObject");
	}
	
	public void addConceptClass(String className, String idName) {
		classList.put(className, "class" + classNum);
		String body = "\t\tConceptual_Class class" + classNum + " = new " + 
				className + "(); \n";
		body += "\t\tallConceptClasses.put(\"" + idName + "\", " + classNum++ 
				+ "); \n \n";
		try {
			out.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addObject(String name, String inst, String text) {
		if(inst.contains("-"))
			inst = inst.replaceAll("-", "_");
		String body = text + "\t\tObject_b object" + objectNum + " = new " + name + 
				"(allClasses.get(\"" + inst + "\")); \n";
		body += "\t\t this.addObjects(object" + objectNum++ + "); \n \n";
		try {
			out.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addConceptObject(String className, String idName) {
		String body = "\t\tConceptual_Object object" + objectNum + " = new " + 
				className + "(); \n";
		body += "\t\tallConceptObjects.add(object" + objectNum++ + "); \n \n";
		try {
			out.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	  * Adds the agent iterator to add all elements and facts
	  */
	public void addAgentElems() {
		/*Iterator<Agent> agtItr = allAgents.iterator();
	    while(agtItr.hasNext()) {
	        Agent ag = agtItr.next();
	        ag.addElements(); //adds attributes, etc
	        FactSet.addInitialFacts(ag, ag.getFacts());
	    }*/
		String body = "Iterator<Agent> agItr = allAgents.iterator(); \n";
		body += "while(agItr.hasNext()) { \n \t Agent ag = agItr.next(); \n";
		body += "\t ag.addElements(); \n } \n \n";
		try {
			out.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**
	  * Adds the agent iterator to add all elements and facts
	  */
	public void addObjectElems() {
		String body = "Iterator<Object_b> objItr = allObjects.iterator(); \n";
		body += "while(objItr.hasNext()) { \n \t Object_b b = objItr.next(); \n";
		body += "\t b.addElements(); \n } \n \n";
		try {
			out.write(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String setup() {
		String body = "\t/**\n\t * Adds elements to all groups, then inherits\n";
		body +="\t */\n\tprivate void setupGroups() {\n\t//Set up all groups\n";
		body +=	"\t\tIterator<Group> gpItr = allGroups.iterator();\n";
		body += "\t\twhile(gpItr.hasNext()) {\n\t\t\tGroup gp = gpItr.next();\n";
		body +=	"\t\t\tgp.addElements();\n\t\t}\n\n\t\t//groups inherit from ";
		body += "each other (must be done separately from setup,\n\t\t";
		body +=	"//since iterators are non-deterministic, and we don't know ";
		body += "which groups\n\t\t//are parents of the others)\n\t\t";
		body += "gpItr = allGroups.iterator();\n\t\twhile(gpItr.hasNext()) {\n";
		body += "\t\t\tGroup gp = gpItr.next();\n\t\t\tif (Simulator.DEBUG)\n";
		body += "\t\t\t\tSystem.out.println(\"gp inheriting: \" + ";
		body += "gp.getName());\n\t\t\tgp.inheritFromMembers(allGroups);\n";
		body += "\t\t}\n\t}\n";

		body += "\t/**\n\t * Adds elements to all agents and inherits from ";
		body += "groups\n\t */\n\tprivate void setupAgents() {\n\t\t//set up ";
		body += "all agents & inherit from groups\n\t\tIterator<Agent> agtItr";
		body += " = allAgents.iterator();\n\t\twhile(agtItr.hasNext()) {\n\t\t";
		body +=	"\tAgent ag = agtItr.next();\n\t\t\tag.addElements();\n\t\t\t";
		body += "if (Simulator.DEBUG)\n\t\t\t\tSystem.out.println(\"ag ";
		body += "inheriting: \" + ag.getName());\n\t\t\t";
		body += "ag.inheritFromMembers(allGroups);\n\t\t\tif (Simulator.DEBUG)\n";
		body += "\t\t\t\tSystem.out.println(\"AGENTSPECS: \" + ag.toString());\n";
		body += "\t\t}\n\t}\n\n";

		body += "\t/**\n\t * Adds elements to all classes, conceptual classes\n";
		body += "\t * Classes inherit, conceptual classes inherit\n\t */\n\t";
		body += "private void setupClasses() {\n\t\t//set up all classes\n\t\t";
		body +=	"for (Map.Entry<String, Class_b> entry : allClasses.entrySet()) {\n";
		body +=	"\t\t\tClass_b cls = (entry.getValue());\n\t\t\tcls.addElements();\n";
		body += "\t\t}\n\n\t\t//set up all conceptual classes\n\t\tfor (Map.";
		body += "Entry<String, Conceptual_Class> entry : allConceptClasses.";
		body += "entrySet()) {\n\t\t\tConceptual_Class ccls = (entry.getValue());\n";
		body += "\t\t\tccls.addElements();\n\t\t}\n\n\t\t//all classes inherit\n";
		body += "\t\tfor (Map.Entry<String, Class_b> entry : allClasses.entry";
		body += "Set()) {\n\t\t\tClass_b cls = (entry.getValue());\n\t\t\tif ";
		body += "(Simulator.DEBUG)\n\t\t\t\tSystem.out.println(\"cls inheriti";
		body += "ng: \" + cls.getName());\n\t\t\tcls.inheritFromClasses(allCl";
		body += "asses);\n\t\t}\n\n\t\t//all conceptual classes inherit\n\t\t";
		body +=	"for (Map.Entry<String, Conceptual_Class> entry : \n\t\t\t";
		body +=	"allConceptClasses.entrySet()) {\n\t\t\tConceptual_Class ccls";
		body += " = (entry.getValue());\n\t\t\tif (Simulator.DEBUG)\n\t\t\t\t";
		body += "System.out.println(\"ccls inheriting: \" + ccls.getName());\n";
		body +=	"\t\t\tccls.inheritFromClasses(allConceptClasses);\n\t\t}\n\t}\n";

		body += "\t/**\n\t * Adds all elements to objects, conceptual objects\n";
		body += "\t * Conceptual objects inherit, objects inherit\n\t */\n";
		body += "\tprivate void setupObjects() {\n\t\t//set up all objects\n";
		body += "\t\tIterator<Object_b> objItr = allObjects.iterator();\n";
		body += "\t\twhile(objItr.hasNext()) {\n\t\t\tObject_b b = objItr.nex";
		body += "t();\n\t\t\tb.addElements();\n\t\t}\n\t\t//set up all concep";
		body += "tual objects\n\t\tIterator<Conceptual_Object> cobjItr = all";
		body += "ConceptObjects.iterator();\n\t\twhile(cobjItr.hasNext()) {\n";
		body += "\t\t\tConceptual_Object b = cobjItr.next();\n\t\t\tb.addElem";
		body += "ents();\n\t\t}\n\t\t//all conceptual objects inherit\n\t\t";
		body +=	"cobjItr = allConceptObjects.iterator();\n\t\twhile(cobjItr.";
		body += "hasNext()) {\n\t\t\tConceptual_Object b = cobjItr.next();\n";
		body += "\t\t\tif (Simulator.DEBUG)\n\t\t\t\tSystem.out.println(\"cob";
		body += "j inheriting: \" + b.getName());\n\t\t\tb.inheritFromClasses";
		body += "(allConceptClasses);\n\t\t}\n\t\t//all objects inherit\n\t\t";
		body += "objItr = allObjects.iterator();\n\t\twhile(objItr.hasNext()) {\n";
		body += "\t\t\tObject_b b = objItr.next();\n\t\t\tif (Simulator.DEBUG)\n";
		body += "\t\t\t\tSystem.out.println(\"obj inheriting: \" + b.getName(";
		body += "));\n\t\t\tb.inheritFromClasses(allClasses);\n\t\t}\n\t}\n\n";

		body += "\t/**\n\t * Removes facts for all groups and classes after t";
		body += "hey've been inherited\n\t */\n\tprivate void removeAllParent";
		body += "Facts() {\n\t\tList<String> keysToRemove = new ArrayList<Str";
		body += "ing>();\n\t\tMap<String, List<Fact>> facts = FactSet.getFact";
		body += "s();\n\t\tfor (Map.Entry<String, List<Fact>> entry : facts";
		body += ".entrySet()) {\n\t\t\tentry.getKey();\n\t\t\t//look for ";
		body += "group facts\n\t\t\tIterator<Group> gpItr = allGroups.itera";
		body += "tor();\n\t\t\t";
		
		
		body += "while (gpItr.hasNext()) {\n\t\t\t\tGroup gp = gpItr.next()";
		body += ";\n\t\t\t\tif (entry.getKey().startsWith(gp.getName() + ";
		body += "\".\"))\n\t\t\t\t\tkeysToRemove.add(entry.getKey());\n\t";
		body += "\t\t\tif (entry.getKey().startsWith(gp.getName() + \" \"))\n";
		body += "\t\t\t\t\tkeysToRemove.add(entry.getKey());\n\t\t\t}\n";
		body += "\t\t\t//look for class facts\n\t\t\tfor( Map.Entry<Strin";
		body += "g, Class_b> clsEntry : allClasses.entrySet()) {\n\t\t\t\t";
		body += "Class_b cls = (clsEntry.getValue());\n\t\t\t\tif (entry.ge";
		body += "tKey().startsWith(cls.getName() + \".\"))\n\t\t\t\t\t";
		body += "keysToRemove.add(entry.getKey());\n\t\t\t\tif (entry.getKe";
		body += "y().startsWith(cls.getName() + \" \"))\n\t\t\t\t\t";
		body += "keysToRemove.add(entry.getKey());\n\t\t\t}\n\t\t}\n";
		body += "\t\t//remove all group/class facts\n\t\tfor (int i = 0; i < ";
		body += "keysToRemove.size(); i++) {\n\t\t\tFactSet.removeFact(keysTo";
		body += "Remove.get(i));\n\t\t}\n\t}\n";
		
		body += "\t/**\n\t * Removes beliefs for all groups and classes after";
		body += "they've been inherited\n\t */\n\tprivate void removeAllParen";
		body += "tBeliefs() {\n\t\tIterator<Group> gpItr = allGroups.iterator";
		body += "();\n\t\twhile (gpItr.hasNext()) {\n\t\t\tGroup gp = gpI";
		body += "tr.next();\n\t\t\tMap<String, List<Belief>> beliefs = gp.g";
		body += "etBeliefs();\n\t\t\tbeliefs.clear();\n\t\t}\n\t\t";
		body +=	"for (Map.Entry<String, Class_b> clsEntry : allClasses.entryS";
		body += "et()) {\n\t\t\tClass_b cls = (clsEntry.getValue());\n\t";
		body += "\t\tMap<String, List<Belief>> beliefs = cls.getBeliefs();\n";
		body += "\t\t\tbeliefs.clear();\n\t\t}\n\t}\n\n";
		return body;
	}
	
	public void closeMain() {
		String body = "\t}\n\n"; //close initialize
		//setup
		body += setup();
		
		//add main method
		body += "\tpublic static void main(String[] args) { \n";
		body += "\t\tlong startTime = System.currentTimeMillis(); \n";
		body += "\t\t" + modelName +"Main m = new " + modelName + "Main(); \n";
		body += "\t\tm.initialize(); //sets up geography & inits agents \n";
		body += "\t\tUtils.setUpMas(m); \n";
		body += "\t\t//set up the distance matrix \n";
		body += "\t\tUtils.setUpPaths(); \n";
		body += "\t\tSimulator.startSim(m); \n";
		
		body += "\t\t long simTime = (System.currentTimeMillis() - startTime)/1000; \n";
		body += "\t\tSystem.out.println(\"**!**!**!**";
		body += modelName + "Simulation Compete in time \" + simTime + \" seconds **!**!**!**\"); \n";
		
		body += "\t}\n\n";
		//add printFinalValues method
		body += "\tprivate void printFinalValues() { \n";
		body += "\t\tIterator<Agent> agItr = allAgents.iterator(); \n";
		body += "\t\twhile(agItr.hasNext()) { \n";
		body += "\t\t\tAgent ag = agItr.next(); \n";
		body += "\t\t\tMap<String, List<Belief>> beliefs = ag.getBeliefs(); \n";
		body += "\t\t\tIterator<String> bItr = beliefs.keySet().iterator(); \n";
		body += "\t\t\tSystem.out.println(\"--------------------------------------\");\n";
		body += "\t\t\tSystem.out.println(\"ag_ :\" + ag.getName()); \n";
		body += "\t\t\twhile(bItr.hasNext()) { \n";
		body += "\t\t\t\tString key = bItr.next(); \n";
		body += "\t\t\t\tList<Belief> b = beliefs.get(key); \n";
		body += "\t\t\t\tfor (int i = 0; i < b.size(); i++) { \n";
		body += "\t\t\t\t\tExpression exp  = b.get(i).getBelief(); \n";
		body += "\t\t\t\t\tString valRel = \"\";\n";
		body += "\t\t\t\t\tif (exp instanceof RelationalExpression)\n";
		body += "\t\t\t\t\t\tvalRel = \" \" + ((RelationalExpression) exp).getRhsObjRef() + \" is \" + ((RelationalExpression) exp).getTruthVal();\n";
		body += "\t\t\t\t\telse if (exp instanceof ValueExpression)\n";
		body += "\t\t\t\t\t\tvalRel = \"=\" + exp.getValue(ag, null).get(0).toString();\n";
		body += "\t\t\t\t\telse\n";
		body += "\t\t\t\t\t\tvalRel = \"=\" + ((MapExpression) exp).toString();\n";
		body += "\t\t\t\t\tSystem.out.println(key + valRel); \n";
		body += "\t\t\t\t}\n";
		body += "\t\t\t} \n ";
		body += "\t\t\tSystem.out.println(\"--------------------------------------\");\n";
		body +=	"\t\t}\n";
		body += "\t\tIterator<Object_b> objItr = allObjects.iterator(); \n";
		body += "\t\twhile(objItr.hasNext()) { \n";
		body += "\t\t\tObject_b ag = objItr.next(); \n";
		body += "\t\t\tMap<String, List<Belief>> beliefs = ag.getBeliefs(); \n";
		body += "\t\t\tIterator<String> bItr = beliefs.keySet().iterator(); \n";
		body += "\t\t\tSystem.out.println(\"--------------------------------------\");\n";
		body += "\t\t\tSystem.out.println(\"ag_ :\" + ag.getName()); \n";
		body += "\t\t\twhile(bItr.hasNext()) { \n";
		body += "\t\t\t\tString key = bItr.next(); \n";
		body += "\t\t\t\tList<Belief> b = beliefs.get(key); \n";
		body += "\t\t\t\tfor (int i = 0; i < b.size(); i++) { \n";
		body += "\t\t\t\t\tExpression exp  = b.get(i).getBelief(); \n";
		body += "\t\t\t\t\tString valRel = \"\";\n";
		body += "\t\t\t\t\tif (exp instanceof RelationalExpression)\n";
		body += "\t\t\t\t\t\tvalRel = \" \" + ((RelationalExpression) exp).getRhsObjRef();\n";
		body += "\t\t\t\t\telse if (exp instanceof ValueExpression)\n";
		body += "\t\t\t\t\t\tvalRel = \"=\" + exp.getValue(ag, null).get(0).toString();\n";
		body += "\t\t\t\t\telse\n";
		body += "\t\t\t\t\t\tvalRel = \"=\" + ((MapExpression) exp).toString();\n";
		body += "\t\t\t\t\tSystem.out.println(key + valRel); \n";
		body += "\t\t\t\t}\n";
		body += "\t\t\t} \n ";
		body += "\t\t\tSystem.out.println(\"--------------------------------------\");\n";
		body +=	"\t\t}\n";
		body += "\t\tSystem.out.println(\"World Facts:\");\n";
		body += "\t\tFactSet.printFacts();\n";
		
		body += "\t}\n";
		body += "}\n"; //close the class
		try {
			out.write(body);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
