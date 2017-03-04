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

package gov.nasa.arc.brahms.vm.api.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.Belief;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.concept.Object_b;
import gov.nasa.arc.brahms.model.expression.DoubleValue;
import gov.nasa.arc.brahms.model.expression.EvaluationOperator;
import gov.nasa.arc.brahms.model.expression.IntegerValue;
import gov.nasa.arc.brahms.model.expression.MapExpression;
import gov.nasa.arc.brahms.model.expression.RelationalExpression;
import gov.nasa.arc.brahms.model.expression.SglObjRef;
import gov.nasa.arc.brahms.model.expression.StringValue;
import gov.nasa.arc.brahms.model.expression.SymbolValue;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.TplObjRef;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.expression.ValueExpression;
import gov.nasa.arc.brahms.model.expression.BooleanValue;
import gov.nasa.arc.brahms.model.expression.CommonExpUtils;
import gov.nasa.arc.brahms.simulator.Utils;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.exceptions.InvalidTypeException;
import gov.nasa.arc.brahms.vm.api.exceptions.UnknownValueException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

public class JObject implements IObject {

	Object_b brahmsObject;

	public String toString() {
		return brahmsObject.getName();
	}

	public JObject(Object_b brahmsObject) {
		this.brahmsObject = brahmsObject;
	}

	public Object_b getBrahmsObject() {
		return brahmsObject;
	}

	public boolean hasBeliefAttributeAnyValue(IConcept lhsConcept, String attributeName, boolean includeUnknown,
			IContext ctx) throws ExternalException {
		String beliefName = CommonUtils.constructBeliefName(this, lhsConcept, attributeName);
		List<Belief> beliefs = brahmsObject.getBeliefs().get(beliefName);
		if (beliefs == null)
			return false;
		for (Belief b : beliefs) {
			if (b.getBelief() instanceof ValueExpression) {
				ValueExpression valExp = (ValueExpression) b.getBelief();
				return (valExp.getValue() != null);
			} else if (b.getBelief() instanceof MapExpression) {
				MapExpression mapExp = (MapExpression) b.getBelief();
				return (mapExp.getMap().size() > 0);
			} else {
				throw new RuntimeException(
						"handle other expression" + "								 in hasBeliefAttributeAnyValue");
			}
		}
		return false;
	}

	public double getBeliefAttributeDouble(IConcept obj, String beliefName, IContext ctx)
			throws UnknownValueException, InvalidTypeException, ExternalException {
		Value val = getBeliefValue(obj, beliefName);
		assert (val instanceof DoubleValue);
		return ((DoubleValue) val).getDblValue();
	}

	public int getBeliefAttributeInt(IConcept obj, String beliefName, IContext ctx)
			throws UnknownValueException, InvalidTypeException, ExternalException {
		Value val = getBeliefValue(obj, beliefName);
		assert (val instanceof IntegerValue);
		return ((IntegerValue) val).getIntValue();

	}

	public void setBeliefAttributeInt(IConcept obj, String beliefName, int setVal, IContext ctx)
			throws InvalidTypeException, ExternalException {
		Term t = CommonUtils.initializeNewTerm(this, obj, beliefName);
		ValueExpression valExp = new ValueExpression(t, EvaluationOperator.EQ, new IntegerValue(setVal));
		brahmsObject.addBelief(t, valExp);
	}

	private Value getBeliefValue(IConcept lhsConcept, String attributeName) {
		String beliefName = CommonUtils.constructBeliefName(this, lhsConcept, attributeName);
		List<Belief> beliefs = brahmsObject.getBeliefs().get(beliefName);
		assert (beliefs.size() > 0);
		Belief b = beliefs.get(0);
		if (b.getBelief() instanceof ValueExpression) {
			ValueExpression valExp = (ValueExpression) b.getBelief();
			return valExp.getValue();
		} else {
			throw new RuntimeException(
					"handle other expression" + "								 in getBeliefValue");
		}
	}

	public String getName() {
		return brahmsObject.getName();
	}

	public String getBeliefAttributeString(IConcept lhsConcept, String attributeName, IContext ctx)
			throws UnknownValueException, InvalidTypeException, ExternalException {
		Value val = getBeliefValue(lhsConcept, attributeName);
		if (val instanceof StringValue)
			return ((StringValue) val).getStringValue();

		assert (val instanceof SymbolValue);
		return ((SymbolValue) val).getVal();
	}

