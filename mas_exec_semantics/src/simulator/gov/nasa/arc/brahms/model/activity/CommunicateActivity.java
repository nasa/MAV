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

package gov.nasa.arc.brahms.model.activity;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.expression.StringValue;

/**
 * 
 * @author jhunter
 *communicate-activity 	::= 	communicate PAC.activity-name (
{ PAC.param-decl [ , PAC.param-decl ]* } )
{
{ display : ID.literal-string ; }
{ priority : [ ID.unsigned | PAC.param-name ] ; }
{ random : [ ID.truth-value | PAC.param-name ] ; }
{ min_duration : [ ID.unsigned | PAC.param-name ] ; }
{ max_duration : [ ID.unsigned | PAC.param-name ] ; }
{ PAC.resources }
{ type : [ phone | fax | email | face2face | terminal |
pager | none | PAC.param-name ] ; }
with : [ [ AGT.agent-name |OBJ.object-name |
PAC.param-name ] [ , [ AGT.agent-name |
OBJ.object-name | PAC.param-name ] ]* ;
about : TDF.transfer-definition [ , TDF.transfer-definition ]* ;
{ when : [ start | end | PAC.param-name ] ; }
}
 */

public class CommunicateActivity extends Activity {
	
	List<String> with;
	protected String when; //default = end
	protected ParameterValue whenVal;
	List<TransferDefinition> defs; 
	
	public CommunicateActivity(String name, String display, int max_duration,
			int min_duration, int priority, boolean random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, 
												priority, random, params);
		defs = new ArrayList<TransferDefinition>();
		this.with = new ArrayList<String>();
		this.when = "end";

	}
	
	public CommunicateActivity(String name, String display, int max_duration,
			int min_duration, int priority, boolean random,
			ArrayList<Parameter> params, double weight, String sensoryType) {
		super(name, display, max_duration, min_duration, 
												priority, random, params);
		defs = new ArrayList<TransferDefinition>();
		this.with = new ArrayList<String>();
		this.when = "end";
		
	}
	
	public CommunicateActivity(Value name, Value display, Value max_duration,
			Value min_duration, Value priority, Value random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, 
												priority, random, params);
		defs = new ArrayList<TransferDefinition>();
		this.with = new ArrayList<String>();
		this.when = "end";
	}

	public CommunicateActivity(Value name, Value display, Value max_duration,
			Value min_duration, Value priority, Value random,
			ArrayList<Parameter> params, double weight, String sensoryType) {
		super(name, display, max_duration, min_duration, 
												priority, random, params);
		defs = new ArrayList<TransferDefinition>();
		this.with = new ArrayList<String>();
		this.when = "end";
		
	}
	
	public void setWith(List<String> with) {
		this.with = with;
	}

	public List<String> getWith() {
		return with;
	}
	
	public void setWhen(String when) {
		this.when = when;
	}
	
	public void setWhen(Value when) {
		if (when instanceof StringValue)
			this.when = ((StringValue) when).getStringValue();
		else if (when instanceof ParameterValue) {
			this.when = null;
			this.whenVal = (ParameterValue) when;
		} else throw new RuntimeException("communicate: when needs to be string");
	}
	
	public String getWhen(Basic b) {
		if (when == null) {
			Parameter p = getParentParam(b, whenVal.getName());
			if (p == null)
				throw new RuntimeException("***ERROR: no parameter exists");
			String s = "";
			try {
				s = ((StringValue) p.getVals().get(0)).getStringValue();
			} catch (Exception e) {
				throw new RuntimeException("***ERROR: when parameter needs to be a string");
			}
			return s;
		}
		return when;
	}
	
	private Parameter getParentParam(Basic b, String paramName) {
		if (b == null)
			return null;
		Stack<Object> stack = b.getCurrentWorkFrame();
		if (stack.size() > 1) {
			for (int i = stack.size() - 1; i >= 0; i--) {
				if (stack.get(i) instanceof ActivityInstance) {
					ActivityInstance ai = (ActivityInstance) stack.get(i);
					if (ai.getActivity() instanceof CompositeActivity) {
						CompositeActivity comp = (CompositeActivity) ai.getActivity();
						List<Parameter> parameters = comp.getParams();
						for (int j = 0; j < parameters.size(); j++) {
							if (parameters.get(j).getName().equals(paramName)) {
								return parameters.get(j);
							}
						}
					}
				}
			}
		} else throw new RuntimeException("***ERROR: couldn't find parameter for activity " + name);
		return null;
	}
	
	public void addTransferDefinition(TransferDefinition tf) {
		defs.add(tf);
	}
	
	public List<TransferDefinition> getTransferDef() {
		return defs;
	}
	
	public String toString(){	
		StringBuilder retVal = new StringBuilder();
		retVal.append("communicate:" + super.toString());
		retVal.append("with: " + with + "\n");
		retVal.append("about; \n");
		for(int defIndex = 0; defIndex < defs.size(); defIndex++) {
			TransferDefinition td = defs.get(defIndex);
			retVal.append(td.toString());
		}
		retVal.append("when: " + when + "\n");
		retVal.append("-------\n");
		return retVal.toString();
	}
	
	
	
	@Override
	public Object clone(){
		try {
			CommunicateActivity act = ((CommunicateActivity) super.clone());
			
			act.name = new String(name);
			if(display != null)
				act.display = new String(display);
			act.when = new String(when);
			
			List<String> newWith = new ArrayList<String>();
			Iterator <String> withIt = with.iterator();
			while(withIt.hasNext()){
				String w = withIt.next();
				String w2 = new String (w);
				newWith.add(w2);
			}
			act.with = newWith;
			
			List<TransferDefinition> newDefs = new ArrayList<TransferDefinition>();
			Iterator<TransferDefinition> transIt = defs.iterator();
			while(transIt.hasNext()){
				TransferDefinition td = transIt.next();
				TransferDefinition td2 = (TransferDefinition) td.clone();
				newDefs.add(td2);
			}
			act.defs = newDefs;
		
			List<Parameter> newParams = new ArrayList<Parameter>();
			Iterator<Parameter> paramIt = params.iterator();
			while(paramIt.hasNext()){
				Parameter param = paramIt.next();
				Parameter param2 = (Parameter) param.clone();
				newParams.add(param2);
				
			}
			if(whenVal != null)
				act.whenVal = new ParameterValue(whenVal.toString());
			act.params = newParams;
			return act;
		}catch (Exception e) {
			System.out.println("Error in communicate activity " + name + " with message" + e);
			return null;
		}
	}
	
}
