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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ConceptPieces.Event;
import ConceptPieces.Guard;
import ConceptPieces.Frame;
import ConceptPieces.Detect;
import ConceptPieces.DetectType;
import ConceptPieces.Parameter;
import ConceptPieces.VariableParse;
import ConceptPieces.VarQuant;

public class ParseFrames {
	protected static Element root;
	protected static String packageName;
	protected static boolean DEBUG = false;

	public static Frame[] parseWorkframes(Element r, String pn) {
		root = r;
		packageName = pn;
		NodeList wfList = root.getElementsByTagName("WORKFRAME");
		Frame[] wfs = new Frame[wfList.getLength()]; //list to hold wfs
		for (int i = 0; i < wfList.getLength(); i++) {
			Element wf = (Element)wfList.item(i);
			Frame newWf = getBasicFrameInfo(wf); //get basic info
			//System.out.println("operating on workframe: " + newWf.getName());
			//get preconditions
			getSetPreconditions(wf, newWf);
	
			//get frame's components
			getSetEvents(wf, newWf);
			//getSetVariables(wf, newWf);
			getSetDectectables(wf, newWf);
			wfs[i] = newWf;
		}
		return wfs;
	}
	
	public static Frame getFrame(Element wf) {
		Frame newWF = getBasicFrameInfo(wf);
		if(DEBUG)
			System.out.println("parsing workframe: " + newWF.getName());
		
		//Check if workframe is empty, not sure why someone would do this but hey
		//get preconditions
		getSetPreconditions(wf, newWF);
		//get events
		getSetEvents(wf, newWF);
		//get variables
		getSetVariables(wf, newWF);
		//get detectables
		getSetDectectables(wf, newWF);
		return newWF;
	}

	
	public static Frame getBasicFrameInfo(Element f) {
		String name = f.getAttribute("name");
		//System.out.println(name);
		int index = name.lastIndexOf('.');
		name = name.substring(index+1); //brahms.bob.wf_watchTv -> wf_watchTV
		String disp = f.getAttribute("display");
		String type = f.getAttribute("wftype"); //factframe or dataframe
		String prior = f.getAttribute("priority");
		String repeat = f.getAttribute("repeat");
		if (prior.equals("")) //default prior = 0
			prior = "0";
		if (repeat.equals("")) //default repeat = false
			repeat = "false";
		if (type.equals("")) //default type = 1 (factframe)
			type = "1";
		Frame newf = new Frame(name, disp, type, repeat, prior);
		return newf;
	}
	
/*	public static void getSetPreconditions(Element f, Frame newF) {

		NodeList guards = f.getElementsByTagName("PRECONDITION");
		newF.setGuardLength(guards.getLength());
		for (int j = 0; j < guards.getLength(); j++) {
			Element guard = (Element) guards.item(j); //next precondition
			String modifier = guard.getAttribute("modifier"); //knownval/not
			NodeList compare = guard.getElementsByTagName("VALUECOMPARISON");
			Element comp = (Element) compare.item(0); //should only be 1
			String math = comp.getAttribute("relation"); //eq, >, 
			NodeList expr = comp.getElementsByTagName("EXPRESSION");
			System.out.println("expr.length = " + expr.getLength());
			Element exp0 = (Element) expr.item(0);
			System.out.println(exp0.getAttribute("operator"));
			Element exp1 = (Element) expr.item(1); //2 expressions, lhs rhs
			String connector1 = exp1.getAttribute("operator"); //possibly *, +
			NodeList operands0 = exp0.getElementsByTagName("OPERAND");
			NodeList operands1 = exp1.getElementsByTagName("OPERAND");
			Element op0 = (Element)operands0.item(0); //only 1 operand on lhs
			Element op1 = (Element)operands1.item(0);
			Element op2 = (Element)operands1.item(1); //try??
			String opType0 = op0.getAttribute("type"); //OA or V
			String opType1 = op1.getAttribute("type");
			String opObj0 = op0.getAttribute("objRef"); //objRef
			int index = opObj0.lastIndexOf('.');
			opObj0 = opObj0.substring(index+1);
			String opAtt0 = op0.getAttribute("attRef"); //attRef
			index = opAtt0.lastIndexOf('.');
			opAtt0 = opAtt0.substring(index+1);
			String opObj1 = op1.getAttribute("objRef"); //try?
			index = opObj1.lastIndexOf('.');
			opObj1 = opObj1.substring(index+1);
			String opAtt1 = op1.getAttribute("attRef"); //try?
			index = opAtt1.lastIndexOf('.');
			opAtt1 = opAtt1.substring(index+1);
			String opVal1Type = op1.getAttribute("valueType"); //try?
			String opVal1 = op1.getAttribute("value");
			Guard tempg = new Guard(modifier, math, connector1, opType0,
					opType1, opVal1Type, opObj0, opAtt0, opObj1, opAtt1,
					opVal1);
			if (op2 != null) {
				String opType2 = op2.getAttribute("type");
				String opObj2 = op2.getAttribute("objRef");
				index = opObj2.lastIndexOf('.');
				opObj2 = opObj2.substring(index+1);
				String opAtt2 = op2.getAttribute("attRef");
				index = opAtt2.lastIndexOf('.');
				opAtt2 = opAtt2.substring(index+1);
				String opVal2Type = op2.getAttribute("valueType");
				String opVal2 = op2.getAttribute("value");
				tempg.addExp3(opType2, opVal2Type, opObj2, opAtt2, opVal2);
			}
			newF.addGuard(tempg, j);
		}
	}
*/	

