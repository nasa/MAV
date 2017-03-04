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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import ConceptPieces.Attribute;
import ConceptPieces.Belief;
import ConceptPieces.Activity;
import ConceptPieces.Frame;
import ConceptPieces.Guard;
import ConceptPieces.Event;
import ConceptPieces.Detect;
import ConceptPieces.VariableParse;
import ConceptPieces.VarQuant;
import ConceptPieces.Parameter;
import blocks.GetFiles;
import parseElements.ParseActs;
import parseElements.ParseBeliefsFacts;
import parseElements.ParseFrames;


public class groupAgentClassObjectMethods {
	String name, modelName, destination;
	File file;
	FileWriter stream;
	BufferedWriter out;
	String packageName;
	String packageAndAgent;
	Element root;
	List<String> activityList = new ArrayList<String>();
	int exp = 0;
	int comp = 0;
	int ev = 0; //event & conclude
	int numOfWf = 0;
	int numOfTf = 0;
	String classification;
	int actNum;
	boolean DEBUG = false;
	Attribute theAttributes[] = null;
	Element currentFrame = null;
	

	/**
	 * Sets the modelName, the agentName
	 * and calls initAgent
	 * @param modelName, agentName
	 * @throws IOException
	 */
	public groupAgentClassObjectMethods(String modelName, String destination, String name, File file,
			String packageName, String classification) {
		this.modelName = modelName;
		this.destination = destination;
		this.name = name;
		this.file = file;
		this.packageName = packageName;
		this.packageAndAgent = packageName + name + ".";
		this.classification = classification;
	}
	
	public void init(String body){
		if(DEBUG)
			System.out.println("Creating " + classification + ": " + this.name);
		try {
			//File tmp = new File(modelName + "/Main.java");
			//if (!tmp.exists())
				//tmp.createNewFile();
			stream = new FileWriter(destination + modelName + "/" + name + ".java");
			out = new BufferedWriter(stream);
			//out.write(body);
		}catch (Exception e){//Catch exception if any
			System.err.println("Error creating classification: " + this.name + ": " + e.getMessage());
		}
		body += setLocation();
		body += getAtts();
		body += getRels();
		body += getBeliefs();
		body += getFacts();
		body += getActivities();
		body += getThoughtframes();
		body += getWorkframes();
		try{
			out.write(body);
			//Close the output stream
			out.write("} //End class");
			out.close();
		}
		catch (Exception e){
			System.err.println("Error creating agent class agent " + name + ": " + e.getMessage());
		}
		//Produce workload config file
	//	agent ag = new agent(name, comms, concs, detects,
		//		prims, workframes);
	}
	
	public String setLocation(){
		String body = "protected void addLocation() {\n";
		if(!root.getAttribute("location").equals("")){
			
			//body += "// " + root.getAttribute("location");
			String loc = root.getAttribute("location");
			if(loc.contains(".")){
				int index = loc.lastIndexOf(".") + 1;
				loc = loc.substring(index);
			}
			
			body += "ValueExpression locExpF = new ValueExpression(\""
					+ name + "\", \"" + "location"+
					"\", \n EvaluationOperator.EQ , " + " new TplObjRef(\"" + loc + "\")" + 
					");\n";
			body += "Term locTermF" + " = new Term(\"" + name +
					"\", \"" + "location" + "\");\n";
			body += "FactSet.addFact(locTermF, locExpF);\n\n";
			
			body += "ValueExpression locExpB = new ValueExpression(\""
					+ "current" + "\", \"" + "location"+
					"\", \n EvaluationOperator.EQ , " + " new TplObjRef(\"" + loc + "\")" + 
					");\n";
			body += "Term locTermB" + " = new Term(\"" + "current" +
					"\", \"" + "location" + "\");\n";
			body += "this.addBelief(locTermB, locExpB);\n\n";
			
			body += "}\n \n";
			
			return body;
		}
		else{
			body += "}\n \n";
			return body;
		}
	}
	
	public String getAtts() {
		Attribute[] atts = parseAtts(); //parse
		//write to file
		String body = "protected void addAttributes() {\n";
		for (int i = 0; i < atts.length; i++) {
			body += "Attribute attr" + i + " = new Attribute(\"" +
					atts[i].getName() + "\", \"" + atts[i].getPrivacy() +
					"\", \"" + atts[i].getType() + "\"); \n";
			body += "attributes.add(attr" + i + ");\n\n";
		}
		body += "}\n \n";

		return body;
	}
	
	public String getRels() {
		Attribute[] atts = parseRels(); //parse
		//write to file
		String body = "protected void addRelations() {\n";
		for (int i = 0; i < atts.length; i++) {
			body += "Relation rel" + i + " = new Relation(\"" +
					atts[i].getName() + "\", \"" + atts[i].getPrivacy() +
					"\", \"" + atts[i].getType() + "\"); \n";
			body += "relations.add(rel" + i + ");\n\n";
		}
		body += "}\n \n";

		return body;
	}
	
	public Attribute[] parseAtts() {
		Attribute[] atts;
		//parse file
		NodeList attList = root.getElementsByTagName("ATTRIBUTE");
		atts = new Attribute[attList.getLength()];
		for (int i = 0; i < attList.getLength(); i++) {
			Element attElem = (Element)attList.item(i);
			String attPriv = attElem.getAttribute("scope");
			String attType = attElem.getAttribute("type");
			String attName = attElem.getAttribute("name");
			attName = attName.replace(packageAndAgent, "");
			Attribute newAtt = new Attribute(attPriv, attType, attName);
			atts[i] = newAtt;
		}
		
		theAttributes = atts;
		
		return atts;
	}
	
	public Attribute[] parseRels() {
		Attribute[] rels;
		//parse file
		NodeList attList = root.getElementsByTagName("RELATION");
		rels = new Attribute[attList.getLength()];
		for (int i = 0; i < attList.getLength(); i++) {
			Element attElem = (Element)attList.item(i);
			String attPriv = attElem.getAttribute("scope");
			String attType = attElem.getAttribute("type");
			int idx = attType.lastIndexOf(".");
			attType = attType.substring(idx+1);
			String attName = attElem.getAttribute("name");
			attName = attName.replace(packageAndAgent, "");
			Attribute newAtt = new Attribute(attPriv, attType, attName);
			rels[i] = newAtt;
		}
		return rels;
	}
	
	public String getBF(Belief bf[], String bOrF, String bOrF2){
		String body = "protected void add"+bOrF+"() {\n";
		for (int i = 0; i < bf.length; i++) {
			if (bf[i].getExpType().equals("oav")) {
				String val = bf[i].getType();
				if (val.equals("int"))
					val = "IntegerValue(" + bf[i].getVal() + ")";
				if (val.equals("double"))
					val = "DoubleValue(" + bf[i].getVal() + ")";
				if (val.equals("boolean"))
					val = "BooleanValue(" + bf[i].getVal() + ")";
				if (val.equals("string"))
					val = "StringValue(\"" + bf[i].getVal() + "\")";
				if(val.equals("symbol"))
					val = "SymbolValue(\"" + bf[i].getVal() + "\")";
				if(val.equals("unknown") || val.equals("UNKNOWN")){
					val = " SglObjRef(\"unknown\")";
				}
				String evalOp = "";
				if(bf[i].getEvalOp().equals("uneq")){
					evalOp = "EvaluationOperator.NEQ";
				}
				else{
					evalOp = "EvaluationOperator.EQ";
				}
				
				if (bf[i].getIndex() == null) {
					//body += "//here \n";
					body += "Term t" + i + " = new Term(\"" + bf[i].getObj() +
							"\", \"" + bf[i].getAtt() + "\");\n";
					body += "ValueExpression valExp" + i + " = new ValueExpression(t" + i + ", \n"+evalOp+", new " + val + 
							");\n";
					body += bOrF2+"(t" + i + ", valExp" + i + ");\n\n";
				} else {
					body += "MapKeyValPair mappair" + i + " = new MapKeyValPair(new MapKey(";
					if (bf[i].getIndexType().equals("string")) {
						body += "\"" + bf[i].getIndex() + "\"";
					} else body += bf[i].getIndex();
					body += "), new " + val + ");\n";
					body += bOrF2+"(new Term(\"" + bf[i].getObj() 
							+ "\", \"" + bf[i].getAtt() + "\"), mappair" +
							i + ");\n\n";
				}
			} 
			else if(bf[i].getExpType().equals("oro")){ //Object Relation Object
				//body += "//bf[i].getObj() = " + bf[i].getObj() + "\n";
				//body += "//bf[i].getRel() = " + bf[i].getRel() + "\n";
				//body += "//bf[i].getRObj() = " + bf[i].getRObj() + "\n";
				body += "RelationalExpression relExp" + i + " = new RelationalExpression(\""
						+ bf[i].getObj() + "\", \n\"" + bf[i].getRel() +
						"\", \"" + bf[i].getRObj() + "\", " +
						bf[i].getTruthVal() + ");\n"; 
				body += bOrF2+"(\"" + bf[i].getObj() + " " + 
						bf[i].getRel() + "\", relExp" + i + ");\n\n";
			}
			else if(bf[i].getExpType().equals("oao")){ //Object operator Object
				if (bf[i].getIndex() == null) {
					String evalOp = "";
					if(bf[i].getEvalOp().equals("uneq")){
						evalOp = "EvaluationOperator.NEQ";
					}
					else{
						evalOp = "EvaluationOperator.EQ";
					}
					body += "Term t" + i + " = new Term(\"" + bf[i].getObj() +
							"\", \"" + bf[i].getAtt() + "\");\n";
					body += "ValueExpression valExp" + i + " = new ValueExpression(t"+i+", \n"+evalOp+
							", new SglObjRef(\"" + bf[i].getRObj() + "\"));\n";
					body += bOrF2+"(t" + i + ", valExp" + i + ");\n\n";	
				}
				else {
					body += "MapKeyValPair mappair" + i + " = new MapKeyValPair(new MapKey(";
					if (bf[i].getIndexType().equals("string")) {
						body += "\"" + bf[i].getIndex() + "\"";
					} else body += bf[i].getIndex();
					body += "), new SglObjRef(\"" + bf[i].getRObj() + "\"));\n";
					body += bOrF2+"(new Term(\"" + bf[i].getObj() 
							+ "\", \"" + bf[i].getAtt() + "\"), mappair" +
							i + ");\n\n";
				}
			}
		}
		body += "}\n \n";

		return body;
		
	}
	
