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

import gov.nasa.arc.brahms.model.activity.CreateAreaActivity;
import gov.nasa.arc.brahms.model.activity.CreateAgentActivity;
import gov.nasa.arc.brahms.model.activity.CreateObjectActivity;
import gov.nasa.arc.brahms.model.activity.JavaActivity;
import gov.nasa.arc.brahms.model.comparison.Comparison;
import gov.nasa.arc.brahms.model.comparison.EvalValComp;
import gov.nasa.arc.brahms.model.comparison.EvalValCompExpOpExp;
import gov.nasa.arc.brahms.model.comparison.NoValComparison;
import gov.nasa.arc.brahms.model.comparison.RelComp;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.expression.VariableValue;
import gov.nasa.arc.brahms.simulator.elems.WorkFrame_Sim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public abstract class Frame implements Cloneable{
	
	protected String name;
	protected String display;
	protected String repeat;
	
	protected int priority; // default;
	protected boolean executed; //has the frame been executed?
	
	protected List<Variable> variables;
	protected Map<String, List<Value>> varValues;
	protected List<Map<String, List<Value>>> varInstances;
	protected List<Map<String, List<Value>>> executedInstances;
	
	protected List<Detectable> detectables;
	
	protected List<Comparison> preconditions;
	double decisionValue = 0;
	protected boolean bindVars;
	//need a workbody decls
	
	//this includes the list of activities and concludes
	//specified in the workframes;
	protected List<Event> events;
	
	//this is just till the regression tests are fixed -- neha
	protected boolean operateOnFacts = false;
	
	public void setOperateOnFacts() {
		operateOnFacts = true;
	}
	
	public Frame(String name, String display, 
		    int priority, String repeat) {

		this.name = name;
		this.display = display;
		this.priority = priority;
		this.repeat = repeat;
		this.executed = false;
		this.variables = new ArrayList<Variable>();
		this.varValues = new HashMap<String, List<Value>>();
		this.varInstances = new ArrayList<Map<String, List<Value>>>();
		this.executedInstances = new ArrayList<Map<String, List<Value>>>();
		this.detectables = new ArrayList<Detectable>();
		this.preconditions = new ArrayList<Comparison>();
		this.events = new ArrayList<Event>();		
	}
	
	public boolean getBindVars() {
		return bindVars;
	}
	
	public void setBindVars(boolean bind) {
		bindVars = bind;
	}
	
	public void addCondition(Comparison compare) {
		preconditions.add(compare);
	}
	
	public double getDecisionValue(){
		return decisionValue;
	}
	
	public List<Comparison> getPreConditions(){ 
		return preconditions;
	}
	
	public void addVariable(Variable var) {
		variables.add(var);
		List<Value> emptyList = new ArrayList<Value>();
		varValues.put(var.getVarName(), emptyList);
	}
	
	public List<Variable> getVariables() {
		return variables;
	}
	
	public Variable getVariable(String varName) {
		Iterator<Variable> iter = variables.iterator();
		while (iter.hasNext()) {
		  Variable tempVar = (Variable) iter.next();
		  if (tempVar.getVarName().equals(varName))
			  return tempVar;
		}
		throw new RuntimeException("getVariable: " + varName + ", no such var");
	}
	
	public boolean variableExists(String var) {
		Iterator<Variable> iter = variables.iterator();
		while (iter.hasNext()) {
		  Variable tempVar = (Variable) iter.next();
		  if (tempVar.getVarName().equals(var))
			  return true;
		}
		return false;
	}
	
	public String getVarType(String var) {
		Iterator<Variable> iter = variables.iterator();
		while (iter.hasNext()) {
		  Variable tempVar = (Variable) iter.next();
		  if (tempVar.getVarName().equals(var))
			  return tempVar.getVarType();
		}
		return null;
	}
	
	public void setVarInstances(List<Map<String, List<Value>>> varI){
		varInstances = varI;
	}
	
	public void setAllVarValues(Map<String, List<Value>> newVarVals){
		varValues = new HashMap<String, List<Value>>();
		varValues.putAll(newVarVals);
	}
	
	
	
	
	public List<Value> getVarValues(String varName) {
		return varValues.get(varName);
	}
	
	

	
	public void removeAllVarValues() {
		if(varValues != null)
			varValues.clear();
	}
	
	public void removeAllPrevBindings() {
		executedInstances.clear();
	}
	
	public void firstInstanceVarsExecuted() {
		if (!(varInstances.isEmpty())){
			executedInstances.add(varInstances.get(0));
		}
	}
	
	public void removeFirstInst() {
		if (!(varInstances.isEmpty())){
			varInstances.remove(0);
		}
	}
	
	public void removeAllInstances() {
		varInstances.clear();
	}
	
	public List<Map<String, List<Value>>> getVarInstances() {
		return varInstances;
	}
	
	public boolean hasVarInstances() {
		if (varInstances == null)
			return false;
		if (varInstances.isEmpty())
			return false;
		if (varInstances.get(0) == null)
			return false;
		if (varInstances.get(0).isEmpty())
			return false;
		return true;
	}

	public List<Value> getVarInstanceVal(String varName) {
		try{
			return varInstances.get(0).get(varName);
		}
		catch(Exception e){
			//throw new NullPointerException("No variable found with the name " + varName);
			//System.out.println("WARNING: no value found for " + this);
			return null;
		}
	}
	
	public List<Value> getVarInstanceVal(String varName, int idx) {
		return varInstances.get(idx).get(varName);
	}
	
	public void removeVarInstance(int idx) {
		varInstances.remove(idx);
	}
	
	public List<Map<String, List<Value>>> getExecInstances() {
		return executedInstances;
	}
	
	public void addDetectable(Detectable detect) {
		detectables.add(detect);
	}
	
	public void addEvent(Event event) {
		events.add(event);
	}
	
	public List<Event> getEvents() {
		return events;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public String getRepeat() {
		return repeat;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public List<Detectable> getDetectables() {
		return detectables;
	}
	
	public boolean getExecuted() {
		return executed;
	}
	
	public void setExecuted(boolean b) {
		executed = b;
	}
	
	public void setRepeatFalse() {
		repeat = "false";
	}
	
	public boolean allEmptyVarsOK(Basic obj) {
		for (int i = 0; i < varInstances.size(); i++) {
			Map<String, List<Value>> oneList = varInstances.get(i);
			for (Map.Entry<String, List<Value>> entry : oneList.entrySet())
			{
				if (entry.getValue().isEmpty()) {
					String varKey = entry.getKey(); //check if var is passed to java first - ok
					if (!(checkEmptyVarOK(obj, varKey)))
						return false;
				} //else not empty, no issue
			}
		}
		if (varInstances.isEmpty())
			return false;
		return true;
	}
	
	public void removeEmptyVars(Basic obj) {
		List<Integer> idsToRemove = new ArrayList<Integer>();
		for (int i = 0; i < varInstances.size(); i++) {
			Map<String, List<Value>> oneList = varInstances.get(i);
			for (Map.Entry<String, List<Value>> entry : oneList.entrySet())
			{
				if (entry.getValue().isEmpty()) {
					String varKey = entry.getKey(); //check if var is passed to java first - ok
					if (!(checkEmptyVarOK(obj, varKey))) {
						idsToRemove.add(i);
					}
				} //else not empty, no issue
			}
		}
		if (varInstances.isEmpty())
			return;
		int moveLeft = 0;
		for (int i = 0; i < idsToRemove.size(); i++) {
			Integer id = idsToRemove.get(i);
			varInstances.remove(id - moveLeft);
			moveLeft++;
		}
	}
	
	private boolean checkRelComp(String varKey, RelComp rel) {
		if (rel.getLhs() instanceof VariableValue) {
			VariableValue lhs = (VariableValue) rel.getLhs();
			if (lhs.getVarName().equals(varKey))
				return false;
		}
		if (rel.getRhs() instanceof VariableValue) {
			VariableValue rhs = (VariableValue) rel.getRhs();
			if (rhs.getVarName().equals(varKey))
				return false;
		}
		if (rel.getKeyVar() instanceof VariableValue) {
			VariableValue rkey = (VariableValue) rel.getKeyVar();
			if (rkey.getVarName().equals(varKey))
				return false;
		}
		return true;
	}
	
	public boolean checkEmptyVarOK(Basic obj, String varKey) {
		boolean found = false;
		for (int j = 0; j < events.size(); j++) { //search wf events
			if (!found) {
				if (inPreconditions(varKey)) //if var is in preconditions it cannot be empty
					return false;
				Event ev = events.get(j);
				if (ev instanceof EventActivity) { //activity
					EventActivity ea = (EventActivity) ev;
					//if ea is java activity or create, ok
					List<Value> params = ea.getParams();
					for (int k = 0; k < params.size(); k++) {
						if (params.get(k) instanceof ParameterValue) {
							ParameterValue pv = (ParameterValue) params.get(k);
							if (pv.getName().equals(varKey)) { //var is called in this act
								Activity act = WorkFrame_Sim.getActivity(obj, ea);
								if ((act instanceof JavaActivity) || 
										(act instanceof CreateObjectActivity) || 
										(act instanceof CreateAreaActivity) || 
										(act instanceof CreateAgentActivity))//is set in java, create acts
									found = true;
								else
									return false; //can't be used if not set!
							} //otherwise var isn't called in this activity
						}
					}
				} else { //conclude
					Conclude conc = (Conclude) ev;
					Expression result = conc.getResult();
					if (result instanceof RelComp) { //conclude is either relcomp
						RelComp rel = (RelComp) result;
						if (!(checkRelComp(varKey, rel)))
							return false;
					} else { //or evalvalcompexpopexp
						EvalValCompExpOpExp eval = (EvalValCompExpOpExp) result;
						if (!(checkEvalValCompExpOpExp(varKey, eval)))
							return false;
					}
				}
			}
		}
		return true;
	}
	
	private boolean inPreconditions(String varKey) {
		for (int i = 0; i < preconditions.size(); i++) {
			Comparison compare = preconditions.get(i);
			if (compare instanceof NoValComparison) {
				NoValComparison noval = (NoValComparison) compare;
				if (noval.getExp() instanceof VariableValue) {
					if (((VariableValue) noval.getExp()).getVarName().equals(varKey))
						return true;
				}
				if (noval.getKeyVar() instanceof VariableValue) {
					if (((VariableValue) noval.getKeyVar()).getVarName().equals(varKey))
						return true;
				}
			} else {
				EvalValComp eval = (EvalValComp) compare;
				if (eval.getExp() instanceof RelComp) {
					if (!(checkRelComp(varKey, (RelComp) eval.getExp())))
						return true;
				} else {
					if (!(checkEvalValCompExpOpExp(varKey, (EvalValCompExpOpExp) eval.getExp())))
						return true;
				}
					
			}
		}
		return false;
	}
	
	private boolean checkEvalValCompExpOpExp(String varKey, EvalValCompExpOpExp eval) {
		if (eval.getLhsExp() instanceof VariableValue) {
			VariableValue lhs = (VariableValue) eval.getLhsExp();
			if (lhs.getVarName().equals(varKey))
				return false;
		}
		if (eval.getRhsExp() instanceof VariableValue) {
			VariableValue rhs = (VariableValue) eval.getRhsExp();
			if (rhs.getVarName().equals(varKey))
				return false;
		}
		if (eval.getLKeyVar() instanceof VariableValue) {
			VariableValue lkey = (VariableValue) eval.getLKeyVar();
			if (lkey.getVarName().equals(varKey))
				return false;
		}
		if (eval.getRKeyVar() instanceof VariableValue) {
			VariableValue rkey = (VariableValue) eval.getRKeyVar();
			if (rkey.getVarName().equals(varKey))
				return false;
		}
		return true;
	}
	
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append("repeat: " + this.repeat + ";\n");
		retVal.append("priority: " + this.priority + ";\n");
		if(detectables.size() > 0) {
			retVal.append("detectables: \n");
			retVal.append(detectables.toString());
		}
		if(variables.size() > 0) {
			retVal.append("variables: \n");
			retVal.append(variables.toString());
		}
		retVal.append("when ( " + preconditions.toString() + ")\n");
		retVal.append("do { " + events.toString() + "}\n -------\n");
		return retVal.toString();
	}
	
	@SuppressWarnings("unchecked")
	public Object clone() throws CloneNotSupportedException {
		
			Frame f = ((Frame) super.clone());
			f.name = new String(name);
			f.display = new String(display);
			f.repeat = new String(repeat);
			f.priority = priority;
			f.executed = executed;
			
			Object vars = ((ArrayList<Variable>) this.variables).clone();
			f.variables = ((List<Variable>) vars);
			
			List<Detectable> dets = new ArrayList<Detectable>();
			Iterator <Detectable> detIt = detectables.iterator();
			while(detIt.hasNext()){
				Detectable d = detIt.next();
				Detectable d2 = (Detectable) d.clone();
				dets.add(d2);
			}
			f.detectables = dets;
			
			List<Event> evts = new ArrayList<Event>();
			Iterator <Event> eveIt = events.iterator();
			while(eveIt.hasNext()){
				Event e = eveIt.next();
				Event e2 = (Event) e.clone();
				evts.add(e2);
			}
			f.events = evts;
			
			List<Comparison> newPreconditions = new ArrayList<Comparison>();
			Iterator <Comparison> preConIt = preconditions.iterator();
			while(preConIt.hasNext()){
				Comparison c = preConIt.next();
				Comparison c2 = (Comparison) c.clone();
				newPreconditions.add(c2);
			}
			f.preconditions = newPreconditions;
			
			
			Map<String, List<Value>> new_varValues = new HashMap<String, List<Value>>(varValues);
			f.varValues = new_varValues;
			List<Map<String, List<Value>>> new_varInstances = new ArrayList<Map<String, List<Value>>>(varInstances);
			f.varInstances = new_varInstances;
			List<Map<String, List<Value>>> new_executedInstances = new ArrayList<Map<String, List<Value>>>(executedInstances);
			f.executedInstances = new_executedInstances;
			List<Comparison> new_preconditions = new ArrayList<Comparison>(preconditions);
			f.preconditions = new_preconditions;
			
			return f;
	}
	public abstract boolean operatesOnFacts();
}