	public static void getSetPreconditions(Element f, Frame newF) {
		if(DEBUG)
			System.out.println("Getting preconditions");
		NodeList guards = f.getElementsByTagName("PRECONDITION");
		newF.setGuardLength(guards.getLength());
		for (int j = 0; j < guards.getLength(); j++) {
			boolean isRelation = false;
			Element guard = (Element) guards.item(j); //next precondition
			
			String modifier = guard.getAttribute("modifier"); //knownval/not
			NodeList compare = guard.getElementsByTagName("VALUECOMPARISON");
			if(compare.getLength() == 0){
				compare = guard.getElementsByTagName("OBJECTCOMPARISON");
				isRelation = true;
			}
			
			Element comp = (Element) compare.item(0); //should only be 1
			String math = comp.getAttribute("relation"); //eq, >, 
			
		
			NodeList expr = comp.getElementsByTagName("EXPRESSION");
			//System.out.println("expr.length = " + expr.getLength());
			
			/*Left hand side of guard R.S*/
			Element exp0 = (Element) expr.item(0);
			NodeList operands0 = exp0.getElementsByTagName("OPERAND");
			Element op0 = (Element)operands0.item(0); //only 1 operand on lhs

			String opType0 = op0.getAttribute("type"); //OA or V
			String opObjType0 = op0.getAttribute("objType"); //if there is a variable
			String opObj0 = op0.getAttribute("objRef"); //objRef
			String opAtt0 = op0.getAttribute("attRef"); //attRef
			String opCollectionIndex0 = op0.getAttribute("collectionIndex");
			String opCollectionIndexType0 = op0.getAttribute("collectionIndexType");
			
			
			
			
			
			//Fix structure of objRef R.S
			int index = opObj0.lastIndexOf('.');
			opObj0 = opObj0.substring(index+1);
			
			//Fix structure of attRef R.S
			index = opAtt0.lastIndexOf('.');
			opAtt0 = opAtt0.substring(index+1);
			
		
			/*Right hand side of the guard*/
			if(!(modifier.equals("known") || modifier.equals("unknown"))){
				Element exp1 = (Element) expr.item(1); //2 expressions, lhs rhs
				NodeList operands1 = exp1.getElementsByTagName("OPERAND");
				Element op1 = (Element)operands1.item(0);
				
				String connector1 = exp1.getAttribute("operator"); //possibly *, +
				String opType1 = op1.getAttribute("type");
				String opObjType1 = op1.getAttribute("objType"); //if there is a variable
				String opObj1 = op1.getAttribute("objRef"); //try?
				String opAtt1 = op1.getAttribute("attRef"); //try?
				String opVal1Type = op1.getAttribute("valueType"); //try?
				String opVal1 = op1.getAttribute("value");
				String opCollectionIndex1 = op1.getAttribute("collectionIndex");
				String opCollectionIndexType1 = op1.getAttribute("collectionIndexType");
				
				
				//Fix structure of objRef R.S
				index = opObj1.lastIndexOf('.');
				opObj1 = opObj1.substring(index+1);
				
				//Fix structure of attRef R.S
				index = opAtt1.lastIndexOf('.');
				opAtt1 = opAtt1.substring(index+1);
				
				
				
				Guard tempg = new Guard(modifier, math, connector1, opType0,
						opType1, opVal1Type, opObj0, opAtt0, opObj1, opAtt1,
						opVal1, opObjType0, opObjType1,
						opCollectionIndexType0, opCollectionIndex0, opCollectionIndexType1, opCollectionIndex1);
				
				if(isRelation)
					tempg.setIsRelationTrue();
				
				//RHS2
				Element op2 = (Element)operands1.item(1); //try??
				
				if (op2 != null) { //If there is a 2nd RHS
					String opType2 = op2.getAttribute("type");
					String opObjType2 = op2.getAttribute("objType");
					String opObj2 = op2.getAttribute("objRef");
					index = opObj2.lastIndexOf('.');
					opObj2 = opObj2.substring(index+1);
					String opAtt2 = op2.getAttribute("attRef");
					index = opAtt2.lastIndexOf('.');
					opAtt2 = opAtt2.substring(index+1);
					String opVal2Type = op2.getAttribute("valueType");
					String opVal2 = op2.getAttribute("value");
					tempg.addExp3(opType2, opVal2Type, opObj2, opAtt2, opVal2, opObjType2);
				}
				newF.addGuard(tempg, j);
			}
			else{
				Guard tempg = new Guard(modifier, math, "", opType0,
						"", "", opObj0, opAtt0, "", "",
						"", opObjType0, "", "", "", "", "");
				if(isRelation)
					tempg.setIsRelationTrue();
				newF.addGuard(tempg, j);
			}			
		}
	}
	
/*	public static Guard getSetCondition(Element f) {


		
		
	
	}*/
	
