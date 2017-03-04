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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.Activity;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.activity.BroadcastActivity;
import gov.nasa.arc.brahms.model.activity.CommunicateActivity;
import gov.nasa.arc.brahms.model.activity.TransferDefinition;
import gov.nasa.arc.brahms.model.comparison.EvalValCompExpOpExp;
import gov.nasa.arc.brahms.model.comparison.RelComp;
import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Area;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.concept.Object_b;
import gov.nasa.arc.brahms.model.expression.CommonExpUtils;
import gov.nasa.arc.brahms.model.expression.EvaluationOperator;
import gov.nasa.arc.brahms.model.expression.MapExpression;
import gov.nasa.arc.brahms.model.expression.MapKey;
import gov.nasa.arc.brahms.model.expression.MapKeyValPair;
import gov.nasa.arc.brahms.model.expression.RelationalExpression;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.TplObjRef;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.expression.ValueExpression;
import gov.nasa.arc.brahms.simulator.Utils;

public class PerformCommunicateActivity {
	
	private static void sendVal(Basic sender, Activity act, Basic receiver, 
			EvalValCompExpOpExp eval, Map<String, Value> varCtx) {
		
		//System.out.println("senderName " + sender.getName());
		//System.out.println("receiever Name " + receiver.getName()) ;
		if(sender.getName().equals(receiver.getName())) return;
		
		List<Value> lVals = UpdateUtils.getValuesOfExpression(sender,
				eval, varCtx, true);
		Basic concept = CommonExpUtils.getSingleBoundConcepts(sender, 
				sender.getActualWorkFrame(), eval.getLhsExp(),
				varCtx);
		
		String objRef = CommonExpUtils.getBeliefIdentifier(sender, concept);
		String factRef = CommonExpUtils.getFactIdentifier(sender);
		
		String lAttributeName = UpdateUtils.getAttributeNameString(eval, true);
		String identifier = 
				CommonExpUtils.getBeliefOrFactAttribute(null, sender, objRef, lAttributeName);
		
		if(sender.getBelief(identifier) == null) return;
		Expression expB = sender.getBelief(identifier).get(0).getBelief();
		Term updateTerm = PerformCommunicateActivity.constructTerm(objRef, factRef, 
														lAttributeName, receiver);
		
		if (UpdateUtils.isMapExpression(eval.getLMapKey(), eval.getLKeyVar())) {
			MapKeyValPair mapKeyValPair = UpdateUtils.
					createNewMapExpression(sender, concept, eval.getLhsExp(),
							lAttributeName, eval.getLMapKey(),
					eval.getLKeyVar(), lVals, varCtx);
			
			receiver.updateBeliefs(updateTerm, mapKeyValPair);
			return;
		} else if(expB instanceof MapExpression) {
			MapExpression exp = (MapExpression) expB;
			Map<String, Value> currMap = exp.getMap();
			for(String key : currMap.keySet()) {
				MapKey mapKey = new MapKey(key);
				Value val = (Value) currMap.get(key).clone();
				MapKeyValPair pair = new MapKeyValPair(mapKey, val);
				receiver.updateBeliefs(updateTerm, pair);
			}
			return;
		}
		
		ValueExpression beliefValExp = new ValueExpression(getRef(objRef, factRef, receiver),
				lAttributeName, EvaluationOperator.EQ, lVals.get(0));
		receiver.updateBeliefs(updateTerm, beliefValExp);
	}
	
	private static String getRef(String objRef, String factRef, Basic receiver) {
		if(objRef.toString().equals(receiver.getName())) 
			return "current";
		return factRef;
	}
	
	private static Term constructTerm(String objRef, String factRef, String attrName,
														Basic receiver) {
			return new Term(getRef(objRef, factRef, receiver), attrName);
	}

	private static void sendRel(Basic sender, Activity act, Basic receiver,
			RelComp relCom, Map<String, Value> varCtx) {
		
		if(sender.getName().equals(receiver.getName())) return;
		
		Basic lhsConcept = relCom.resolveTarget
				(sender, null, varCtx, relCom.getLhsExp(), relCom.getLAtt(),
						null);

		Basic rhsConcept = relCom.resolveTarget
				(sender, null, varCtx, relCom.getRhsExp(), relCom.getRAtt(),
						relCom.getIndexR());

		String relationName = relCom.getRelation();
		boolean truthValue = relCom.getTruthVal();


		String identifier;

		String lhsConceptName;
		if(lhsConcept.getName().equals(receiver.getName())) {
			lhsConceptName = "current";
			identifier = "current "+ relationName;
		} else {
			lhsConceptName = lhsConcept.getName();
			identifier = CommonExpUtils.getFactRelation(sender, relationName);

		}
		RelationalExpression relExp = new RelationalExpression(
				lhsConceptName, relationName, rhsConcept.getName(),
				truthValue);
		
		receiver.addBelief(identifier, relExp);
	}
	
