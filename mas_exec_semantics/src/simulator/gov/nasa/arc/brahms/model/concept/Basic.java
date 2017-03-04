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

package gov.nasa.arc.brahms.model.concept;

import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.activity.CompositeActivity;
import gov.nasa.arc.brahms.model.Attribute;
import gov.nasa.arc.brahms.model.Belief;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.ThoughtFrame;
import gov.nasa.arc.brahms.model.WorkFrame;
import gov.nasa.arc.brahms.model.activity.ActivityInstance;
import gov.nasa.arc.brahms.model.comparison.RelComp;
import gov.nasa.arc.brahms.model.expression.EvaluationOperator;
import gov.nasa.arc.brahms.model.expression.MapExpression;
import gov.nasa.arc.brahms.model.expression.MapKeyValPair;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.RelationalExpression;
import gov.nasa.arc.brahms.model.expression.SglObjRef;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.TplObjRef;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.expression.ValueExpression;
import gov.nasa.arc.brahms.simulator.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;

/**
 * 
 * @author josie
 * Basic Syntax for BasicConcept (group, agent, class, object) extends Concept:
 * basicType ::= basic basic-name 
 * {
 * {display : ID.literal-string;}	//inherited from Concept
 * {cost	: ID.number}
 * {time_unit: ID.number}
 * {icon 	: ID.literal-string;}	//inherited from Concept
 * {GRP.attributes}					//inherited from Concept
 * {GRP.relations}					//inherited from Concept
 * {initial-beliefs}
 * {initial-facts}
 * {activities}
 * {workframes}
 * {thoughtframes}
 * }
 * 
 * concept-name ::= ID.name
 * 
 * This is a the basic parts that make up a concept.  The exception is
 * Path, which will only use name and display from this class
 */
public abstract class Basic extends Concept {
	//variables
	protected double cost;
	protected int timeUnit;
	protected Map<String, List<Belief>> beliefs;
	protected Set<Activity> activities;
	protected Set<Activity> allPossibleActs;
	protected Set<WorkFrame> workFrames;
	protected Set<ThoughtFrame> thoughtFrames;
	protected Stack<Object> currentWF;
	protected WorkFrame currWorkFrame;
	protected ArrayList<Stack<Object>> interruptedWFs;
	public int currentTime;
	protected boolean isCurrentlyScheduled = false;
	
	StringBuilder beliefUpdates = new StringBuilder();
	StringBuilder eventUpdates = new StringBuilder();
	
	List <WorkFrame> activeWFs = new ArrayList<WorkFrame>();
	List <WorkFrame> inactiveWFs = new ArrayList<WorkFrame>();
	
	//Holds the current workload at a single instance
	Map<String, Double> currentWorkload = new HashMap<String, Double>();
	
	List <Map<String, Double>> workload = new ArrayList<Map<String, Double>>();
	List<String> sensoryTypes = new ArrayList<String>();
	
	String workLoad = "Time  , Temp   ,  TempDur ,  TempInt ,  Per ,  Per Aud ,  Per Vis ,  Dec ,  Activity \n";
	//String workLoad = "     |      |        |         |     |         |         |     | ";
	

	public Map <Double, String> workframeExecuting = new HashMap<Double, String>();
	public String currentWFName = "";
	public String activityName = "";
	public int activityStart = 0;
	public int activityEnd = 0;
	
	
	public void resetScheduled() {
		isCurrentlyScheduled = false;
	}
	
	public void setScheduled() {
		isCurrentlyScheduled = true;
	}
	
	public boolean isScheduled() {
		return isCurrentlyScheduled;
	}
	
	/**
	 * Basic Constructor, initializes vars
	 * @param name of the basic concept
	 * @param display for the basic concept
	 * @param icon for the basic concept is optional
	 * @param cost of the basic concept
	 * @param time - timeUnit for the basic concept
	 */
	protected Basic(String name, String display, double cost, int time) {
		super(name, display);
		this.cost = cost;
		this.timeUnit = time;
		this.activities = new HashSet<Activity>();
		this.beliefs = new HashMap<String, List<Belief>>();
		this.workFrames = new HashSet<WorkFrame>();
		this.thoughtFrames = new HashSet<ThoughtFrame>();
		this.currentWF = new Stack<Object>();
		this.interruptedWFs = new ArrayList<Stack<Object>>();
	}
	
