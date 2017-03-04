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


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.Conclude;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.CommonExpUtils;
import gov.nasa.arc.brahms.model.expression.EvaluationOperator;
import gov.nasa.arc.brahms.model.expression.MapKey;
import gov.nasa.arc.brahms.model.expression.MapKeyValPair;
import gov.nasa.arc.brahms.model.expression.RelationalExpression;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.expression.ValueExpression;
import gov.nasa.arc.brahms.model.comparison.RelComp;
import gov.nasa.arc.brahms.model.comparison.EvalValCompExpOpExp;
import gov.nasa.arc.brahms.simulator.world.FactSet;


public class Conclude_Sim {

	protected static Basic objReference = null;
	/**
	 * Gets the belief and fact-certainties, calculates them
	 * sends to sametype to type check, and sends to concludeBelief or
	 * concludeFact to assign the new belief/fact
	 * @param b Agent or Object_b
	 * @param c the conclude statement
	 */
	public static void concludeStatement(Basic b, Conclude c, Frame frame) {
			
			Map<String, Value> varCtx = getVarContext(frame);
		
			Expression result = c.getResult();
			if (result instanceof EvalValCompExpOpExp) {
				EvalValCompExpOpExp exp0 = (EvalValCompExpOpExp) result;
				concludeVal(b, frame, c, exp0, varCtx);
			} else { //if (result instanceof RelComp) {
				RelComp exp0 = (RelComp) result;
				concludeRel(b, frame, c, exp0, varCtx);
			}
	}
	
	private static Map<String, Value> getVarContext(Frame frame) {
		//System.out.println(frame.getVarInstances().size()  + " size");
		Map<String, Value> varCtx = new HashMap<String, Value>();
		if(frame.getVarInstances() == null || frame.getVarInstances().size() == 0)
			return varCtx;
		List<Map<String, List<Value>>> varInstances = frame.getVarInstances();
		//TODO: this needs to be fixed, when there are many you need to pick the right one
		Map<String, List<Value>> varVals = varInstances.get(0);
		for(String varName : varVals.keySet()) {
			varCtx.put(varName, varVals.get(varName).get(0));
		}
		return varCtx;
	}

	
	private static void concludeVal(Basic b, Frame f, Conclude c, 
			EvalValCompExpOpExp exp0, Map<String, Value> varCtx) {
		//System.out.println("================ conclude ========== f." + f.getName());
		//System.out.println(exp0.toString());
		//System.out.println("var context :" + varCtx.toString());
		
		List<Value> rVals = UpdateUtils.
					getValuesOfExpression(b, exp0, varCtx, false);
		
		if(rVals.size() == 0) return;
		
		Expression lhsExp = exp0.getLhsExp();
		String lAttributeName = UpdateUtils.getAttributeNameString(exp0, true);
		MapKey lMapIndex = exp0.getLMapKey();
		Value lKeyVar = exp0.getLKeyVar();

		Basic concept = CommonExpUtils.getSingleBoundConcepts(b, f, lhsExp,
				varCtx);
		
		String objRef = CommonExpUtils.getBeliefIdentifier(b, concept);
		if(rVals.size() > 1) {
			System.out.println("the size is greater 1");
			if(UpdateUtils.assigningMap(concept, lAttributeName)) {
				System.out.println("assigng the a map");
				System.exit(1);
			}
		}
		
		
		if (UpdateUtils.isMapExpression(lMapIndex, lKeyVar)) {
			createNewMapExpression(b, concept, objRef, lhsExp, lAttributeName, lMapIndex,
					lKeyVar, rVals, varCtx);
			return;
		}
	
		ValueExpression beliefValExp = new ValueExpression(objRef,
				lAttributeName, EvaluationOperator.EQ, rVals.get(0));

		b.updateBeliefs(new Term(objRef, lAttributeName), beliefValExp);

		ValueExpression factValExp = new ValueExpression(concept.getName(),
				lAttributeName, EvaluationOperator.EQ, rVals.get(0));
		FactSet.updateFacts(new Term(concept.getName(), lAttributeName), factValExp);

	}
	
	private static void createNewMapExpression(Basic b, Basic lhsConcept,
			String objRef,
			Expression lhsExp, String lAttributeName,
			MapKey lMapIndex, Value lKeyVar, List<Value> rVals, 
									Map<String, Value> varCtx) {
	
		MapKeyValPair mapKeyValPair = UpdateUtils.
				createNewMapExpression(b, lhsConcept, lhsExp, lAttributeName,
						lMapIndex, lKeyVar, rVals, varCtx);
		
		b.updateBeliefs(new Term(objRef, lAttributeName), mapKeyValPair);	
		
		FactSet.updateFacts(new Term(lhsConcept.getName(), 
										lAttributeName), mapKeyValPair);
	}
	
	private static void concludeRel(Basic b, Frame f, Conclude c,
			RelComp relCom, Map<String, Value> varCtx) {
		
		Basic lhsConcept = relCom.resolveTarget
					(b, f, varCtx, relCom.getLhsExp(), relCom.getLAtt(),
							null);
		
		Basic rhsConcept = relCom.resolveTarget
					(b, f, varCtx, relCom.getRhsExp(), relCom.getRAtt(),
							relCom.getIndexR());
		
		String relationName = relCom.getRelation();
		boolean truthValue = relCom.getTruthVal();

		String beliefIdentifier = CommonExpUtils.getBeliefRelation(b, lhsConcept,
				relationName);

		//System.out.println(lhsConcept.toString());
		//System.out.println(rhsConcept.toString());
		
		RelationalExpression relExp = new RelationalExpression(
				lhsConcept.getName(), relationName, rhsConcept.getName(),
				truthValue);
		b.addBelief(beliefIdentifier, relExp);

		String factIdentifier = CommonExpUtils.getFactRelation(b, relationName);
		String objRef;
		if (lhsConcept.getName().equals("current"))
			objRef = b.getName();
		else
			objRef = lhsConcept.getName();
		RelationalExpression relExpFact = new RelationalExpression(objRef,
				relationName, rhsConcept.getName(), truthValue);
		FactSet.addFact(factIdentifier, relExpFact);

	}
	
}