	public String getBeliefs() {
		String body = "";
		Belief[] beliefs = ParseBeliefsFacts.parseBeliefs(root, packageName, name);
		body += getBF(beliefs, "Beliefs", "this.addBelief");
		return body;
	}
	
	public String getFacts() {
		String body = "";
		Belief[] facts = ParseBeliefsFacts.parseFacts(root, packageName, name);
		body += getBF(facts, "Facts", "FactSet.addFact");
		return body;
	}
	
	public String getActivities() {
		if(DEBUG)
			System.out.println("getting activities");

		String body = "";
		NodeList activities = root.getElementsByTagName("ACTIVITIES");
		if ((activities != null) && (activities.getLength() > 0)) {
			NodeList acts = activities.item(0).getChildNodes();
			Parameter[] params = new Parameter[0];
			body += getActivities(acts, "activities", params) + "\n";
		
		body += "protected void addActivities() {\n";
			for(int i = 0; i<activityList.size() ;i++){
				body += "addActivity"+activityList.get(i)+"();\n";
			}
		body += "} \n\n";	
			
		}

		return body;
	}
	
	public String getActivities(NodeList acts, String parent, Parameter[] parentParams) {
		String body = "";
		if(DEBUG)
			System.out.println("activities at level  " + actNum + ", " + acts.getLength());
		for (int i = 1; i < acts.getLength() - 1; i=i+2) {
			//body += "//Activites: " + acts.getLength() + "\n";
			Activity a = ParseActs.getActivity(acts.item(i), destination);
			
			//Used to identify if the activity uses values or parameters			
			boolean nameActParam = false;
			boolean dispActParam  = false;
			boolean maxActParam  = false;
			boolean minActParam  = false;
			boolean priorActParam  = false;
			boolean randActParam  = false;
			boolean destNameActParam = false;
			boolean locationActParam = false;
			boolean sourceActParam = false;
			String parameters = "";
			Parameter[] params;
			if(a.hasParams()){
				//Get activities parameters
				Parameter[] tempParams = a.getParams();
				int numOfParams = tempParams.length + parentParams.length;
				params = new Parameter[numOfParams];
				//Add two arrays together
				for(int j = 0 ;j<tempParams.length;j++){
					params[j] = tempParams[j];
				}
				for(int j = 0 ;j<parentParams.length;j++){
					params[j+tempParams.length] = parentParams[j];
				}
				
				//Loop through all parameters and check for where they are used
				for(int j = 0; j < params.length ;j++){
					//add parameter in code also
					parameters += "paramsAct" + actNum + ".add(new Parameter(\""+params[j].getType()+"\", \""+params[j].getName()+"\")); \n";
					if(params[j].getName().equals(a.getName())){
						nameActParam = true;
					}
					else if(params[j].getName().equals(a.getDestName())){
						destNameActParam = true;
					}
					else if(params[j].getName().equals(a.getSource())){
						sourceActParam = true;
					}
					else if(params[j].getName().equals(a.getLoc())){
						locationActParam = true;
					}
					else if(params[j].getName().equals(a.getDisp())){
						dispActParam = true;
					}
					else if(params[j].getName().equals(a.getMax())){
						maxActParam = true;
					}
					else if(params[j].getName().equals(a.getMin())){
						minActParam = true;
					}
					else if(params[j].getName().equals(a.getPrior())){
						priorActParam = true;
					}
					else if(params[j].getName().equals(a.getRand())){
						randActParam = true;
					}
					//body += "//"+params[j].getName();
				}
			}
			else{
				params = new Parameter[0];
			}
			
			//Uses if statements to check whether a value or parameter has been used
			//body += "//"+a.getName()+" and parent is " + parent + "\n";
			if(parent.equals("activities")){
				String tempName = a.getName();
				if(activityList.contains(tempName)){
					tempName = tempName + i;
				}
				body += "protected void addActivity"+tempName+"() {\n";
				activityList.add(tempName);
			}
			
			if(!nameActParam){
				try{
					if(!(a.getName().equals("null") || a.getName().equals("")))
						body += "StringValue nameAct" + actNum + " = new StringValue(\"" + a.getName() + "\");\n";
				}
				catch(Exception e) {}
			}
			else{
				body += "ParameterValue nameAct" + actNum + " = new ParameterValue(\"" + a.getName() + "\");\n";
			}
			
			if(!dispActParam){
				try{
					if(!(a.getDisp().equals("null") || a.getDisp().equals("")))
						body += "StringValue dispAct" + actNum + " = new StringValue(\"" + a.getDisp() + "\");\n";
				}
				catch(Exception e) {}
			}
			else{
				body += "ParameterValue dispAct" + actNum + " = new ParameterValue(\"" + a.getDisp() + "\");\n";
			}
			
			if(!maxActParam){
					body += "IntegerValue maxAct" + actNum + " = new IntegerValue(" + a.getMax() + ");\n";
			}
			else{
				body += "ParameterValue maxAct" + actNum + " = new ParameterValue(\"" + a.getMax() + "\");\n";
			}
			
			if(!minActParam){
				body += "IntegerValue minAct" + actNum + " = new IntegerValue(" + a.getMin() + ");\n";
			}
			else{
				body += "ParameterValue minAct" + actNum + " = new ParameterValue(\"" + a.getMin() + "\");\n";
			}
			
			if(!priorActParam){
				body += "IntegerValue priorAct" + actNum + " = new IntegerValue(" + a.getPrior() + ");\n";
			}
			else{
				body += "ParameterValue priorAct" + actNum + " = new ParameterValue(\"" + a.getPrior() + "\");\n";
			}
			if(!randActParam){
				body += "BooleanValue randAct" + actNum + " = new BooleanValue(" + a.getRand() + ");\n";
			}
			else{
				body += "ParameterValue randAct" + actNum + " = new ParameterValue(\"" + a.getRand() + "\");\n";
			}
			if(!destNameActParam){
				try{
					if(!(a.getDestName().equals("null") || a.getDestName().equals("")))
						body += "StringValue destNameAct" + actNum + " = new StringValue(\"" + a.getDestName() + "\");\n";
				}
				catch(Exception e) {}
			}
			else{
				body += "ParameterValue destNameAct" + actNum + " = new ParameterValue(\"" + a.getDestName() + "\");\n";
			}
			if(!locationActParam){
				try{
					if(!(a.getLoc().equals("null") || a.getLoc().equals("")))
						body += "StringValue location" + actNum + " = new StringValue(\"" + a.getLoc() + "\");\n";
				}
				catch(Exception e) {}
			}
			else{
				body += "ParameterValue locationAct" + actNum + " = new ParameterValue(\"" + a.getLoc() + "\");\n";
			}
			if(!sourceActParam){
				try{
					if(!(a.getSource().equals("null") || a.getSource().equals("")))
						body += "StringValue sourceAct" + actNum + " = new StringValue(\"" + a.getSource() + "\");\n";
				}
				catch(Exception e) {}
			}
			else{
				body += "ParameterValue sourceAct" + actNum + " = new ParameterValue(\"" + a.getSource() + "\");\n";
			}
			//
			body += "ArrayList<Parameter> paramsAct" + actNum +
					" = new ArrayList<Parameter>();\n";
			//Add parameters
			body += parameters;
			
			if (!(a.getType().equals("CompositeActivity") || a.getType().equals("CreateObjectActivity")
					|| a.getType().equals("CreateAgentActivity"))) {
				body += "Activity act" + actNum + " = new " + a.getType() +
					"(nameAct" + actNum + ", dispAct" + actNum + ", maxAct" + 
					actNum + ", minAct" + actNum + ", priorAct" + actNum + 
					", randAct" + actNum + ", paramsAct" + actNum;
			}

			String type = a.getType();
			
			int x = actNum++;
			if (type.equals("PrimitiveActivity")) //type of act
				body += ");\n";
			if (type.equals("MoveActivity"))
				body += ");\n ((MoveActivity) act" + x + ").setLocation(\"" +
						a.getLoc() + "\");\n";
			if (type.equals("CommunicateActivity"))
				body += getCommunicateStr(a, x);
			if (type.equals("BroadcastActivity"))
				body += getBroadcastStr(a, x);
			if (type.equals("GetActivity"))
				body += getGetActStr(a, x);
			if (type.equals("PutActivity"))
				body += getPutStr(a, x);
			if (type.equals("GestureActivity"))
				body += getGestureStr(a, x);
			if (type.equals("JavaActivity"))
				body += getJavaStr(a, x);
			if (type.equals("CompositeActivity")){
				try{
					body += getCompositeStr(a, x, (Element) acts.item(i), params);
				}
				catch(Exception e){
					
				}
			}
			if (type.equals("CreateAgentActivity"))
				body += getCreateAgentStr(a, x);
			if (type.equals("CreateObjectActivity"))
				body += getCreateObjectStr(a, x);
			if (type.equals("CreateArea"))
				body += getCreateAreaStr(a, x);
			if (!parent.equals("activities"))
				body += "((CompositeActivity) " + parent + ")";
			else
				body += parent;
			body += ".add(act" + x + ");\n\n";
			
			if(parent.equals("activities"))
				body += "} \n \n";
		}
		
		return body;
	}
	
