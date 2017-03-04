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

package parseElements;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ConceptPieces.Activity;
import ConceptPieces.Guard;
import ConceptPieces.Parameter;

//<!ELEMENT ACTIVITIES ((PRIM_ACT | MOVE_ACT | COMM_ACT | 
//BRDC_ACT | CROB_ACT | CRAG_ACT | CRAR_ACT | JAVA_ACT | 
//GET_ACT | PUT_ACT | GEST_ACT | COMP_ACT)*)>

public class ParseActs {
	protected static Element root;
	protected static String destination;
	protected static String packageName;
	protected static boolean DEBUG = false; 
	
	public static Activity getActivity(Node act, String dest) {
		destination = dest;
		switch (act.getNodeName()) {
			case "PRIM_ACT":	
				return getPrimitive((Element) act);
			case "COMM_ACT":
				return getCommunicate((Element) act);
			case "MOVE_ACT":
				return getMove((Element) act);
			case "BRDC_ACT":
				return getBroadcast((Element) act);
			case "GET_ACT":
				return getGet((Element) act);
			case "PUT_ACT":
				return getPut((Element) act);
			case "GEST_ACT":
				return getGesture((Element) act);
			case "CROB_ACT":
				return getCreateObj((Element) act);
			case "CRAR_ACT":
				return getCreateArea((Element) act);
			case "CRAG_ACT":
				return getCreateAgent((Element) act);
			case "JAVA_ACT":
				return getJava((Element) act);
			case "COMP_ACT":
				return getComposite((Element) act);
			default:
				throw new RuntimeException("undefined activity " + act.getNodeName());
		}
	}
	
	public static Activity getPrimitive(Element prim) {
		return getBasicActivityInfo("PrimitiveActivity", prim);
	}
	
	public static Activity getCommunicate(Element comm) {
		Activity newAct = getBasicActivityInfo("CommunicateActivity", comm);
		getCommActInfo(newAct, comm);
		return newAct;
	}
	
	public static Activity getBroadcast(Element broad) {
		Activity newAct = getBasicActivityInfo("BroadcastActivity", broad);
		getBroadActInfo(newAct, broad);
		return newAct;
	}
	
	public static Activity getMove(Element move) {
		Activity newAct = getBasicActivityInfo("MoveActivity", move);
		getMoveActInfo(newAct, move);
		return newAct;
	}
	
	public static Activity getGet(Element get) {
		Activity newAct = getBasicActivityInfo("GetActivity", get);
		getGetActInfo(newAct, get);
		return newAct;
	}
	
	public static Activity getPut(Element put) {
		Activity newAct = getBasicActivityInfo("PutActivity", put);
		getPutActInfo(newAct, put);
		return newAct;
	}
	
	public static Activity getGesture(Element gesture) {
		throw new RuntimeException("Need to implement parsing of " +
				"GestureActivity in ParseActs");
	}
	
	public static Activity getCreateObj(Element createObj) {
		Activity newAct = getBasicActivityInfo("CreateObjectActivity", createObj);
		//getCreateObjActInfo(newAct, createObj);
		try{
			String action = createObj.getAttribute("action");
			newAct.setAction(action);
		}
		catch(Exception e){}
		try{
			String source = createObj.getAttribute("source");
			if(source.contains(".")){
				int index = source.lastIndexOf(".") + 1;
				source = source.substring(index);
			}
			newAct.setSource(source);
		}
		catch(Exception e){}
		try{
			String destination = createObj.getAttribute("destination");
			newAct.setDest(destination);
		}
		catch(Exception e){}
		try{
			String location = createObj.getAttribute("objLocation");
			newAct.setLoc(location);
		}
		catch(Exception e){}
		try{
			//TODO
			//String conceptual_object = createObj.getAttribute("conceptual_object");
		
		}
		catch(Exception e){}
		try{
			String destination_name = createObj.getAttribute("objName");
			newAct.setDestName(destination_name);
		}
		catch(Exception e){}
		try{
			String when = createObj.getAttribute("when");
			newAct.setWhen(when);
		}
		catch(Exception e){}
		
		NodeList params = createObj.getElementsByTagName("PARAMETER");
		Parameter[] parameters = new Parameter[params.getLength()]; 
		for (int i = 0; i < params.getLength(); i++) {
			Element param = (Element) params.item(i);
			String pName = param.getAttribute("name");
			String pType = param.getAttribute("type");
			String pTypeDetail = param.getAttribute("typeDetail");
			
			parameters[i] = new Parameter(pName, pType, pTypeDetail);
		}
		
		newAct.setParams(parameters);
		
		return newAct;
	}
	
