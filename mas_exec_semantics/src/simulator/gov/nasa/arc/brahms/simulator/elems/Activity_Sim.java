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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Fact;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.WorkFrame;
import gov.nasa.arc.brahms.model.activity.*;
import gov.nasa.arc.brahms.model.comparison.EvalValCompExpOpExp;
import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Area;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.concept.Class_b;
import gov.nasa.arc.brahms.model.concept.Concept;
import gov.nasa.arc.brahms.model.concept.Conceptual_Class;
import gov.nasa.arc.brahms.model.concept.Conceptual_Object;
import gov.nasa.arc.brahms.model.concept.Object_b;
import gov.nasa.arc.brahms.model.expression.EvaluationOperator;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.RelationalExpression;
import gov.nasa.arc.brahms.model.expression.SglObjRef;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.expression.ValueExpression;
import gov.nasa.arc.brahms.model.expression.StringValue;
import gov.nasa.arc.brahms.simulator.DistanceMatrix;
import gov.nasa.arc.brahms.simulator.Simulator;
import gov.nasa.arc.brahms.simulator.Utils;
import gov.nasa.arc.brahms.simulator.world.FactSet;

public class Activity_Sim {
	
	public static void performActivity(Activity act, Basic b, Frame f) {
		 if (act instanceof JavaActivity) {
			 System.out.println("coming to perform java activity :" + act.getName());
			JavaActivity ja = (JavaActivity) act;
			PerformJavaActivity.performJava(ja, b, f);
		}
		else if (act instanceof PrimitiveActivity) {
			 System.out.println("coming to perform primitive activity :" + act.getName());

				performPrimitive();
		} 
		else if (act instanceof BroadcastActivity) {
			 System.out.println("coming to perform broadcast activity :" + act.getName());

			BroadcastActivity ba = (BroadcastActivity) act;
			PerformCommunicateActivity.performBroadcast(ba, b);
		}
		else if (act instanceof CommunicateActivity) {
			 System.out.println("coming to perform communicate activity :" + act.getName());
			// System.exit(1);
			CommunicateActivity ca = (CommunicateActivity) act;
			PerformCommunicateActivity.performCommunicate(ca, b);
		}
	}
	
	/**
	 * Type checks the activity and send to be performed
	 * @param act the activity to be performed
	 * @param b the current agent/object_b
	 * @param currTime can be start, end (or paramname?)
	 */
	public static void performActivity(Activity act, Basic b, String currTime, int clock) {
		//An activity is performed so we note down the start/end time for workload calculation
		//System.out.println("with list cleared");
		if(currTime.equals("start")){
			b.activityName = act.getName();
			b.activityStart = clock;
		}
		else{
			b.activityEnd = clock;
		}
		if (act instanceof BroadcastActivity) {
			BroadcastActivity ba = (BroadcastActivity) act;
			if (getWhen(act, ba.getWhen(b)).equals(currTime))
				PerformCommunicateActivity.performBroadcast(ba, b);
		}
		else if (act instanceof CommunicateActivity) {
			CommunicateActivity ca = (CommunicateActivity) act;
			if (getWhen(act, ca.getWhen(b)).equals(currTime)){
				PerformCommunicateActivity.performCommunicate(ca, b);
			}
		}
		else if (act instanceof CreateAgentActivity){ 
			throw new RuntimeException("need to add support for create agent activity");
		}
		else if (act instanceof CreateAreaActivity){ 
			throw new RuntimeException("need to add support for create area activity");
		}
		else if (act instanceof CreateObjectActivity) {
			CreateObjectActivity coa = (CreateObjectActivity) act;
			if (getWhen(act, coa.getWhen(b)).equals(currTime))
				performCreateObject(coa, b);
		}
		else if (act instanceof GestureActivity){ 
			throw new RuntimeException("need to add support for GestureActivity");
		}
		else if (act instanceof GetActivity) {
			GetActivity ga = (GetActivity) act;
			if (getWhen(act, ga.getWhen(b)).equals(currTime))
				performGet(ga, b);
		}
		else if (act instanceof PutActivity) {
			PutActivity pa = (PutActivity) act;
			if (getWhen(act, pa.getWhen(b)).equals(currTime))
				performPut(pa, b);
		}
		else if (act instanceof JavaActivity) {
			JavaActivity ja = (JavaActivity) act;
			if (getWhen(act, ja.getWhen(b)).equals(currTime)) {
				//PerformJavaActivity.performJava(ja, b);
			}
		}
		else if (act instanceof MoveActivity && currTime.equals("end")) {
			MoveActivity ma = (MoveActivity) act;
			performMove(ma, b);
		}
		else if (act instanceof PrimitiveActivity) {
			performPrimitive();
		}
	}
	
	

