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

/**
 * @author nrungta & jhunter
 * 
 * broadcast-activity 	::= 	broadcast PAC.activity-name (
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
{ to : [ ARE.area-name | PAC.param-name ] [ , [ ARE.area-name | PAC.param-name ] ]* ; }
{ toSubAreas : [ ID.truth-value | PAC.param-name ] ; }
about : TDF.transfer-definition [ , TDF.transfer-definition ]* ;
{ when : [ start | end | PAC.param-name ] ; }
}



 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.BooleanValue;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.expression.StringValue;

public class BroadcastActivity extends Activity
							   implements Cloneable{
	
	protected List<TransferDefinition> transferDefs;
	protected List<String> areas;
	protected String when; //start, end, param name;
	protected ParameterValue whenVal;
	protected boolean toSubAreas;
	protected ParameterValue toSubAreasVal;

	public BroadcastActivity(String name, String display, int max_duration,
			int min_duration, int priority, boolean random,
			ArrayList<Parameter> params, String when) {
		super(name, display, max_duration, min_duration, 
				priority, random, params);
		this.transferDefs = new ArrayList<TransferDefinition>();
		this.areas = new ArrayList<String>();
		this.when = when;
		this.toSubAreas = true;
	}
	
	public BroadcastActivity(Value name, Value display, Value max_duration,
			Value min_duration, Value priority, Value random,
			ArrayList<Parameter> params, Value when) {
		super(name, display, max_duration, min_duration, 
				priority, random, params);
		this.transferDefs = new ArrayList<TransferDefinition>();
		this.areas = new ArrayList<String>();
		if (when instanceof StringValue)
			this.when = ((StringValue) when).getStringValue();
		else if (when instanceof ParameterValue) {
			this.whenVal = (ParameterValue) when;
			this.when = null;
		} else throw new RuntimeException("Broadcast when needs to be a string");
		this.toSubAreas = true;
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
		} else return when;
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
	
	public void addTransferDefinition(TransferDefinition td) {
		transferDefs.add(td);
	}
	
	public void addAreas(String area) {
		areas.add(area);
	}
	
	public void setAreas(List<String> allAreas) {
		for (int i = 0; i < allAreas.size(); i++) {
			areas.add(allAreas.get(i));
		}
	}
	
	public List<TransferDefinition> getTransferDefs() {
		return transferDefs;
	}
	
	public List<String> getAreas() {
		return areas;
	}
	
	public boolean getToSubAreas(Basic b) {
		if (toSubAreasVal != null) {
			Parameter p = getParentParam(b, whenVal.getName());
			if (p == null)
				throw new RuntimeException("***ERROR: no parameter exists");
			boolean bool;
			try {
				bool = ((BooleanValue) p.getVals().get(0)).getBooleanValue();
			} catch (Exception e) {
				throw new RuntimeException("***ERROR: when parameter needs to be a string");
			}
			return bool;
		}
		return toSubAreas;
	}
	
	protected void setToSubAreas(boolean choice) {
		toSubAreas = choice;
	}
	
	protected void setToSubAreas(Value choice) {
		if (choice instanceof BooleanValue) {
			toSubAreas = ((BooleanValue) choice).getBooleanValue();
			toSubAreasVal = null;
		} else if (choice instanceof ParameterValue) {
			toSubAreasVal = (ParameterValue) choice;
		} else throw new RuntimeException("to sub areas needs to be boolean");
	}
	
	public String toString(){	
		return "Broadcast Acitivity:" +
						super.toString() +
						"-------\n";
	}
	
	@Override
	public Object clone(){
		try {
			BroadcastActivity act = ((BroadcastActivity) super.clone());
			act.name = new String(name);
			if(display != null)
				act.display = new String(display);
			act.when = new String(when);
			act.toSubAreas = toSubAreas;
			
			List<String> newWith = new ArrayList<String>();
			Iterator <String> withIt = areas.iterator();
			while(withIt.hasNext()){
				String w = withIt.next();
				String w2 = new String (w);
				newWith.add(w2);
			}
			act.areas = newWith;
			
			List<TransferDefinition> newDefs = new ArrayList<TransferDefinition>();
			Iterator<TransferDefinition> transIt = transferDefs.iterator();
			while(transIt.hasNext()){
				TransferDefinition td = transIt.next();
				TransferDefinition td2 = (TransferDefinition) td.clone();
				newDefs.add(td2);
			}
			act.transferDefs = newDefs;
			
			//act.params = (List<Parameter>) params.clone();
			List<Parameter> newParams = new ArrayList<Parameter>();
			Iterator<Parameter> paramIt = params.iterator();
			while(paramIt.hasNext()){
				Parameter param = paramIt.next();
				Parameter param2 = (Parameter) param.clone();
				newParams.add(param2);
				
			}
			if(whenVal != null)
				act.whenVal = new ParameterValue(whenVal.toString());
			if(toSubAreasVal != null)
				act.toSubAreasVal = new ParameterValue(toSubAreasVal.toString());
			
			act.params = newParams;
			return act;
		}catch (Exception e) {
			System.out.println("Error with activity " + name + " of type " + this.getClass() + " with message "+e);
			return null;
		}
	}
}