	public static void getSetEvents(Element f, Frame newF) {
		NodeList doList = f.getElementsByTagName("WF_DO");
		if (doList == null || doList.getLength() < 1)
			doList = f.getElementsByTagName("TF_DO");
		if (doList != null && doList.getLength() > 0){
			Element doBody = (Element) doList.item(0); //get body of wf_do
			NodeList events = doBody.getChildNodes();
			Node n = doBody.getFirstChild();
			Node sib = n.getNextSibling();
			int length = (events.getLength() - 1) / 2;
			if(DEBUG)
				System.out.println("Workframe is " + newF.getName());
			newF.setEventLength(length);
			for (int k = 0; k < length; k++) {
				boolean isRelation = false;
				Element event = (Element) sib;
				if (event.getTagName().equals("ACTIVITYREF")) {
					String actName = event.getAttribute("ref");
					int index = actName.indexOf("(");
					actName = actName.substring(0, index);
					NodeList actParams = event.getElementsByTagName("PARAMVAL");
					Parameter[] params;
					boolean hasParams = false;
					if(actParams.getLength() > 0){
						hasParams = true;
						params = new Parameter[actParams.getLength()];
						for(int m=0; m<actParams.getLength() ;m++){
							Element elementParam = (Element)actParams.item(m);
							String paramName = elementParam.getAttribute("value");
							String paramType = elementParam.getAttribute("valueType");
							params[m] = new Parameter(paramName, paramType, "");
						}
						
					}
					else{
						params = null;
					}
					Event newEvent = new Event("activity", actName);
					if(hasParams)
						newEvent.setParams(params);
					newF.addEvent(newEvent, k);
				}
				else { //event == consequence
					NodeList valCompList = event.getElementsByTagName("VALUECOMPARISON");
					NodeList condition = event.getElementsByTagName("CONDITION");
					Element cond = (Element) condition.item(0);
					String truthVal = cond.getAttribute("truthValue");
					boolean truthValue = true;
					if(truthVal != null){
						//System.out.println(truthVal);
						if(truthVal.equals("false"))
							truthValue = false;
					}
					if(valCompList.getLength() == 0){
						valCompList = event.getElementsByTagName("OBJECTCOMPARISON");
						isRelation = true;
					}
					Element valComp = (Element) valCompList.item(0);
					NodeList expressions = valComp.getElementsByTagName("EXPRESSION");
					NodeList operands = valComp.getElementsByTagName("OPERAND");
					
					//LHS
					Element lhs = (Element) operands.item(0);
					String lhsAtt = lhs.getAttribute("attRef");
					int strIndex = lhsAtt.lastIndexOf(".");
					lhsAtt = lhsAtt.substring(strIndex+1);
					
					String lhsObj = lhs.getAttribute("objRef");
					int strIndex1 = lhsObj.lastIndexOf(".");
					lhsObj = lhsObj.substring(strIndex1+1);
					
					// allows for handling of concludes of maps; update a value at specific key
					String collectionType = lhs.getAttribute("collectionIndexType");
					String collectionIndex = lhs.getAttribute("collectionIndex");
					//if(collectionType.equals("string") && collectionIndex != "") {
					//	lhsAtt += "(" + collectionIndex + ")";
					//}	
					
					String lhsObjType0 = lhs.getAttribute("objType"); //if there is a variable
	
					Event newEvent = new Event("consequence", 
							valComp.getAttribute("relation"),
							lhsObj,
							//lhs.getAttribute("objRef"), 
							lhsAtt);
					newEvent.setIsRelation(isRelation);
					newEvent.setLhsExpType(lhs.getAttribute("type"));
					newEvent.setTruthVal(truthValue);
					if(collectionType != null && collectionIndex != null){
						newEvent.setLhsCollectionIndex(collectionIndex);
						newEvent.setLhsCollectionType(collectionType);
					}
					if (event.hasAttribute("factCertainty"))
						newEvent.setFC(event.getAttribute("factCertainty"));
					if (event.hasAttribute("beliefCertainty"))
						newEvent.setBC(event.getAttribute("beliefCertainty"));
					
					
					newEvent.setLHSObjType(lhsObjType0);
					//RHS
					Element rhs = (Element) expressions.item(1);
					if(operands.getLength() > 1){
						if (rhs.hasAttribute("operator"))
							newEvent.setRhsOp(rhs.getAttribute("operator"));
						
						Element rhs1 = (Element) operands.item(1);
						
						newEvent.setRhsExpType(rhs1.getAttribute("type"));
						
						//RHS1
						if (rhs1.getAttribute("type").equals("OA")) { //obj.att
							String rhsObj = rhs1.getAttribute("objRef");
							int strIndex2 = rhsObj.lastIndexOf(".");
							rhsObj = rhsObj.substring(strIndex2+1);
							newEvent.setRhsObj1(rhsObj);
							
							String rhs1Att = rhs1.getAttribute("attRef");
							int tmpInd = rhs1Att.lastIndexOf(".");
							newEvent.setRhsAtt1(rhs1Att.substring(tmpInd+1));
							String rhsObjType1 = rhs1.getAttribute("objType"); //if there is a variable
							newEvent.setRHSObjType(rhsObjType1);
						}
						else if(rhs1.getAttribute("type").equals("O")){
							String rhsObj = rhs1.getAttribute("objRef");
							int strIndex2 = rhsObj.lastIndexOf(".");
							rhsObj = rhsObj.substring(strIndex2+1);
							newEvent.setRhsObj1(rhsObj);
							String rhs1Att = rhs1.getAttribute("attRef");
							int tmpInd = rhs1Att.lastIndexOf(".");
							String rhsObjType1 = rhs1.getAttribute("objType"); //if there is a variable
							newEvent.setRHSObjType(rhsObjType1);
							newEvent.setRhsAtt1(rhs1Att.substring(tmpInd+1));	
						}
						else { //val
							newEvent.setRhsValType1(rhs1.getAttribute("valueType"));
							newEvent.setRhsVal1(rhs1.getAttribute("value"));
						}
						
						//RHS2
						if(operands.getLength() > 2){
							Element rhs2 = (Element) operands.item(2);
							String rhsObjType2 = rhs2.getAttribute("objType"); //if there is a variable
							newEvent.setRhsExpType2(rhs2.getAttribute("type"));
							newEvent.setRHSObj2Type(rhsObjType2);
							if (rhs2.getAttribute("type").equals("OA")) { //obj.att
								newEvent.setRhsObj2(rhs2.getAttribute("objRef"));
								String rhs2Att = rhs2.getAttribute("attRef");
								int tmpInd = rhs2Att.lastIndexOf(".");
								newEvent.setRhsAtt2(rhs2Att.substring(tmpInd+1));
								
								
							} else { //val
								if(rhsObjType2.equals("variable") || rhsObjType2.equals("parameter")){
									newEvent.setRhsObj2(rhs2.getAttribute("objRef"));
								}
								else{		
									newEvent.setRhsValType2(rhs2.getAttribute("valueType"));
									newEvent.setRhsVal2(rhs2.getAttribute("value"));
								}
							}
						}
					}	
					newF.addEvent(newEvent, k);
				}
				sib = sib.getNextSibling();
				sib = sib.getNextSibling();
			}
		}
	}
	