	public String getCommunicateStr(Activity act, int i) {
		String body = "";
		//with
		body += ");\nList<String> with" + i + " = new " +
				"ArrayList<String>();\n";
		List<String> withList = act.getWith();
		for (int j = 0; j < withList.size(); j++) {
			body += "with" + i + ".add(\"" + withList.get(j) + "\");\n";
		}
		body += "((CommunicateActivity) act" + i + ").setWith(with" + i
				+ ");\n";
		//about
		List<Guard> about = act.getAbout();
		for (int j = 0; j < about.size(); j++) {
			int tempExp = exp;
			body += getAboutStr(about.get(j), exp);
			body += "((CommunicateActivity) act" + i + 
					").addTransferDefinition(def" + tempExp + ");\n";
			exp++;
		}
		//when
		if (act.getWhen().equals("start"))
			body += "((CommunicateActivity) act" + i + 
				").setWhen(\"start\");\n"; 
		return body;
	}
	
	public String getBroadcastStr(Activity act, int i) {
		String body = "";
		
		Parameter[] params = act.getParams();
		//Loop through all parameters and check for where they are used
		boolean whenActParam = false;
		for(int j = 0; j < params.length ;j++){
			if(params[j].getName().equals(act.getWhen())){
				whenActParam = true;
			}
			
		}
		if(whenActParam)
			body += ", new ParameterValue(\"" + act.getWhen() + "\"));\n";
		else	
			body += ", new StringValue(\"" + act.getWhen() + "\"));\n";
		//to
		body += "\nList<String> to" + i + " = new " +
				"ArrayList<String>();\n";
		List<String> toList = act.getWith();
		for (int j = 0; j < toList.size(); j++) {
			body += "to" + i + ".add(\"" + toList.get(j) + "\");\n";
		}
		body += "((BroadcastActivity) act" + i + ").setAreas(to" + i
				+ ");\n";
		//about
		List<Guard> about = act.getAbout();
		for (int j = 0; j < about.size(); j++) {
			int tempExp = exp;
			//body += "//exp = " + exp + "\n";
			body += getAboutStr(about.get(j), exp);
			//body += "//tempExp = " + tempExp + "\n";
			body += "((BroadcastActivity) act" + i + 
					").addTransferDefinition(def" + tempExp + ");\n";
			exp++;
		}
		//when
		/*if (act.getWhen().equals("start"))
			body += "((BroadcastActivity) act" + i + 
			").setWhen(\"start\");\n"; */

		return body;
	}

	
	public String getAboutStr(Guard tmp, int j) {
		String body = "";
		//body += "//getExp2Obj = " + tmp.getExp2Obj() + ", getExp2Att() = " + tmp.getExp2Att() + ", tmp.getExp2() = " + tmp.getExp2() + "\n";
		if(tmp.getIsRelation()){
			if(tmp.getExp2Obj().equals("?")){
				body += relationExpression("knownval", tmp.getExp1Obj(), tmp.getObj1Type(), tmp.getCompare(), "unknown", tmp.getObj2Type(), tmp.getExp1Att(), tmp.getExp2Att(), true, false, "");
			}
			else
				body += relationExpression("knownval", tmp.getExp1Obj(), tmp.getObj1Type(), tmp.getCompare(), tmp.getExp2Obj(), tmp.getObj2Type(), tmp.getExp1Att(), tmp.getExp2Att(), true, false, "");
		}
		else if(tmp.getExp2Obj().equals("") && tmp.getExp2Att().equals("") && !tmp.getExp2().equals("UNKNOWN")){//if there is no RHS then use LHS as RHS
			body += expressionXOX(tmp.getObj1Type(), tmp.getExp1(), tmp.getExp1Obj(), tmp.getExp1Att(), 
					tmp.getObj1Type(), tmp.getExp1(), tmp.getExp2Type(), tmp.getExp2Val(), tmp.getExp1Obj(), tmp.getExp1Att(), 
					tmp.getObj3Type(), tmp.getExp3Type(), tmp.getExp3(), tmp.getExp3Val(), tmp.getExp3Obj(), tmp.getExp3Att(),
					tmp.getCompare(), tmp.getMathOp(), "EvalValCompExpOpExp", tmp.getCollectionIndexType1(), tmp.getCollectionIndex1(),
					tmp.getCollectionIndexType2(), tmp.getCollectionIndex2(),
					tmp.getCollectionIndexType3(), tmp.getCollectionIndex3(), false, "");
		}
		else{
			body += expressionXOX(tmp.getObj1Type(), tmp.getExp1(), tmp.getExp1Obj(), tmp.getExp1Att(), 
					tmp.getObj2Type(), tmp.getExp2(), tmp.getExp2Type(), tmp.getExp2Val(), tmp.getExp2Obj(), tmp.getExp2Att(), 
					tmp.getObj3Type(), tmp.getExp3Type(), tmp.getExp3(), tmp.getExp3Val(), tmp.getExp3Obj(), tmp.getExp3Att(),
					tmp.getCompare(), tmp.getMathOp(), "EvalValCompExpOpExp", tmp.getCollectionIndexType1(), tmp.getCollectionIndex1(),
					tmp.getCollectionIndexType2(), tmp.getCollectionIndex2(),
					tmp.getCollectionIndexType3(), tmp.getCollectionIndex3(), false, "");

		}
		/*body += "Expression exp0" + j + " = new Term(\"" + tmp[1] + 
				"\", \"" + tmp[2] + "\");\n";
		body += "Expression exp1_" + j + " = new " +
				"EvalValCompExpOpExp(exp0" + j + ", EvaluationOperator.EQ, exp0" + j + 
				");\n";*/
		body += "TransferDefinition def" + j + " = new " +
				"TransferDefinition(\"" + tmp.getSendReceive() + "\", " 
				+ "xox" + j + ");\n";
		//exp = exp + j;
		return body;
	}
	
	public String getGetActStr(Activity act, int i) {
		String body = ");\n";
		//source
		body += "((GetActivity) act" + i + ").setSource(\"" + 
				act.getSourceDest() + "\");\n";
		//items
		List<String> items = act.getItems();
		for (int j = 0; j < items.size(); j++) {
			body += "((GetActivity) act" + i + 
					").addItem(\"" + items.get(j) + "\");\n";
		}
		//when
		body += "((GetActivity) act" + i + 
			").setWhen(\"" + act.getWhen() + "\");\n"; 

		return body;
	}
	
	public String getPutStr(Activity act, int i) {
		String body = ");\n";
		//dest
		body += "((PutActivity) act" + i + ").setDestination(\"" + 
				act.getSourceDest() + "\");\n";
		//items
		List<String> items = act.getItems();
		for (int j = 0; j < items.size(); j++) {
			body += "((PutActivity) act" + i + 
					").addItem(\"" + items.get(j) + "\");\n";
		}
		//when
		body += "((PutActivity) act" + i + 
			").setWhen(\"" + act.getWhen() + "\");\n"; 

		return body;
	}
	
	public String getGestureStr(Activity act, int i) {
		String body = "implement GESTURE activity!";
		//TODO
		//body += "Expression exp" + exp
		
		return body;
	}
	
	public String getCreateAgentStr(Activity act, int i) {
		CreateActivity ca = new CreateActivity(act);
		String tmp = ca.constructBody("CreateAgentActivity", i);
		System.out.println(tmp);
		return tmp;
		
	}
	
	public String getCreateObjectStr(Activity act, int i) {
		CreateActivity ca = new CreateActivity(act);
		return ca.constructBody("CreateObjectActivity", i);
	}
	
	public String getCreateAreaStr(Activity act, int i) {
		//TODO
		return "//Create area has not yet been implemented! \n";
	}
	
