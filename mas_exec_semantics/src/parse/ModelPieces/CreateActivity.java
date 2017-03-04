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

import ConceptPieces.Activity;
import ConceptPieces.Parameter;

public class CreateActivity {
	Activity act; 
	String body;
	boolean actionParam = false;
	boolean sourceParam = false;
	boolean destParam = false;
	boolean destNameParam = false;
	boolean locParam = false;
	
	public CreateActivity(Activity act) {
		this.act = act;
		body = new String();
		if(act.getParams() == null || act.getParams().length ==0)  return;

		Parameter[] params = act.getParams();
		for(int paramIdx = 0; paramIdx < params.length; paramIdx++) {
			if(act.getAction().equals(params[paramIdx].getName())){
				actionParam = true;
			}
			if(act.getSource().equals(params[paramIdx].getName())){
				sourceParam = true;
			}
			if(act.getDest().equals(params[paramIdx].getName())){
				destParam = true;
			}
			if(act.getDestName().equals(params[paramIdx].getName())){
				destNameParam = true;
			}
			if(act.getLoc().equals(params[paramIdx].getName())){
				locParam = true;
			}
		}
	}
	
	public String constructBody(String createConstruct, int i) {
		body += "Activity act" + i + "= new "+createConstruct+"(nameAct" + i + ", \n" +
				"dispAct" + i + ", maxAct" + i + ", minAct" + i + ", \n" +
				"priorAct" + i + ", randAct" + i + ", paramsAct" + i + ");\n";
		
		System.out.println("body  :" + body);
		if(!act.getAction().equals("") && !actionParam)
			body += "(("+createConstruct+") act" + i + ").setAction(new StringValue(\""+act.getAction()+"\"));\n";
		else if (!act.getAction().equals("")){
			body += "(("+createConstruct+") act" + i + ").setAction(new ParameterValue(\""+act.getAction()+"\"));\n";
		}
		if(!act.getSource().equals("") && !sourceParam)
			body += "(("+createConstruct+") act" + i + ").setSource(new StringValue(\""+act.getSource()+"\"));\n";
		else if(!act.getSource().equals("")){
			body += "(("+createConstruct+") act" + i + ").setSource(new ParameterValue(\""+act.getSource()+"\"));\n";
		}
		if(!act.getDest().equals("")  && !destParam){
			body += "(("+createConstruct+") act" + i + ").setDest(new StringValue(\""+act.getDest()+"\"));\n";
		}
		else if(!act.getDest().equals("")){
			body += "(("+createConstruct+") act" + i + ").setDest(new ParameterValue(\""+act.getDest()+"\"));\n";
		}
		if(!act.getDestName().equals("") && !destNameParam){
			body += "(("+createConstruct+") act" + i + ").setDestName(new StringValue(\""+act.getDestName()+"\"));\n";
		}
		else if(!act.getDestName().equals("")){
			body += "(("+createConstruct+") act" + i + ").setDestName(new ParameterValue(\""+act.getDestName()+"\"));\n";
		}
		if(!act.getLoc().equals("") && !locParam){
			body += "(("+createConstruct+") act" + i + ").setLocation(new StringValue(\""+act.getLoc()+"\"));\n";
		}
		else if(!act.getLoc().equals("")){
			body += "(("+createConstruct+") act" + i + ").setLocation(new ParameterValue(\""+act.getLoc()+"\"));\n";
		}
		if(!act.getMemberOf().equals("")) {
			body += "(("+createConstruct+") act" + i + ").setMemberOf(new SglObjRef(\""+act.getMemberOf()+"\"));\n";
			
		}
		return body;
	}
}