	protected Basic(String name, String display, String icon, double cost, int time) {
		super(name, display, icon);
		this.cost = cost;
		this.timeUnit = time;
		this.activities = new HashSet<Activity>();
		this.beliefs = new HashMap<String, List<Belief>>();
		this.workFrames = new HashSet<WorkFrame>();
		this.thoughtFrames = new HashSet<ThoughtFrame>();
		this.currentWF = new Stack<Object>();
		this.interruptedWFs = new ArrayList<Stack<Object>>();
	}
	
	protected void addElements() {
		addAttributes();
		addRelations();
		addBeliefs();
		addFacts();
		addLocation();
		addActivities();
		addWorkFrames();
		addThoughtFrames();
	}
	
	public double getCost() {
		return cost;
	}
	
	public int getTimeUnit() {
		return timeUnit;
	}
	
	/** Instantiated in each specific model**/
	protected void addLocation() {}
	protected void addBeliefs() {}
	protected void addFacts() {}
	protected void addActivities() {}
	protected void addWorkFrames() {}
	protected void addThoughtFrames() {}
	
	/*Added for a quick fix when referencing an area as if it is an object*/
	public void addAtts(Set<Attribute> atts){
		attributes.addAll(atts);
	}
	
	public Map<String, List<Belief>> getBeliefs() {
		return beliefs;
	}
	public Set<Activity> getActivities() {
		return activities;
	}
	public Set<WorkFrame> getWorkFrames() {
		return workFrames;
	}
	
	public Set<Activity> getAllPossibleActs() {
		return allPossibleActs;
	}
	
	public void setupAllPossibleActs() {
		Set<Activity> allActs = new HashSet<Activity>();
		allActs = searchForActs(activities);
		allPossibleActs = allActs;
	}
	
	public Set<Activity> searchForActs(Set<Activity> acts) {
		Set<Activity> allActs = new HashSet<Activity>();
		Iterator<Activity> actItr = acts.iterator();
		while (actItr.hasNext()) {
			Activity tmp = actItr.next();
			if (tmp instanceof CompositeActivity) {
				CompositeActivity comp = (CompositeActivity) tmp;
				Set<Activity> subActs = searchForActs(comp.getActivities());
				Iterator<Activity> actItr2 = subActs.iterator();
				while (actItr2.hasNext()) {
					allActs.add(actItr2.next());
				}
			}
			allActs.add(tmp);
		}
		return allActs;
	}
	
	public Set<ThoughtFrame> getThoughtFrames() {
		return thoughtFrames;
	}
	
	public Set<Frame> getAllThoughtFrames() {
		Set<Frame> frames = new HashSet<Frame>();
		for(ThoughtFrame tf : thoughtFrames) {
			frames.add(tf);
		}
		return frames;
	}
	
	public Set<Frame> getAllWorkFrames() {
		Set<Frame> frames = new HashSet<Frame>();
		for(WorkFrame wf : workFrames) {
			frames.add(wf);
		}
		return frames;
	}
	


	
	/**
	 * Replaces the old belief if already initialized
	 * Otherwise adds a new belief
	 * @param lhs name of the attribute
	 * @param exp is the expression to be updated
	 */
	public void updateBeliefs(Term lhs, ValueExpression exp) {
		boolean createNew = false;
		String lhsString = lhs.getObjRefName() + "." + lhs.getAttrName();
		if (beliefs.get(lhsString) != null) {//belief has been initialized
			List<Belief> newBel = beliefs.get(lhsString);
			if (newBel.isEmpty())
				createNew = true;
			else {
				Belief bel = newBel.get(0); //only one element
				bel.setBelief(exp);
				beliefs.put(lhsString, newBel);
			}
		} else 
			createNew = true;
		if (createNew) {//belief has not yet been initialized
			List<Belief> newBel = new ArrayList<Belief>();
			newBel.add(new Belief(exp));
			beliefs.put(lhsString, newBel);
		}
	}
	
	public void updateEvents(String event){
		eventUpdates.append(event);
	}
	
