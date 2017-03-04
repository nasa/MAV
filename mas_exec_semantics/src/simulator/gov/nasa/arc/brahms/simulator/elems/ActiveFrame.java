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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gov.nasa.arc.brahms.model.Fact;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Variable;
import gov.nasa.arc.brahms.model.comparison.Comparison;
import gov.nasa.arc.brahms.model.comparison.InstanceMap;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.CommonExpUtils;
import gov.nasa.arc.brahms.model.expression.TplObjRef;
import gov.nasa.arc.brahms.model.expression.Value;

/**

pseudocode for the check if the frame is Active.

Input Frame, Basic (Concept that owns the frame)
for each c \in Conditions
	vals_c := getVariableBindings(c);

{c_0, <v_0, {A, B, C}>, <v_1, {B, C}>}
{c_1, <v_0, {A, B, C}>, <v_1, {D, C}>}

Intersection, commonVars

foreach v_i \in V
 for each c_i \in C
    if(commonVars(v_i) == \empty)
       commonVars.add(v_i) := getVars(v_i)
    else
       commonVars.get(v_i).intersect(getVars(v_i))

v_0 := A, B, C
v_1 := C

knownval(plane.x == plane.y)

generate(vals_c) {
 

  if(vals_c == empty) return new frame(table);
 
  if(val == forone)
      	if(val \not\in table)
      	   val_rand pickrandom Val
      	   addtable(val_rand);
      	   generate(new (vals_c).remove(val);
      	   return new frame(table);
  
  for each val \in vals_c 
      if(val \not\in table)
           addtable(val)	   
      generate(new (vals_c).remove(val));     
}

**/

public class ActiveFrame {
	
	static Map<Comparison, List<InstanceMap>> possibleBindings =
			new HashMap<Comparison, List<InstanceMap>>();
	
	static Map<String, List<String>> commonVars =
			new HashMap<String, List<String>>();
	
	static List<Map<String, String>> bindingCombinations = 
			new ArrayList<Map<String, String>>();
		
	static Frame frame;
	
	static Random rand = new Random();
	
	static boolean guardEval = false;
	
	
	static List<Map<String, Value>> variableCombinations = 
			new ArrayList<Map<String, Value>>();
	
	static Map<Integer, Boolean> guardTrue = new HashMap<Integer, Boolean>();
	
	public static void binding(int indexOfGuard, Map<String, Value> varCtx,
										Frame frame, Basic b) {
		
		if(getNumberOfPreconditions(frame) == indexOfGuard) {
			HashMap<String, Value> copyVarCtx = new HashMap<String, Value>();
			for(String key : varCtx.keySet()) {
				if(CommonExpUtils.isPrimitiveType(varCtx.get(key))) {
					copyVarCtx.put(new String(key), varCtx.get(key));
				} else {
				TplObjRef currObj = (TplObjRef) varCtx.get(key);
				TplObjRef objRef;
				if(b.getName().equals(currObj.getObjRefName()))
					objRef = new TplObjRef("current");
				else
					objRef = new TplObjRef(currObj.getObjRefName());
				copyVarCtx.put(new String(key), objRef);
				}
			}
			if(copyVarCtx.size() > 0)
				variableCombinations.add(copyVarCtx);
			guardEval = true;
			return;
		}
		
		Comparison c = frame.getPreConditions().get(indexOfGuard);
		Variable var = c.getNextFreeVariable(frame, varCtx);
	
		
		if(var == null) {
			
			if(c.eval(b, frame, varCtx)) {
				guardTrue.put(indexOfGuard, true);
				binding(indexOfGuard+1, varCtx, frame, b);
			} else {
				// not true
			}
			return;
		} 

		
		List<Value> boundValues =
					c.getVariableBindings(b, frame, var, varCtx);
		if (boundValues.size() == 0)
			return;
		
		
		if (var.getQuantifier().equals("forone")) {
			Collections.shuffle(boundValues, rand);
			for(Value randValue : boundValues) {
				varCtx.put(var.getVarName(), randValue);
				binding(indexOfGuard, varCtx, frame, b);
				varCtx.remove(var.getVarName());
				//if(guardTrue.containsKey(indexOfGuard)) {
				//	guardTrue.remove(indexOfGuard);
				//	break;
				//}
				if(allFuturePreconditionsSatisfied(frame, indexOfGuard))
					break;
			}
		} else if (var.getQuantifier().equals("foreach")) {
			
			for (Value singleVal : boundValues) {
				varCtx.put(var.getVarName(), singleVal);
				binding(indexOfGuard, varCtx, frame, b);
				varCtx.remove(var.getVarName());
			}
		} else { //this is for collectall
			for (Value singleVal : boundValues) {
				varCtx.put(var.getVarName(), singleVal);
				binding(indexOfGuard, varCtx, frame, b);
				varCtx.remove(var.getVarName());
			}
		}

	}
	
