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

package gov.nasa.arc.brahms.model.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Knowledge;
import gov.nasa.arc.brahms.model.Variable;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.CommonExpUtils;
import gov.nasa.arc.brahms.model.expression.MapExpression;
import gov.nasa.arc.brahms.model.expression.MapKey;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.expression.VariableValue;
import gov.nasa.arc.brahms.model.expression.ParameterValue;

/**
 * @author nrungta
 * 
 *         novalcomparison ::= BEL.obj-attr | BEL.obj-attr REL.relation-name |
 *         BEL.tuple-object-ref REL.relation-name
 * 
 *         obj-attr ::= tuple-object-ref . ATT.attribute-name { (
 *         collection-index ) }
 * 
 *         tuple-object-ref ::= AGT.agent-name | OBJ.object-name |
 *         COB.conceptual-object-name | ARE.area-name | VAR.variable-name |
 *         PAC.param-name | current
 * 
 *         collection-index ::= ID.literal-string | ID.unsigned |
 *         VAR.variable-name | PAC.param-name
 */

public class NoValComparison implements Comparison, Cloneable {
	String type; // known or unknown
	Expression exp; // obj.att (Term) or objRef/current (TplObjRef)
	MapKey mapIndex;
	String att; // need if exp is var/param
	String relationName; // this can be empty
	Value keyVar;
	List<Value> mapKeys;
	List<Value> allValsOther = new ArrayList<Value>();
	Map<String, Value> varCtx;
	boolean evalutesTrue = false;

	public NoValComparison() {
		// default constructor;
	}

	public void assignFields(String type, Expression exp, MapKey mapIndex,
			String att, String relationName, Value keyVar, List<Value> mapKeys,
			List<Value> allVals) {
		this.type = type;
		this.exp = exp;
		this.mapIndex = mapIndex;
		this.att = att;
		this.relationName = relationName;
		this.keyVar = keyVar;
		this.mapKeys = mapKeys;
		this.allValsOther = allVals;

	}

	/* Constructor for (un)known(obj.att) */
	public NoValComparison(String type, Expression e) {
		assignFields(type, e, null, null, "", null, null,
				new ArrayList<Value>());
	}

	/* Constructor for (un)known(obj.map(key)) */
	public NoValComparison(String type, Expression e, MapKey index) {
		assignFields(type, e, index, null, "", null, null,
				new ArrayList<Value>());
	}

	/* Constructor for (un)known(var.map(key)) */
	public NoValComparison(String type, Value v, String map, MapKey index) {
		assignFields(type, v, index, map, "", null, null,
				new ArrayList<Value>());
	}

	/* Constructor for (un)known(obj.map(var)) */
	public NoValComparison(String type, Expression e, Value v) {
		assignFields(type, e, null, "", "", v, new ArrayList<Value>(),
				new ArrayList<Value>());
	}

	/* Constructor for (un)known(var.map(var)) */
	public NoValComparison(String type, Value v0, String map, Value v1) {
		assignFields(type, v0, null, map, "", v1, new ArrayList<Value>(),
				new ArrayList<Value>());
	}

	/* Constructor for (un)known(obj.att rel) or (un)known(objRef rel) */
	public NoValComparison(String type, Expression e, String rel) {
		assignFields(type, e, null, "", rel, null, null, new ArrayList<Value>());
	}

	/*
	 * Constructor for (un)known(var.att rel), (un)known(var.att), (un)known(var
	 * rel) use (var, att, rel); (var, att, ""); or (var, "", rel)
	 */
	public NoValComparison(String type, Value v, String s, String rel) {
		assignFields(type, v, null, s, rel, null, null, new ArrayList<Value>());
	}


	
	public Expression getExp() {
		return exp;
	}

	public String getRelation() {
		return relationName;
	}

	public Value getKeyVar() {
		return keyVar;
	}

	public List<Value> getAllVals() {
		return allValsOther;
	}

