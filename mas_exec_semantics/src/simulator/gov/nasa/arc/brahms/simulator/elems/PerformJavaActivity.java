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

package gov.nasa.arc.brahms.simulator.elems;

import gov.nasa.arc.brahms.model.Event;
import gov.nasa.arc.brahms.model.EventActivity;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.activity.JavaActivity;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.simulator.Utils;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class PerformJavaActivity {
	
	static AbstractExternalActivity javaact;
	static String className;
	static Method method;
	static Object dynamicObj;
	
	public static void performJava(JavaActivity ja, Basic b, Frame f) {	
		initializeAbstractExternalActivityInstance(ja, b);
		setupParameters(ja);

		try {
			method.invoke(dynamicObj);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		updateVariables(ja, f);

	}
	
	//TODO: need to distinguish between parameters and variables
	//this will come into play when there are composite activities
	//some variables will be in the 
	private static void updateVariables(JavaActivity ja,
			Frame f) {

		List<Event> events = f.getEvents();
		EventActivity ea = null;
		for(Event event : events) {
			if(event instanceof EventActivity) { 
				EventActivity eventAct = (EventActivity) event;
				if(ja.getName().equals(eventAct.getName())) {
					ea = eventAct;
				}
			}
		}
		
		assert(ea != null);
		
		List<Parameter> params = ja.getParams();
		List<Value> eventParams = ea.getParams();
		for (int paramIndex = 0; paramIndex < eventParams.size(); paramIndex++) {
			Value val = eventParams.get(paramIndex);
			if(!(val instanceof ParameterValue)) continue;
			ParameterValue paramVal = (ParameterValue) val;
			Parameter jactParam = params.get(paramIndex);
			if(f.getVarInstances() != null &&
						f.variableExists(paramVal.getName())) {
				Map<String, List<Value>> map = f
						.getVarInstances().get(0);
				map.put(paramVal.getName(), jactParam.getVals());
			}
		}
	}

	private static void initializeAbstractExternalActivityInstance
	(JavaActivity ja, Basic b) {
		AbstractExternalActivity.initialize(Utils.getMas(), ja, b);
		className = ja.getJavaClass(b);
		try {
			Class<?> dynamicClass = Class.forName(className);
			dynamicObj = dynamicClass.newInstance();
			javaact = (AbstractExternalActivity) dynamicObj;
			method = dynamicClass.getMethod("doActivity");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void setupParameters(JavaActivity ja){ 
		List<Parameter> params = ja.getParams();
		for (int paramIdx = 0; paramIdx < params.size(); paramIdx++) {
			AbstractExternalActivity.addParameter(params.get(paramIdx));
		}
	}


}