	public static Activity getCreateArea(Element createArea) {
		Activity newAct = getBasicActivityInfo("CreateObjectActivity", createArea);
		getCreateObjActInfo(newAct, createArea);
		return newAct;
	}
	
	public static Activity getCreateAgent(Element createAgent) {
		Activity newAct = getBasicActivityInfo("CreateAgentActivity", createAgent);
		getCreateObjActInfo(newAct, createAgent);
		
		System.out.println(createAgent.toString());
		NodeList memberOf = createAgent.getElementsByTagName("MEMBEROFREF");
		String ref = ((Element) memberOf.item(0)).getAttribute("ref");
		System.out.println("ref: " + ref);
		newAct.setMemberOf(ref);
		//System.exit(1);
		return newAct;
	}
	
	public static Activity getJava(Element java) {
		Activity newAct = getBasicActivityInfo("JavaActivity", java);
		getJavaActInfo(newAct, java);
		return newAct;
	}
	
	public static Activity getComposite(Element comp) {
		Activity newAct = getBasicActivityInfo("CompositeActivity", comp);
		getCompositeActInfo(newAct, comp);
		return newAct;
	}
	
	
	public static Activity getBasicActivityInfo(String actType, Element actElem) {
		String name = actElem.getAttribute("name");
		int index = name.lastIndexOf('.');
		name = name.substring(index+1);
		index = name.lastIndexOf("$");
		name = name.substring(index+1);
		String disp = actElem.getAttribute("display");
		String max = actElem.getAttribute("maxDuration");
		String min = actElem.getAttribute("minDuration");
		String prior = actElem.getAttribute("priority");
		String rand = actElem.getAttribute("random");

		if (max.equals(""))
			max = "0";
		if (min.equals(""))
			min = "0";
		if (prior.equals(""))
			prior = "0";
		if (rand.equals(""))
			rand = "false";
		 		
		Activity newAct = new Activity(actType, name, disp,
				max, min, prior, rand);
		
		NodeList paramList = actElem.getElementsByTagName("PARAMETER");
		//Parameter[] params = new Parameter[paramList.getLength()];
		List <Parameter> paramSet = new ArrayList<Parameter>();
		for(int i = 0; i < paramList.getLength() ;i++){
			Element param = (Element) paramList.item(i);
			//Check if it is the correct set of parameters.  If it is a composite activity then parameters of its children's activities
			//will also be picked up by getElementsByTagName, so we need to check if their parent (<Parameters>) has the correct parent actElem
			Element grandParent = (Element) param.getParentNode().getParentNode();
			if(grandParent.getAttribute("name").equals(actElem.getAttribute("name"))){
				paramSet.add(new Parameter(param.getAttribute("name"), param.getAttribute("type"), "")); 
				//params[i] = new Parameter(param.getAttribute("name"), param.getAttribute("type"), "");
			}
		}
		Parameter[] params = paramSet.toArray(new Parameter[paramSet.size()]);
		newAct.setParams(params);
		
		return newAct;
	}
	
	//get move specific info = location
	public static void getMoveActInfo(Activity act, Element move) {
		String loc = move.getAttribute("location");
		int index = loc.lastIndexOf('.');
		loc = loc.substring(index+1);
		act.setLoc(loc);
	}
	