	public String getJavaStr(Activity act, int i) {
		String body = ");\n";
		//set class
		body += "((JavaActivity) act" + i + ").setJavaClass(\"" + 
				act.getClassName() + "\");\n";
		//when
		body += "((JavaActivity) act" + i + 
			").setWhen(\"" + act.getWhen() + "\");\n"; 
		return body;
	}
	
	public String getCompositeStr(Activity act, int i, Element elm, Parameter[] params) {
		String body = "CompositeActivity act" + i + " = new " + "CompositeActivity" +
				"(nameAct" + i + ", dispAct" + i + ", maxAct" + 
				i + ", minAct" + i + ", priorAct" + i + 
				", randAct" + i + ", paramsAct" + i;
		body += ");\n";
		//endCondition
		body += "((CompositeActivity) act" + i + 
			").setEndCondition(EndCondition." + act.getEndCondition().toUpperCase() + ");\n\n";
		//activities;
		NodeList actList = elm.getElementsByTagName("ACTIVITIES");
		if(DEBUG)
			System.out.println("actList? " + actList.getLength());
		if ((actList != null) && (actList.getLength() > 0)) {
			NodeList compActs = actList.item(0).getChildNodes();
			body += getActivities(compActs, "act" + i, params);
		}
		//workFrames;
		NodeList wfList = elm.getElementsByTagName("WORKFRAMES");	
		if ((wfList != null) && (wfList.getLength() > 0)) {
			//Get the last WORKFRAMES tag to avoid any other compostie nestings
			NodeList wfs = wfList.item(wfList.getLength()-1).getChildNodes(); 
			body += getWorkFrames(wfs, "act" + i);
		}
		//thoughtFrames;
		NodeList tfList = elm.getElementsByTagName("THOUGHTFRAMES");
		if ((tfList != null) && (tfList.getLength() > 0)) {
			NodeList tfs = tfList.item(0).getChildNodes();
			body += getThoughtFrames(tfs, "act" + i);
		}
		
		return body;
	}
	
	public String getWorkframes() {
		exp = 0;
		comp = 0;
		if(DEBUG)
			System.out.println("getting Group/Agent "+name+"'s wfs");
		String body = "protected void addWorkFrames() { \n";

		NodeList wfList = root.getElementsByTagName("WORKFRAMES");
		//There are multiple WORKFRAMES tags, one is for the base workframes and the others
		//are for workframes of a composite activity, we are after the base workframes.
		//To find which is the base we look at the parent of each WORKFRAMES tag
		//If the parent is not a COMP_ACT then it is the base set of workframes
		int baseWorkframes = -1; //Set as negative to show none have been found
		for(int i = 0; i < wfList.getLength(); i++){
			//NodeList wfs = wfList.item(i).getChildNodes();
			String parent = wfList.item(i).getParentNode().getNodeName();
			if(!parent.equals("COMP_ACT")){
				baseWorkframes = i;
			}
		}
		
		
		//if ((wfList != null) && (wfList.getLength() > 0)) {
		if(baseWorkframes != -1){
			NodeList wfs = wfList.item(baseWorkframes).getChildNodes();
			body += getWorkFrames(wfs, "workFrames");
		}

		body += "}\n \n";
		
		return body;
	}
	
	public String getThoughtframes() {
		exp = 0;
		comp = 0;
		String body = "protected void addThoughtFrames() {\n";
		NodeList tfList = root.getElementsByTagName("THOUGHTFRAMES");
		if ((tfList != null) && (tfList.getLength() > 0)) {
			NodeList tfs = tfList.item(tfList.getLength()-1).getChildNodes();
			body += getThoughtFrames(tfs, "thoughtFrames");
			
		}
		//include } and stream.close
		body += "}\n  \n";
		return body;
	}
	
	public String getWorkFrames(NodeList wfs, String parent) {
	/*	if(DEBUG)
			System.out.println("wfs " + wfs.getLength());*/
		String body = "";
		for (int i = 1; i < wfs.getLength() - 1; i=i+2) {
			Frame f = ParseFrames.getFrame((Element) wfs.item(i));
			currentFrame = (Element) wfs.item(i);
			
			String type = f.getType();
			if (type.equals("1"))
				type = "dataframe";
			else
				type = "factframe";
			body += "WorkFrame wf" + numOfWf + " = new WorkFrame(\"" +
				f.getName() + "\", \"" + f.getDisp() + "\", \"" +
				type + "\", \"" + f.getRepeat() + "\", " +
				f.getPrior() + "); \n";		
			body += getVars(f, "wf", numOfWf);
			body += getDetectables(f, "wf", numOfWf);
			body += getGuards(f, "wf", numOfWf);
			Event[] ev =  f.getEvents();
			if(ev != null)
				body += getEvents(f, "wf", numOfWf);
			body += parent + ".add(wf" + numOfWf + ");\n\n";
			numOfWf = numOfWf+2;
		}
		return body;
	}
	
	public String getThoughtFrames(NodeList tfs, String parent) {
		String body = "";
		/*if(DEBUG)
			System.out.println("tfs " + tfs.getLength());*/
		for (int i = 1; i < tfs.getLength() - 1; i=i+2) {
			Frame f = ParseFrames.getFrame((Element) tfs.item(i));
			body += "ThoughtFrame tf" + numOfTf + " = new ThoughtFrame(\"" +
				f.getName() + "\", \"" + f.getDisp() + "\", \"" 
					+ f.getRepeat() + "\", " +
				f.getPrior() + ");\n";
			
			body += getVars(f, "tf", numOfTf); 
			body += getGuards(f, "tf", numOfTf);
			body += getEvents(f, "tf", numOfTf);
			
			body += parent + ".add(tf" + numOfTf + ");\n\n";
			numOfTf = numOfTf+2;
		}
/*		try {
			out.write(body);
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}*/
		return body;
	}
	
	public String getDetectables(Frame wf, String frameType, int wfNum) {
		//TODO //Done R.S
		String Detectables = "//Detectables \n";
		//Detectables += "exp = " + exp;
		int numOfExp = 0;
		Detect[] theDetectables = wf.getDetects();
/*		if(DEBUG)
			System.out.println("length = " + theDetectables.length);*/
		for (int i = 0; i < wf.getDetects().length; i++) {
			Detect tempd = wf.getDetects()[i];

			Guard tempg = tempd.getCondition();
			
			String relation = tempg.getCompare();
			if (relation.equals("lt"))
				relation = "EvaluationOperator.LT";
			if (relation.equals("lteq"))
				relation = "EvaluationOperator.LTE";
			if (relation.equals("gt"))
				relation = "EvaluationOperator.GT";
			if (relation.equals("gteq"))
				relation = "EvaluationOperator.GTE";
			if (relation.equals("eq"))
				relation = "EvaluationOperator.EQ";
			if (relation.equals("uneq"))
				relation = "EvaluationOperator.NEQ";

			if(tempg.getCompare().equals("contains") || !relation.contains("EvaluationOperator")){
				Detectables += "TplObjRef detExp" + exp + " = new TplObjRef(\"" + tempg.getExp1Obj() + "\"); \n";
				if(!(tempg.getExp2Obj().equals("") || tempg.getExp2Obj().equals("?"))){
					Detectables += "SglObjRef detExp" + (exp+1) + " = new SglObjRef(\"" + tempg.getExp2Obj() + "\"); \n";
					Detectables += "DetectRelComp dtVal" + (exp) + " = new DetectRelComp(detExp" + exp + ", \"" + tempg.getCompare() + "\", detExp" + (exp+1) + "); \n";
				}
				else{
					Detectables += "DetectRelComp dtVal" + (exp) + " = new DetectRelComp(detExp" + exp + ", \"" + tempg.getCompare() + "\"); \n";
				}
				Detectables += "ResultComparison ev" + (exp) + " = new ResultComparison(dtVal" + exp + "); \n";
				Detectables += "Detectable detect" + exp + " = new Detectable(\""+ tempd.getName() +"\", \"" + tempd.getWhen() + "\", ev" + exp + ",\n"; 
				Detectables += "    " + tempd.getCertainty() + ", DetectableAction." + tempd.getAction() + "); \n";
				//String x = tempd.getWhen();
				numOfExp = numOfExp + 2;
			}
			else{
				if(tempg.getExp2Type().equals("boolean")){
					
				}
				String val = tempg.getExp2Type();
				if (val.equals("int"))
					val = "IntegerValue(" + tempg.getExp2Val() + ")";
				if (val.equals("double"))
					val = "DoubleValue(" + tempg.getExp2Val() + ")";
				if (val.equals("boolean"))
					val = "BooleanValue(" + tempg.getExp2Val() + ")";
				if (val.equals("string"))
					val = "StringValue(\"" + tempg.getExp2Val() + "\")";
				if ((val.equals("Agent")) || (val.equals("Object")))
					val = "SglObjRef(\"" + tempg.getExp2Val() + "\")";
				if (val.equals("symbol"))
					val = "SymbolValue(\"" + tempg.getExp2Val() + "\")";
				if (val.equals("undefined")){
					if(tempg.getExp2().equals("UNKNOWN")){
						
						val = " SglObjRef(\"unknown\")";
					}
					else if(tempg.getExp2Obj().equals("?")){
						if(tempg.getExp2Att().equals("")){
							val = " SglObjRef";
							val += "(\"" + tempg.getExp2Obj() + "\")";
						}
						else{
							val = " Term";
							val += "(\"" + tempg.getExp2Obj() + "\", \"" +
							tempg.getExp2Att() + "\")";
						}					
					}
					else{
						if(tempg.getExp2Att().equals("")){
							val = " SglObjRef";
							val += "(\"" + tempg.getExp2Obj() + "\")";
						}
						else{
							val = " Term";
							val += "(\"" + tempg.getExp2Obj() + "\", \"" +
							tempg.getExp2Att() + "\")";
						}
					}
				}
				
				//Needs work
				Detectables += "Term detExp" + exp + " = new Term(\"" + tempg.getExp1Obj() + "\", \""+ tempg.getExp1Att() +"\"); \n";
				Detectables += "Expression detExp" + (exp+1) + " = new " + val + "; \n";
				Detectables += "DetectValComp dtVal" + (exp) + " = new DetectValComp(detExp" + exp + ", " + relation + ", detExp" + (exp+1) + "); \n";
				Detectables += "ResultComparison ev" + (exp) + " = new ResultComparison(dtVal" + exp + "); \n";
				Detectables += "Detectable detect" + exp + " = new Detectable(\""+ tempd.getName() +"\", \"" + tempd.getWhen() + "\", ev" + exp + ",\n"; 
				Detectables += "    " + tempd.getCertainty() + ", DetectableAction." + tempd.getAction() + "); \n";
				//String x = tempd.getWhen();
				numOfExp = numOfExp + 2;
				
			}
			if(theDetectables.length > 0)
				Detectables += frameType + wfNum + ".addDetectable(detect" + exp + "); \n \n";
			
			exp = exp + numOfExp;;
			//System.out.println("finished deteectables");
			
		}

		return Detectables;
	}	
	public String getVars(Frame wf, String frameType, int wfNum) {
		String varString = "";
		//TODO
		VariableParse[] vars = wf.getVars();
		int numOfExp = 0;
		for (int i = 0; i < vars.length; i++) {
			String tempq = "";
			if(vars[i].getQuant().equals(VarQuant.FORONE))
				tempq = "forone";
			if(vars[i].getQuant().equals(VarQuant.FOREACH))
				tempq = "foreach";
			if(vars[i].getQuant().equals(VarQuant.COLLECTALL))
				tempq = "collectall";
			
			String tempType = vars[i].getType();
			if(tempType.contains(".")){
				int ind = tempType.lastIndexOf(".");
				tempType = tempType.substring(ind+1);
			}
			
			varString += "Variable var" + (exp+i) + " = new Variable(\"" + tempq + "\", \"" 
				+ tempType + "\", \"" + vars[i].getName() + "\");\n";
			varString += frameType + wfNum + ".addVariable(var" + (exp+i + ");\n\n");
			numOfExp++;
			
		}
		exp = exp + numOfExp;
		//System.out.println(varString);
		return varString;
	}
	