	/**
	 * If the when is a parameter, it finds the parameter value
	 * Otherwise returns the when property
	 * @param act
	 * @param w
	 * @return
	 */
	private static String getWhen(Activity act, String w) {
		String when = w;
		if ((!(when.equals("start"))) && (!(when.equals("end")))) {
			List<Parameter> params = act.getParams();
			for (int i = 0; i < params.size(); i++) {
				if (params.get(i).getName().equals(when)) {
					Parameter p = params.get(i);
					when = ((StringValue) p.getVals().get(0)).toString();
				}
			}
		}
		return when;
	}

	



	private static void performCreateObject(CreateObjectActivity coa, Basic b) {
		//Commented out so I don't have to fix the bugs just yet
		/*if (Simulator.DEBUG)
			System.out.println("MQMQMQMQMQ CREATE OBJ MQMQMQMQMQ");
		String action = coa.getAction(b);
		//get obj
		Concept src = getSource(b, coa);
		Concept cpt = null;
		cpt = getConcept(b, coa, action, src);
		if (cpt == null)
			throw new RuntimeException("***ERROR in Activity_Sim: no concept " +
					"to create new object");
		//get location
		String loc = coa.getLocation(b); //get location property
		List<Parameter> params = coa.getParams(); //check if parameter
		for (int i = 0; i < params.size(); i++) {
			if (params.get(i).getName().equals(loc)) {
				Value v = params.get(i).getVals().get(0);
				if (v instanceof ParameterValue) {
					WorkFrame wf = (WorkFrame) b.getCurrentWorkFrame().get(b.getCurrentWorkFrame().size()-2);
					List<Value> locVals = wf.getVarInstanceVal(((ParameterValue) v).getName());
					loc = locVals.get(0).getValue(b, wf).get(0).toString();
				}
				else loc = params.get(i).getVals().get(0).toString();
			}
		}
		//get location of source if null
		if ((loc == null || loc.equals("")) && (src instanceof Object_b)) {
			Object_b obj = (Object_b) src;
			loc = obj.getLocation();
		}

		Concept newObj;
		String newObjNm = coa.getDestName(b);
		//check if destnm is parameter
		for (int i = 0; i < params.size(); i++) {
			if (params.get(i).getName().equals(newObjNm)) {
				Value v = params.get(i).getVals().get(0);
				if (v instanceof ParameterValue) {
					WorkFrame wf = (WorkFrame) b.getCurrentWorkFrame().get(b.getCurrentWorkFrame().size()-2);
					List<Value> vals = wf.getVarInstanceVal(((ParameterValue) v).getName());
					newObjNm = vals.get(0).getValue(b, wf).toString();
				}
				else
					newObjNm = ((StringValue) params.get(i).getVals().get(0)).getStringValue();
			}
		}

		//create conceptual object if conceptual class or conceptual object
		if (cpt instanceof Conceptual_Class) {
			newObj = createConceptObj(coa, cpt, src, newObjNm, loc);
		} else if (cpt instanceof Conceptual_Object) {
			newObj = createConceptObj(coa, cpt, src, newObjNm, loc);
		} else { //create 'regular' object from class or object
			newObj = createObject(coa, cpt, src, newObjNm, loc);
		}

		//create fact for location
		if (loc != null && !(loc.equals(""))) {
			ValueExpression valExp1 = new ValueExpression(newObj.getName(), "location",
					EvaluationOperator.EQ, new StringValue(loc));
			FactSet.updateFacts(new Term(newObj.getName(), "location"), valExp1);
			//all agents in loc detect it TODO
			Set<Agent> agents = Utils.getMas().getAgents();
			Iterator<Agent> agItr = agents.iterator();
			while (agItr.hasNext()) {
				Agent tmp = agItr.next();
				if (tmp.getLocation().equals(loc))
					tmp.addBelief(new Term(newObj.getName(), "location"), valExp1);
			}
		}

		//if copy & obj, copy all beliefs
		if ((action.equals("copy")) && (newObj instanceof Object_b) && (src 
				instanceof Object_b)) {
			Object_b srcObj = (Object_b) src;
			Object_b newObject = (Object_b) newObj;
			Map<String, List<Belief>> allBels = srcObj.getBeliefs();
			newObject.copyAllBeliefs(allBels);
		}
		//System.out.println("copied beliefs?");

		//bind dest param to new instance
		ParameterValue pv = coa.getDestination();
		boolean found = false;
		for (int i = 0; i < params.size(); i++) {
			if (params.get(i).getName().equals(pv.getName())) {
				params.get(i).addVal(new TplObjRef(newObj.getName()));
				found = true;
			}
		}
		//System.out.println("bound dest param to new inst?");

		if (!found)
			throw new RuntimeException("***ERROR in Activity_Sim: could not " +
					"bind createObject to destination parameter");
					*/
	}

