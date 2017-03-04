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

package gov.nasa.arc.brahms.simulator;

import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.Attribute;
import gov.nasa.arc.brahms.model.Fact;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.MultiAgentSystem;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.Relation;
import gov.nasa.arc.brahms.model.ThoughtFrame;
import gov.nasa.arc.brahms.model.WorkFrame;
import gov.nasa.arc.brahms.model.activity.ActivityInstance;
import gov.nasa.arc.brahms.model.activity.CompositeActivity;
import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Area;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.concept.Class_b;
import gov.nasa.arc.brahms.model.concept.Concept;
import gov.nasa.arc.brahms.model.concept.Conceptual_Class;
import gov.nasa.arc.brahms.model.concept.Conceptual_Object;
import gov.nasa.arc.brahms.model.concept.Group;
import gov.nasa.arc.brahms.model.concept.Object_b;
import gov.nasa.arc.brahms.model.concept.Path;
import gov.nasa.arc.brahms.model.expression.MapExpression;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.RelationalExpression;
import gov.nasa.arc.brahms.model.expression.SglObjRef;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.TplObjRef;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.simulator.world.FactSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class Utils {
	
	protected static MultiAgentSystem mas;
	protected static DistanceMatrix matrix;
	protected static Random generator; 
	
	private static void initializeRandomSeedGenerator() {
		generator = new Random();
		long seed = System.currentTimeMillis();
		generator.setSeed(seed);
	}
	
	public static void setUpMas(MultiAgentSystem m) {
		Utils.mas = m;
		initializeRandomSeedGenerator();
	}
	
	public static int getRandomIndex(int size) {
		if(generator == null) {
			initializeRandomSeedGenerator();
		}
		return Utils.generator.nextInt(size);
	}
	
	//This should only be invoked once for a particular
		//certainty.
	public static int getCertainty (int certainty) {
		//cover the base cases just to be sure
		if(certainty == 100) return 1;
		if(certainty == 0) return 0;
		// this generates a random value between 1
		// and 99. The cases 0 and 100 are already
		// covered.
		int random = Utils.generator.nextInt(99);
		random++;
		if(certainty >= random) return 1;
		else return 0;
	}
	
	public static List<SglObjRef> getMembersOf(String parent){
		Iterator<Agent> AgItr = mas.getAgents().iterator();
		List<SglObjRef> members = new ArrayList<SglObjRef>();;
		while (AgItr.hasNext()) {
			Agent ag = AgItr.next();
			Set<String> memberOfs = ag.getMemberOfs();
			if (memberOfs.contains(parent)){
				members.add(new SglObjRef(ag.getName()));
			}
			
		}
		Iterator<Object_b> ObjItr = mas.getObjects().iterator();
		while (ObjItr.hasNext()) {
			Object_b obj = ObjItr.next();
			Set<String> memberOfs = obj.getClassExtends();
			memberOfs.add(obj.getClassInstance().toString());
			if (memberOfs.contains(parent)){
				members.add(new SglObjRef(obj.getName()));
			}
		}
		return members;
	}

	public static List<SglObjRef> getMembersOf2(String parent){
		Iterator<Group> AgItr = mas.getGroups().iterator();
		List<SglObjRef> members = new ArrayList<SglObjRef>();;
		while (AgItr.hasNext()) {
			Group ag = AgItr.next();
			Set<String> memberOfs = ag.getMemberOfs();
			if (memberOfs.contains(parent)){
				members.add(new SglObjRef(ag.getName()));
				//find its parents
				members.addAll(getMembersOf2(ag.getName()));
			}
			
		}
		Map <String, Class_b> classes = mas.getClasses();
		for(Class_b cl : classes.values()){
			Set<String> memberOfs = cl.getClassExtends();
			if(memberOfs.contains(parent)){
				members.add(new SglObjRef(cl.getName()));
				members.addAll(getMembersOf2(cl.getName()));
				
			}
			
		}		
		return members;
	}
	

	
	/**
	 * Returns a random value for the duration between inputs min_duration and
	 * max_duration
	 */
	public static int getActualDuration(int min_duration, int max_duration) {
			int diff = max_duration - min_duration;
			int randVal = generator.nextInt(diff) + 1;
			return (min_duration + randVal);
	}
	
	//Point of non-determinism.  This is where a random selection is made between
	//the highest priority workframes, if there is more than one with the highest priority
	public static int getHighestPriorityFrameIndex(int size) {
		return generator.nextInt(size);
	}
	
	public static MultiAgentSystem getMas() {
		return mas;
	}
	
	public static DistanceMatrix getDistMatrix() {
		return matrix;
	}
	
	public static Basic getAgentObj(String objName) {
		Basic basic = null;
		Iterator<Agent> AgItr = mas.getAgents().iterator();
		while (AgItr.hasNext()) {
			basic = AgItr.next();
			if (basic.getName().equals(objName)){
				return basic;
			}
		}
		Iterator<Object_b> ObjItr = mas.getObjects().iterator();
		while (ObjItr.hasNext()) {
			basic = ObjItr.next();
			if (basic.getName().equals(objName)){
				return basic;
			}
		}
		
		Iterator<Conceptual_Object> COItr = mas.getConceptObjs().iterator();
		while (ObjItr.hasNext()) {
			Concept con = COItr.next();
			if (con.getName().equals(objName)){
				//String name, String display, double cost, int time_unit, String location
				basic = new Agent(con.getName(), con.getDisplay(), 0, 0, "");
				Set<Attribute> atts = con.getAttributes();
				basic.addAtts(atts);
				return basic;
			}
		}
		
		Iterator <Area> AreaItr = mas.getAllAreas().iterator();
		while(AreaItr.hasNext()){
			Concept con = AreaItr.next();
			if (con.getName().equals(objName)){
				//String name, String display, double cost, int time_unit, String location
				basic = new Agent(con.getName(), con.getDisplay(), 0, 0, "");
				Set<Attribute> atts = con.getAttributes();
				basic.addAtts(atts);
				return basic;
			}
		}
		
		return null;
	}
	
	/*
	 * Searches through all concepts: groups, agents, classes, objects, 
	 * conceptual classes and conceptual objects for one that matches the input
	 * string s
	 */
	public static Concept getConcept(String s) {
		Concept concept = getAgentObj(s);
		if (concept == null) {
			Iterator<Group> GpItr = mas.getGroups().iterator();
			while (GpItr.hasNext()) {
				concept = GpItr.next();
				if (concept.getName().equals(s))
					return concept;
			}
			Iterator<Conceptual_Object> CObjItr = mas.getConceptObjs().iterator();
			while (CObjItr.hasNext()) {
				concept = CObjItr.next();
				if (concept.getName().equals(s))
					return concept;
			}
			Map<String, Class_b> classes = mas.getClasses();
			if (classes.containsKey(s))
				concept = classes.get(s);
			Map<String, Conceptual_Class> conceptClasses = mas.getConceptClasses();
			if (conceptClasses.containsKey(s))
				concept = conceptClasses.get(s);
		}
		return concept;
	}
	
	public static boolean verifyClassGroupSameType(Value rhsVal, Value oldVal, 
			String type) {
		String newClsGpName, oldClsGpName;
		if (rhsVal instanceof SglObjRef)
			newClsGpName = ((SglObjRef) rhsVal).getObjRefName();
		else if (rhsVal instanceof SglObjRef)
			newClsGpName = ((TplObjRef) rhsVal).getObjRefName();
		else
			return true;
		if (oldVal instanceof SglObjRef)
			oldClsGpName = ((SglObjRef) oldVal).getObjRefName();
		else if (oldVal instanceof TplObjRef)
			oldClsGpName = ((TplObjRef) oldVal).getObjRefName();
		else
			oldClsGpName = type;
		Basic bNew = Utils.getAgentObj(rhsVal.toString());

		if (oldClsGpName.equals(newClsGpName))
			return true;
		if (bNew instanceof Agent) {
			Agent a = (Agent) bNew;
			Set<String> memberofs = a.getMemberOfs();
			if (memberofs.contains(oldClsGpName) || memberofs.contains(type))
				return true;
		}
		if (bNew instanceof Object_b) {
			Object_b obj = (Object_b) bNew;
			if ((type.equals(obj.getClassInstance())) || (oldClsGpName.equals(obj.getClassInstance()))) {
				return true;
			}
			Set<String> memberofs = obj.getClassExtends();
			if ((memberofs.contains(type)) || memberofs.contains(oldClsGpName))
				return true;
		}
		return false;

	}
	
	/**
	 * Uses getAttribute to determine whether a given term
	 * is a valid attribute of basic
	 * @param basic
	 * @param lhs
	 * @return
	 */
	public static boolean validAttribute(Basic basic, Term t) {
		Attribute attribute = getAttribute(basic, t);
		if (attribute == null)
			return false;
		return true;
	}
	
	/**
	 * Gets the attributes for a given basic and iterates through them to
	 * find the attribute that matches lhs.getAttrName()
	 * Returns null if lhs.getAttrName() is not an attribute of basic
	 * @param basic
	 * @param lhs
	 * @return
	 */
	public static Attribute getAttribute(Basic basic, Term t) {
		Set<Attribute> atts = basic.getAttributes();
		Iterator<Attribute> attItr = atts.iterator();
		while (attItr.hasNext()) {
			Attribute attribute = attItr.next();
			if (attribute.getName().equals(t.getAttrName()))
				return attribute;
		}
		return null;
	}
	
	public static Basic findTermOwner(Basic referencedFrom, Term t) {
		if (t.getObjRefName().equals("current"))
			return referencedFrom;
		Iterator<Agent> agItr = mas.getAgents().iterator();
		while (agItr.hasNext()) {
			Agent ag = agItr.next();
			if (t.getObjRefName().equals(ag.getName()))
				return ag;
		}
		Iterator<Object_b> objItr = mas.getObjects().iterator();
		while (objItr.hasNext()) {
			Object_b obj = objItr.next();
			if (t.getObjRefName().equals(obj.getName()))
				return obj;
		}
		return null;
	}
	
	public static List<Basic> findAllAttOwners(Basic referencedFrom, String att) {
		List<Basic> objs = new ArrayList<Basic>();
		Iterator<Agent> agItr = mas.getAgents().iterator();
		while (agItr.hasNext()) {
			Agent ag = agItr.next();
			Set<Attribute> atts = ag.getAttributes();
			Iterator<Attribute> atItr = atts.iterator();
			while (atItr.hasNext()) {
				Attribute tmpAtt = atItr.next();
				if ((tmpAtt.getName().equals(att) || att.equals("location")) && !objs.contains(ag))
					objs.add(ag);
			}
		}
		Iterator<Object_b> objItr = mas.getObjects().iterator();
		while (objItr.hasNext()) {
			Object_b obj = objItr.next();
			Set<Attribute> atts = obj.getAttributes();
			Iterator<Attribute> atItr = atts.iterator();
			while (atItr.hasNext()) {
				Attribute tmpAtt = atItr.next();
				if ((tmpAtt.getName().equals(att) || att.equals("location")) && !objs.contains(obj))
					objs.add(obj);
					
			}
		}
		return objs;
	}
	
	public static List<Basic> findAllRelOwners(Basic referencedFrom, String rel) {
		List<Basic> objs = new ArrayList<Basic>();
		Iterator<Agent> agItr = mas.getAgents().iterator();
		while (agItr.hasNext()) {
			Agent ag = agItr.next();
			Set<Relation> rels = ag.getRelations();
			Iterator<Relation> relItr = rels.iterator();
			while (relItr.hasNext()) {
				Relation tmpRel = relItr.next();
				if ((tmpRel.getName().equals(rel) || rel.equals("contains")) && !objs.contains(ag)){
					objs.add(ag);
				}	
			}
		}
		Iterator<Object_b> objItr = mas.getObjects().iterator();
		while (objItr.hasNext()) {
			Object_b obj = objItr.next();
			Set<Relation> rels = obj.getRelations();
			Iterator<Relation> relItr = rels.iterator();
			while (relItr.hasNext()) {
				Relation tmpRel = relItr.next();
				if ((tmpRel.getName().equals(rel) || rel.equals("contains")) && !objs.contains(obj))
					objs.add(obj);
					
			}
		}
		return objs;
	}
	
	public static List<Basic> returnType(String type) {
		List<Basic> retVal = new ArrayList<Basic>();
		Iterator<Agent> agItr = mas.getAgents().iterator();
		while (agItr.hasNext()) {
			Agent ag = agItr.next();
			Set<String> memberofs = ag.getMemberOfs();
			Iterator<String> memberItr = memberofs.iterator();
			while (memberItr.hasNext()) {
				String str = memberItr.next();
				if (str.equals(type))
					retVal.add(ag);
			}
		}
		Iterator<Object_b> objItr = mas.getObjects().iterator();
		while (objItr.hasNext()) {
			Object_b obj = objItr.next();
			Set<String> instofs = obj.getClassExtends();
			Iterator<String> instItr = instofs.iterator();
			while (instItr.hasNext()) {
				String str = instItr.next();
				if (str.equals(type))
					retVal.add(obj);
			}
			if (obj.getClassInstance().equals(type))
				retVal.add(obj);
		}
		return retVal;
	}
	
	public static Frame getTopWF(Basic b) {
		Stack<Object> stack = b.getCurrentWorkFrame();
		if (stack.size() > 0) {
				for (int i = stack.size() - 1; i >= 0; i--) {
					if (stack.get(i) instanceof WorkFrame)
						return ((WorkFrame) stack.get(i));
				}
		}
		return null;
	}
	
	//findFramesCompositeParents didn't seem to work so I created a new method
	public static List<CompositeActivity> findFramesCompositeParents2(Basic b, Frame f) {
		List<CompositeActivity> compList = new ArrayList<CompositeActivity>();
		
		Set<Activity> acts = new HashSet<Activity>(); 
		acts.addAll(b.getActivities());
		Iterator<Activity> it = acts.iterator();
		//Loop through all agent's activities
		while(it.hasNext()){
			Activity a1 = it.next();
			//if the activity is a composite then check if the frame is contained inside
			if (a1 instanceof CompositeActivity){
				CompositeActivity ca1 = (CompositeActivity) a1;
				//Check for the frame inside the activities thoughtframes
				Set <ThoughtFrame> tfs = ((CompositeActivity) a1).getThoughtFrames();
				Iterator<ThoughtFrame> iter1 = tfs.iterator();
				while(iter1.hasNext()){
					ThoughtFrame tf = iter1.next();
					if(f.getName().equals(tf.getName())){
						compList.add(ca1);
					}
				}
				//Check for the frame inside the workframes
				Set <WorkFrame> wfs = ((CompositeActivity) a1).getWorkFrames();
				Iterator<WorkFrame> iter2 = wfs.iterator();
				while(iter2.hasNext()){
					WorkFrame wf = iter2.next();
					if(f.getName().equals(wf.getName())){
						compList.add(ca1);
					}
				}
			}
		}
		//return the list of composite activities
		return compList;
		
	}

	public static List<CompositeActivity> findFramesCompositeParents(Basic b, Frame f) {
		List<CompositeActivity> compList = new ArrayList<CompositeActivity>();
		Stack<Object> stack = b.getCurrentWorkFrame();
		int framelevel = 0;
		
		try{
			framelevel = ((WorkFrame) f).getLevel();
		}
		catch(Exception e){
			//No layering in composite activities for thoughtframes.
			//Instead of doing this incorporation a quick fix here was produced which finds out
			//what composite activity the thoughtframe belongs to and then finds that activities
			//depth
			Set<Activity> a = b.getActivities();
			Iterator<Activity> it = a.iterator();
			while(it.hasNext()){
				Activity a1 = it.next();
				if (a1 instanceof CompositeActivity){
					Set <ThoughtFrame> tfs = ((CompositeActivity) a1).getThoughtFrames();
					Iterator<ThoughtFrame> iter1 = tfs.iterator();
					while(iter1.hasNext()){
						ThoughtFrame tf = iter1.next();
						if(f.getName().equals(tf.getName())){
							framelevel = a1.getLevel() +1;
						}
						
					}
					
				}
			}
		}
		if (framelevel == 0) {
			if (stack.size() > 1) {
				Object obj = stack.get(stack.size()-1);
				if (obj instanceof ActivityInstance) {
					ActivityInstance ai = (ActivityInstance) obj;
					if (ai.getActivity() instanceof CompositeActivity) {
						compList.add((CompositeActivity) ai.getActivity());
						return compList;
					}
				}
			}
			return null;
		}
		for (int i = stack.size()-1; i >=0; i--) {
			Object obj = stack.get(i);
			if (obj instanceof ActivityInstance) {
				ActivityInstance ai = (ActivityInstance) obj;
				if (ai.getActivity() instanceof CompositeActivity) {
					CompositeActivity comp = (CompositeActivity) ai.getActivity();
					if (comp.getLevel() == framelevel-1) {
						compList.add(comp);
						framelevel = comp.getLevel();
					}
				}
			}
		}

		return compList;
	}
	
	public static Parameter findParameter(Basic b, Frame f, ParameterValue parVal) {
		List<CompositeActivity> comps = findFramesCompositeParents2(b, f);
		Stack<Object> currentStack = new Stack<Object>();
		currentStack.addAll(b.getCurrentWorkFrame());
		if(comps != null){
			for (int i = 0; i < comps.size(); i++) {
				CompositeActivity comp = comps.get(i);
				//Loop through agent's current stack to identify which activity instance
				//relates to the composite activity which is a parent composite activity
				while(!currentStack.empty()){
					Object o = currentStack.pop();
					if(o instanceof ActivityInstance){
						ActivityInstance AI = ((ActivityInstance) o);
						if(AI.getActivity() instanceof CompositeActivity){
							CompositeActivity CA = (CompositeActivity) ((ActivityInstance) o).getActivity();
							if(CA.getName().equals(comp.getName())){
								List<Parameter> params = CA.getParams();
								for (int j = 0; j < params.size(); j++) {
									if (params.get(j).getName().equals(parVal.getName()))
										return params.get(j);
								}
							}	
						}
					}
				}
			}
		}
		return null;
	}
		
	public static List<Area> getSubAreas(String parentStr) {
		Area parentArea = null;
		Iterator<Area> itr = MultiAgentSystem.allAreas.iterator();
		while (itr.hasNext()) {
			Area tmp = itr.next();
			if (tmp.getName().equals(parentStr))
				parentArea = tmp;
		}
		List<Area> subAreas = new ArrayList<Area>();
		findAllChildren(parentArea, subAreas);
		return subAreas;
	}
	
	/**
	 * Recursively finds all children of parent area 
	 * i.e. all "child partof parent" instances
	 * @param parent the area of which we want to find all subareas
	 * @param subAreas the structure in which to store all children
	 */
	public static void findAllChildren(Area parent, List<Area> subAreas) {
		Set<Area> allAreas = mas.getAllAreas();
		Iterator<Area> itr = allAreas.iterator();
		while (itr.hasNext()) {
			Area tmpArea = itr.next();
			if (tmpArea.getPartOfArea().equals(parent.getName())) {
				subAreas.add(tmpArea);
				findAllChildren(tmpArea, subAreas);
			}
		}
	}
	
	/*
	 * Return facts
	 */
	public static Map<String, List<Fact>> getFacts(){
		Map<String, List<Fact>> allFacts = FactSet.getFacts();
		return allFacts;
	}

	/**
	 * Search through facts to see if any ag/obj contains the item
	 * @param item
	 * @return
	 */
	public static Basic getContainer(String item) {
		Basic container = null;
		Map<String, List<Fact>> allFacts = FactSet.getFacts();
		for(Map.Entry<String, List<Fact>> entry: allFacts.entrySet()) {
			String tmpKey = entry.getKey();
			if (tmpKey.endsWith(" contains")) { //something contains something
				List<Fact> containFacts = entry.getValue();
				for (int i = 0; i < containFacts.size(); i++) {
					Fact f = containFacts.get(i);
					RelationalExpression rel = (RelationalExpression) f.getFact();
					//check if item is contained
					if (rel.getRhsObjRef().toString().equals(item))
						container = getAgentObj(rel.getLhsObjRef().toString());
				}
			}
		}
		return container;
	}
	
	public static void setUpPaths() {
		Utils.matrix = new DistanceMatrix(mas.getNumAreas());
		Set<Area> areas = mas.getAllAreas();
		Set<Path> paths = mas.getAllPaths();
		Iterator<Path> itr = paths.iterator();
		while (itr.hasNext()) {
			Path aPath = itr.next();
			String a1 = aPath.getArea1();
			String a2 = aPath.getArea2();
			Iterator<Area> itr2 = areas.iterator();
			Area area1 = null;
			Area area2 = null;
			while (itr2.hasNext()) {
				Area someArea = itr2.next();
				if (someArea.getName().equals(a1))
					area1 = someArea;
				if (someArea.getName().equals(a2))
					area2 = someArea;
			}
			if (area1 == null || area2 == null) {
				
					System.err.println("***ERROR: path has a null area " + a1
						+ " " + a2);
			}
			else {
				int a1ID = area1.getID();
				int a2ID = area2.getID();
				int dist = aPath.getDistance();
				matrix.setValue(a1ID, a2ID, dist);
				matrix.setValue(a2ID, a1ID, dist);
			}
		}
		matrix.computeShortestPathEstimates();
		if(Simulator.DEBUG)
			matrix.printMatrix();
	}
	
	public static int getGlobalClock(){
		if(mas == null) return 0;
		
		return mas.getGlobalClock();
	}
	/**
	 * This method constructs the key from the values passed in and uses it to retrieve the correlating weight value from the agent's map
	 * @param struct represents what object and type the weight is tied to (.dw = decisionweight, .tw = temporalweight, .pw = perceptionweight, .w = weight)
	 * @param keyName the name of the object (name of the conclude, workframe, detectable, activity of agent)
	 * @return the weight value from the agents map
	 */
	public static double getWeightValueFromBelief(String struct, String keyName) {
		double weightValue = 1.0;
		Iterator<Agent> agItr = mas.getAgents().iterator();
		String key = null;
		while (agItr.hasNext()) {
			Agent ag = agItr.next();
			MapExpression mapExp = (MapExpression) ag.getBelief("current", "weightList").getBelief();
			if(struct.equals("wf.dw"))
					key = "\"wf_" + keyName + ".dw\"";			
			else if(struct.equals("wf.tw"))
					key = "\"wf_" + keyName + ".tw\"";
			else if(struct.equals("det.dw"))
					key = "\"det_" + keyName + ".dw\"";			
			else if(struct.equals("det.pw"))
					key = "\"det_" + keyName + ".pw\"";			
			else if(struct.equals("con.w"))
					key = "\"con_" + keyName + "\"";		
			else if(struct.equals("act.pw"))
					key = "\"act_" + keyName + ".pw\"";			
			else if(struct.equals("act.w"))
					key = "\"act_" + keyName + ".w\"";
					
			Value amount = null;
			if(mapExp != null) {
				amount = mapExp.getMap().get(key);
				if(amount == null) {
					key = key.replaceAll("\"", "");
					amount = mapExp.getMap().get(key);
				}
			}
			if(amount != null) {
				try {
					weightValue = Double.parseDouble(amount.toString());
					return weightValue;
				} catch(Exception ex) {
					System.out.println("Couldn't convert " + amount + " to a double");
					return weightValue;
				}
			}
		}
		if(Simulator.DEBUG)
			System.out.println("\nINVALID KEY: " + key + " is not a valid key in any agent's weight map\n");
		return weightValue;
	}
	
	public static int getShortestDuration(){
		return mas.getDuration();
	}
}
