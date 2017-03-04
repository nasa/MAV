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


import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Knowledge;
import gov.nasa.arc.brahms.model.Variable;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.CommonExpUtils;
import gov.nasa.arc.brahms.model.expression.MapKey;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.RelationalExpression;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.simulator.elems.ActiveFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jhunter
 *
 *rel-comp 	::= BEL.obj-attr REL.relation-name BEL.obj-attr
 *			{ is ID.truth-value } |
 *				BEL.obj-attr REL.relation-name BEL.sgl-object-ref
 *			{ is ID.truth-value } |
 *				BEL.tuple-object-ref REL.relation-name BEL.sgl-object-ref
 *			{ is ID.truth-value }
 *
 */

public class RelComp extends Expression {
	Expression lhsExp; //obj.att (Term) or objRef/current (TplObjRef) or variable
	String lAtt;
	String relationName;
	Expression rhsExp; //obj.att (Term) or objRef/current/unknown (SglObjRef), variable
	String rAtt;
	MapKey mapIndexR;
	Value keyVar;
	boolean truthVal; //is true/false
	String type = "knownval";
	List<Value> lhsVal = null, rhsVal = null;
	Map <String, List<Value>> varVals = new HashMap<String, List<Value>>(); 

	protected void helperFunction(Expression lhsExp, String lAtt,
			String relationName, Expression rhsExp, String rAtt,
			MapKey mapIndexR, Value keyVar, boolean truthVal, String type,
			List<Value> lhsVal, List<Value> rhsVal,
			Map<String, List<Value>> varVals) {
		this.lhsExp = lhsExp;
		this.lAtt = lAtt;
		this.relationName = relationName;
		this.rhsExp = rhsExp;
		this.rAtt = rAtt;
		this.mapIndexR = mapIndexR;
		this.keyVar = keyVar;
		this.truthVal = truthVal;
		if(type != null) this.type = type;
		this.lhsVal = lhsVal;
		this.rhsVal = rhsVal;
	}
	
	public RelComp(RelComp exp) {
		helperFunction(exp.lhsExp, exp.lAtt, exp.relationName, exp.rhsExp,
				exp.rAtt, exp.mapIndexR, exp.keyVar, exp.truthVal, exp.type,
				exp.lhsVal, exp.rhsVal, exp.varVals);
	}
	
	public RelComp(Expression lhsExp, String lAtt, String relationName,
			Expression rhsExp, String rAtt, MapKey mapIndexR, Value keyVar,
			boolean truthVal, String type, List<Value> lhsVal,
			List<Value> rhsVal, Map<String, List<Value>> varVals) {
		helperFunction(lhsExp, lAtt, relationName, rhsExp, rAtt, mapIndexR,
				keyVar, truthVal, type, lhsVal, rhsVal, varVals);
	}

	 /** Default truth value is true **/
	public RelComp(Expression lhs, String rel, Expression rhs) {
		helperFunction(lhs, null, rel, rhs, null, null,
				null, true, null, null, null, this.varVals);
	}

	public RelComp(Expression lhs, String att, String rel, Expression rhs) {
		helperFunction(lhs, att, rel, rhs, null, null, null, true, null, null,
				null, this.varVals);
	}
	
	public RelComp(Expression lhs, String rel, Expression rhs, boolean isTF) {
		helperFunction(lhs, null, rel, rhs, null, null, null, isTF, null, null,
				null, this.varVals);
	}

	// var.latt rel rhs is true/false
	public RelComp(Expression lhs, String att, String rel, Expression rhs,
			boolean isTF) {
		helperFunction(lhs, att, rel, rhs, null, null, null, isTF, null, null,
				null, this.varVals);
	}

	// lhs rel var.att is true/false
	public RelComp(Expression lhs, String rel, Expression rhs, String att,
			boolean isTF) {
		helperFunction(lhs, null, rel, rhs, att, null, null, isTF, null, null,
				null, this.varVals);
	}

	//var.att1 rel var.att2 is true/false
	public RelComp(Expression lhs, String att1, String rel, Expression rhs, 
			String att2, boolean isTF) {
		helperFunction(lhs, att1, rel, rhs, att2, null, null, isTF, null, null,
				null, this.varVals);
	}

	// lhs rel obj.att(key)
	public RelComp(Expression lhs, String rel, Expression rhs, MapKey i2,
			boolean isTF) {
		helperFunction(lhs, null, rel, rhs, null, i2, null, isTF, null, null,
				null, this.varVals);
	}