	/**
	 * Get the source property from createObjectActivity
	 * includes checking parameters
	 * @param coa
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Concept getSource(Basic b, CreateObjectActivity coa) {
		Concept cpt = null;
		String source = coa.getSource(b);
		List<Parameter> params = coa.getParams(); //check if parameter
		for (int i = 0; i < params.size(); i++) {
			if (params.get(i).getName().equals(source)) {
				Value v = params.get(i).getVals().get(0);
				if (v instanceof ParameterValue) {
					WorkFrame wf = (WorkFrame) b.getCurrentWorkFrame().get(b.getCurrentWorkFrame().size()-2);
					List<Value> vals = wf.getVarInstanceVal(((ParameterValue) v).getName());
					source = vals.get(0).getValue(b, wf).toString();
				}
				else
					source = params.get(i).getVals().get(0).toString();
			}
		}
		cpt = Utils.getConcept(source);
		return cpt;
	}

	/**
	 * Gets the set of conceptual objects from the concept obj property 
	 * and checks for params, if not set, gets concept objs from source
	 * @param concepts
	 * @param params
	 * @param src
	 * @return
	 */
	private static Set<String> getConceptualObjects(Set<String> concepts, 
			List<Parameter> params, Concept src) {
		Set<String> finalConcepts = new HashSet<String>();
		Iterator<String> strItr = concepts.iterator();
		while (strItr.hasNext()) { //get concept obj property, check for params
			String s = strItr.next();
			boolean paramConcept = false;
			for (int i = 0; i < params.size(); i++) {
				if (params.get(i).getName().equals(s)) {
					finalConcepts.add(params.get(i).getVals().get(0).toString());
					paramConcept = true;
				}
			}
			if (!paramConcept)
				finalConcepts.add(s);
		}
		if (finalConcepts.isEmpty()) { //check source for concept objs
			if (src instanceof Object_b) {
				Object_b srcObj = (Object_b) src;
				finalConcepts = srcObj.getPartOfs();
			}
			if (src instanceof Conceptual_Object) {
				Conceptual_Object srcObj = (Conceptual_Object) src;
				finalConcepts = srcObj.getPartOfs();
			}
		}
		return finalConcepts;
	}