	public static void performCommunicate(CommunicateActivity act, Basic b) {
	
		List<Basic> communicateWith = new ArrayList<Basic>();
		act.populateParameterMap();
		for(String basicName : act.getWith()) {
			String targetName = basicName;
			//Check if the communication is to a parameter value		
			if(act.getParameterName(basicName) != null) {
				targetName = act.getParameterValue(basicName).toString();
			} 
			communicateWith.add(CommonExpUtils.findConcept(b, targetName));		 
		}
		Map<String, Value> varCtx = new HashMap<String, Value>();
		List<Parameter> params = act.getParams();
		for(Parameter p : params) {
			if(p.getVals().get(0).toString().equals("current")) {
				varCtx.put(p.getName(),  new TplObjRef(b.getName()));
			}
			else
				varCtx.put(p.getName(), p.getVals().get(0));
			//System.out.println("Printing the variables" + varCtx.toString());
		}
		
	
		List<TransferDefinition> defs = act.getTransferDef();
	
		for(Basic communicatingWith : communicateWith) {
		
			for (int j = 0; j < defs.size(); j++) {
				TransferDefinition td = defs.get(j);
				String action = td.getTransferAction();
				Expression exp = td.getExpression();
				
		
				if (action.equals("send")) {
					if (exp instanceof EvalValCompExpOpExp) {
						sendVal(b, act, communicatingWith, (EvalValCompExpOpExp) exp, varCtx);
						
					} else if (exp instanceof RelComp) {
						sendRel(b, act, communicatingWith, (RelComp) exp, varCtx);

					}
				} else if (action.equals("receive")) { //receive
					if (exp instanceof EvalValCompExpOpExp) {
						sendVal(communicatingWith, act, b, (EvalValCompExpOpExp) exp, varCtx);
					} else if (exp instanceof RelComp) {
							sendRel(communicatingWith, act, b, 
									(RelComp) exp, varCtx);
					} 
				} 
			}
		}
	
	}			
	

	/**
	 * Broadcast only sends to agents, not objects
	 * @param act
	 * @param b the Basic who is broadcasting
	 */
	public static void performBroadcast(BroadcastActivity act, Basic b) {
		act.populateParameterMap();
		Map<String, Value> varCtx = new HashMap<String, Value>();
		List<Parameter> params = act.getParams();
		for(Parameter p : params) {
			varCtx.put(p.getName(), p.getVals().get(0));
		}
		List<TransferDefinition> defs = act.getTransferDefs();
		String loc = "";
		if (b instanceof Agent)  //get the sender's location
			loc = ((Agent) b).getLocation();
		if (b instanceof Object_b)
			loc = ((Object_b) b).getLocation();
		
		
		for (int i = 0; i < defs.size(); i++) { //loop through transfer defs
			if (defs.get(i).getTransferAction().equals("send")) {
				Iterator<Agent> AgItr = Utils.getMas().getAgents().iterator();
				while(AgItr.hasNext()) {
					Agent a = AgItr.next();
					Expression exp = defs.get(i).getExpression();
					if (exp instanceof EvalValCompExpOpExp) {
						EvalValCompExpOpExp eval = (EvalValCompExpOpExp) exp;
						if (broadcastingTo(b, a, loc, act)) {
							sendVal(b, act, a, eval, varCtx);
						}
					} else { //relationalexpression
						RelComp rel = (RelComp) exp;
						if (broadcastingTo(b, a, loc, act)) {
							//communication = "with = \"" + a.getName() + "\" type = \"send\" belief = \"" + rel +"\"";  
							//messages.add(communication);
							sendRel(b, act, a, rel, varCtx);
						}
					}
				}
			} 
		}
	}

	/**
	 * Determines if "receiver" should receive the broadcasted message
	 * If receiver is in the location the broadcast is being sent, return true
	 * If broadcast activity location is not specified, it checks against
	 * the sender's location, if sender has no location, send to all
	 * Also checks for subareas
	 * @param broadcaster sender of the message
	 * @param receiver potential receiver of the message
	 * @param loc location of the broadcaster
	 * @param act the broadcast activity
	 * @return
	 */
	private static boolean broadcastingTo(Basic broadcaster, Agent receiver, 
			String loc, BroadcastActivity act) {
		if (broadcaster.getName().equals(receiver.getName()))
			return false;
		
		String receiverLocation = receiver.getLocation();
		//no 'to', and location same as sender
		if (receiverLocation.equals(loc) && act.getAreas().isEmpty())
			return true;
		//no 'to', and no location (send to all agents)
		if ((loc.equals("") || loc.equals("none")) && act.getAreas().isEmpty())
			return true;
		//'to' contains agents location
		if (act.getAreas().contains(receiverLocation))
			return true;
		//if yes to sub areas, find subareas, and check
		if (act.getToSubAreas(broadcaster) == true) {
			for (int j = 0; j < act.getAreas().size(); j++) {
				String tmpArea = act.getAreas().get(j);
				List<Area> subAreas = Utils.getSubAreas(tmpArea);
				for (int k = 0; k < subAreas.size(); k++) {
					if (subAreas.get(k).getName().equals(receiverLocation))
						return true;
				}
			}
			//if no 'to', check loc subAreas
			if ((act.getAreas().isEmpty()) && (!(loc.equals("") || loc.equals("none")))) {
				List<Area> subAreas = Utils.getSubAreas(loc);
				for (int k = 0; k < subAreas.size(); k++) {
					if (subAreas.get(k).getName().equals(receiverLocation))
						return true;
				}
			}
		}
		return false;
	}

}