	public void updateBeliefs(Term lhs, MapKeyValPair exp) {
		boolean createNewMap = false;
		if (beliefs.get(lhs.toString()) != null) {//belief has been initialized
			List<Belief> newBel = beliefs.get(lhs.toString());
			if (newBel.isEmpty()) 
				createNewMap = true;
			Belief bel = newBel.get(0); //only one map element, or unknown
			if (bel.getBelief() instanceof MapExpression) {
				MapExpression map = (MapExpression) bel.getBelief();
				map.add(exp);
			} else {
				createNewMap = true;
				newBel.clear(); //remove obj.map = unknown
			}
		} else
			createNewMap = true;
		
		if (createNewMap) {//belief has not yet been initialized
			List<Belief> newBel = new ArrayList<Belief>();
			MapExpression map = new MapExpression(lhs.getAttrName()); //create new map
			map.add(exp);
			newBel.add(new Belief(map));
			beliefs.put(lhs.toString(), newBel);
		}
	}
	
	public void updateBeliefs(String key, RelationalExpression rel) {
		if (beliefs.get(key) != null) { //belief for key exists
			List<Belief> bels = beliefs.get(key);
			boolean newBelief = true; //don't add same belief twice
			for (int i = 0; i < bels.size(); i++) {
				Belief oldBel = bels.get(i);
				RelationalExpression oldExp = (RelationalExpression) oldBel.getBelief();
				if (oldExp.getRhsObjRef().equals(rel.getRhsObjRef())) {
					newBelief = false;
					if (oldExp.getTruthVal() != rel.getTruthVal())
						oldExp.setTruthVal(rel.getTruthVal());
				}
			}
			if (newBelief) {
				bels.add(new Belief(rel));
				beliefs.put(key, bels);
			}
		} else { //no belief for key
			List<Belief> newBel = new ArrayList<Belief>();
			newBel.add(new Belief(rel));
			beliefs.put(key, newBel);
		}
	}
	
	public void copyAllBeliefs(Map<String, List<Belief>> allBels) {
		for (Map.Entry<String, List<Belief>> entry : allBels.entrySet())
		{
		    List<Belief> belList = entry.getValue();
		    Belief firstBel = belList.get(0);
		    Expression exp = firstBel.getBelief();
		    if (exp instanceof ValueExpression) {
		    	int index = entry.getKey().indexOf(".");
		    	this.addBelief(new Term(entry.getKey().substring(0, 
		    			index), entry.getKey().substring(index+1)), exp);
		    } else if (exp instanceof MapKeyValPair) {
		    	int index = entry.getKey().indexOf(".");
		    	for (int i = 0; i < belList.size(); i++)
		    		this.addBelief(new Term(entry.getKey().substring(0, 
		    			index), entry.getKey().substring(index+1)), belList.get(i).getBelief());
		    } else { //relation
		    	for (int i = 0; i < belList.size(); i++)
		    		this.addBelief(entry.getKey(), (RelationalExpression) belList.get(i).getBelief());
		    }
		}
	}
	
	
	public void clearMapBeliefs(Term lhs) {
		if (beliefs.get(lhs.toString()) != null) {
			List<Belief> mapBels = beliefs.get(lhs.toString());
			/*Neha, I could probably work around this if necessary, but this 
			 * makes for much less complicated checking in several areas (is 
			 * list empty VS is list empty? is 1st element map? is map empty?*/
			mapBels.clear();
		}
		ValueExpression valExp = new ValueExpression(lhs.getObjRefName(),
				lhs.getAttrName(), EvaluationOperator.EQ, new 
				SglObjRef("unknown"));
		updateBeliefs(lhs, valExp);
	}
	
	/*The only beliefs that can be retracted are relational expressions, instead of actually
	 * removing the belief from the set of beliefs we set a `truthValue' to false
	 * to signify the belief has been retracted*/
/*	public void retractBelief(String key, RelationalExpression rel) {
		if (beliefs.get(key) != null) { //belief for key exists
			List<Belief> bels = beliefs.get(key);
			int indexOfRemoved = -1;
			for (int i = 0; i < bels.size(); i++) {
				Belief oldBel = bels.get(i);
				RelationalExpression oldExp = (RelationalExpression) oldBel.getBelief();
				if (oldExp.getRhsObjRef().equals(rel.getRhsObjRef()))
					indexOfRemoved = i;
			}
			if (indexOfRemoved > -1) {
				bels.remove(indexOfRemoved);
				
			}
		}
	}*/
	