	//var.att rel obj.att(key)
	public RelComp(Expression lhs, String att, String rel, Expression rhs, 
			MapKey i2, boolean isTF) {
		helperFunction(lhs, att, rel, rhs, null, i2, null, isTF, null, null,
				null, this.varVals);
	}

	//lhs rel var.att(key)
	public RelComp(Expression lhs, String rel, Expression rhs, String att, 
			MapKey i2, boolean isTF) {
		helperFunction(lhs, null, rel, rhs, att, i2, null, isTF, null, null,
				null, this.varVals);
	}

	// var.att1 rel var.att2(key)
	public RelComp(Expression lhs, String att1, String rel, Expression rhs,
			String att2, MapKey i2, boolean isTF) {
		helperFunction(lhs, att1, rel, rhs, att2, i2, null, isTF, null, null,
				null, this.varVals);
	}

	public RelComp(Expression lhs, String rel, Term rhs, Value mapKey,
			boolean isTF) {
		helperFunction(lhs, null, rel, rhs, null, null, mapKey, isTF, null,
				null, null, this.varVals);
	}

	public RelComp(Expression lhs, String rel, Value paramVar, String att2,
			Value mapKey, boolean isTF) {
		helperFunction(lhs, null, rel, paramVar, att2, null, mapKey, isTF,
				null, null, null, this.varVals);
	}

	public RelComp(Expression lhs, String att, String rel, Term rhs, Value 
			mapKey, boolean isTF) {
		helperFunction(lhs, att, rel, rhs, null, null, mapKey, isTF,
				null, null, null, this.varVals);
	}

	public RelComp(Expression lhs, String att, String rel, Expression rhs, 
			String att2, Value mapKey, boolean isTF) {
		helperFunction(lhs, att, rel, rhs, att2, null, mapKey, isTF,
				null, null, null, this.varVals);
	}

	public Expression getLhs() {
		return lhsExp;
	}
	
	public void setLhsExpression(Expression e){
		lhsExp = e;
	}

	public String getRelation() {
		return relationName;
	}

	public Expression getRhs() {
		return rhsExp;
	}
	
	public void setRhsExpression(Expression e){
		rhsExp = e;
	}

	public String getLAtt() {
		return lAtt;
	}
	
	public void setLAtt(String l) {
		lAtt = l;
	}

	public String getRAtt() {
		return rAtt;
	}
	
	public void setRAtt(String r) {
		rAtt = r;
	}

	public boolean getTruthVal() {
		return truthVal;
	}

	public MapKey getIndexR() {
		return mapIndexR;
	}

	public Value getKeyVar() {
		return keyVar;
	}

	public void setType(String t) {
		type = t;
	}
	
	
	public boolean beliefIsTrue(Basic b, Frame f) {
		return ActiveFrame.isFrameActive(b, f);
	}
	
	public boolean factIsTrue(Basic b, Frame f) {
		return ActiveFrame.isFrameActive(b, f);
	}

	public Basic resolveTarget(Basic b, Frame f, Map<String, Value> varCtx,
			Expression exp, String attrName, MapKey mapKey) {
		Basic concept = CommonExpUtils.getSingleBoundConcepts
				(b, f, exp, varCtx);

		if (!exp.getAttributeString().equals("")) {
			attrName = exp.getAttributeString();
		}
		
		if(attrName != null && !attrName.equals("")) {
			Map<Basic, Basic> target = CommonExpUtils
				.getTargetsOfAttribute(f, b, concept, attrName, mapKey);
			assert(target.size() == 1);
			//what it points to
			concept = target.get(concept);
		}
		
		return concept;
	}
	
