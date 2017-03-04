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
import org.w3c.dom.NodeList;

import ConceptPieces.Belief;

public class ParseBeliefsFacts {
	protected static Element root;
	protected static String packageName;
	protected static String agentName;
	protected static boolean warning = false;
	
	public static Belief[] parseBeliefs(Element rt, String pn, String agn) {
		root = rt;
		packageName = pn;
		agentName = agn;
		
		NodeList belList = root.getElementsByTagName("BELIEF");
		Belief[] beliefs = new Belief[belList.getLength()];
		for (int i = 0; i < belList.getLength(); i++) {
			Element b = (Element)belList.item(i);
			NodeList oavs = b.getElementsByTagName("OAV");
			for (int j = 0; j < oavs.getLength(); j++)
				beliefs[i] = parseOav((Element) oavs.item(j), false);
			NodeList oros = b.getElementsByTagName("ORO");
			for (int j = 0; j < oros.getLength(); j++)
				beliefs[i] = parseOro((Element)oros.item(j), false);
			NodeList oaos = b.getElementsByTagName("OAO"); //relexp
			for (int j = 0; j < oaos.getLength(); j++)
				beliefs[i] = parseOao((Element) oaos.item(j), false);
		}
		return beliefs;
	}
	
	public static Belief[] parseFacts(Element rt, String pn, String agn) {
		root = rt;
		packageName = pn;
		agentName = agn;
		
		NodeList factList = root.getElementsByTagName("FACT");
		Belief[] facts = new Belief[factList.getLength()];
		for (int i = 0; i < factList.getLength(); i++) {
			Element f = (Element)factList.item(i);
			NodeList oavs = f.getElementsByTagName("OAV"); //valexp
			for (int j = 0; j < oavs.getLength(); j++)
				facts[i] = parseOav((Element) oavs.item(j), true);
			NodeList oaos = f.getElementsByTagName("OAO"); //relexp
			for (int j = 0; j < oaos.getLength(); j++)
				facts[i] = parseOao((Element) oaos.item(j), true);
			NodeList oros = f.getElementsByTagName("ORO"); //relexp
			for (int j = 0; j < oros.getLength(); j++)
				facts[i] = parseOro((Element) oros.item(j), true);
		}
		return facts;
	}
	
	public static Belief parseOav(Element oav, boolean fact) {
		String obj = oav.getAttribute("lObjRef");
		obj = obj.replace(packageName, "");
		if (obj.equals("current") && fact)
			obj = agentName;
/*		else if (fact)
			System.out.println("***ERROR: facts must be for 'current' only");
*/		String att = oav.getAttribute("attRef");
		int k = att.lastIndexOf('.'); //remove the path
		att = att.substring(k+1);
		String evalOp = oav.getAttribute("evalOp");
		if (!(oav.getAttribute("collectionIndexType").equals(""))) {
			String indexType = oav.getAttribute("collectionIndexType");
			String index = oav.getAttribute("collectionIndex");
			String val, type;
			if (!(oav.getAttribute("value").equals(""))) {
				type = oav.getAttribute("valueType");
				val = oav.getAttribute("value");
				if (!(type.equals("double"))) {
					k = val.lastIndexOf('.');
					val = val.substring(k+1);
				}
			}
			else {
				type = oav.getAttribute("rObjType");
				val = oav.getAttribute("rObjRef");
				if (!(type.equals("double"))) {
					k = val.lastIndexOf('.');
					val = val.substring(k+1);
				}

			}
			return new Belief(obj, att, index, indexType, evalOp, val, type, 
					"oav");
		}
		String val = oav.getAttribute("value");
		String type = oav.getAttribute("valueType");
		String expType = "oav";
		return new Belief(obj, att, evalOp, val, type, expType);
	}
	

	
	public static Belief parseOro(Element oro, boolean fact) {
		String obj = oro.getAttribute("lObjRef");
		obj = obj.replace(packageName, "");
		if (obj.equals("current") && fact)
			obj = agentName;
/*		else if (fact)
			System.out.println("***ERROR: facts must be for 'current' only");
*/		String relRef = oro.getAttribute("relRef");
		int k = relRef.lastIndexOf('.'); //remove the path
		relRef = relRef.substring(k+1); //relation
		String rObj = oro.getAttribute("rObjRef");
		k = rObj.lastIndexOf('.');
		rObj = rObj.substring(k+1); //RobjRef
		String type = oro.getAttribute("rObjType");
		String expType = "oro";
		String tf = oro.getAttribute("truthValue");
		return new Belief(obj, relRef, rObj, type, expType, tf, 3);
	}
	
	public static Belief parseOao(Element oao, boolean fact) {
		String obj = oao.getAttribute("lObjRef");
		obj = obj.replace(packageName, "");
		if (obj.equals("current") && fact)
			obj = agentName;
		else if (fact && warning)
			System.out.println("*Warning: facts must be for 'current' only, " +
					"check fact or bel");
		String att = oao.getAttribute("attRef");
		int k = att.lastIndexOf('.');
		att = att.substring(k+1); //RobjRef
		String evalOp = oao.getAttribute("equalOp");
		String rObj = oao.getAttribute("rObjRef");
		k = rObj.lastIndexOf('.');
		rObj = rObj.substring(k+1); //RobjRef
		String type = oao.getAttribute("rObjType");
		String expType = "oao";
		
		if (!(oao.getAttribute("collectionIndexType").equals(""))) {
			String indexType = oao.getAttribute("collectionIndexType");
			String index = oao.getAttribute("collectionIndex");
			return new Belief(obj, att, index, indexType, evalOp, rObj, type, expType);
		}
		
		//String truthV = oao.getAttribute("truthValue");
		//System.out.println("found belief: " + obj + "." + att + " " + evalOp + " " + rObj);
		return new Belief(obj, att, evalOp, rObj, type, expType);
	}
}