	public String getType() {
		return type;
	}

	
	public boolean eval(Basic b, Frame f, Map<String, Value> varCtx) {
		this.varCtx = varCtx;
		evalutesTrue = false;
		hasBeliefAttributeOrRelation(b, f, varCtx);
		return evalutesTrue;
	}

	/** If known, return whether or not belief exists, otherwise negate */
	public boolean compareEvaluatesTrue(Basic b, Frame f) {
		//System.out.println("coming to compare Evaluate True");
		evalutesTrue = false;
		hasBeliefAttributeOrRelation(b, f, new HashMap<String, Value>());
		return evalutesTrue;

	}

	public boolean factEvaluatesTrue(Basic b, Frame f) {
		// this is a hack until the junit tests are fixed -- neha
		if (f != null)
			f.setOperateOnFacts();
		boolean tmpOther = hasBeliefAttributeOrRelation(b, f,
									new HashMap<String, Value>());
		return tmpOther;

	}

	protected boolean hasBeliefAttributeOrRelation(Basic b, Frame f, Map<String, Value> varCtx) {
		//System.out.println(" ==> has belief or relation method");
		//System.out.println("expression is ;" + exp.toString() + ":");
		List<Basic> actualBindings = new ArrayList<Basic>();
		List<Basic> targets = new ArrayList<Basic>();

		List<Basic> concepts = CommonExpUtils.getBoundConcepts(b, f, exp, varCtx);
		
		
		if (!exp.getAttributeString().equals(""))
			att = exp.getAttributeString();

		if (att != null && !att.equals("")) {
			actualBindings = checkExistenceOfAttribute(f, b, concepts);

			if (relationName != null && !relationName.equals("")) {
				Map<Basic, Basic> bindToTarget = CommonExpUtils.getTargetsOfAttribute(f, b,
						actualBindings, att, mapIndex);
				actualBindings.clear();
				for (Basic basic : bindToTarget.keySet())
					targets.add(bindToTarget.get(basic));
				targets = checkExistenceOfRelation(f, b, targets);
				for (Basic basic : bindToTarget.keySet()) {
					if (!targets.contains(bindToTarget.get(basic)))
						continue;
					actualBindings.add(basic);
				}
			}
		} else if (relationName != null && !relationName.equals("")) {
			actualBindings = checkExistenceOfRelation(f, b, concepts);
		}

		return (actualBindings.size() > 0);
	}

	
	private List<Basic> checkExistenceOfAttribute(Frame f, Basic currBasic,
			List<Basic> lhsConcepts) {
		List<Basic> bindings = new ArrayList<Basic>();
		for (Basic lhsBasic : lhsConcepts) {
			String index = CommonExpUtils.getBeliefOrFactAttribute(f,
					currBasic, lhsBasic, att);
			boolean found = false;
			if (CommonExpUtils.hasBeliefsOrFactsFor(f, currBasic, index)) {
				found = true;
				if (mapIndex != null) {
					found = found & checkMapIndex(f, currBasic, index);
				} else if (keyVar != null) {
					found = found & checkKeyVarIndex(f, currBasic, index);
				}
			}
			if (relationName == null || relationName.equals(""))
				addBindingForKnown(found, lhsBasic, bindings);
			else if (found)
				bindings.add(lhsBasic);
		}
		return bindings;
	}

	private void addBindingForKnown(boolean found, Basic lhsBasic,
			List<Basic> bindings) {
		if (type.equals("known") && found) {
			evalutesTrue = true;
			bindings.add(lhsBasic);
		}

		if (type.equals("unknown") && !found) {
			evalutesTrue = true;
			bindings.add(lhsBasic);
		}
	}

