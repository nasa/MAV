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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.StringValue;
import gov.nasa.arc.brahms.model.expression.Value;


/**
 * 
 * @author josie
 * create-object-activity 	::= 	create_object PAC.activity-name (
 * 									{ PAC.param-decl [ , PAC.param-decl ]* } )
 * 									{
 * 									{ display : ID.literal-string ; }
 * 									{ priority : [ ID.unsigned | PAC.param-name ] ; }
 * 									{ random : [ ID.truth-value | PAC.param-name ] ; }
 * 									{ min_duration : [ ID.unsigned | PAC.param-name ] ; }
 * 									{ max_duration : [ ID.unsigned | PAC.param-name ] ; }
 * 									{ PAC.resources }
 * 									action : [ new | copy | PAC.param-name ] ;
 * 									source : [ CLS.class-name |
 * 										OBJ.object-name | 
 * 										COC.conceptual-object-name | 
 * 										COB.conceptual-object-name | 
 * 										PAC.param-name ] ;
 * 									destination : [PAC.param-name ] ;	
 * 									{ destination_name : [ ID.literal-symbol | PAC.param-name ] ; }
 * 									{ location : [ ARE.area-name | PAC.param-name ] ; }			
 * 									{ conceptual_object :
 * 									[ COB.conceptual-object-name | PAC.param-name ]
 * 									[ , [ COB.conceptual-object-name | PAC.param-name ] ]* ; }
 * 									{ when : [ start | end | PAC.param-name ] ; }
 * 									}
 */
public class CreateObjectActivity extends CreateConceptActivity {
	protected String action; //new or copy
	protected ParameterValue actionVal = null;
	protected Set<String> conceptualObjs;


	public CreateObjectActivity(String name, String display, int max_duration,
			int min_duration, int priority, boolean random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, priority, random, 
				params);
		when = "end";
		location = "";
		action = "new";
		conceptualObjs = new HashSet<String>();
		destName = "";
	}

	public CreateObjectActivity(Value name, Value display, Value max_duration,
			Value min_duration, Value priority, Value random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, priority, random, 
				params);
		when = "end";
		location = "";
		action = "new";
		conceptualObjs = new HashSet<String>();
		destName = "";
	}

	public void setAction(String s) {
		action = s;
	}

	public void setAction(Value s) {
		if (s instanceof StringValue)
			action = ((StringValue) s).getStringValue();
		else if (s instanceof ParameterValue) {
			action = null;
			actionVal = (ParameterValue) s;
		} else throw new RuntimeException("***ERROR: action needs to be string");

	}

	

	public void addConceptObj(String s) {
		conceptualObjs.add(s);
	}
	
	private Parameter getParentParam(Basic b, String paramName) {
		if (b == null)
			return null;
		List<Parameter> parameters = super.getParams();
		for (int j = 0; j < parameters.size(); j++) {
			if (parameters.get(j).getName().equals(locVal.toString())) {
				return parameters.get(j);
			}
		}
		Stack<Object> stack = b.getCurrentWorkFrame();
		if (stack.size() > 1) {
			for (int i = stack.size() - 1; i >= 0; i--) {
				if (stack.get(i) instanceof ActivityInstance) {
					ActivityInstance ai = (ActivityInstance) stack.get(i);
					if (ai.getActivity() instanceof CompositeActivity) {
						CompositeActivity comp = (CompositeActivity) ai.getActivity();
						parameters = comp.getParams();
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

	public String getAction(Basic b) {
		if (actionVal != null) {
			Parameter p = getParentParam(b, actionVal.getName());
			if (p == null)
				throw new RuntimeException("***ERROR: no parameter exists");
			String s = "";
			try {
				s = ((StringValue) p.getVals().get(0)).getStringValue();
			} catch (Exception e) {
				throw new RuntimeException("***ERROR: action parameter needs to be a string");
			}
			return s;
		}
		return action;
	}

	public String getSource(Basic b) {
		if (sourceVal != null) {
			Parameter p = getParentParam(b, sourceVal.getName());
			if (p == null)
				throw new RuntimeException("***ERROR: no parameter exists");
			String s = "";
			try {
				s = ((StringValue) p.getVals().get(0)).getStringValue();
			} catch (Exception e) {
				throw new RuntimeException("***ERROR: source parameter needs to be a string");
			}
			return s;
		}
		return source;
	}

	public ParameterValue getDestination() {
		return destination;
	}

	public String getDestName(Basic b) {
		if (destNameVal != null) {
			Parameter p = getParentParam(b, destNameVal.getName());
			if (p == null)
				throw new RuntimeException("***ERROR: no parameter exists");
			String s = "";
			try {
				s = ((StringValue) p.getVals().get(0)).getStringValue();
			} catch (Exception e) {
				throw new RuntimeException("***ERROR: destName parameter needs to be a string");
			}
			return s;
		}
		return destName;
	}

	public String getWhen(Basic b) {
		if (whenVal != null) {
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

	public String getLocation(Basic b) {
		if (locVal != null) {
			Parameter p = getParentParam(b, locVal.getName());
			if (p == null)
				throw new RuntimeException("***ERROR: no parameter exists");
			String s = "";
			try {
				s= p.getVals().toString();
			} catch (Exception e) {
				throw new RuntimeException("***ERROR: loc parameter needs to be a string");
			}
			return s;
		}
		return location;
	}

	public Set<String> getConceptObjs() {
		return conceptualObjs;
	}

	public String toString(){	
		return "Create Object Acitivity:" +
				super.toString() +
				"-------\n";
	}
}