	/**
	 * Creates the object, inherits, adds to MAS
	 * @param coa
	 * @param cpt
	 * @param src
	 * @param name
	 * @param loc
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Object_b createObject(CreateObjectActivity coa, Concept cpt,
			Concept src, String name, String loc) {
		int uniqueID = 0;
		List<Parameter> params = coa.getParams();
		Basic basic = (Basic) cpt;
		String clsnm = basic.getName();
		if (basic instanceof Object_b)
			clsnm = ((Object_b) basic).getClassInstance();
		Class_b clsInst = (Class_b) Utils.getConcept(clsnm);
		uniqueID = findNextID(name);
		name = name + uniqueID;
		Object_b newObj = new Object_b(name, name, 
				basic.getCost(), basic.getTimeUnit(), loc, clsInst);
		//find any conceptual object parents
		Set<String> concepts = coa.getConceptObjs();
		Set<String> finalConcepts = getConceptualObjects(concepts, params, src);		
		newObj.setPartOfs(finalConcepts);
		//inherit and add to MAS
		newObj.inheritFromClasses(Utils.getMas().getClasses());
		Utils.getMas().addObjects((Object_b) newObj);
		return newObj;
	}

	/**
	 * Creates the conceptual object, inherits, adds to MAS
	 * @param coa
	 * @param cpt
	 * @param src
	 * @param name
	 * @param loc
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Conceptual_Object createConceptObj(CreateObjectActivity
			coa, Concept cpt, Concept src, String name, String loc) {
		int uniqueID = 0;
		List<Parameter> params = coa.getParams();

		Concept clsInst = Utils.getConcept(((Conceptual_Object) 
				cpt).getClassInstance());
		uniqueID = findNextConceptID(name);
		name = name + uniqueID;
		Conceptual_Object newObj;
		if (cpt instanceof Conceptual_Object)
			newObj = new Conceptual_Object(name, cpt.getDisplay(), 
					(Conceptual_Class) clsInst);
		else
			newObj = new Conceptual_Object(name, cpt.getDisplay(), 
					(Conceptual_Class) cpt);
		//find any conceptual object parents
		Set<String> concepts = coa.getConceptObjs();
		Set<String> finalConcepts = getConceptualObjects(concepts, params, src);		
		newObj.setPartOfs(finalConcepts);
		//inherit and add to MAS
		((Conceptual_Object) newObj).inheritFromClasses(
				Utils.getMas().getConceptClasses());
		Utils.getMas().addConceptualObjects((Conceptual_Object) newObj);
		return newObj;
	}

	/**
	 * Gets the concept to create the object from
	 * source if new, dest if copy
	 * @param coa
	 * @param action
	 * @param src
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Concept getConcept(Basic b, CreateObjectActivity coa, String action, Concept src) {
		Concept cpt;
		if (action.equals("new")) {
			cpt = src;
		} else {
			//copy
			String dest = coa.getDestination().getName();
			List<Parameter> params = coa.getParams(); //check if parameter
			for (int i = 0; i < params.size(); i++) {
				if (params.get(i).getName().equals(dest)) {
					Value v = params.get(i).getVals().get(0);
					if (v instanceof ParameterValue) {
						WorkFrame wf = (WorkFrame) b.getCurrentWorkFrame().get(b.getCurrentWorkFrame().size()-2);
						List<Value> vals = wf.getVarInstanceVal(((ParameterValue) v).getName());
						dest = vals.get(0).getValue(b, wf).toString();
					}
					else dest = params.get(i).getType();
				}
			}
			cpt = Utils.getConcept(dest);
		}
		return cpt;
	}


	private static int findNextConceptID(String name) {
		int uniqueID = 0;
		Set<Conceptual_Object> conceptObjs = Utils.getMas().getConceptObjs();
		Iterator<Conceptual_Object> coItr = conceptObjs.iterator();
		while (coItr.hasNext()) {
			Conceptual_Object tmp = coItr.next();
			if (tmp.getName().contains(name))
				uniqueID++;
		}
		return uniqueID;
	}

	private static int findNextID(String name) {
		int uniqueID = 0;
		Set<Object_b> objs = Utils.getMas().getObjects();
		Iterator<Object_b> objItr = objs.iterator();
		while (objItr.hasNext()) {
			Object_b tmp = objItr.next();
			if (tmp.getName().contains(name))
				uniqueID++;
		}
		return uniqueID;
	}

	public void performGesture() {
		//TODO
	}

	private static void performGet(GetActivity ga, Basic b) {
		if (Simulator.DEBUG)
			System.out.println("MGMGMGMG GET MGMGMGMG");
		List<String> allItems = ga.getItems();
		for (int i = 0; i < allItems.size(); i++) {
			//check if someone contains it
			Basic container = Utils.getContainer(allItems.get(i));
			//someone contains item
			if (container != null) {
				if (container.getName().equals(ga.getSource(b))) {
					//Add the beliefs for the agent/object getting the item
					RelationalExpression relExpB = new RelationalExpression("current", 
							"contains", allItems.get(i));
					b.addBelief("current contains", relExpB);
					RelationalExpression relExpF = new RelationalExpression(b.getName(), 
							"contains", allItems.get(i));
					
					FactSet.addFact(b.getName() + " contains", relExpF);
					
					//retract belief/fact for container
					relExpB.setTruthVal(false);
					container.updateBeliefs("current contains", relExpB);
					RelationalExpression relExpF2 = new RelationalExpression(container.getName(), 
							"contains", allItems.get(i));
					relExpF2.setTruthVal(false);
					FactSet.updateFacts(container, container.getName() + " contains", relExpF2);
				}
			} else { //nobody contains item
				RelationalExpression relExpB = new RelationalExpression("current", 
						"contains", allItems.get(i));
				b.addBelief("current contains", relExpB);
				RelationalExpression relExpF = new RelationalExpression(b.getName(), 
						"contains", allItems.get(i));
				FactSet.addFact(b.getName() + " contains", relExpF);
			}
		}
	}

	private static void performPut(PutActivity pa, Basic b) {
		if (Simulator.DEBUG) {
			System.out.println("MPMPMPMP PUT MPMPMPMP");
		}
		String bLoc = "";
		if (b instanceof Agent)
			bLoc = ((Agent) b).getLocation();
		if (b instanceof Object_b)
			bLoc = ((Object_b) b).getLocation();
		List<String> allItems = pa.getItems();
		Map<String, List<Fact>> allFacts = FactSet.getFacts();
		List<Fact> contains = allFacts.get(b.getName() + " contains");
		if (contains == null)
			return;
		for (int i = 0; i < allItems.size(); i++) {	//search for each item
			boolean bContainsItem = false;
			for(int j = 0; j < contains.size(); j++) { //search through contains
				Fact f = contains.get(j);
				RelationalExpression rel = (RelationalExpression) f.getFact();
				//if b contains item
				if (rel.getRhsObjRef().equals(allItems.get(i)))
					bContainsItem = true;
			}
			if (bContainsItem) {
				//retract bel/fact
				RelationalExpression relExpB = new RelationalExpression("current", 
						"contains", allItems.get(i));
				relExpB.setTruthVal(false);
				b.updateBeliefs("current contains", relExpB);
				RelationalExpression relExpF = new RelationalExpression(b.getName(), 
						"contains", allItems.get(i));
				relExpF.setTruthVal(false);
				FactSet.updateFacts(b, b.getName() + " contains", relExpF);
				String dest = pa.getDestination(b);
				if (!(dest.equals(""))) {
					Basic destObj = Utils.getAgentObj(dest);
					//dest is ag/obj, add belief/fact
					if (destObj != null) {
						destObj.addBelief("current contains", relExpB);
						RelationalExpression relExpF2 = new RelationalExpression(
								destObj.getName(), "contains", allItems.get(i));
						FactSet.addFact(destObj.getName() + " contains", relExpF2);
					} else { //dest is loc - broadcast to agents
						putItemInArea(dest, allItems.get(i));
					}
				} else if (!(bLoc.equals(""))) {
					putItemInArea(bLoc, allItems.get(i));
				}	
			}
		}
	}

	private static void putItemInArea(String dest, String item) {
		Basic itemObj = Utils.getAgentObj(item);
		ValueExpression valExp = new ValueExpression("current", "location",
				EvaluationOperator.EQ, new StringValue(dest));
		itemObj.updateBeliefs(new Term("current", "location"), valExp);
		ValueExpression valExp2 = new ValueExpression(itemObj.getName(), "location",
				EvaluationOperator.EQ, new StringValue(dest));
		FactSet.updateFacts(new Term(item, "location"), valExp2);

		ArrayList<Parameter> act1params = new ArrayList<Parameter>();
		BroadcastActivity newact = new BroadcastActivity("tmpBroad",
				"tmpBroad", 0, 0, 0, false, act1params, "end");
		//transfer defs
		Expression exp0 = new Term("current", "location");
		Expression exp2 = new EvalValCompExpOpExp(exp0, EvaluationOperator.EQ, exp0);
		TransferDefinition def0 = new TransferDefinition("send", exp2);
		newact.addTransferDefinition(def0);
		newact.addAreas(dest);
		PerformCommunicateActivity.performBroadcast(newact, itemObj);
	}

	/**
	 * Moves the agent or obj to the location specified by the activity
	 * @param act
	 * @param b the agent or object_b
	 */
	private static void performMove(MoveActivity act, Basic b) {
		if (Simulator.DEBUG)
			System.out.println("MTMTMTMT MOVE MTMTMTMT");
		String destination = act.getLocation(b);
		if (b instanceof Agent)
			((Agent) b).setLocation(destination);
		else
			((Object_b) b).setLocation(destination);
		Term lhs = new Term("current", "location");
		ValueExpression valExp = new ValueExpression(lhs.getObjRefName(),
				lhs.getAttrName(), EvaluationOperator.EQ, new SglObjRef(destination));
		b.updateBeliefs(lhs, valExp);
		System.out.println(b.getName() + " just updatedbelief: " + b.getBelief(lhs.toString()));
		ValueExpression valExp1 = new ValueExpression(b.getName(), "location",
				EvaluationOperator.EQ, new SglObjRef(destination));
		FactSet.updateFacts(new Term(b.getName(), "location"), valExp1);
	}

	private static void performPrimitive() {/*do nothing*/}


	public static int findPathDist(String oldLoc, String newLoc) {
		DistanceMatrix matrix = Utils.getDistMatrix();
		Set<Area> allAreas = Utils.getMas().getAllAreas();
		Iterator<Area> areaItr = allAreas.iterator();
		int area1ID = -1;
		int area2ID = -1;
		while (areaItr.hasNext()) {
			Area anArea = areaItr.next();
			if (anArea.getName().equals(oldLoc))
				area1ID = anArea.getID();
			if (anArea.getName().equals(newLoc))
				area2ID = anArea.getID();
		}
		if (area1ID < 0 || area2ID < 0) {
			return Integer.MAX_VALUE;
		}
		int dist = matrix.getValue(area1ID, area2ID);
		if (Simulator.DEBUG)
			System.out.println("MATRIX: " + oldLoc + " -> " + newLoc + " = " + dist);
		return dist;
	}

}