	//get communicate specific info
	public static void getCommActInfo(Activity act, Element comm) {
		//get when
		String when = comm.getAttribute("when");
		act.setWhen(when);
		//get with
		getWithTo(act, comm, "WITHREF");
		//get 'send' transfer defs
		List<Guard> transdefs = getSendTransferDefs(comm, "SEND");
		List<Guard> receiveDefs = getSendTransferDefs(comm, "RECEIVE");
		transdefs.addAll(receiveDefs);
		//get 'receive' transfer defs
		/*NodeList receiveList = comm.getElementsByTagName("RECEIVE");
		for (int j = 0; j < receiveList.getLength(); j++) {
			Element sendItem = (Element) receiveList.item(j);
			NodeList operandList = sendItem.getElementsByTagName("OPERAND");
			Element operandLHS = (Element) operandList.item(0);
			String objRef = operandLHS.getAttribute("objRef");
			int index = objRef.lastIndexOf(".");
			objRef = objRef.substring(index+1, objRef.length());
			//objRef = objRef.replace(packageName, "");
			String attRef = operandLHS.getAttribute("attRef");
			int strIndex = attRef.lastIndexOf(".");
			attRef = attRef.substring(strIndex+1);
			//note: this doesn't get the RHS, but RHS is ignored in Brahms
			transdefs.add(new String[] {"receive", objRef, attRef});
		}*/
		act.setAbout(transdefs);
	}
	
	//get broadcast specific info
	public static void getBroadActInfo(Activity act, Element broad) {
		//get when
		String when = broad.getAttribute("when");
		act.setWhen(when);
		//get 'send' transfer defs
		List<Guard> transdefs = getSendTransferDefs(broad, "SEND");
		act.setAbout(transdefs);
		//get to
		getWithTo(act, broad, "TOAREAREF");
	}
	
	//get 'send' transfer definitions
	public static List<Guard> getSendTransferDefs(Element elem, String SendReceive) {
		List<Guard> transdefs = new ArrayList<Guard>();
		NodeList sendList = elem.getElementsByTagName(SendReceive);
		for (int j = 0; j < sendList.getLength(); j++) {
			boolean isRelation = false;
			Element sendItem = (Element) sendList.item(j);
			NodeList valComp = sendItem.getElementsByTagName("VALUECOMPARISON");
			if(valComp.getLength() < 1){
				valComp = sendItem.getElementsByTagName("OBJECTCOMPARISON");//Check for object comp
				if(valComp.getLength() > 0)//if there is one then it is a relation
					isRelation = true;
			}
			NodeList operandList = sendItem.getElementsByTagName("OPERAND");
			Element operandLHS = (Element) operandList.item(0);
			Element val = (Element) valComp.item(0);
			String relation = "eq"; //set as default
			try{
				relation = val.getAttribute("relation");
			}
			catch(Exception e){
			}
			//System.out.println("relation is " + relation);
			String LHSobjRef = operandLHS.getAttribute("objRef");
			int index = LHSobjRef.lastIndexOf(".");
			LHSobjRef = LHSobjRef.substring(index+1, LHSobjRef.length());
			String LHSattRef = operandLHS.getAttribute("attRef");
			int strIndex = LHSattRef.lastIndexOf(".");
			LHSattRef = LHSattRef.substring(strIndex+1);
			String LHSobjType = operandLHS.getAttribute("objType");
			String LHStype = operandLHS.getAttribute("type");
			String RHSobjRef = "";
			String RHSattRef = "";
			String RHSobjType = "";
			String RHStype = "";
			if(operandList.getLength() > 1){
				Element operandRHS = (Element) operandList.item(1);
				RHSobjRef = operandRHS.getAttribute("objRef");
				index = RHSobjRef.lastIndexOf(".");
				RHSobjRef = RHSobjRef.substring(index+1, RHSobjRef.length());
				RHSattRef = operandRHS.getAttribute("attRef");
				strIndex = RHSattRef.lastIndexOf(".");
				RHSattRef = RHSattRef.substring(strIndex+1);
				RHSobjType = operandRHS.getAttribute("objType");
				RHStype = operandRHS.getAttribute("type");
			}
			Guard tempGuard = new Guard("", relation, "", LHStype, RHStype, "", LHSobjRef, LHSattRef, RHSobjRef, RHSattRef, "", LHSobjType, RHSobjType, "", "", "", "");
			tempGuard.setSendReceive(SendReceive);
			if(isRelation)
				tempGuard.setIsRelationTrue();
			//note: this doesn't get the RHS, but RHS is ignored in Brahms
			//transdefs.add(new String[] {"send", objRef, attRef});
			transdefs.add(tempGuard);
		}
		return transdefs;
	}
	