	private boolean checkMapIndex(Frame f, Basic currBasic, String index) {
		List<Knowledge> mapBelifOrFact = CommonExpUtils.getBeliefsOrFactsFor(f,
				currBasic, index);
		for (Knowledge knowElem : mapBelifOrFact) {
			Expression exp = knowElem.getBeliefOrFactExp();
			if (!(exp instanceof MapExpression))
				continue;
			List<Value> vals = ((MapExpression) exp).getValue(mapIndex,
					currBasic, f);
			if (vals.size() > 0)
				return true;
		}

		return false;
	}

	private boolean checkKeyVarIndex(Frame f, Basic currBasic, String index) {
		List<Knowledge> mapBelifOrFact = CommonExpUtils.getBeliefsOrFactsFor(f,
				currBasic, index);
		for (Knowledge knowElem : mapBelifOrFact) {
			Expression exp = knowElem.getBeliefOrFactExp();
			if (!(exp instanceof MapExpression))
				continue;
			MapExpression mapExp = (MapExpression) exp;
			Map<String, Value> mapVals = mapExp.getMap();
			Value otherVal = varCtx.get(keyVar.toString());
			for (String mapIndex : mapVals.keySet()) {
				if (!mapIndex.equals(otherVal.toString()))
					continue;
				return true;
			}
		}
		return false;
	}

	private List<Basic> checkExistenceOfRelation(Frame f, Basic currBasic,
			List<Basic> lhsConcepts) {
		List<Basic> bindings = new ArrayList<Basic>();
		for (Basic lhsBasic : lhsConcepts) {
			boolean found = false;
			String index = CommonExpUtils.getBeliefOrFactRelation(f, currBasic,
					lhsBasic, relationName);
			if (CommonExpUtils.hasBeliefsOrFactsFor(f, currBasic, index)) {
				found = true;
			}
			addBindingForKnown(found, lhsBasic, bindings);
		}

		return bindings;
	}

	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append(super.toString() + "(");
		retVal.append(exp.toString());
		if ((att != null) && (!(att.equals(""))))
			retVal.append("." + att);
		if (mapIndex != null)
			retVal.append("(" + mapIndex + ")");
		if (keyVar != null) {
			if (keyVar instanceof VariableValue)
				retVal.append("(" + ((VariableValue) keyVar).getVarName() + ")");
			else
				retVal.append("(" + ((ParameterValue) keyVar).getName() + ")");
		}
		if (relationName != null && !(relationName.equals("")))
			retVal.append(" " + relationName);
		retVal.append(")\n");
		return retVal.toString();
	}

	
	public Variable getNextFreeVariable(Frame f, Map<String, Value> varCtx) {
		Variable expVar = CommonExpUtils.getVariable(exp, varCtx, f);
		if (expVar != null)
			return expVar;
		return CommonExpUtils.getVariable(keyVar, varCtx, f);
	}

	
	public List<Value> getVariableBindings(Basic b, Frame f, Variable var,
			Map<String, Value> varCtx) {

		if (CommonExpUtils.variableMatchesExpression(b, f, exp, var, varCtx)) {
			return CommonExpUtils.getVariableBindings(b, f, exp, var, varCtx);
		}
		
		if (keyVar == null || !keyVar.isVariableValue())
			return null;
		return CommonExpUtils.getKeyVarValues(b, f, exp, att, varCtx);
	}

	@Override
	public Object clone() {
		NoValComparison nvc = null;
		try {
			nvc = ((NoValComparison) super.clone());
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Value> copyMapKeys = null;
		if (this.mapKeys != null) {
			copyMapKeys = new ArrayList<Value>();
			for (Value val : mapKeys)
				copyMapKeys.add((Value) val.clone());
		}

		List<Value> boundVars = new ArrayList<Value>();
		for (Value val : allValsOther)
			boundVars.add((Value) val.clone());

		Value keyVarClone = null;
		if(keyVar != null) 
			keyVarClone = (Value) keyVar.clone();
	
		
		nvc.assignFields(new String(type), (Expression) exp.clone(), mapIndex,
				new String(att), new String(relationName),
				keyVarClone, copyMapKeys, boundVars);
		return nvc;
	}

}