	public void addBelief(Term lhs, Expression exp) {
		if (exp instanceof ValueExpression)
			updateBeliefs(lhs, (ValueExpression) exp);
		else
			updateBeliefs(lhs, (MapKeyValPair) exp);
	}
	
	public void addBelief(String key, RelationalExpression rel) {
		updateBeliefs(key, rel);
	}
	
	/**
	 * Checks for beliefs about an attribute (obj.att = ?)
	 * @param objRefName
	 * @param attrName
	 * @return
	 */
	public boolean beliefExists(String objRefName, String attrName) {
		return (beliefs.containsKey(objRefName + "." + attrName));
	}
	
	/**
	 * Checks for beliefs about relations (obj relationName ?)
	 * @param exp the relational expression we're checking for
	 * @return
	 */
	public boolean beliefExists(RelationalExpression exp) {
		String key = exp.getLhsObjRef() + " " + exp.getRelationName();
		if (!beliefs.containsKey(key))
			return false;
		else {
			List<Belief> rels = beliefs.get(key);
			for (int i = 0; i < rels.size(); i++) {
				Belief tmpB = rels.get(i);
				//RelationalExpression relExp = new RelationalExpression(tempVal.toString(), 
					//	relationName, v.toString(), true);
				RelationalExpression tmpR = (RelationalExpression)tmpB.getBelief();
				if (tmpR.getRhsObjRef().equals(exp.getRhsObjRef()))
					return true;
			}
			return false;
		}
	}
	
