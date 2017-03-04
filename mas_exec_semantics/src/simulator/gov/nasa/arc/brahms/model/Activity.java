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

package gov.nasa.arc.brahms.model;

/**
 * Base activity class where an activities names, parameters, etc., are defined.
 */

import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.*;
import gov.nasa.arc.brahms.simulator.Utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Activity implements Cloneable{
	
	protected String name;
	protected String display;
	protected ParameterValue displayVal;
	
	//TODO: @nsr the types of these can be extensible
	protected int max_duration; 
	protected ParameterValue max_durationVal;
	protected int min_duration;
	protected ParameterValue min_durationVal;
	protected int priority; // default needs to be negative!! in case activities set priority
	protected ParameterValue priorityVal;
	protected int level;
	protected boolean random;
	protected ParameterValue randomVal;

	protected List<Parameter> params;
	
	private HashMap<String, Parameter> nameToParameterMap = 
					new HashMap<String, Parameter>();
	
	
	public Value getParameterValue(String pName) {
		if(nameToParameterMap.size() == 0 ||
				!nameToParameterMap.containsKey(pName))
			return null;
		return nameToParameterMap.get(pName).getVals().get(0);
	}
	
	public Parameter getParameterName(String pName) {
		if(nameToParameterMap.size() == 0 ||
				!nameToParameterMap.containsKey(pName))
			return null;
		
		return nameToParameterMap.get(pName);
	}
	
	public void populateParameterMap() {
		nameToParameterMap.clear();
		for(Parameter p : params) {
			nameToParameterMap.put(p.getName(), p);
		}
	}
	
	public Activity(String name, String display, int max_duration,
			int min_duration, int priority, boolean random,
			ArrayList<Parameter> params) {
		this.name = name;
		this.display = display;
		this.max_duration = max_duration;
		this.min_duration = min_duration;
		this.priority = priority;
		this.random = random;
		this.randomVal = null;
		this.params = params;
		this.level = 0;
		populateParameterMap();

	}
	
	public int getDefaultMaxDuration() {
		return this.max_duration;
	}
	
	public ParameterValue getDefaultMaxDurationParamVal() {
		return this.max_durationVal;
	}
	
	public Activity(Value nm, Value disp, Value max,
			Value min, Value prior, Value rand,
			ArrayList<Parameter> parameters) {
		if (nm instanceof StringValue)
			this.name = ((StringValue) nm).getStringValue();
		else throw new RuntimeException("***ERROR: issue with Activity name declaration");
		if (disp instanceof StringValue)
			this.display = ((StringValue) disp).getStringValue();
		else if (disp instanceof ParameterValue) {
			this.display = null;
			displayVal = (ParameterValue) disp;
		} else throw new RuntimeException("***ERROR: issue with Activity display declaration");
		if (max instanceof IntegerValue)
			this.max_duration = ((IntegerValue) max).getIntValue();
		else if (max instanceof ParameterValue) {
			this.max_duration = Constants.PARAMETER_VAL;
			max_durationVal = (ParameterValue) max;
		} else throw new RuntimeException("***ERROR: issue with Activity max_duration declaration");
		if (min instanceof IntegerValue)
			this.min_duration = ((IntegerValue) min).getIntValue();
		else if (min instanceof ParameterValue) {
			min_duration = Constants.PARAMETER_VAL;
			min_durationVal = (ParameterValue) min;
		} else throw new RuntimeException("***ERROR: issue with Activity min_duration declaration");
		if (prior instanceof IntegerValue)
			this.priority = ((IntegerValue) prior).getIntValue();
		else if (prior instanceof ParameterValue) {
			priority = Constants.PARAMETER_VAL;
			priorityVal = (ParameterValue) prior;
		} else throw new RuntimeException("***ERROR: issue with Activity priority declaration");
		if (rand instanceof BooleanValue) {
			this.random = ((BooleanValue) rand).getBooleanValue();
			this.randomVal = null;
		} else if (rand instanceof ParameterValue) {
			random = false;
			randomVal = (ParameterValue) rand;
		} else throw new RuntimeException("***ERROR: issue with Activity random declaration");
		this.params = parameters;
		this.level = 0;
		populateParameterMap();
	}
	
	
	
	public List<Parameter> getParams() {
		return params;
	}
	
	public Parameter getParameter(int index) {
		if(index >= params.size()) return null;
		return params.get(index);
	}
	
	public String getName() {
		return name;
	}
	
/*	public String getDisplay(Basic b) {
		if (display == null) {
			Parameter p = getParentParam(b, displayVal.getName());
			if (p == null)
				throw new RuntimeException("***ERROR: no parameter exists");
			String s = "";
			try {
				s = ((StringValue) p.getVals().get(0)).getStringValue();
			} catch (Exception e) {
				throw new RuntimeException("***ERROR: display parameter needs to be a string");
			}
			return s;
		} else
			return display;
	}*/
	
	/**
	 * Finds the maximum duration of an activity.  If the `max_duration' is Constants.PARAMETER_VAL then it is
	 * obtained through a parameter, if not it simply returns the value.
	 * @param b
	 * @return
	 *//*
	public int getMaxDuration(Basic b){ 
		if (max_duration == Constants.PARAMETER_VAL) {
			//Get the value from the parameter
			Parameter p = getParentParam(b, max_durationVal.getName());
			int max = getDuration(b, p);
			return max;
		} else
			return max_duration;
	}
	
	*//**
	 * Finds the minimum duration of an activity.  If the `max_duration' is Constants.PARAMETER_VAL then it is
	 * obtained through a parameter, if not it simply returns the value.  
	 * @param b
	 * @return
	 *//*
	public int getMinDuration(Basic b) {
		if (min_duration == Constants.PARAMETER_VAL) {
			Parameter p = getParentParam(b, min_durationVal.getName());
			int min = getDuration(b, p);
			return min;
		} else
			return min_duration;
	}*/
	
	/**
	 * If the duration is a parameter then it converts the value to an integer
	 * @param b
	 * @param p
	 * @return
	 */
	public int getDuration(Basic b, Parameter p){
		if (p == null)
			throw new RuntimeException("***ERROR: no parameter exists");
		int dur;
		try {
			//Try to convert it to an integer
			dur = ((IntegerValue) p.getVals().get(0)).getIntValue();
		} catch (Exception e) {
			//if that fails then try to get the value from the string value
			try{
				dur = Integer.parseInt(p.getVals().get(0).toString());
			}catch (Exception e2) {
				//If that also fails then try to look through the workframes
				//variable values for the value
				Stack<Object> o = b.getCurrentWorkFrame();
				WorkFrame f = (WorkFrame) o.peek();
				List<Map<String, List<Value>>> varVals = f.getVarInstances();
				Map<String, List<Value>> var = varVals.get(0);
				Value v = var.get(p.getVals().get(0).toString()).get(0);
				try{
					dur = Integer.parseInt(v.toString());
				}
				catch(Exception e3){
					throw new RuntimeException("***ERROR: can not find parameter value");
				}
			}
		}
		return dur;
	}
	
	/**
	 * Gets how deeply nested the activity is
	 * @return
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Set the nesting level
	 * @param i
	 */
	public void setLevel(int i) {
		level = i;
	}
	
	/**
	 * Sets the values of the parameters 
	 * @param ps a list of parameters passed in
	 * @param b the current agent or object
	 * @param f the workframe containing the activity
	 */
	public void setParams(List<Value> ps, Basic b, Frame f){ 
		Map<String, List<Value>> map;
		try {
			//Get all var instances from the frame
			map = f.getVarInstances().get(0);
		} catch (IndexOutOfBoundsException e) {
			map = null;
		}
		//Loop through all the parameters
		for (int i = 0; i < ps.size(); i++) {
			Value v = ps.get(i);
			if (v instanceof ParameterValue) {
				if (map != null) {
					ParameterValue pv = (ParameterValue) v;
					//If the map contains the parameter name
					if(map.containsKey(pv.getName())){
						//Set parameter values
						List<Value> parameter = map.get(pv.getName());				
						params.get(i).setVals(parameter);
						params.get(i).setIsVar(true);
					}
					else{
						List <Value> newVals = new ArrayList<Value>();
						newVals.add(ps.get(i));
						params.get(i).setVals(newVals);
						params.get(i).setIsVar(false);
					}
				}
				else{
					ParameterValue pv = (ParameterValue) v;
					Parameter param = Utils.findParameter(b, f, pv);
					if(param != null){
						List<Value> val = param.getVals();
						params.get(i).setVals(val);
					}
					else{
						List <Value> newVals = new ArrayList<Value>();
						newVals.add(ps.get(i));
						params.get(i).setVals(newVals);
						params.get(i).setIsVar(false);
						//else throw new NullPointerException("***ERROR in Activity: parameterValue doesn't have a value");
					}
				}
			} else if (v instanceof VariableValue) {
				List<Value> vals = f.getVarInstanceVal(((VariableValue) v).getVarName());
				params.get(i).setVals(vals);
				params.get(i).setIsVar(false);
			} else {
				params.get(i).setVals(ps.get(i).getValue(b, f));
				params.get(i).setIsVar(false);
			}
		}
	}
	
/*	*//**
	 * Get the priority of the activity, this is only if it is a composite activity
	 * @param b
	 * @return
	 *//*
	public int getPriority(Basic b) {
		//If the value is Constants.PARAMETER_VAL then it is a parameter
		if (priority == Constants.PARAMETER_VAL) {
			Parameter p = getParentParam(b, max_durationVal.getName());
			if (p == null)
				throw new RuntimeException("***ERROR: no parameter exists");
			int prior;
			try {
				prior = ((IntegerValue) p.getVals().get(0)).getIntValue();
			} catch (Exception e) {
				throw new RuntimeException("***ERROR: priority parameter needs to be an int");
			}
			return prior;
		} else
			return priority;
	}*/
	
	/**
	 * Activities have a minimum and a maximum duration and a boolean flag called random.  If random
	 * is set to true then a random duration between the min and max is selected, if not then the
	 * max duration is selected.  This method is for retrieving the random value
	 * @param b
	 * @return
	 */
	/*public boolean isRandom(Basic b){ 
		if (randomVal != null) {
			Parameter p = getParentParam(b, randomVal.getName());
			if (p == null)
				throw new RuntimeException("***ERROR: no parameter exists");
			boolean rand;
			try {
				rand = ((BooleanValue) p.getVals().get(0)).getBooleanValue();
			} catch (Exception e) {
				throw new RuntimeException("***ERROR: random parameter needs to be a bool");
			}
			return rand;
		} else
			return random;
	}*/
	
	/**
	 * Used to turn the activity into a string for printing purposes
	 */
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append("Name: " + name + "\n");
		if (display == null)
			retVal.append("Display: " + displayVal.getName() + "\n");
		else
			retVal.append("Display: " + display + "\n");
		if (max_duration == Constants.PARAMETER_VAL)
			retVal.append("max_duration: " + max_durationVal.getName() + "\n");
		else
			retVal.append("max_duration: " + max_duration + "\n");
		if (min_duration == Constants.PARAMETER_VAL)
			retVal.append("min_duration: " + min_durationVal.getName() + "\n");
		else
			retVal.append("min_duration:" + min_duration + "\n");
		if (priority == Constants.PARAMETER_VAL)
			retVal.append("priority: " + priorityVal.getName() + "\n");
		else
			retVal.append("priority:" + priority + "\n");
		if (randomVal != null)
			retVal.append("random: " + randomVal.getName() + "\n");
		else
			retVal.append("random: " + random + "\n");
			retVal.append("params: " + params + "\n");
		return retVal.toString();
	}
	
	
	
	/**
	 * Used to clone activity so agents inheriting this activity will have a separate instance
	 * to the parent group and sibling agents
	 * @throws CloneNotSupportedException 
	 */
	public Object clone() throws CloneNotSupportedException {
		Activity act = ((Activity) super.clone());
		act.name = new String(name);
		if (display != null)
			act.display = new String(display);
		List<Parameter> newParams = new ArrayList<Parameter>();
		Iterator<Parameter> paramIt = params.iterator();
		while (paramIt.hasNext()) {
			Parameter param = paramIt.next();
			Parameter param2 = (Parameter) param.clone();
			newParams.add(param2);
		}
		act.params = newParams;
		return act;
	}
	
	public boolean getRandom(){
		return random;
	}
	
}