	public static void getSetVariables(Element f, Frame newF) {
		String name;
		VarQuant quant = VarQuant.FORONE; //default to FORONE
		String type;
		String display;
		
		NodeList varList = f.getElementsByTagName("VARIABLE"); //Find all variables in XML
		VariableParse[] vars = new VariableParse[varList.getLength()]; //list to hold variables
		if(vars.length > 0){
			if(DEBUG)
				System.out.println(varList.getLength() + " variables found:");
			for(int i = 0; i < varList.getLength(); i++){
				Element variable = (Element) varList.item(i); //get variable
				name = variable.getAttribute("name");
				type = variable.getAttribute("type");
				//Identify which type of quantification it is using ENUM
				String tempQuant = variable.getAttribute("quantification");
				if(tempQuant.equals("forone"))
					quant = VarQuant.FORONE;
				if(tempQuant.equals("foreach"))
					quant = VarQuant.FOREACH;
				if(tempQuant.equals("collectall"))
					quant = VarQuant.COLLECTALL;
				display = variable.getAttribute("display");
				
				VariableParse v = new VariableParse(name, quant, type, display);
				vars[i] = v;
				if(DEBUG){
					System.out.println("    name:" + name);
					System.out.println("    type:" + type);
					System.out.println("    quant:" + quant);
					System.out.println("    display:" + display + "\n");
				}
			}
		}
		newF.addVars(vars);
		
	}
	
