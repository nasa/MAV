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
import java.util.List;
import java.util.Stack;

import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.StringValue;
import gov.nasa.arc.brahms.model.expression.Value;

/**
 * 
 * @author jhunter
 *
 * java-activity 	::= 	java PAC.activity-name (
 * 							{ PAC.param-decl [ , PAC.param-decl ]* } )
 * 							{
 * 							{ display : ID.literal-string ; }
 * 							{ priority : [ ID.unsigned | PAC.param-name ] ; }
 * 							{ random : [ ID.truth-value | PAC.param-name ] ; }
 * 							{ min_duration : [ ID.unsigned | PAC.param-name ] ; }
 * 							{ max_duration : [ ID.unsigned | PAC.param-name ] ; }
 * 							{ PAC.resources }
 * 							class : [ ID.literal-string | PAC.param-name ] ;
 * 							{ when : [ start | end | PAC.param-name ] ; }
 * 							}
 */
public class JavaActivity extends Activity {
	
	protected String when; //start, end (default)
	protected ParameterValue whenVal = null;
	protected String javaClass;
	protected ParameterValue javaClassVal = null;

	public JavaActivity(String name, String display, int max_duration,
			int min_duration, int priority, boolean random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, 
												priority, random, params);
		when = "end";
	}
	
	public JavaActivity(Value name, Value display, Value max_duration,
			Value min_duration, Value priority, Value random,
			ArrayList<Parameter> params) {
		super(name, display, max_duration, min_duration, 
												priority, random, params);
		when = "end";
	}
	
	public void setWhen(String w) {
		when = w;
	}
	
	public void setWhen(Value w) {
		if (w instanceof StringValue)
			when = ((StringValue) w).getStringValue();
		else if (w instanceof ParameterValue) {
			when = null;
			whenVal = (ParameterValue) w;
		} else throw new RuntimeException("java when must be string");
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
	
	public void setJavaClass(String jc) {
		javaClass = jc;
	}
	
	public void setJavaClass(Value jc) {
		if (jc instanceof StringValue)
			javaClass = ((StringValue) jc).getStringValue();
		else if (jc instanceof ParameterValue) {
			javaClass = null;
			javaClassVal = (ParameterValue) jc;
		} else throw new RuntimeException("java class must be string");
	}
	
	public String getJavaClass(Basic b) {
		if (javaClassVal != null) {
			Parameter p = getParentParam(b, javaClassVal.getName());
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
		return javaClass;
	}
	
	public String toString(){	
		return "Java Activity:" +
						super.toString() +
						"-------\n";
	}
}
