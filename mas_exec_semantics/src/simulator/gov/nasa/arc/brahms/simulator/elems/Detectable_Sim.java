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

import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.Conclude;
import gov.nasa.arc.brahms.model.Detectable;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Event;
import gov.nasa.arc.brahms.model.Fact;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.Variable;
import gov.nasa.arc.brahms.model.WorkFrame;
import gov.nasa.arc.brahms.model.comparison.DetectRelComp;
import gov.nasa.arc.brahms.model.comparison.DetectValComp;
import gov.nasa.arc.brahms.model.comparison.ResultComparison;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.RelationalExpression;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.TplObjRef;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.expression.ValueExpression;
import gov.nasa.arc.brahms.model.expression.VariableValue;
import gov.nasa.arc.brahms.model.DetectableAction;
import gov.nasa.arc.brahms.model.activity.ActivityInstance;
import gov.nasa.arc.brahms.model.activity.CompositeActivity;
import gov.nasa.arc.brahms.simulator.Simulator;
import gov.nasa.arc.brahms.simulator.Utils;
import gov.nasa.arc.brahms.simulator.world.FactSet;
import gov.nasa.arc.brahms.model.Frame;

import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Detectable_Sim {
	
	protected static List<Detectable> detectables;
	protected static Stack<Object> stack;
	protected static Expression newExpression;
	protected static Frame frame;
	
	public static void checkForDetectables(Basic b) {
		stack = b.getCurrentWorkFrame();
		boolean firstWF = false; //Used to determine which element is the top level workframe
		int currentLevel = 0;
		for (int i = stack.size() -1; i >= 0; i--) { //could be a wf or ai
			if (stack.get(i) instanceof WorkFrame) {
				WorkFrame wf = (WorkFrame) stack.get(i);
				if(!firstWF){
					firstWF = true;
					currentLevel = wf.getLevel(); //find the nested level of the workframe
				}
				else if(currentLevel > -1){
					//If it isn't the first and we haven't already gone below the 0th level
					//then deduct 1 
					currentLevel--;	
				}
				frame = wf;
				detectables = wf.getDetectables();
				if (!(detectables.isEmpty()) && currentLevel > -1) {
					//If a detectable fires when checked the stack will be rebuilt
					//and rechecked, so we need to stop checking the remaining detectables
					boolean cont = checkEachDetectable(b, wf);
					if(!cont){
						return;
					}
				}
			} else {
				ActivityInstance ai = (ActivityInstance) stack.get(i);
				if (ai.getActivity() instanceof CompositeActivity) {
					CompositeActivity comp = (CompositeActivity) ai.getActivity();
					detectables = comp.getDetectables();
					if (!(detectables.isEmpty())){
						//If a detectable fires when checked the stack will be rebuilt
						//and rechecked, so we need to stop checking the remaining detectables
						boolean cont = checkEachDetectable(b, comp);
						if(!cont){
							return;
						}
					}
				}
			}
		}
	}
	
	/*Loop through all the detectables and check whether any are active.  Those that are active
	 * are placed in a list.  Only one detectable can fire so we need to decide which to fire
	 * if more than one is active.  The priority is Abort > Impasse > Complete > End_Activity.  So
	 * when a detectable is active we assign an int `type' a value corresponding to the detectable type
	 * we assign a higher number for the higher priority type and we only change this value if a 
	 * higher priority detectable becomes active.  Default value of `type' is 0 for nothing active */
	public static boolean checkEachDetectable(Basic b, Object obj) {
		int type = 0;
		for (int i = 0; i < detectables.size(); i++) {
			Detectable detect = detectables.get(i);	
			if (detect.getWhen().equals("whenever")) {//or signed = now?
				ResultComparison resultComp = detect.getResultComparison();
				updateBeliefs(b, resultComp); //get facts, update beliefs
				//Increase workload if the agent 
				
				boolean guardIsTrue = checkCondition(b, resultComp);
				if (guardIsTrue) { //if resultComparison is true for some ag/obj
					//If guard is fired then increase workload again
					//check detect certainty
					if (certaintyIsTrue(detect)) {	
						//Set the detectable type, if it has the highest priority action
						if (detect.getAction() == DetectableAction.ABORT) {
							type = 4;
						}
						if (detect.getAction() == DetectableAction.IMPASSE && type < 4) {
							type = 3;
						}
						if (detect.getAction() == DetectableAction.COMPLETE && type < 3) {
							type = 2;
						}
						if (detect.getAction() == DetectableAction.END_ACTIVITY && type < 2) {
							type = 1;
						}
					}
				}
			}
		}	
		if (type == 4) {
			abortWorkFrame(b, obj);
			WorkFrame_Sim.buildWorkStack(b);
			if (Simulator.DEBUG)
				System.out.println("reloaded stack: " + b.getCurrentWorkFrame().toString());
			checkForDetectables(b);
			return false;
		}
		else if (type == 3)
			impasseWorkFrame(b, obj);
		else if (type == 2)
			completeConcludes(b, obj);
		else if (type == 1)
			endActivity(b, obj);
		else{
			//It is a `CONTINUE' or none are active so do nothing
		}
		return true;
	}

	/**
	 * Removes the workframe and related activity instance from the given stack
	 * @param stackLocation - index of the wf to be removed
	 * @param stack - currentWF stack or interruptedWF stack
	 */
	public static void abortWorkFrame(Basic b, Object obj) {
		if (obj instanceof WorkFrame) {
			WorkFrame wf = (WorkFrame) obj;
			int level = wf.getLevel();
			if (Simulator.DEBUG)	
				System.out.println("Aborting wf " + wf.getName());
			
			
			boolean withinWFToAbort = false;
			for (int i = 0; i < stack.size(); i++) {
				Object stackObj = stack.get(i);
				if (stackObj instanceof WorkFrame) {
					WorkFrame tmpWF = (WorkFrame) stackObj;
					if (tmpWF.getName().equals(wf.getName())) {
						tmpWF.resetIndex();
						tmpWF.removeAllVarValues();
						tmpWF.firstInstanceVarsExecuted();
						stack.remove(i);
						i--;
						withinWFToAbort = true;
					} else if (tmpWF.getLevel() == level) {
						withinWFToAbort = false;
					} else if (withinWFToAbort) {
						tmpWF.resetIndex();
						tmpWF.removeAllVarValues();
						tmpWF.firstInstanceVarsExecuted();
						stack.remove(i);
						i--;
					}
				}
				if (withinWFToAbort) {
					if (stackObj instanceof ActivityInstance) {
						ActivityInstance ai = (ActivityInstance) stackObj;
						if (ai.getActivity().getLevel() >= level) {
							stack.remove(i);
							i--;
						}
						
					}
				}
			}
			//or remove from impassed frames
			b.removeFromImpassed(wf);
						
			if (Simulator.DEBUG)
				System.out.println("aborted stack: " + b.getCurrentWorkFrame().toString());
		} else {
			throw new RuntimeException("Detectable_Sim: Cannot abort composite activities");
		}
	}
	
	/**
	 * Loops through the remaining events in the curr wf and executes all
	 * conclude statements, bypassing any activities
	 * @param b the agent or object
	 * @param wf the current workframe
	 */
	public static void completeConcludes(Basic b, Object obj) {
		if (obj instanceof WorkFrame) {
			WorkFrame wf = (WorkFrame) obj;

			Event currEvent = WorkFrame_Sim.getNextEvent(wf);
			while (currEvent != null) {
				if (currEvent instanceof Conclude)
					Conclude_Sim.concludeStatement(b, (Conclude) currEvent, wf);
				wf.incIndex();
				currEvent = WorkFrame_Sim.getNextEvent(wf);
			}
			abortWorkFrame(b, wf); //remove from stack
			WorkFrame_Sim.buildWorkStack(b);
			if (Simulator.DEBUG)
				System.out.println("reloaded stack: " + b.getCurrentWorkFrame().toString());
			checkForDetectables(b);
		} else {
			throw new RuntimeException("Detectable_Sim: Cannot 'complete' composite activities");
		}
	}
	
	
	public static void endActivity(Basic b, Object obj) {
		if (obj instanceof CompositeActivity) {
			CompositeActivity comp = (CompositeActivity) obj;
			Stack<Object> stack = b.getCurrentWorkFrame();
			for (int i = stack.size() - 1; i > 0; i--) {
				if (stack.get(i) instanceof ActivityInstance) {
					ActivityInstance ai = (ActivityInstance) stack.get(i);
					if (ai.getActivity().getName().equals(comp.getName())) {
						stack.pop();
						
						((WorkFrame) stack.peek()).incIndex();
						WorkFrame_Sim.pushNextEvent(b, null);
						return;
					}
					else {
						stack.pop();
					}
				} else {
					abortWorkFrame(b, (WorkFrame) stack.get(i));
				}
			}
		} else {
			throw new RuntimeException("Detectable_Sim: cannot use end_activity" +
					" for workframes");
		}
	}
	
	/**
	 * Finds the workframe and any composite activities and subsequent wfs, 
	 * removes them from the stack, and adds them to the list of 
	 * interrupted frames
	 * @param b the agent or object
	 * @param wf the workframe being impassed
	 */
	public static void impasseWorkFrame(Basic b, Object obj) {
		if (obj instanceof WorkFrame) {
			WorkFrame wf = (WorkFrame) obj;
			int level = wf.getLevel();
			Stack<Object> interruptedStack = new Stack<Object>();
			boolean withinWFToInterrupt = false;
			for (int i = 0; i < stack.size(); i++) {
				Object stackObj = stack.get(i);
				if (stackObj instanceof WorkFrame) {
					WorkFrame tmpWF = (WorkFrame) stackObj;
					if (tmpWF.getName().equals(wf.getName())) {
						interruptedStack.push(stackObj);
						stack.remove(i);
						i--;
						withinWFToInterrupt = true;
					} else if (tmpWF.getLevel() == level) {
						withinWFToInterrupt = false;
					} else if (withinWFToInterrupt) {
						interruptedStack.push(stack.get(i));
						stack.remove(i);
						i--;
					}
				}
				if (withinWFToInterrupt) {
					if (stackObj instanceof ActivityInstance) {
						ActivityInstance ai = (ActivityInstance) stackObj;
						if (ai.getActivity().getLevel() >= level) {
							interruptedStack.push(stackObj);
							stack.remove(i);
							i--;
						}
					}
				}
			}
			b.addInterruptedStack(interruptedStack);
			if (Simulator.DEBUG)
				System.out.println("impassed: " + b.printInterruptedFrames());
		} else {
			throw new RuntimeException("Detectable_Sim: Cannot impasse composite activities");
		}
	}
	
	/**
	 * Randomly generates a number between 0 and 100
	 * If the detect certainty is >= to the random #, it has been detected
	 * @param detect the current detectable (used to find certainty)
	 * @return true if the detectable was detected, false otherwise
	 */
	public static boolean certaintyIsTrue(Detectable detect) {
		int certainty = detect.getCertainty();
		if (certainty == 0)
			return false;
		Random generator = new Random();
		double random = generator.nextDouble() * 100;
		if (certainty >= random) {
			return true;
		}
		return false;
	}

	/***
	 * Checks through facts, if exists, updates belief
	 * @param b the current agent or object that has the current wf (and
	 * current detectable)
	 * @param resultComparison the ResultComparison to check against facts
	 */
	public static void updateBeliefs(Basic b, ResultComparison resultComp) {	
		if (resultComp.getCmp() instanceof DetectValComp) {
			DetectValComp valComp = (DetectValComp) resultComp.getCmp();
			List<Value> vals;
			if (valComp.getLhs() instanceof Term) {
				Term tempTerm = (Term) valComp.getLhs();
				Term t = new Term(tempTerm.getObjRefName(), tempTerm.getAttrName());
				List<Variable> vars = frame.getVariables();
				for(int i = 0 ; i < vars.size(); i++){
					if(t.getObjRefName().equals(vars.get(i).getVarName())){
						List<Value> varVal = frame.getVarInstanceVal(t.getObjRefName());
						t.setObjRefName(varVal.get(0).toString());
					}
				}
				getValAndUpdate(b, t.getObjRefName(), t.getAttrName(), valComp);
			} else if (valComp.getLhs() instanceof ParameterValue) {
				ParameterValue pv = (ParameterValue) valComp.getLhs();
				Parameter param = Utils.findParameter(b, frame, pv);
				vals = param.getVals();
				for (int i = 0; i < vals.size(); i++) {
					getValAndUpdate(b, vals.get(i).toString(), valComp.getLAtt(), valComp);
				}
			} else if (valComp.getLhs() instanceof VariableValue) {
				VariableValue variable = (VariableValue) valComp.getLhs();
				try {
					vals = frame.getVarInstanceVal(variable.getVarName());
					if (vals == null)
						throw new RuntimeException("");
				} catch (RuntimeException e2) {
					return;
				}
				for (int i = 0; i < vals.size(); i++) {
					getValAndUpdate(b, vals.get(i).toString(), valComp.getLAtt(), valComp);
				}
			}
		} else if (resultComp.getCmp() instanceof DetectRelComp) {
			List<Value> vals;
			DetectRelComp relComp = (DetectRelComp) resultComp.getCmp();
			String key;
			if (relComp.getLhs() instanceof TplObjRef) {
				key = relComp.getLhs().toString();
				getRelAndUpdate(b, key, relComp);
			} else if (relComp.getLhs() instanceof VariableValue) {
				VariableValue variable = (VariableValue) relComp.getLhs();
				try {
					vals = frame.getVarInstanceVal(variable.getVarName());
					if (vals == null)
						throw new RuntimeException("");
				} catch (RuntimeException e2) {
					return;
				}
				for (int i = 0; i < vals.size(); i++) {
					getRelAndUpdate(b, vals.get(i).toString(), relComp);
				}
			} else if (relComp.getLhs() instanceof ParameterValue) {
				ParameterValue pv = (ParameterValue) relComp.getLhs();
				Parameter param = Utils.findParameter(b, frame, pv);
				vals = param.getVals();
				for (int i = 0; i < vals.size(); i++) {
					getRelAndUpdate(b, vals.get(i).toString(), relComp);
				}
			}
		}
	}
	
	private static void getValAndUpdate(Basic b, String lhs, String att, DetectValComp valComp) {
		if(lhs.equals("current")){
			lhs = b.getName();
		}
		String key = lhs + "." + att;
		if (FactSet.factExists(key)) {
			//only 1 fct per obj.att
			Fact fact = FactSet.getFact(key).get(0);
			if (fact.getFact() instanceof ValueExpression) {
				ValueExpression oldVal = (ValueExpression) fact.getFact();
				ValueExpression newVal = new ValueExpression(lhs, att, 
						valComp.getEvalOp(), oldVal.getFactValue(b, frame).get(0));
				Term tmp = new Term(lhs, att);
				b.updateBeliefs(tmp, newVal);
			}
			//TODO else map
		}
	}
	
	private static void getRelAndUpdate(Basic b, String key, DetectRelComp relComp) {
		key += " " + relComp.getRelation();
		key = key.replaceFirst("current", b.getName());
		if (FactSet.factExists(key)) {
			//search for fact
			List<Fact> fList = FactSet.getFact(key);
			for (int i = 0; i < fList.size(); i++) {
				Fact fact = fList.get(i);
				RelationalExpression oldRel = (RelationalExpression) 
						fact.getFact();
				RelationalExpression newCurrRel = new 
						RelationalExpression(key, 
						relComp.getRelation(), oldRel.getRhsObjRef());
				b.updateBeliefs(key + " " + 
						relComp.getRelation(), newCurrRel);
				System.out.println("Detectable Belief Update: updating " + b.getName() + "'s beliefs to match " + newCurrRel);

			}
		}
	}
	
	/**
	 * Checks detect condition against belief/fact
	 * @param b the current agent or object that has the current wf (and
	 * current detectable)
	 * @param resultComparison the EvalValComp to check against facts
	 * @return true if the fact is found to be true
	 */
	public static boolean checkCondition(Basic b, ResultComparison resultComp) {
		if (resultComp.getCmp() instanceof DetectValComp) {
			DetectValComp valComp = (DetectValComp) resultComp.getCmp();
			List<Value> vals;
			if (b instanceof Agent) {
				if (valComp.getLhs() instanceof VariableValue) {
					VariableValue variable = (VariableValue) valComp.getLhs();
					try {
						vals = frame.getVarInstanceVal(variable.getVarName());
						if (vals == null)
							throw new RuntimeException("");
					} catch (RuntimeException e2) {
						return false;
					}
					for (int i = 0; i < vals.size(); i++) {
						DetectValComp tmp = new DetectValComp(new Term(
								vals.get(i).toString(), valComp.getLAtt()), 
								valComp.getEvalOp(), valComp.getRhs());
						try{
						if (tmp.compareEvaluatesTrue(b, frame))
							return true;
						} catch (NullPointerException npe) {} 
					}
					return false;
				}
				if (valComp.getLhs() instanceof ParameterValue) {
					ParameterValue pv = (ParameterValue) valComp.getLhs();
					Parameter param = Utils.findParameter(b, frame, pv);
					vals = param.getVals();
					for (int i = 0; i < vals.size(); i++) {
						DetectValComp tmp = new DetectValComp(new Term(
								vals.get(i).toString(), valComp.getLAtt()), 
								valComp.getEvalOp(), valComp.getRhs());
						try {
							if (tmp.compareEvaluatesTrue(b, frame))
								return true;
						} catch (NullPointerException npe) {} 
					}
					return false;
				}
				return valComp.compareEvaluatesTrue(b, frame);
			} else
				return valComp.factEvaluatesTrue(b, frame);
		} else { 
			DetectRelComp relComp = (DetectRelComp) resultComp.getCmp(); 
			if (b instanceof Agent)
				return relComp.compareEvaluatesTrue(b, frame);
			else
				return relComp.factEvaluatesTrue(b, frame);
		}
	}
}