	protected boolean eval(Basic b, Frame f, Map<String, Value> varCtx) {
		
		Basic lhsConcept = resolveTarget(b, f, varCtx, lhsExp, this.lAtt, null);
		Basic rhsConcept = resolveTarget(b, f, varCtx, rhsExp, this.rAtt, mapIndexR);
		
		//check relation between rhsconcept and 
		
		String lhsIndex = CommonExpUtils.getBeliefOrFactRelation(f, b,
				lhsConcept, relationName);
		
		List<Knowledge> allConcepts = 
					CommonExpUtils.getBeliefsOrFactsFor(f, b, lhsIndex);
		
		boolean found = false;
		for(Knowledge singleConcept : allConcepts) {
			assert(singleConcept.getBeliefOrFactExp() instanceof RelationalExpression);
			RelationalExpression relExp = (RelationalExpression) singleConcept.getBeliefOrFactExp();
			if(relExp == null) break;
			
			String rhsString = "";
	
			if(rhsConcept == null) {
				if(rhsExp.toString().equals("unknown")) {
					rhsString = rhsExp.toString();
				} else break;
			} else 
				rhsString = rhsConcept.getName();
			
			
			if (relExp.getRhsObjRef().equals(rhsString)
					&& relExp.getTruthVal() == this.truthVal) {
				//System.out.println("found the relation");
				found = true;	
			} else if (relExp.getRhsObjRef().equals("current") 
					&& rhsString.equals(b.getName())
					&& relExp.getTruthVal() == this.truthVal) {
				found = true;
			}
		}
		if(type.equals("knownval"))
			return found;
		return !found;
	}

	public Variable getNextFreeVariable(Frame f, Map<String, Value> varCtx) {
	
		Variable lhsExpVar = CommonExpUtils.getVariable(lhsExp, varCtx, f);
		if (lhsExpVar != null) return lhsExpVar;
		
		return CommonExpUtils.getVariable(rhsExp, varCtx, f);
		

	}
	
	public List<Value> getVariableBindings(Basic b, Frame f, Variable var,
			Map<String, Value> varCtx) {
		
		if (CommonExpUtils.variableMatchesExpression(b, f, lhsExp, var, varCtx)) {
			return CommonExpUtils.getVariableBindings(b, f, lhsExp, var, varCtx);
		} else if (CommonExpUtils.variableMatchesExpression(b, f, rhsExp, var, varCtx)) {
			return CommonExpUtils.getVariableBindings(b, f, rhsExp, var, varCtx);
		} else 
			return null;
		
	}
	
	public List<Value> getLHSVals(){
		return lhsVal;
	}
	
	public List<Value> getRHSVals(){
		return rhsVal;
	}

	public void resetBindings(){
		try{
			lhsVal.clear();
		}
		catch(Exception e){}
		try{
			rhsVal.clear();
		}
		catch(Exception e){}
	}
	
	public boolean hasParams() {
		if (lhsExp instanceof ParameterValue)
			return true;
		if (rhsExp instanceof ParameterValue)
			return true;
		if (keyVar != null && keyVar instanceof ParameterValue)
			return true;
		return false;
	}	

	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append(super.toString() + "(");
		retVal.append(lhsExp.toString() + " " + relationName + " " 
				+ rhsExp.toString() + " is " + truthVal);
		return retVal.toString();
	}
	
	@Override
	public Map <String, List<Value>> getVarValues(){
		return varVals;
	}
	
	@Override
	public List<Value> getVarValue(String name){
		return varVals.get(name);
	}
	
	public void setVarValues(String varName, List<Value> vals) {
		List<Value> values = varVals.get(varName);
		if (values == null){
			varVals.put(varName, vals);
		}
		else{
			for (int i = 0; i < vals.size(); i++)
				values.add(vals.get(i));
		}
	}
	
	public String getType(){
		return type;
	}

	@Override
	public List<Value> getValue(Basic b, Frame f) {
		throw new RuntimeException("RelComp.getValue not implemented");
	}

	@Override
	public List<Value> getFactValue(Basic b, Frame f) {
		throw new RuntimeException("RelComp.getFactValue not implemented");
	}
	
	@Override
	public Expression getLhsExp() {
		return lhsExp;
	}
	
	@Override
	public Expression getRhsExp() {
		return rhsExp;
	}
	@Override
	public void resetVarValues(){
		varVals = new HashMap<String, List<Value>>();
	}
	
	@Override
	public Value getKeyVarL(){
		return keyVar;
	}
	@Override
	public Value getKeyVarR(){
		return keyVar;
	}
	@Override
	public List<Value> getLhsValues(){
		return lhsVal;
	}
	@Override
	public List<Value> getRhsValues(){
		return rhsVal;
	}
	public void setTruthValue(boolean truthValue){
		this.truthVal = truthValue;
	}
	
	@Override
	public Object clone(){
		RelComp rc = new RelComp(
				lhsExp,
				lAtt,
				relationName,
				rhsExp,
				rAtt,
				mapIndexR,
				keyVar,
				truthVal,
				type,
				lhsVal,
				rhsVal,
				null);
		return rc;
		
	}
}