	public String objAttExpressions(int i, String object, String attribute){
		String guards = "Expression exp" + i + " = new Term(\""+object+"\", \"" + attribute + "\"); \n";
		return guards;
	}
	
	public String valueExpressionObjSymb(int i, String eType, String objSymb){
		String guards = "Expression exp" + i + " = new " + eType + "(\""+objSymb+"\"); \n";
		return guards;
	}
	
	public String valueExpression(int i, String eType, String eVal){
		
		String guards = "Expression exp" + i + " = new " + eType + "("+eVal+"); \n";
		return guards;
	}
	public String varExpressionSngle(int i, String object, String type){
		String guards = "";
		if(type.equals("V"))
			guards = "Expression exp" + i + " = new VariableValue("+object+"); \n";
		else
			guards = "Expression exp" + i + " = new VariableValue(\""+object+"\"); \n";
		return guards;
	}
	public String varExpressionObjAtt(int i, String object, String attribute){
		String guards = "Expression exp" + i + " = new VariableValue(\""+object+"\"); \n";
		if(!attribute.equals(""))
			guards += "String strExp" + i + " = \""+ attribute +"\"; \n";
		return guards;
	}

	public String paramExpression(int i, String object, String attribute){
		String guards = "Expression exp" + i + " = new ParameterValue(\""+object+"\"); \n";
		//guards += "//attribute = \"" + attribute + "\" \n";
		if(!attribute.equals("")){
			guards += "String strExp" + i + " = \""+ attribute +"\"; \n";
		}
		return guards;
	}
	
	
	public String thirdExpression(String e3ObjType, String e3Type, String e2objType, String e2Type, String exp3, 
			String exp3Val, String exp3Obj, String exp2Att, String exp3Att, int i, String mathOp){
		String guards = "";
		//guards += "//e3ObjType = " + e3ObjType + ", e3Type = " + e3Type + ", exp3 = " + exp3 + ", exp3Val =  " + exp3Val + ",  exp3Obj = " + exp3Obj + ", exp3Att = " + exp3Att + "\n";
		//e3ObjType = variable, e3Type = , exp3 = O, exp3Val =  ,  exp3Obj = b, exp3Att = 
		String exp3Type = "";
		if (e3Type.equals("int")) {
			e3Type = "IntegerValue";
			exp3Type = "BinaryExpression";
		}
		else if (e3Type.equals("bool") || e3Type.equals("boolean"))
			e3Type = "BooleanValue";
		else if (e3Type.equals("string") || e3Type.equals("String")) {
			e3Type = "StringValue";
		}
		else if (e3Type.equals("double")) {
			e3Type = "DoubleValue";
			exp3Type = "BinaryExpression";
		}
		else if (e3Type.equals("undefined") || e3Type.equals("")) {
			if(e3ObjType.equals("parameter")){
				e3Type = "ParameterValue";
				//If it is a parameter then we need to find if it is an integer or a double.  To do this we look at the ancestors
				//of the event, since is a parameter then this needs to come from a composite activity so we loop back until we find
				//a composite activity as an ancestor.  When we find one we then check to see if the composite activity has a parameter of
				//the same name, if it does then we assign the expression as this type.
				Element parent = currentFrame;
				while(true){
					try{
						parent = (Element) parent.getParentNode();
					}
					catch(Exception e){
						//When it reaches the root note the parent will return a null, this catches the null pointer
						//and breaks out the loop
						break;
					}
					if(parent == null){
						break;
					}
					if(parent.getNodeName().equals("COMP_ACT")){ //Loop until we find a composite activity
						NodeList parameters = parent.getElementsByTagName("PARAMETERS");
						//Loop through all parameters
						if (parameters != null && parameters.getLength() > 0){
							Element paramsTag = (Element) parameters.item(0); //first parameter tag is the one we need
							NodeList params = paramsTag.getChildNodes();
							Node n = paramsTag.getFirstChild();//First child is #text
							Node sib = n.getNextSibling();//we need the second child
							int length = (params.getLength() - 1) / 2;//Every other child is text so length is calculated by this equation
							//Loop through all the parameters
							for (int k = 0; k < length; k++) {
								Element param = (Element) sib;
								if(param.getAttribute("name").equals(exp3Obj)){	//Check if the parameter name matches our parameter value's name
									String type = param.getAttribute("type");
									if(type.equals("int")){
										exp3Type = "BinaryExpression";
										break;
									}
									else if(type.equals("double")){
										exp3Type = "BinaryExpression";
										break;
									}
								}
							}
						}
					}
				}	
				
			}
			else if(e3ObjType.equals("variable")){
				e3Type = "VariableValue";
			}
			else{
				e3Type = "Term";//Undefined means it is a term
			}

			//if the type still can't be indentified check all known attributes
			for(int j = 0; j < theAttributes.length; j++){
				String tempName = theAttributes[j].getName();
				if(tempName.contains(".")){
					int index = tempName.lastIndexOf(".") + 1;
					tempName = tempName.substring(index);
				}
				//guards += "//Comparing " + exp3Att + " against " + tempName + " \n";
				if(tempName.equals(exp3Att)){
					//exp3Type = theAttributes[j].getType();
					if(theAttributes[j].getType().equals("int")){
						exp3Type = "BinaryExpression";
					}
					else if(theAttributes[j].getType().equals("double")){
						exp3Type = "BinaryExpression";
					}
					break;
				}	
			}
			if(exp3Type.equals("") || exp3Type.equals("undefined")) //if all else fails just set it as a double
				exp3Type = "BinaryExpression"; //Quick fix????
		}
		String previousExp = "";
		if(!exp2Att.equals("") && ((e2Type.equals("VariableValue") || e2Type.equals("ParameterValue")) || (e2objType.equals("variable") || e2objType.equals("parameter")))){
			previousExp = "exp" + i + ", \"" + exp2Att + "\"";
		}
		else{
			previousExp = "exp" + i;
		}
		if(exp3.equals("V")){
			if(e3ObjType.equals("parameter") || e3ObjType.equals("variable"))
				guards += "exp" + i + " = new " + exp3Type + "(" + previousExp + ", MathOperator." + mathOp + ", new " + e3Type + "(" + exp3Obj+")); \n";
			else
				guards += "exp" + i + " = new " + exp3Type + "(" + previousExp + ", MathOperator." + mathOp + ", new " + e3Type + "(" + exp3Val+")); \n";
		}
		else if(exp3.equals("OA")){
			if(e3ObjType.equals("parameter") || e3ObjType.equals("variable")){
				guards += "exp" + i + " = new " + exp3Type + "(" + previousExp +", MathOperator." + mathOp + ", new " + e3Type + "(\""+ exp3Obj+"\"), \"" + exp3Att + "\"); \n";
			}
			else
				guards += "exp" + i + " = new " + exp3Type + "(" + previousExp +", MathOperator." + mathOp + ", new " + e3Type + "(\""+ exp3Obj+"\", \"" + exp3Att + "\")); \n";
		}
		else if(exp3.equals("O")){
			guards += "exp" + i + " = new " + exp3Type + "(" + previousExp + ", MathOperator." + mathOp + ", new " + e3Type + "(\"" + exp3Obj+"\")); \n";
		}
		
		return guards;
	}
	