	private static int getNumberOfPreconditions(Frame f) {
		return f.getPreConditions().size();
	}
	
	private static boolean allFuturePreconditionsSatisfied(Frame f, int indexOfGuard) {
		int numPreconditions = getNumberOfPreconditions(f);
		for(int i = indexOfGuard; i < numPreconditions; i++) {
			if(!guardTrue.containsKey(i)) return false;
			guardTrue.remove(i);
		}
		return true;
	}
	
	public static boolean isFrameActive(Basic b, Frame frame) {
		guardEval = false;
		guardTrue.clear();
		variableCombinations.clear();
		frame.removeAllVarValues();
		frame.removeAllInstances();
		binding(0, new HashMap<String, Value>(), frame, b);
		List<Map<String, List<Value>>> varInstances = new ArrayList<Map<String, List<Value>>>();
		
		
		for(Map<String, Value> varCom : variableCombinations) {
			Map<String, List<Value>> varVals = new HashMap<String, List<Value>>();
			for(String varIndex : varCom.keySet()) {
				List<Value> possibleValues = new ArrayList<Value>();	
				possibleValues.add(varCom.get(varIndex));
				varVals.put(varIndex, possibleValues);
			}
			if(varInstances.size() == 0)
				varInstances.add(varVals);
			else {
			if(!ActiveFrame.foundVars(frame, varInstances, varVals))
				varInstances.add(varVals);		
			}
		}
		
		
		if(varInstances.size() > 0)  {
			frame.setVarInstances(varInstances);
		}
		//System.out.println("check in the guard"  + varInstances.toString());
		return guardEval;
		
	}
	
	
	public static boolean isForOneVariable(Frame f, String varName) {
		for(Variable var : f.getVariables()) {
			if(var.getVarName().equals(varName) &&
					var.getQuantifier().equals("forone"))
				return true;
		}
		return false;
	}
	
	public static boolean foundVars(Frame frame, List<Map<String, List<Value>>> varInstances, 
								Map<String, List<Value>> varVals) {
		assert(varInstances.size() >= 1);
		boolean found = true;
		for(Map<String, List<Value>> oneInstance : varInstances) {
			found = true;
			if(oneInstance.size() != varVals.size()) {
				found = false;
				continue;
			}
			for(String varName : varVals.keySet()) {
				//System.out.println("varName" + varName);
				if(!oneInstance.containsKey(varName)) {
					found = false;
					continue;
				}
				//if it differs just in the forone var no need to 
				//continue checking
				if(ActiveFrame.isForOneVariable(frame, varName))
					continue;
				
				List<Value> possVals = varVals.get(varName);
				List<Value> instVals = oneInstance.get(varName);
				if(possVals.size() != instVals.size()) {
					found = false;
					continue;
				}
				//System.out.println("poss vals " + possVals.toString());
				//System.out.println(" instant vals " + instVals.toString())	;
				for(Value i : possVals) {
					boolean secondFound = false;
					for(Value j : instVals) {
						if(i.toString().equals(j.toString())) {
							secondFound = true;
							break;
						}
					}
					if(!secondFound) {
						found = false;
						break;
					}
				}
			}
			if(found) return true;
		}
		return found;
	}
	
	public static boolean isFrameActive(Basic b, Frame frame, 
								Map<String, List<Fact>> facts) {
		return isFrameActive(b, frame);
	}
	
}