	//set with/to for communicate/broadcast
	public static void getWithTo(Activity act, Element elem, String withTo) {
		List<String> withList = new ArrayList<String>();
		NodeList withRefList = elem.getElementsByTagName(withTo);
		for (int j = 0; j < withRefList.getLength(); j++) {
			Element with = (Element) withRefList.item(j);
			String ref = with.getAttribute("ref"); //with agent/object
			int index = ref.lastIndexOf(".");
			ref = ref.substring(index+1, ref.length());
			//ref = ref.replace(packageName, "");
			withList.add(ref);
		}
		act.setWith(withList);
	}
	
	//get getAct specific info
	public static void getGetActInfo(Activity act, Element get) {
		//get when
		String when = get.getAttribute("when");
		act.setWhen(when);
		//get items
		List<String> items = getItems(get);
		act.setItems(items);
		//get source
		String source = get.getAttribute("source");
		int index = source.lastIndexOf(".");
		source = source.substring(index+1);
		act.setSourceDest(source);
	}
	
	//get putAct specific info
	public static void getPutActInfo(Activity act, Element get) {
		//get when
		String when = get.getAttribute("when");
		act.setWhen(when);
		//get items
		List<String> items = getItems(get);
		act.setItems(items);
		//get destination
		String dest = get.getAttribute("destination");
		int index = dest.lastIndexOf(".");
		dest = dest.substring(index+1);
		act.setSourceDest(dest);
	}
	
	public static void getJavaActInfo(Activity act, Element java) {
		//get when
		String when = java.getAttribute("when");
		act.setWhen(when);
		//get class
		String cls = java.getAttribute("class");
		act.setClassName(cls);
	}
	
	//TODO
	public static void getCreateObjActInfo(Activity act, Element createObj) {
		//action; //new or copy
		String action = createObj.getAttribute("action");
		act.setAction(action);
		//source; //(conceptual) classname, (conceptual) objectname, param
		String source = createObj.getAttribute("source");
		if(source.contains(".")){
			int index = source.lastIndexOf(".");
			source = source.substring(index+1);
		}
		act.setSource(source);
		//destination;
		String dest = createObj.getAttribute("destination");
		act.setDest(dest);
		//destName;
		String destName = createObj.getAttribute("objName");
		act.setDestName(destName);
		//get when
		String when = createObj.getAttribute("when");
		act.setWhen(when);
		//location;
		String location = createObj.getAttribute("objLocation");
		act.setLoc(location);
		//conceptualObjs;
		
	}
	
	//TODO
	public static void getCompositeActInfo(Activity act, Element comp) {
		//endcondition; //detectable or nowork
		String endCond = comp.getAttribute("endCondition");
		act.setEndCondition(endCond);
	}
	
	public static List<String> getItems(Element elem) {
		List<String> items = new ArrayList<String>();
		NodeList itemList = elem.getElementsByTagName("ITEMREF");
		for (int j = 0; j < itemList.getLength(); j++) {
			Element item = (Element) itemList.item(j);
			String itemRef = item.getAttribute("ref");
			int index = itemRef.lastIndexOf(".");
			itemRef = itemRef.substring(index+1, itemRef.length());
			//itemRef = itemRef.replace(packageName, "");
			items.add(itemRef);
		}
		return items;
	}
}
