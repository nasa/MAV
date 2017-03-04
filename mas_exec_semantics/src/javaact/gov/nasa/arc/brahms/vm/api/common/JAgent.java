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

import gov.nasa.arc.brahms.model.Belief;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.expression.BooleanValue;
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
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.exceptions.InvalidTypeException;
import gov.nasa.arc.brahms.vm.api.exceptions.UnknownValueException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JAgent implements IAgent {

	Agent agent;
	
	public JAgent(Agent brahmsAgent) {
		this.agent = brahmsAgent;
	}
	
	public Agent getBrahmsAgent() {
		return agent;
	}
	
	private Value getBeliefValue(IConcept lhsConcept, String attributeName) {
		String beliefName = CommonUtils.constructBeliefName(this, lhsConcept, attributeName);
		List<Belief> beliefs = agent.getBeliefs().get(beliefName);
		assert(beliefs.size() > 0);
		Belief b = beliefs.get(0); 
		if(b.getBelief() instanceof ValueExpression) {
			ValueExpression valExp = (ValueExpression) b.getBelief();
			return valExp.getValue();
		} else {
			throw new RuntimeException("handle other expression"
					+ "								 in getBeliefValue");
		}
	}
	
	public boolean hasBeliefAttributeAnyValue(IConcept obj, String attributeName,
			boolean includeUnknown, IContext ctx) throws ExternalException {
		String beliefName = CommonUtils.constructBeliefName(this, obj,
				attributeName);
		if(!agent.getBeliefs().containsKey(beliefName)) return false;
		List<Belief> beliefs = agent.getBeliefs().get(beliefName);
		for (Belief b : beliefs) {
			if (b.getBelief() instanceof ValueExpression) {
				ValueExpression valExp = (ValueExpression) b.getBelief();
				return (valExp.getValue() != null);
			} else if (b.getBelief() instanceof MapExpression) {
				MapExpression mapExp = (MapExpression) b.getBelief();
				return (mapExp.getMap().size() > 0);
			} else {
				throw new RuntimeException("handle other expression"
						+ "								 in hasBeliefAttributeAnyValue");
			}
		}
		return false;
	}

	public double getBeliefAttributeDouble(IConcept obj, String beliefName,
			IContext ctx) throws UnknownValueException, InvalidTypeException,
			ExternalException {
		Value val = getBeliefValue(obj, beliefName);
		assert(val instanceof DoubleValue);
		return ((DoubleValue) val).getDblValue();
	}

	public int getBeliefAttributeInt(IConcept obj, String beliefName,
			IContext ctx) throws UnknownValueException, InvalidTypeException,
			ExternalException {
		Value val = getBeliefValue(obj, beliefName);
		assert(val instanceof IntegerValue);
		return ((IntegerValue) val).getIntValue();
	}

    //Retrieves a String value for an attribute of a concept from a belief
    //in the belief state The attribute's Brahms type may be either string or symbol.
	public String getBeliefAttributeString(IConcept lhsConcept,
			String attributeName, IContext ctx) throws UnknownValueException,
			InvalidTypeException, ExternalException {
		Value val = getBeliefValue(lhsConcept, attributeName);
		if(val instanceof StringValue)
			return ((StringValue) val).getStringValue();
		if(val instanceof SymbolValue)
			return ((SymbolValue) val).getVal();
		
		throw new RuntimeException("trying to get belief attribute string of: " + 
								val.getClass().toString());
	}

	
	
	public void setBeliefAttributeInt(IConcept obj, String beliefName,
			int setVal, IContext ctx) throws InvalidTypeException,
			ExternalException {
		Term t = CommonUtils.initializeNewTerm(this, obj, beliefName);
		ValueExpression valExp = new ValueExpression(t,
					EvaluationOperator.EQ, new IntegerValue(setVal));
		agent.updateBeliefs(t, valExp);
	}

	public void setBeliefAttributeDouble(IConcept lhsConcept,
			String attributeName, double value, IContext ctx)
					throws InvalidTypeException, ExternalException {
		Term t = CommonUtils.initializeNewTerm(this, lhsConcept, attributeName);
		ValueExpression valExp = new ValueExpression(t,
					EvaluationOperator.EQ, new DoubleValue(value));
		agent.updateBeliefs(t, valExp);
	}

	public void setBeliefAttributeSymbol(IConcept lhsConcept,
			String attributeName, String value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		Term t = CommonUtils.initializeNewTerm(this, lhsConcept, attributeName);
		ValueExpression valExp = new ValueExpression(t,
				EvaluationOperator.EQ, new SymbolValue(value));
		agent.updateBeliefs(t, valExp);		
	}

	public void setBeliefAttributeString(IConcept lhsConcept,
			String attributeName, String value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		Term t = CommonUtils.initializeNewTerm(this, lhsConcept, attributeName);
		ValueExpression valExp = new ValueExpression(t,
				EvaluationOperator.EQ, new StringValue(value));
		agent.updateBeliefs(t, valExp);			
	}

	public IConcept getBeliefAttributeConcept(IConcept lhsConcept,
			String attributeName, IContext ctx) throws UnknownValueException,
			InvalidTypeException, ExternalException {
		Value val = getBeliefValue(lhsConcept, attributeName);
		if (val instanceof SglObjRef) {
			SglObjRef refVal = (SglObjRef) val;
			return CommonUtils.findConcept(AbstractExternalActivity.MAS,
					refVal.getObjRefName());
		} else if (val instanceof TplObjRef) {
			TplObjRef refVal = (TplObjRef) val;
			return CommonUtils.findConcept(AbstractExternalActivity.MAS,
					refVal.getObjRefName());
		}
		throw new RuntimeException("need to handle concept :" + val.getClass().toString());
	}

	public Map<Object, Object> getBeliefAttributeMap(IConcept lhsConcept,
			String attributeName, boolean includeUnknown, IContext ctx)
			throws InvalidTypeException, ExternalException {
		return BeliefGetterUtils.getBeliefAttributeMap
					(this, agent, lhsConcept, attributeName, includeUnknown, ctx);
	}

	public void setBeliefAttributeJava(IConcept lhsConcept,
			String attributeName, int index, Object value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		throw new RuntimeException("set belief attribute java");
		
	}

	public boolean hasBeliefRelationAnyValue(IConcept lhsConcept,
			String relationName, boolean includeUnknown, IContext ctx)
			throws ExternalException {
		throw new RuntimeException("ERROR: implement hasBeliefRelationAnyValue");
	}


	/**
	 *
	 * Retrieves a list of concepts that are related to a given concept by a
	 * named relation by inspecting the beliefs in the activity performer's
	 * belief state. Unknown values are filtered out of the returned list so
	 * that (concept relation unknown) will result in an empty list being
	 * returned.
	 * 
	 * Parameters: lhsConcept - an IConcept whose values for a relation are to
	 * be retrieved relationName - a String name for a relation defined for
	 * lhsConcept
	 * 
	 * Returns: List the list of IConcept instances that are related
	 * to the lhsConcept by the relation Throws: ExternalException - if no
	 * attribute with the specified name is defined for this concept or an
	 * internal error occurs
	 */

	public List<IConcept> getBeliefRelationConcepts(IConcept lhsConcept,
			String relationName, IContext ctx) throws ExternalException {
		List<IConcept> concepts = new ArrayList<IConcept>();
		String relationKey = CommonUtils.constructRelationName(this, lhsConcept,
				relationName);
		List<Belief> currRelations = agent.getBeliefs().get(relationKey);
		for (Belief belief : currRelations) {
			Expression exp = belief.getBelief();
			assert (exp instanceof RelationalExpression);
			RelationalExpression relExp = (RelationalExpression) exp;
			concepts.add(CommonUtils.findConcept(AbstractExternalActivity.MAS,
					relExp.getRhsObjRef()));
		}
		return concepts;
	}

	public void addBeliefRelationConcept(IConcept lhsConcept,
			String relationName, IConcept rhsConcept, IContext ctx)
			throws ExternalException {
		throw new RuntimeException("ERROR: implement addBeliefRelationConcept");
		
	}

	public void removeBeliefRelationConcept(IConcept lhsConcept,
			String relationName, IConcept rhsConcept, IContext ctx)
			throws ExternalException {
		throw new RuntimeException("ERROR: implement removeBeliefRelationConcept");
		
	}
	
	public void setBeliefAttributeConcept(IConcept lhsConcept,
			String attributeName, int index, IConcept value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		throw new ExternalException("ERROR: implement setBeliefAttributeConcept");

		
	}

	public String getName() {
		return agent.getName();
	}
	
	public void setBeliefAttributeConcept(IConcept lhsConcept, String attributeName,
			IConcept value, IContext ctx)
			throws InvalidTypeException, ExternalException {
			Term t = CommonUtils.initializeNewTerm(this, lhsConcept, attributeName);
			ValueExpression valExp = new ValueExpression(t, 
						EvaluationOperator.EQ, new SglObjRef(value.getName()));
			agent.addBelief(t, valExp);
	}

	@Override
	public boolean getBeliefAttributeBoolean(IConcept lhsConcept, String attributeName, IContext ctx)
			throws UnknownValueException, InvalidTypeException, ExternalException {
		Value val = getBeliefValue(lhsConcept, attributeName);
		assert(val instanceof BooleanValue);
		return ((BooleanValue) val).getBooleanValue();
	}

	@Override
	public void setBeliefAttributeBoolean(IConcept lhsConcept, String attributeName, boolean value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		Term t = CommonUtils.initializeNewTerm(this, lhsConcept, attributeName);
		ValueExpression valExp = new ValueExpression(t,
				EvaluationOperator.EQ, new BooleanValue(value));
		agent.updateBeliefs(t, valExp);		
	}
}