	public static void getSetDectectables(Element f, Frame newF) {
		String name;
		String when;
		String act;
		int certainty = 100;//Default is 100
		DetectType action = null;
		
		NodeList detList = f.getElementsByTagName("DETECTABLE"); //Find all detectables in XML
		Detect[] dets = new Detect[detList.getLength()]; //list to hold Detectables
		//System.out.println("Workframe " + newF.getName() + " has " + detList.getLength() + " detectables ");
		for (int i = 0; i < detList.getLength(); i++) { //Loop through all detectables
			Element det = (Element)detList.item(i); //Detectable i
			name = det.getAttribute("name"); //Find the name
			when = det.getAttribute("when"); //Find the when value
			String cert = det.getAttribute("certainty");//Find the certainty
			if(!cert.equals(""))
				certainty = Integer.parseInt(cert);
			act = det.getAttribute("action"); //Find the action
			if(act.equals("abort")) //Assign action to appropriate Enum
				action = DetectType.ABORT;
			else if(act.equals("continue"))
				action = DetectType.CONTINUE;
			else if(act.equals("impasse"))
				action = DetectType.IMPASSE;
			else if (act.equals("complete"))
				action = DetectType.COMPLETE;
			else if(act.equals("end_activity"))
				action = DetectType.END_ACTIVITY;
			else
				System.out.println("Error: undefined action " + act);
			
		//	Guard condition = getSetCondition(det); //Find the detectables firing condition, store as a Guard

			//Find firing condition
			NodeList conds = f.getElementsByTagName("CONDITION");
			//newF.setGuardLength(guards.getLength());
			Element con = (Element) conds.item(i); //get condition, all conditions from all detectables are taken into account
			String modifier = "knownval";//Detectables always use knownval
			NodeList compare = con.getElementsByTagName("VALUECOMPARISON");
			boolean value = true;
			//System.out.println(compare.getLength());
			if(compare.getLength() == 0){
				compare = con.getElementsByTagName("OBJECTCOMPARISON");
				value = false;
			}
			
			Element comp = (Element) compare.item(0); //should only be 1
			String math = comp.getAttribute("relation"); //eq, >, 
			int index = math.lastIndexOf('.');
			math = math.substring(index+1);
			
		
			NodeList expr = comp.getElementsByTagName("EXPRESSION");
			//System.out.println("expr.length = " + expr.getLength());
			
			/*Left hand side of guard R.S*/
			Element exp0 = (Element) expr.item(0);
			NodeList operands0 = exp0.getElementsByTagName("OPERAND");
			Element op0 = (Element)operands0.item(0); //only 1 operand on lhs

			String opType0 = op0.getAttribute("type"); //OA or V
			String opObj0 = op0.getAttribute("objRef"); //objRef
			String opAtt0 = op0.getAttribute("attRef"); //attRef
			String opCollectionIndex0 = op0.getAttribute("collectionIndex");
			String opCollectionIndexType0 = op0.getAttribute("collectionIndexType");
			
			//Fix structure of objRef R.S
			index = opObj0.lastIndexOf('.');
			opObj0 = opObj0.substring(index+1);
			
			//Fix structure of attRef R.S
			index = opAtt0.lastIndexOf('.');
			opAtt0 = opAtt0.substring(index+1);
			
		
			/*Right hand side of the guard*/
			Element exp1 = (Element) expr.item(1); //2 expressions, lhs rhs

			NodeList operands1 = exp1.getElementsByTagName("OPERAND");
			Element op1 = (Element)operands1.item(0);
			
			String connector1 = exp1.getAttribute("operator"); //possibly *, +
			String opType1 = op1.getAttribute("type");
			//System.out.println(opType1);
			String opObj1 = op1.getAttribute("objRef"); //try?
			String opAtt1 = op1.getAttribute("attRef"); //try?
			String opVal1Type = op1.getAttribute("valueType"); //try?
			String opVal1 = op1.getAttribute("value");
			String opCollectionIndex1 = op1.getAttribute("collectionIndex");
			String opCollectionIndexType1 = op1.getAttribute("collectionIndexType");
			
			//Fix structure of objRef R.S
			index = opObj1.lastIndexOf('.');
			opObj1 = opObj1.substring(index+1);
			
			//Fix structure of attRef R.S
			index = opAtt1.lastIndexOf('.');
			opAtt1 = opAtt1.substring(index+1);
			
			
			
			Guard tempg = new Guard(modifier, math, connector1, opType0,
					opType1, opVal1Type, opObj0, opAtt0, opObj1, opAtt1,
					opVal1, "", "", 
					opCollectionIndexType0, opCollectionIndex0, opCollectionIndexType1, opCollectionIndex1); //Need to change to accept variables
			
			
			//RH2
			Element op2 = (Element)operands1.item(1); //try??
			
			if (op2 != null) { //If there is a 2nd RHS
				String opType2 = op2.getAttribute("type");
				String opObj2 = op2.getAttribute("objRef");
				index = opObj2.lastIndexOf('.');
				opObj2 = opObj2.substring(index+1);
				String opAtt2 = op2.getAttribute("attRef");
				index = opAtt2.lastIndexOf('.');
				opAtt2 = opAtt2.substring(index+1);
				String opVal2Type = op2.getAttribute("valueType");
				String opVal2 = op2.getAttribute("value");
				tempg.addExp3(opType2, opVal2Type, opObj2, opAtt2, opVal2, ""); //Need to add variables
			}
			//newF.addGuard(tempg, j);			
			

			Detect detectable = new Detect(name, when, action, tempg, certainty, value); //Create detectable
			dets[i] = detectable;//Add to array of detectables
			
		
		}
		newF.addDetects(dets); //Add detectables to the workframe		
	}

}