	//Calculates how to structure string/int/object comparrisons
	public String expressionXOX(String obj1Type, String exp1, String exp1Obj, String exp1Att, 
			String obj2Type, String exp2, String e2Type, String e2Val, String exp2Obj, String exp2Att,
			String obj3Type, String e3Type, String exp3, String exp3Val, String exp3Obj, String exp3Att,
			String relation, String mathOp, String evalComp,
			String collectionIndexType1, String collectionIndex1, String collectionIndexType2, String collectionIndex2, String collectionIndexType3, String collectionIndex3,
			boolean workload, String frameName){
		String XOX = "";
		//XOX += "//1 " + obj2Type + " - " + e2Type +"\n";
		int numOfExp= 0;
		
		//Workload strings
		String lhs = "";
		String rhs1 = "";
		String rhs2 = "";
		
		//String exp2Type = "";
		if (e2Type.equals("int")) {
			e2Type = "IntegerValue";
			//exp2Type = "BinaryExpression";
		}
		if (e2Type.equals("bool") || e2Type.equals("boolean"))
			e2Type = "BooleanValue";
		if (e2Type.equals("string") || e2Type.equals("String")) {
			e2Type = "StringValue";
			e2Val = "\"" + e2Val + "\""; //put strings in quotes
		}
		if (e2Type.equals("double")) {
			e2Type = "DoubleValue";
		}
		if(e2Type.equals("symbol")){
			e2Type = " SymbolValue";
		}
		if (e2Type.equals("undefined") || e2Type.equals("")){
			if(exp2.equals("UNKNOWN")){
				e2Type = " SglObjRef";
				e2Val = "\"unknown\"";
			}
			else if(exp2Obj.equals("?")){
				if(exp2Att.equals("")){
					e2Type = " SglObjRef";
					e2Val += "\"" + exp2Obj + "\"";
				}
				else{
					e2Type = " Term";
					e2Val += "\"" + exp2Obj + "\", \"" +
					exp2Att + "\"";
				}					
			}
			else{
				if(exp2Att.equals("")){
					e2Type = " SglObjRef";
					e2Val += "\"" + exp2Obj + "\"";
				}
				else{
					e2Type = " Term";
					e2Val += "\"" + exp2Obj + "\", \"" +
						exp2Att + "\"";
				}
				
			}
		}
				
		//Identify which mathemaical operator "=,<,>,<=,>="
		if (relation.equals("lt"))
			relation = "LT";
		if (relation.equals("lteq"))
			relation = "LTE";
		if (relation.equals("gt"))
			relation = "GT";
		if (relation.equals("gteq"))
			relation = "GTE";
		if (relation.equals("eq"))
			relation = "EQ";
		if (relation.equals("uneq"))
			relation = "NEQ";
		
		//Identify which mathematical operator "*,+,-,\"
		if (mathOp.equals("mult"))
			mathOp = "MULTIPLY";
		if (mathOp.equals("plus"))
			mathOp = "PLUS";
		if (mathOp.equals("div"))
			mathOp = "DIVIDE";
		if (mathOp.equals("minus"))
			mathOp = "MINUS";
		
		//Find the various combinations of values, objects and attributes etc
		//obj.att = obj.att or value (+ obj.att or value)
		XOX += ""; 
		//LHS is a variable value
		if(obj1Type.equals("variable") && !exp1.equals("OA")){
			XOX += varExpressionSngle(exp, exp1Obj, exp1);
			lhs += "var("+exp1Obj+")";
			numOfExp++;
			
		}
		else if(obj1Type.equals("variable") && exp1.equals("OA")){
			XOX += varExpressionObjAtt(exp, exp1Obj, exp1Att);
			lhs = "var("+exp1Obj +")." + exp1Att; 
			numOfExp++;
		}
		else if(obj1Type.equals("parameter")){
			XOX += paramExpression(exp, exp1Obj, exp1Att);
			lhs += "param(" + exp1Obj + ")." + exp1Att;
			numOfExp++;
		}
		//LHS is not a variable
		else if(!obj1Type.equals("variable") && !exp1Att.equals("")){
			XOX += objAttExpressions(exp, exp1Obj, exp1Att);
			lhs += exp1Obj + "." + exp1Att;
			numOfExp++;
		}
		else if(exp1Att.equals("")){
			XOX += valueExpression(exp, "SglObjRef", "\"" + exp1Obj + "\"");
			lhs += exp1Obj;
		}
		
		
		//if RHS2 = obj.att
		if(!(obj2Type.equals("variable") || obj2Type.equals("parameter")) && exp2.equals("OA")){
			XOX += objAttExpressions(exp+1, exp2Obj, exp2Att);
			rhs1 += "var(" + exp2Obj + ")." + exp2Att;
			numOfExp++;
		}
		//if RHS2 = value
		else if(!exp2.equals("OA") && !obj2Type.equals("variable")){	
			if(e2Type.equals(" SymbolValue")){
				XOX +=  valueExpressionObjSymb(exp+1, e2Type, e2Val);
				rhs1 += e2Val;
			}
			else{
				XOX += valueExpression(exp+1, e2Type, e2Val);
				rhs1 += e2Val;
			}
			numOfExp++;
		}
		//if RHS2 is var value
		else if(!exp2.equals("OA") && obj2Type.equals("variable")){
			XOX += varExpressionSngle(exp+1, exp2Obj, exp2);
			rhs1 += "var(" + exp2Obj + ")" ;
			numOfExp++;
		}
		else if(obj2Type.equals("variable") && exp2.equals("OA")){
			XOX += varExpressionObjAtt(exp+1, exp2Obj, exp2Att);
			rhs1 += "var(" + exp2Obj + ")." + exp2Att;
			numOfExp++;
		}
		else if(obj2Type.equals("parameter")){
			XOX += paramExpression(exp+1, exp2Obj, exp2Att);
			rhs1 += "param(" + exp2Obj + ")." + exp2Att;
			numOfExp++;
			numOfExp++;
		}
			
		//If there is a third expression, overwrite second expression with 2nd mathOp 3rd
		try{
			if(!exp3.equals("") && !exp3.equals("undefined")){
				XOX += thirdExpression(obj3Type, e3Type, obj2Type, e2Type, exp3, exp3Val, exp3Obj, exp2Att, exp3Att, (exp+1), mathOp);
				rhs2 += workLoadThirdExpression( obj3Type,  exp3Obj,  exp3Att,  exp3Val,  exp3,  e3Type);
			}
		}
		catch(Exception e){
			XOX += "//An exception is being thrown: "+e+" \n";
		}
		if(!collectionIndex1.equals("")){
			if(collectionIndexType1.equals("int")){
				XOX += "MapKey MK" + exp  + " = new MapKey(" + collectionIndex1 + ");\n";
			}
			else if(collectionIndexType1.equals("string")){
				XOX += "MapKey MK" + exp  + " = new MapKey(\"" + collectionIndex1 + "\");\n";
			}
			else if(collectionIndexType1.equals("variable")){
				XOX += "Value MK" + exp  + " = new VariableValue(\"" + collectionIndex1 + "\");\n";
			}
			else if(collectionIndexType1.equals("parameter")){
				XOX += "Value MK" + exp  + " = new ParameterValue(\"" + collectionIndex1 + "\");\n";
			}
		}
		if(!collectionIndex2.equals("")){
			if(collectionIndexType2.equals("int")){
				XOX += "MapKey MK" + (exp+1)  + " = new MapKey(" + collectionIndex2 + ");\n";
			}
			else if(collectionIndexType2.equals("string")){
				XOX += "MapKey MK" + (exp+1)  + " = new MapKey(new StringValue(\"" + collectionIndex2 + "\"));\n";
			}
			else if(collectionIndexType2.equals("variable")){
				XOX += "Value MK" + (exp+1)  + " = new VariableValue(\"" + collectionIndex2 + "\");\n";
			}
		}
		
		
		//Check if LHS, RHS, RHS2 are either value variables or have no variables	
		String lhsExpression = "";
		String rhsExpression = "";
		
		//if(collectionIndex1.equals("") && collectionIndex2.equals("") && collectionIndex3.equals("")){
		if((obj1Type.equals("variable") || obj1Type.equals("parameter")) && exp1.equals("OA"))
			lhsExpression = "exp" + exp + ", " + "strExp" + exp+ ", ";
		else
			lhsExpression = "exp" + exp + ", ";
		
		if(!collectionIndex1.equals("")){
			lhsExpression += "MK" + exp + ", ";
		}
		
		if((obj2Type.equals("variable") || obj2Type.equals("parameter")) && exp2.equals("OA")){
			if(!collectionIndex2.equals(""))
				rhsExpression = "exp" + (exp+1) + ", " + "strExp" + (exp+1)+ ", MK" + (exp+1) + "); \n";
			else
				rhsExpression = "exp" + (exp+1) + ", " + "strExp" + (exp+1)+ "); \n";
		}
		else{
			if(!collectionIndex2.equals("")){
				rhsExpression = "exp" + (exp+1)+ ", " + "MK" + (exp+1) +"); \n";
			}
			else
				rhsExpression = "exp" + (exp+1)+ "); \n";
		}
		
		XOX += "Expression xox" + exp + "= new "+evalComp+"("
				+ lhsExpression
				+"EvaluationOperator." + relation + ", "
				+ rhsExpression + "\n";
		//}
		if(workload){
			Map<String, String> concMap = new HashMap<String, String>();
			String key = name + "_" + frameName + "_xox" + exp;
			String mapValue = "";
			if(rhs2.equals(""))
				mapValue = lhs + " " + relation + " " + rhs1;
			else 
				mapValue = lhs + " " + relation + " " + rhs1 + " " + mathOp + " " + rhs2;
			concMap.put(key, mapValue);
		}
		exp = exp + numOfExp;
		return XOX;
	}
	