	public void setBeliefAttributeDouble(IConcept lhsConcept, String attributeName, double value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		Term t = CommonUtils.initializeNewTerm(this, lhsConcept, attributeName);
		ValueExpression valExp = new ValueExpression(t, EvaluationOperator.EQ, new DoubleValue(value));
		brahmsObject.addBelief(t, valExp);

	}

	public void setBeliefAttributeSymbol(IConcept lhsConcept, String attributeName, String value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		Term t = CommonUtils.initializeNewTerm(this, lhsConcept, attributeName);
		ValueExpression valExp = new ValueExpression(t, EvaluationOperator.EQ, new SymbolValue(value));
		brahmsObject.addBelief(t, valExp);

	}

	public void setBeliefAttributeString(IConcept lhsConcept, String attributeName, String value, IContext ctx)
			throws InvalidTypeException, ExternalException {

		Term t = CommonUtils.initializeNewTerm(this, lhsConcept, attributeName);
		ValueExpression valExp = new ValueExpression(t, EvaluationOperator.EQ, new StringValue(value));
		brahmsObject.addBelief(t, valExp);
	}

	public boolean isInstanceOf(IClass cls) throws ExternalException {
		throw new RuntimeException("isInstanceOf()");
	}

	public IConcept getBeliefAttributeConcept(IConcept lhsConcept, String attributeName, IContext ctx)
			throws UnknownValueException, InvalidTypeException, ExternalException {
		Value val = getBeliefValue(lhsConcept, attributeName);
		assert (val instanceof SglObjRef);
		SglObjRef refVal = (SglObjRef) val;
		return CommonUtils.findConcept(AbstractExternalActivity.MAS, refVal.getObjRefName());
	}

	public Map<Object, Object> getBeliefAttributeMap(IConcept lhsConcept, String attributeName, boolean includeUnknown,
			IContext ctx) throws InvalidTypeException, ExternalException {
		return BeliefGetterUtils.getBeliefAttributeMap(this, brahmsObject, lhsConcept, attributeName, includeUnknown,
				ctx);

		/*String beliefName = CommonUtils.constructBeliefName(this, lhsConcept, attributeName);
		List<Belief> beliefs = brahmsObject.getBeliefs().get(beliefName);
		Map<Object, Object> higherLevelMap = new HashMap<Object, Object>();
		for (Belief b : beliefs) {
			assert (b.getBelief() instanceof MapExpression);
			MapExpression mapExp = (MapExpression) b.getBelief();
			Map<String, Value> map = mapExp.getMap();
			for (String str : map.keySet()) {
				// TODO: change types of "Val" to either primitive
				// or the ICONCEPT
				Value valStore = map.get(str);

				if (valStore instanceof SglObjRef) {
					SglObjRef sglObjRef = (SglObjRef) valStore;
					IConcept iConcept = CommonUtils.findConcept(Utils.getMas(), sglObjRef.getObjRefName());
					if (CommonExpUtils.isInteger(str)) {
						higherLevelMap.put(Integer.valueOf(str), iConcept);
					} else {
						higherLevelMap.put(str, iConcept);
					}
				} else if (valStore instanceof TplObjRef) {
					TplObjRef tplObjRef = (TplObjRef) valStore;
					IConcept iConcept = CommonUtils.findConcept(Utils.getMas(), tplObjRef.getObjRefName());
					if (CommonExpUtils.isInteger(str)) {
						higherLevelMap.put(Integer.valueOf(str), iConcept);
					} else {
						higherLevelMap.put(str, iConcept);
					}
				} else {
					higherLevelMap.put(str, map.get(str));
				}
			}
		}

		return higherLevelMap;*/
	}

	public void setBeliefAttributeJava(IConcept lhsConcept, String attributeName, int index, Object value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		String beliefName = CommonUtils.constructBeliefName(this, lhsConcept, attributeName);
		List<Belief> beliefs = brahmsObject.getBeliefs().get(beliefName);
		for (Belief b : beliefs) {
			assert (b.getBelief() instanceof MapExpression);
			MapExpression mapExp = (MapExpression) b.getBelief();
			Map<String, Value> map = mapExp.getMap();
			if (value instanceof Integer) {
				Integer intV = (Integer) value;
				IntegerValue intVal = new IntegerValue(intV);
				map.put(String.valueOf(index), intVal);
			} else {
				throw new RuntimeException("need to implment other types in setBeliefAttributeJava");
			}

		}

	}