	public boolean beliefExists(RelComp exp) {
		StringBuilder key = new StringBuilder();
		Expression lhs = exp.getLhs();
		if (lhs instanceof TplObjRef) {
			key.append(((TplObjRef) lhs).getObjRefName());
		} else if (lhs instanceof ParameterValue) {
			ParameterValue pv = (ParameterValue) lhs;
			try {
				List<CompositeActivity> comps = Utils.findFramesCompositeParents(this, Utils.getTopWF(this));
				for (int i = 0; i < comps.size(); i++) {
					CompositeActivity comp = comps.get(i);
					List<Parameter> parameters = comp.getParams();
					for (int j = 0; j < parameters.size(); j++) {
						Parameter parameter = parameters.get(j);
						if (parameter.getName().equals(pv.getName())) {
							List<Value> vals = parameter.getVals();
							if (vals.get(0) instanceof TplObjRef) {
								key.append(((TplObjRef) vals.get(0)).getObjRefName());
							} else if (vals.get(0) instanceof SglObjRef)
								key.append(((SglObjRef) vals.get(0)).getObjRefName());
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("error with getting parameter " + e.getMessage());
			}
		}
		key.append(" " + exp.getRelation());
		
		String rhsName = "";
		Expression rhs = exp.getRhs();
		if (rhs instanceof TplObjRef)
			rhsName = ((TplObjRef) rhs).getObjRefName();
		else if (rhs instanceof SglObjRef)
			rhsName = ((SglObjRef) rhs).getObjRefName();
		else if (rhs instanceof ParameterValue) {
			ParameterValue pv = (ParameterValue) rhs;
			try {
				List<CompositeActivity> comps = Utils.findFramesCompositeParents(this, Utils.getTopWF(this));
				for (int i = 0; i < comps.size(); i++) {
					CompositeActivity comp = comps.get(i);
					List<Parameter> parameters = comp.getParams();
					for (int j = 0; j < parameters.size(); j++) {
						Parameter parameter = parameters.get(j);
						if (parameter.getName().equals(pv.getName())) {
							List<Value> vals = parameter.getVals();
							if (vals.get(0) instanceof TplObjRef) {
								rhsName = ((TplObjRef) vals.get(0)).getObjRefName();
							} else if (vals.get(0) instanceof SglObjRef)
								rhsName = ((SglObjRef) vals.get(0)).getObjRefName();
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("error with getting parameter " + e.getMessage());
			}
		}
		if (!beliefs.containsKey(key.toString()))
			return false;
		else {
			List<Belief> rels = beliefs.get(key.toString());
			for (int i = 0; i < rels.size(); i++) {
				Belief tmpB = rels.get(i);
				RelationalExpression tmpR = (RelationalExpression)tmpB.getBelief();
				if (tmpR.getRhsObjRef().equals(rhsName))
					return true;
			}
			return false;
		}
	}
	
	
	public boolean beliefExists(String key) {
		return beliefs.containsKey(key);
	}
	
	/**
	 * Gets the belief about an attribute
	 * @param objRefName
	 * @param attrName
	 * @return
	 */
	public Belief getBelief(String objRefName, String attrName) {
		if(objRefName.equals(name)){
			objRefName = "current";
		}
		if (beliefExists(objRefName, attrName)) {
			Belief tmpBel = beliefs.get(new String(objRefName + "." + attrName)).get(0);
			return tmpBel;
		}
		return null;
	}
	
	/**
	 * Gets the belief about a relation
	 * @param objRefName
	 * @param attrName
	 * @return
	 */
	public List<Belief> getBelief(String key) {
		if (beliefExists(key)) {
			return beliefs.get(key);
		}
		return null;
	}
	
	public Stack<Object> getCurrentWorkFrame() {
		return currentWF;
	}
	public void setCurrentWorkFrame(Stack<Object> curr) {
		this.currentWF = curr;
	}
	
	public void setActualWorkFrame(WorkFrame wf) {
		this.currWorkFrame = wf;
	}
	
	public WorkFrame getActualWorkFrame() {
		return this.currWorkFrame;
	}
	
	/**
	 * Removes the given wf from the set of workframes
	 * @param wf to be removed
	 */
	public void removeWorkFrame(WorkFrame wf) {
		assert workFrames.contains(wf);
		workFrames.remove(wf);
	}
	
	public void addWorkFrame(WorkFrame wf) {
		workFrames.add(wf);
	}
	
	//TODO test
	public void removeWorkFrameFromStack(WorkFrame wf) {
		int level = wf.getLevel();
		boolean withinWFToRemove = false;
		for (int i = 0; i < currentWF.size(); i++) {
			if (currentWF.get(i) instanceof WorkFrame) {
				WorkFrame tmpWF = (WorkFrame) currentWF.get(i);
				if (tmpWF.getName().equals(wf.getName())) { //remove the orig wf
					withinWFToRemove = true;
					currentWF.remove(i);
					i--;
				}
				else if (tmpWF.getLevel() == level) //if level==orig, you've reached
					withinWFToRemove = false; //a wf that interrupted the orig, don't remove!
			}
			if (withinWFToRemove) {
				if (currentWF.get(i) instanceof ActivityInstance) {
					ActivityInstance ai = (ActivityInstance) currentWF.get(i);
					if (ai.getActivity().getLevel() >= level){
						currentWF.remove(i);
					}
				}
			}
		}
	}
	
	public ArrayList<Stack<Object>> getInterruptedFrames() {
		return interruptedWFs;
	}
	
	/**
	 * Removes the stack that contains this workframe as the 'bottom' spot
	 * TODO check if workframe is in spot other than 'bottom' 
	 * TODO test
	 * @param wf
	 */
	public void removeInterruptedFrame(WorkFrame wf) {
		for (int i = 0; i < interruptedWFs.size(); i++) {
			if (interruptedWFs.get(i).get(0) instanceof WorkFrame) {
				WorkFrame tmpWF = (WorkFrame) interruptedWFs.get(i).get(0);
				if (tmpWF.getName().equals(wf.getName())) { //remove the orig wf
					interruptedWFs.remove(i);
					return;
				}
			}
		}
	}
	
	/**
	 * Adds the interrupted workframe and its associated activity instance
	 * to the end of the list
	 * @param wf the workframe that has been interrupted
	 * @param ai the activity instance that was running when the wf was interrupted
	 */
	public void addInterruptedStack(Stack<Object> stack) {
		interruptedWFs.add(stack);
	}
	
	/**
	 * Removes the given tf from the set of thoughtframes
	 * @param tf to be removed
	 */
	public void removeThoughtFrame(ThoughtFrame tf) {
		assert thoughtFrames.contains(tf);
		thoughtFrames.remove(tf);
	}
	
	public void addThoughtFrame(ThoughtFrame tf) {
		thoughtFrames.add(tf);
	}
	
	public void removeFromImpassed(WorkFrame wf) {
		int index = -1;
		for (int i = 0; i < interruptedWFs.size(); i++) {
			Stack<Object> stack = interruptedWFs.get(i);
			if (stack.get(0) instanceof WorkFrame) {
				WorkFrame top = (WorkFrame) stack.get(0);
				if (top.getName().equals(wf.getName())) {
					index = i;
				}
			}
		}
		if (index > -1)
			interruptedWFs.remove(index);
	}
	
	public String printStack() {
		StringBuilder s = new StringBuilder();
		s.append("\n<<<Stack> \n");
		for (int i = 0; i < currentWF.size(); i++) {
			if (currentWF.get(i) instanceof WorkFrame) {
				WorkFrame wf = (WorkFrame) currentWF.get(i);
				int level = wf.getLevel();
				for (int j = 0; j < level; j++)
					s.append("\t");
					s.append(wf.getName() + "\n");
			}
			if (currentWF.get(i) instanceof ActivityInstance) {
				ActivityInstance ai = (ActivityInstance) currentWF.get(i);
				Activity act = ai.getActivity();
				int level = act.getLevel();
				for (int j = 0; j < level; j++)
					s.append("\t");
					s.append(ai.getActivity().getName() + "\n");
			}
		}
		s.append("</Stack>>> \n");
		return s.toString();
	}
	
	public String printInterruptedFrames() {
		StringBuilder s = new StringBuilder();
		s.append("\n[[[InterruptedFrames] \n");
		for (int i = 0; i < interruptedWFs.size(); i++) {
			Stack<Object> tmp = interruptedWFs.get(i);
			for (int j = 0; j < tmp.size(); j++) {
				if (tmp.get(j) instanceof WorkFrame) {
					WorkFrame wf = (WorkFrame) tmp.get(j);
					int level = wf.getLevel();
					for (int k = 0; k < level; k++)
						s.append("\t");
						s.append(wf.getName() + "\n");
				}
				if (tmp.get(j) instanceof ActivityInstance) {
					ActivityInstance ai = (ActivityInstance) tmp.get(j);
					Activity act = ai.getActivity();
					int level = act.getLevel();
					for (int k = 0; k < level; k++)
						s.append("\t");
						s.append(ai.getActivity().getName() + "\n");
				}
			}
		}
		s.append("[/InterruptedFrames]]]>>> \n");
		return s.toString();
	}
	
	
	public void addActiveWFs(WorkFrame wf){
		if(!activeWFs.contains(wf))
			activeWFs.add(wf);
	}
	
	public void addInActiveWFs(WorkFrame wf){
		if(!inactiveWFs.contains(wf))
			inactiveWFs.add(wf);
	}
	
	public void resetWfs(){
		inactiveWFs.clear();
		activeWFs.clear();
	}
	
	public void setCurrentTime(int time){
		currentTime = time;
	}
	public int getCurrentTime(){
		return currentTime;
	}
	
	public String toString() {
		StringBuilder retVal = new StringBuilder(); 
		retVal.append(super.toString());
		if (beliefs.size() > 0)
			retVal.append("beliefs: " + beliefs.toString() + "\n");
		if (activities.size() > 0)
			retVal.append("activities: " + activities.toString() + "\n");
		if (workFrames.size() > 0)
			retVal.append("workframes: " + workFrames.toString() + "\n");
		if (thoughtFrames.size() > 0)
			retVal.append("thoughtframes: " + thoughtFrames.toString() + "\n");
		return retVal.toString();
	}
	
	public List<Map<String, Double>> getWorkloadList(){
		return workload;
	}
	
	public void addToCurrentWorkload(Map<String, Double> newWorkload){
		currentWorkload.clear();
		currentWorkload.putAll(newWorkload);
	}
	public void resetCurrentWorkload(){
		
	}
	public StringBuilder getBeliefUpdates(){
		return beliefUpdates;
	}
	public StringBuilder getEventUpdates(){
		return eventUpdates;
	}
}