	public String workLoadThirdExpression(String obj2Type, String exp2Obj, String exp2Att, String e2Val, String exp2, String e2Type){
		String rhs2 = "";
		//if obj.att
		if(!(obj2Type.equals("variable") || obj2Type.equals("parameter")) && exp2.equals("OA")){
			rhs2 += "var(" + exp2Obj + ")." + exp2Att;
		}
		//if RHS2 = value
		else if(!exp2.equals("OA") && !obj2Type.equals("variable")){	
			if(e2Type.equals(" SymbolValue")){
				rhs2 += e2Val;
			}
			else{
				rhs2 += e2Val;
			}
		}
		//if var value
		else if(!exp2.equals("OA") && obj2Type.equals("variable")){
			rhs2 += "var(" + exp2Obj + ")" ;
		}
		else if(obj2Type.equals("variable") && exp2.equals("OA")){
			rhs2 += "var(" + exp2Obj + ")." + exp2Att;
		}
		else if(obj2Type.equals("parameter")){
			rhs2 += "param(" + exp2Obj + ")." + exp2Att;
		}
		return rhs2;
	}
	
	public String relationExpression(String Modifier, String Exp1Obj, String Obj1Type, String Compare, String Exp2Obj, String Obj2Type, String obj1Att, String obj2Att, boolean truthVal, boolean workload, String frameName){
		String relationExp = "";
		//System.out.println("HERE with Modifier = " + Modifier + ", Exp1Obj = " + Exp1Obj + ", Compare = " + Compare + ", Exp2Obj = " + Exp2Obj + ", Obj2Type = " + Obj2Type + ", obj1Att = "+ obj1Att + ", obj2Att = " + obj2Att);
		String workloadString = "";
		if(!Obj1Type.equals("variable")){
			relationExp += "Expression exp" + exp + "= new TplObjRef(\""+Exp1Obj+"\"); \n";
			workloadString += Exp1Obj;
		}
		else{
			relationExp += "Expression exp" + exp + "= new VariableValue(\""+Exp1Obj+"\"); \n";
			workloadString += "var("+Exp1Obj+")";
		}
		if(!obj1Att.equals(""))
			workloadString += "." + obj1Att;
		workloadString += " " + Compare + " ";
		
		if(!Obj2Type.equals("variable")){
			relationExp += "Expression exp" + (exp+1) + "= new SglObjRef(\""+Exp2Obj+"\"); \n";
			workloadString += Exp2Obj;
		}
		else{
			if(!Exp2Obj.equals("?")){
				relationExp += "Expression exp" + (exp+1) + "= new VariableValue(\""+Exp2Obj+"\"); \n";
				workloadString += "var(" + Exp2Obj+")";
			}
		}
		if(Modifier.equals("known") || Modifier.equals("unknown")){
			relationExp += "NoValComparison xox" + exp + " = new NoValComparison(\""+Modifier+"\", exp" + exp + ", \n";
			
			relationExp += "\"" + Compare + "\" \n";	
					
			relationExp +=		");\n\n";
		}
		else{
			relationExp += "RelComp xox" + exp + " = new RelComp(exp" + exp + ", \n";
		
				
			if(!obj1Att.equals(""))//If obj1 has an attribute
				relationExp += "\""+ obj1Att +"\", \n";
			
			relationExp += "\"" + Compare + "\", \n";
			
			relationExp +=	"exp" + (exp+1) + "";		
			
			if(!obj2Att.equals(""))//If obj2 has an attribute
				relationExp += ", \""+ obj2Att +"\", true";		
					
			relationExp +=		");\n";
		}
		
		if(workload){
			String key = name + "_" + frameName + "_xox" + exp;
			Map<String, String> concMap = new HashMap<String, String>();
			concMap.put(key, workloadString);
		}
		
		relationExp += "xox" + exp + ".setTruthValue(" + truthVal + ");\n\n";
		
		exp = exp+2;
		
		return relationExp;
	}
	
	
	public String getGuards(Frame f, String frameType, int wfNum) {
		String guards = "//guards \n";
		for (int i = 0; i < f.getGuards().length; i++) {
			Guard tempg = f.getGuards()[i];
			
			if(tempg.getExp2Type().equals("") && tempg.getExp2Val().equals("")){ //If guard is of type "known" 
				//System.out.println("I'm here comare = " + tempg.getCompare() + ", Modifier = " + tempg.getModifier() + ", exp1 = " + tempg.getExp1() + ", exp1Att = " + tempg.getExp1Att() + ", exp1Obj = " + tempg.getExp1Obj() + ", obj1Type = " + tempg.getObj1Type());
				if(tempg.getObj1Type().equals("variable")){	
					
					guards += "VariableValue exp" + exp + " = new VariableValue(\""+tempg.getExp1Obj()+"\");\n"
							+ "String strExp" + exp + " = \"" + tempg.getExp1Att() + "\"; \n";
					
					//neha: 10/7/2015 added a check if there is a relation
					if(!tempg.getIsRelation()) {
						guards += "NoValComparison compare" + comp + " ="
								+ "	 new NoValComparison(\"" + tempg.getModifier() + "\", " +
								"exp" + exp + ", strExp" + exp + ", \"\");\n";
					}
					else {
						guards += "NoValComparison compare" + comp + " = "
								+ "new NoValComparison(\"" + tempg.getModifier() + "\", " +
								"exp" + exp + ", strExp" + exp + ", \"" + 
								tempg.getCompare() +"\");\n";
					}

				}
				else{
					guards += "Expression garExp" + exp + " = new Term(\""
					+ tempg.getExp1Obj() + "\", \"" + tempg.getExp1Att() +
					"\");\n";
					
					
					guards += "Comparison compare" + comp + 
					" = new NoValComparison(\""+tempg.getModifier()+"\", " + "garExp" + exp + ");\n";
					}
				guards += frameType + wfNum + ".addCondition(compare" + comp++ + ");\n\n";
				exp++;
				
			}
			else{ //For knownval/not
				int tempExp = exp;
				if(!tempg.getIsRelation()){
					guards += expressionXOX(tempg.getObj1Type(), tempg.getExp1(), tempg.getExp1Obj(), tempg.getExp1Att(), 
							tempg.getObj2Type(), tempg.getExp2(), tempg.getExp2Type(), tempg.getExp2Val(), tempg.getExp2Obj(), tempg.getExp2Att(), 
							tempg.getObj3Type(), tempg.getExp3Type(), tempg.getExp3(), tempg.getExp3Val(), tempg.getExp3Obj(), tempg.getExp3Att(),
							tempg.getCompare(), tempg.getMathOp(), "EvalValCompExpOpExp", tempg.getCollectionIndexType1(), tempg.getCollectionIndex1(),
							tempg.getCollectionIndexType2(), tempg.getCollectionIndex2(),
							tempg.getCollectionIndexType3(), tempg.getCollectionIndex3(), false, "");
				}
				else{
					
					guards += relationExpression(tempg.getModifier(), tempg.getExp1Obj(), tempg.getObj1Type(), tempg.getCompare(), tempg.getExp2Obj(), tempg.getObj2Type(), tempg.getExp1Att(), tempg.getExp2Att(), true, false, "");
				}
				guards += "EvalValComp compare" + comp + 
						" = new EvalValComp(\"" + tempg.getModifier() + "\", "
						+ "xox" + (tempExp) + ");\n";
				guards += frameType + wfNum + ".addCondition(compare" + comp++ + ");\n\n";
				exp++;
				
			}

		}
		exp++;
		return guards;
	}
	