	public boolean hasBeliefRelationAnyValue(IConcept lhsConcept, String relationName, boolean includeUnknown,
			IContext ctx) throws ExternalException {
		String relation = CommonUtils.constructRelationName(this, lhsConcept, relationName);
		for (Belief b : brahmsObject.getBeliefs().get(relation)) {
			Expression exp = b.getBelief();
			assert (exp instanceof RelationalExpression);
			RelationalExpression relExp = (RelationalExpression) exp;
			// find one relation that is true
			if (relExp.getTruthVal())
				return true;
		}
		return false;
	}

	public List<IConcept> getBeliefRelationConcepts(IConcept lhsConcept, String relationName, IContext ctx)
			throws ExternalException {
		List<IConcept> concepts = new ArrayList<IConcept>();
		String relationKey = CommonUtils.constructRelationName(this, lhsConcept, relationName);
		List<Belief> currRelations = brahmsObject.getBeliefs().get(relationKey);
		for (Belief belief : currRelations) {
			Expression exp = belief.getBelief();
			assert (exp instanceof RelationalExpression);
			RelationalExpression relExp = (RelationalExpression) exp;
			concepts.add(CommonUtils.findConcept(AbstractExternalActivity.MAS, relExp.getRhsObjRef()));
		}
		return concepts;
	}

	public void addBeliefRelationConcept(IConcept lhsConcept, String relationName, IConcept rhsConcept, IContext ctx)
			throws ExternalException {
		String relationKey = CommonUtils.constructRelationName(this, lhsConcept, relationName);
		assert (brahmsObject.getBeliefs().containsKey(relationKey));
		List<Belief> currRelations = brahmsObject.getBeliefs().get(relationKey);
		RelationalExpression relExp = new RelationalExpression(CommonUtils.getLhsIdentifier(this, lhsConcept),
				relationName, rhsConcept.getName());
		Belief newBelief = new Belief(relExp);
		currRelations.add(newBelief);
	}

	public void removeBeliefRelationConcept(IConcept lhsConcept, String relationName, IConcept rhsConcept, IContext ctx)
			throws ExternalException {
		String relationKey = CommonUtils.constructRelationName(this, lhsConcept, relationName);
		assert (brahmsObject.getBeliefs().containsKey(relationKey));
		List<Belief> currRelations = brahmsObject.getBeliefs().get(relationKey);
		Belief beliefToRemove = null;
		String lhsObjRef = CommonUtils.getLhsIdentifier(this, lhsConcept);
		for (Belief singleRelation : currRelations) {
			Expression exp = singleRelation.getBelief();
			assert (exp instanceof RelationalExpression);
			RelationalExpression relExp = (RelationalExpression) exp;
			if (relExp.getLhsObjRef().equals(lhsObjRef) && relExp.getRelationName().equals(relationName)
					&& relExp.getRhsObjRef().equals(rhsConcept.getName())) {
				beliefToRemove = singleRelation;
				break;
			}

		}
		currRelations.remove(beliefToRemove);
	}

	public void setBeliefAttributeConcept(IConcept lhsConcept, String attributeName, IConcept value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		throw new ExternalException("ERROR: implement setBeliefAttributeConcept");
	}

	public void setBeliefAttributeConcept(IConcept lhsConcept, String attributeName, int index, IConcept value,
			IContext ctx) throws InvalidTypeException, ExternalException {

		String beliefName = CommonUtils.constructBeliefName(this, lhsConcept, attributeName);
		assert (brahmsObject.getBeliefs().containsKey(beliefName));
		List<Belief> conceptBelief = brahmsObject.getBelief(beliefName);
		// System.out.println(conceptBelief.get(0).getClass().toGenericString());
		throw new ExternalException("ERROR: implement setBeliefAttributeConcept");

	}

	@Override
	public boolean getBeliefAttributeBoolean(IConcept lhsConcept, String attributeName, IContext ctx)
			throws UnknownValueException, InvalidTypeException, ExternalException {
		Value val = getBeliefValue(lhsConcept, attributeName);
		assert (val instanceof BooleanValue);
		return ((BooleanValue) val).getBooleanValue();
	}

	@Override
	public void setBeliefAttributeBoolean(IConcept lhsConcept, String attributeName, boolean value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		Term t = CommonUtils.initializeNewTerm(this, lhsConcept, attributeName);
		ValueExpression valExp = new ValueExpression(t, EvaluationOperator.EQ, new BooleanValue(value));
		brahmsObject.addBelief(t, valExp);

	}

}