	public String getEvents(Frame f, String frameType, int wfNum) {
		String events = "//events \n";
		//TODO: I think I've done
		int numOfExp = 0; //Count number of expressions
		for (int i = 0; i < f.getEvents().length; i++) {
			numOfExp = 0; 
			Event event = f.getEvents()[i];
			Parameter[] params = event.getParams();	
				
			if (event.getType().equals("activity")) {
				/*if(DEBUG)
					System.out.println("Workframe: " + f.getName() + " has activity " + event.getActName());*/
				events += "EventActivity event" + ev + " = new EventActivity(\""
						+ event.getActName() + "\");\n";
				try{
					if(params.length > 0){
						for(int j =0 ; j< params.length; j++){
							events += "event" + ev + ".addParameter(new ParameterValue(\""+params[j].getName()+"\"));\n";
						}
					}
				}
				catch(Exception e){}
				events += "wf" + wfNum + ".addEvent(event" + (ev++) + ");\n\n";
			} 
			else { //consequence
				/*if(DEBUG)
					System.out.println("Workframe: " + f.getName() + " has consequnce with LHS " + event.getLhsObj() + " and RHS " + event.getLhsAtt());*/
				int tempExp = exp;
				if(event.getIsRelation()){//if it is a relation
					events += relationExpression("knownval", event.getLhsObj(), event.getLhsObjType(), event.getRelation(), event.getRhsObj1(), event.getRhsObjType(), event.getLhsAtt(), event.getRhsAtt1(), event.getTruthVal(), true, event.toString());
					numOfExp++;
				}
				else{				
					events += expressionXOX(event.getLhsObjType(), event.getLhsExpType(), event.getLhsObj(), event.getLhsAtt(), 
						event.getRhsObjType(), event.getRhsExpType(), event.getRhsValType1(), event.getRhsVal1(), event.getRhsObj1(), event.getRhsAtt1(),
						event.getRhsObjType2(), event.getRhsValType2(), event.getRhsExpType2(), event.getRhsVal2(), event.getRhsObj2(), event.getRhsAtt2(),
						event.getRelation(), event.getRhsOp(), "EvalValCompExpOpExp", event.getLhsCollectionType(), event.getLhsCollectionIndex(),
						"", "",
						"", "", true, f.getName()); //Mappings for events not implemented yet
				}
				String workloadID = "\""+ name + "_" + f.getName() + "_xox" + tempExp+"\"";
                                String concludeClassName = "Conclude";
				if (GetFiles.config.containsKey("replace.vals")) {
					for (int rIndex = 0; rIndex < Integer
							.valueOf(GetFiles.config
									.getStringProperty("replace.vals")); rIndex++) {
						String replace = "replace_" + rIndex;
						if (GetFiles.config.getStringProperty(
								replace + ".agentName").equals(name)
								&& GetFiles.config.getStringProperty(
										replace + ".agentWorkframe").equals(
										f.getName())
								&& GetFiles.config.getStringProperty(
										replace + ".concludeExpression")
										.equals(event.getLhsAtt().toString())) {
							concludeClassName = GetFiles.config
									.getStringProperty(replace
											+ ".concludeName");
							break;
						}
					}
				}
                                events += "Conclude conclude" + ev + " = new " + concludeClassName + "(xox"
						+ tempExp + ", " + event.getBC() + ", " + event.getFC() + ", " + workloadID + "); // This is a test \n";
				events += frameType + wfNum + ".addEvent(conclude" + (ev++) + ");\n\n";
				numOfExp++;
			}
			exp = exp +numOfExp; //update number of expressions
		}
		return events;
	}
	
	public String getTypeValue(String type) {
		
		if (type.equals("int"))
			return "Integer";
		else if (type.equals("string") || type.equals("undefined")  /*|| type.equals("symbol")*/)
			return "String";
		else if (type.equals("double"))
			return "Double";
		else if (type.equals("boolean"))
			return "Boolean";
		else if (type.equals("symbol"))
			return "Symbol";
		else if (type.equals("undefined"))
			return "undefined"; 
			
		else if (type.equals(""))
		
			return "";
		else
			throw new RuntimeException("didn't get all val " + 
					"types in wfs: getevents");
	}
	
	/**
	 * Returns the document after ensuring it exists and can be parsed
	 * @param f
	 * @return
	 */
	public Document startParsingFile(File f) {
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

}


/*	String relation = tempg.getCompare();
if (relation.equals("lt"))
	relation = "EvaluationOperator.LT";
else if (relation.equals("lteq"))
	relation = "EvaluationOperator.LTE";
else if (relation.equals("gt"))
	relation = "EvaluationOperator.GT";
else if (relation.equals("gteq"))
	relation = "EvaluationOperator.GTE";
else if (relation.equals("eq"))
	relation = "EvaluationOperator.EQ";
else if (relation.equals("uneq"))
	relation = "EvaluationOperator.NEQ";
else
	relation = "\""+relation+"\"";

if(tempg.getCompare().equals("contains")){
	Detectables += "TplObjRef detExp" + exp + " = new TplObjRef(\"" + tempg.getExp1Obj() + "\"); \n";
	Detectables += "SglObjRef detExp" + (exp+1) + " = new SglObjRef(\"" + tempg.getExp2Obj() + "\"); \n";
	Detectables += "DetectRelComp dtVal" + (exp) + " = new DetectRelComp(detExp" + exp + ", \"" + tempg.getCompare() + "\", detExp" + (exp+1) + "); \n";
	Detectables += "ResultComparison ev" + (exp) + " = new ResultComparison(dtVal" + exp + "); \n";
	Detectables += "Detectable detect" + exp + " = new Detectable(\""+ tempd.getName() +"\", \"" + tempd.getWhen() + "\", ev" + exp + ",\n"; 
	Detectables += "    " + tempd.getCertainty() + ", DetectableAction." + tempd.getAction() + "); \n";
	//String x = tempd.getWhen();
	numOfExp = numOfExp + 2;
}
else{
	String val = tempg.getExp1();
	if (val.equals("OA")){
		
		val = "Term detExp" + exp + " = new Term(\"" + tempg.getExp1Obj() + "\", \""+ tempg.getExp1Att() +"\"); \n";
	}	
	if(val.equals("O")){
		val = Detectables += "TplObjRef detExp" + exp + " = new TplObjRef(\"" + tempg.getExp1Obj() + "\"); \n";
	}	
							
	String val2 = tempg.getExp2Type();
	if (val2.equals("int"))
		val2 = "IntegerValue(" + tempg.getExp2Val() + ")";
	if (val2.equals("double"))
		val2 = "DoubleValue(" + tempg.getExp2Val() + ")";
	if (val2.equals("boolean"))
		val2 = "BooleanValue(" + tempg.getExp2Val() + ")";
	if (val2.equals("string"))
		val2 = "StringValue(\"" + tempg.getExp2Val() + "\")";
	if ((val2.equals("Agent")) || (val2.equals("Object")))
		val2 = "SglObjRef(\"" + tempg.getExp2Val() + "\")";
	if (val2.equals("undefined") || val2.equals("")){
		if(tempg.getExp2().equals("UNKNOWN")){
			
			val2 = " SglObjRef(\"unknown\")";
		}
		else if(tempg.getExp2Obj().equals("?")){
			if(tempg.getExp2Att().equals("")){
				val2 = " SglObjRef";
				val2 += "(\"" + tempg.getExp2Obj() + "\")";
			}
			else{
				val2 = " Term";
				val2 += "(\"" + tempg.getExp2Obj() + "\", \"" +
				tempg.getExp2Att() + "\")";
			}					
		}
		else{
			if(tempg.getExp2Att().equals("")){
				val2 = " SglObjRef";
				val2 += "(\"" + tempg.getExp2Obj() + "\")";
			}
			else{
				val2 = " Term";
				val2 += "(\"" + tempg.getExp2Obj() + "\", \"" +
				tempg.getExp2Att() + "\")";
			}
		}
	}
	
	//Needs work
	
	
	Detectables += "Term detExp" + exp + " = new Term(\"" + tempg.getExp1Obj() + "\", \""+ tempg.getExp1Att() +"\"); \n";
	if(!tempg.getExp2Type().equals("undefined")){
		//Detectables += "//tempg.getExp2Type = " + tempg.getExp2Type() + "\n";
		Detectables += "Expression detExp" + (exp+1) + " = new " + val2 + "; \n";
	}
	if(relation.contains("EvaluationOperator"))
		Detectables += "DetectValComp dtVal" + (exp) + " = new DetectValComp(detExp" + exp + ", " + relation + ", detExp" + (exp+1) + "); \n";
	else if(!tempg.getExp2Type().equals("underfined"))
		Detectables += "RelComp dtVal" + (exp) + " = new RelComp(detExp" + exp + ", " + relation + ", detExp" + (exp+1) + "); \n";
	else
		Detectables += "DetRelComp dtVal" + (exp) + " = new DetRelComp(detExp" + exp + ", " + relation + "); \n";
	*/
	
