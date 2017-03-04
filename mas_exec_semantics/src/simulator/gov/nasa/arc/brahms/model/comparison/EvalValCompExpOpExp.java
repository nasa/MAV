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
import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Knowledge;
import gov.nasa.arc.brahms.model.Variable;
import gov.nasa.arc.brahms.model.expression.BinaryExpression;
import gov.nasa.arc.brahms.model.expression.CommonExpUtils;
import gov.nasa.arc.brahms.model.expression.EvaluationOperator;
import gov.nasa.arc.brahms.model.expression.MapKey;
import gov.nasa.arc.brahms.model.expression.MapExpression;
import gov.nasa.arc.brahms.model.expression.TplObjRef;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.simulator.elems.ActiveFrame;
import gov.nasa.arc.brahms.model.expression.VariableValue;
import gov.nasa.arc.brahms.model.expression.Term;

public class EvalValCompExpOpExp extends Expression {
	
	Expression lhsExp; //obj.att (Term) or SglObjRef or variable/param
	String lAtt; //need this if lhsExp is variable or param in var.att
	MapKey mapIndexL; //MapKey = int or string, or variable/param
	Value keyVarL;
	List<Value> mapKeysL;
	MapKey mapIndexR;
	Value keyVarR;
	List<Value> mapKeysR;
	EvaluationOperator evalOp; //EQ, NEQ, GT, GTE, LT, LTE
	Expression rhsExp; //int, dbl, string, symbol, bool, Term, SglObjRef, var/param
	String rAtt; //need this if rhsExp is variable/param in var.att
	String type = "knownval";
	Basic b;
	Frame f;

	
	Map<Value, Value> validExpressions;
	//<A, A>; <B, B> 
	//varsVals: <x:= {A,B}, y:{A,B}>
	//{A}
	//varVals: <x: {B}, y:{B}>
	//<B,B>
	
	//first check: if there are any variables 
	//lhs vs. rhs
	//LHS := {A, B} RHS := {A, B}
	// Valid := (LHS=A, RHS=A), (LHS=B, RHS=B)
	
	public EvalValCompExpOpExp(EvalValCompExpOpExp exp){
		this.lhsExp = exp.lhsExp;
		this.lAtt = exp.lAtt;
		this.mapIndexL = exp.mapIndexL;
		this.keyVarL = exp.keyVarL;
		this.mapKeysL = exp.mapKeysL;
		this.mapIndexR = exp.mapIndexR;
		this.keyVarR = exp.keyVarR;
		this.mapKeysR= exp.mapKeysR;
		this.evalOp = exp.evalOp;
		this.rhsExp = exp.rhsExp;
		this.rAtt = exp.rAtt;
		this.type = exp.type;
		this.b = exp.b;
		this.f = exp.f;
	}
	
	public EvalValCompExpOpExp(
			Expression lhsExp, 
			String lAtt, 
			MapKey mapIndexL, 
			Value keyVarL,
			List<Value> mapKeysL,
			MapKey mapIndexR,
			Value keyVarR,
			List<Value> mapKeysR,
			EvaluationOperator evalOp,
			Expression rhsExp, 
			String rAtt, 
			String type,
			Basic b,
			Frame f) {
		
		this.lhsExp =lhsExp;
		this.lAtt = lAtt;
		this.mapIndexL =  mapIndexL;
		this.keyVarL = keyVarL;
		this.mapKeysL = mapKeysL;
		this.mapIndexR = mapIndexR;
		this.keyVarR = keyVarR;
		this.mapKeysR = mapKeysR;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = rAtt;
		this.type = type;
		this.b = b;
		this.f = f;
	}
	
	//obj.att = obj.att, sgl
	public EvalValCompExpOpExp(Expression lhsExp, EvaluationOperator evalOp, 
			Expression rhsExp) {
		this.lhsExp = lhsExp;
		this.lAtt = null;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = null;
		this.mapIndexR = null;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//LHSexp is a map, includes index
	//obj.map(key) = obj.att, sgl
	public EvalValCompExpOpExp(Expression lhsExp, MapKey mapIndex, 
			EvaluationOperator evalOp, Expression rhsExp) {
		this.lhsExp = lhsExp;
		this.lAtt = null;
		this.mapIndexL = mapIndex;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = null;
		this.mapIndexR = null;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//both LHSexp and RHSexp are maps, includes indices
	//obj.map(key) = obj.map(key)
	public EvalValCompExpOpExp(Expression lhsExp, MapKey mapIndex, 
			EvaluationOperator evalOp, Expression rhsExp, MapKey mapIndex2) {
		this.lhsExp = lhsExp;
		this.lAtt = null;
		this.mapIndexL = mapIndex;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.mapIndexR = mapIndex2;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = null;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//RHSexp is a map, indcludes index
	//obj.att = obj.map(key)
	public EvalValCompExpOpExp(Expression lhsExp, 
			EvaluationOperator evalOp, Expression rhsExp, MapKey mapIndex2) {
		this.lhsExp = lhsExp;
		this.lAtt = null;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = null;
		this.mapIndexR = mapIndex2;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//LHS is var.att or param.att
	//var.att = obj.att/sgl
	public EvalValCompExpOpExp(Expression lhsExp, String lAtt, 
			EvaluationOperator evalOp, Expression rhsExp) {
		this.lhsExp = lhsExp;
		this.lAtt = lAtt;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = null;
		this.mapIndexR = null;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//LHS is var.att, RHS is map
	//var.att = obj.map(key)
	public EvalValCompExpOpExp(Expression lhsExp, String lAtt, 
			EvaluationOperator evalOp, Expression rhsExp, MapKey mapIndex) {
		this.lhsExp = lhsExp;
		this.lAtt = lAtt;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = null;
		this.mapIndexR = mapIndex;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//RHS is var.att or param.att
	//obj.att = var.att
	public EvalValCompExpOpExp(Expression lhsExp, EvaluationOperator evalOp, 
			Expression rhsExp, String rAtt) {
		this.lhsExp = lhsExp;
		this.lAtt = null;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = rAtt;
		this.mapIndexR = null;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//LHS AND RHS are var.att or param.att
	//var.att = var.att
	public EvalValCompExpOpExp(Expression lhsExp, String lAtt, 
			EvaluationOperator evalOp, Expression rhsExp, String rAtt) {
		this.lhsExp = lhsExp;
		this.lAtt = lAtt;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = rAtt;
		this.mapIndexR = null;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//LHS exp is a param/var.map(idx), RHS term
	//var.map(key) = obj.att/sgl
	public EvalValCompExpOpExp(Expression lhsExp, String mapName, MapKey mapIndex, 
			EvaluationOperator evalOp, Expression rhsExp) {
		this.lhsExp = lhsExp;
		this.lAtt = mapName;
		this.mapIndexL = mapIndex;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = null;
		this.mapIndexR = null;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//LHS is term, RHS is param/var.map(idx)
	//obj.att = var.map(key)
	public EvalValCompExpOpExp(Expression lhsExp,
			EvaluationOperator evalOp, Expression rhsExp, String mapName, MapKey mapIndex) {
		this.lhsExp = lhsExp;
		this.lAtt = null;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = mapName;
		this.mapIndexR = mapIndex;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//LHS exp is a param/var.att, RHS is param/var.map(idx)
	//var.att = var.map(key)
	public EvalValCompExpOpExp(Expression lhsExp, String att, 
			EvaluationOperator evalOp, Expression rhsExp, String mapName, 
			MapKey mapIndex) {
		this.lhsExp = lhsExp;
		this.lAtt = att;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = mapName;
		this.mapIndexR = mapIndex;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	
	
	//LHS exp is a param/var.map(idx), RHS param/var.att
	//var.map(key) = var.att
	public EvalValCompExpOpExp(Expression lhsExp, String mapName, MapKey mapIndex, 
			EvaluationOperator evalOp, Expression rhsExp, String att) {
		this.lhsExp = lhsExp;
		this.lAtt = mapName;
		this.mapIndexL = mapIndex;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = att;
		this.mapIndexR = null;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//LHS exp is a param/var.map(idx), RHS param/var.map(idx)
	//var.map(key) = var.map(key)
	public EvalValCompExpOpExp(Expression lhsExp, String mapName, MapKey mapIndex, 
			EvaluationOperator evalOp, Expression rhsExp, String map2, MapKey mapIndex2) {
		this.lhsExp = lhsExp;
		this.lAtt = mapName;
		this.mapIndexL = mapIndex;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = map2;
		this.mapIndexR = mapIndex2;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//var.map(var) = obj.att/sgl/int
	public EvalValCompExpOpExp(Expression lhsExp, String mapName, Value keyL, 
			EvaluationOperator evalOp, Expression rhsExp) {
		this.lhsExp = lhsExp;
		this.lAtt = mapName;
		this.mapIndexL = null;
		this.keyVarL = keyL;
		this.mapKeysL = new ArrayList<Value>();
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = null;
		this.mapIndexR = null;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	// obj.att/sgl/int = var.map(var)
	public EvalValCompExpOpExp(Expression lhsExp, EvaluationOperator evalOp, 
			Expression rhsExp, String mapName, Value keyR) {
		this.lhsExp = lhsExp;
		this.lAtt = null;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = new ArrayList<Value>();
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = mapName;
		this.mapIndexR = null;
		this.keyVarR = keyR;
		this.mapKeysR = null;
	}
	
	//var.map(var) = var.att
	public EvalValCompExpOpExp(Expression lhsExp, String mapName, Value keyL, 
			EvaluationOperator evalOp, Expression rhsExp, String att) {
		this.lhsExp = lhsExp;
		this.lAtt = mapName;
		this.mapIndexL = null;
		this.keyVarL = keyL;
		this.mapKeysL = new ArrayList<Value>();
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = att;
		this.mapIndexR = null;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//obj.map(var) = obj.map(var)
	public EvalValCompExpOpExp(Expression lhsExp, Value keyL, 
			EvaluationOperator evalOp, Expression rhsExp, String att, Value keyR) {
		this.lhsExp = lhsExp;
		this.lAtt = null;
		this.mapIndexL = null;
		this.keyVarL = keyL;
		this.mapKeysL = new ArrayList<Value>();
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = att;
		this.mapIndexR = null;
		this.keyVarR = keyR;
		this.mapKeysR = new ArrayList<Value>();
	}
	
	//obj.map(var) = exp
	public EvalValCompExpOpExp(Expression lhsExp, Value keyL, 
			EvaluationOperator evalOp, Expression rhsExp) {
		this.lhsExp = lhsExp;
		this.lAtt = null;
		this.mapIndexL = null;
		this.keyVarL = keyL;
		this.mapKeysL = new ArrayList<Value>();
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = null;
		this.mapIndexR = null;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//var.map(var) = var.map(var)
	public EvalValCompExpOpExp(Expression lhsExp, String mapName, Value keyL, 
			EvaluationOperator evalOp, Expression rhsExp, String mapName2, Value keyR) {
		this.lhsExp = lhsExp;
		this.lAtt = mapName;
		this.mapIndexL = null;
		this.keyVarL = keyL;
		this.mapKeysL = new ArrayList<Value>();
		this.evalOp = evalOp;
		this.rhsExp = rhsExp;
		this.rAtt = mapName2;
		this.mapIndexR = null;
		this.keyVarR = keyR;
		this.mapKeysR = new ArrayList<Value>();
	}

	//var.att = var.att(key)
	public EvalValCompExpOpExp(Expression paramVarLHS, String lattStr,
			EvaluationOperator eq, Expression paramVarRHS,
			String rattStr, Value key) {
		this.lhsExp = paramVarLHS;
		this.lAtt = lattStr;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = eq;
		this.rhsExp = paramVarRHS;
		this.rAtt = rattStr;
		this.mapIndexR = null;
		this.keyVarR = key;
		this.mapKeysR = null;
	}
	
	//var.att = obj.att(var)
	public EvalValCompExpOpExp(Expression varParam, String string,
			EvaluationOperator eq, Term term, Value varKey) {
		this.lhsExp = varParam;
		this.lAtt = string;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = eq;
		this.rhsExp = term;
		this.rAtt = null;
		this.mapIndexR = null;
		this.keyVarR = varKey;
		this.mapKeysR = null;
	}

	//var.att(key) = obj.att(key)
	public EvalValCompExpOpExp(Expression varParam, String att,
			MapKey mapKey, EvaluationOperator eq, Term term, MapKey mapKey2) {
		this.lhsExp = varParam;
		this.lAtt = att;
		this.mapIndexL = mapKey;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = eq;
		this.rhsExp = term;
		this.rAtt = null;
		this.mapIndexR = mapKey2;
		this.keyVarR = null;
		this.mapKeysR = null;
	}

	//var.att(key) = obj.att(var)
	public EvalValCompExpOpExp(Expression varParam, String att,
			MapKey mapKey, EvaluationOperator eq, Term term,
			Value mapKey2) {
		this.lhsExp = varParam;
		this.lAtt = att;
		this.mapIndexL = mapKey;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = eq;
		this.rhsExp = term;
		this.rAtt = null;
		this.mapIndexR = null;
		this.keyVarR = mapKey2;
		this.mapKeysR = null;
	}

	//var.att(key) = var.att(var)
	public EvalValCompExpOpExp(Expression varParam, String att,
			MapKey mapKey, EvaluationOperator eq, Expression varParam2,
			String att2, Value mapKey2) {
		this.lhsExp = varParam;
		this.lAtt = att;
		this.mapIndexL = mapKey;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = eq;
		this.rhsExp = varParam2;
		this.rAtt = att2;
		this.mapIndexR = null;
		this.keyVarR = mapKey2;
		this.mapKeysR = null;
	}

	//var.att(var) = var.att(key)
	public EvalValCompExpOpExp(Expression varParam, String att,
			Value mapKey, EvaluationOperator eq,
			Expression varParam2, String att2, MapKey mapKey2) {
		this.lhsExp = varParam;
		this.lAtt = att;
		this.mapIndexL = null;
		this.keyVarL = mapKey;
		this.mapKeysL = null;
		this.evalOp = eq;
		this.rhsExp = varParam2;
		this.rAtt = att2;
		this.mapIndexR = mapKey2;
		this.keyVarR = null;
		this.mapKeysR = null;
	}
	
	//var/term = obj.att(oldVar)
	public EvalValCompExpOpExp(Expression lhs, EvaluationOperator eq,
			Expression rhs, Value varparam) {
		this.lhsExp = lhs;
		this.lAtt = null;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = eq;
		this.rhsExp = rhs;
		this.rAtt = null;
		this.mapIndexR = null;
		this.keyVarR = varparam;
		this.mapKeysR = null;
	}

	public EvalValCompExpOpExp(Expression lhs, EvaluationOperator eq, 
			Value varparam, String att,	Value varkey) {
		this.lhsExp = lhs;
		this.lAtt = null;
		this.mapIndexL = null;
		this.keyVarL = null;
		this.mapKeysL = null;
		this.evalOp = eq;
		this.rhsExp = varparam;
		this.rAtt = att;
		this.mapIndexR = null;
		this.keyVarR = varkey;
		this.mapKeysR = null;	}

	public String toString(){ 
		String retVal = "";
		if(rAtt != null && !rAtt.equals("")) {
			retVal += lhsExp.toString() + evalOp.toString() + rhsExp.toString() +"."+ rAtt;
		} else 
		retVal += lhsExp.toString() + evalOp.toString() + rhsExp.toString();
		return retVal;
	}
	
	
	@Override
	public Expression getLhsExp() {
		return lhsExp;
	}
	
	@Override
	public String getLAtt() {
		return lAtt;
	}
	
	public MapKey getIndexL() {
		return mapIndexL;
	}
	
	public MapKey getIndexR() {
		return mapIndexR;
	}
	
	public void setIndexL(MapKey L){
		mapIndexL = L;
	}
	
	public void setIndexR(MapKey R){
		mapIndexR = R;
	}
	
	@Override
	public String getRAtt() {
		return rAtt;
	}
	@Override
	public Expression getRhsExp() {
		return rhsExp;
	}
	
	public MapKey getLMapKey() {
		return mapIndexL;
	}
	
	public MapKey getRMapKey() {
		return mapIndexR;
	}
	
	public Value getLKeyVar() {
		return keyVarL;
	}
	
	public Value getRKeyVar() {
		return keyVarR;
	}
	
	public List<Value> getMapKeysL() {
		return mapKeysL;
	}
	
	public List<Value> getMapKeysR() {
		return mapKeysR;
	}
	
	public EvaluationOperator getEvalOp() {
		return evalOp;
	}
	
	public void setType(String t) {
		type = t;
	}
	
	public boolean beliefIsTrue(Basic b, Frame f){
		return ActiveFrame.isFrameActive(b, f);
	}
	public boolean factIsTrue(Basic b, Frame f){
		return ActiveFrame.isFrameActive(b, f);
	}
	
	protected boolean eval(Basic b, Frame f, Map<String, Value> varCtx) {
		//System.out.println(f.operatesOnFacts() + " second check");
		//System.out.println("variable Context:" + varCtx.toString());
		//System.out.println("exp:" + this.toString());
		this.f = f;
		this.b = b;

		if (!lhsExp.getAttributeString().equals(""))
			this.lAtt = lhsExp.getAttributeString();

		if (!rhsExp.getAttributeString().equals("")) {
			this.rAtt = rhsExp.getAttributeString();
		}
		List<Value> lhsValues = getValueOfExp(lhsExp, lAtt, mapIndexL, keyVarL, varCtx);
		List<Value> rhsValues = getValueOfExp(rhsExp, rAtt, mapIndexR, keyVarR, varCtx);
				
		//System.out.println("lhsValue :" + lhsValues.toString());
		//System.out.println("rhsValue :" + rhsValues.toString());

		boolean found = false;
		for (Value lhsVal : lhsValues) {
			for (Value rhsVal : rhsValues) {
				
				if (lhsVal.compareValues(rhsVal, this.getEvalOp()))
					found = true;
				
			}
		}
		return evalOutput(found);
	}
	
	private boolean evalOutput(boolean found) {
		if (type.equals("knownval"))
			return found;
		return !found;
	}
	
	public void setBasicAndFrame(Basic b, Frame f) {
		this.b = b;
		this.f = f;
	}

	public List<Value> getValueOfExp(Expression exp, String attributeName,
			MapKey mapIndex, Value keyVar, Map<String, Value> varCtx) {
		// if it is a primitive type return the value
		if (CommonExpUtils.isPrimitiveType(exp))
			return exp.getValue(b, f);

		// if it is a BinaryExpression recurse on left and then right
		if (exp instanceof BinaryExpression) {
			//System.out.println(exp.toString() + " binary");
			BinaryExpression binExp = (BinaryExpression) exp;
			Expression lhsExp = binExp.getLhs();
			String binLAtt = binExp.getLAtt();
			if (binLAtt == null || binLAtt.equals(""))
				binLAtt = lhsExp.getAttributeString();
			List<Value> lhsVals = getValueOfExp(lhsExp, binLAtt, mapIndex,
					keyVar, varCtx);
			Expression rhsExp = binExp.getRhs();
			String binRAtt = binExp.getRAtt();
			if (binRAtt == null || binRAtt.equals(""))
				binRAtt = rhsExp.getAttributeString();
			List<Value> rhsVals = getValueOfExp(rhsExp, binRAtt, mapIndex,
					keyVar, varCtx);

			return binExp.computeValues(lhsVals, rhsVals,
					binExp.getMathOperator());

		}

		// if you come here then it could be a complex object
		Basic concept = CommonExpUtils
				.getSingleBoundConcepts(b, f, exp, varCtx);

		// Check for the primitive variable bindings and get them for the 
		// the variable context bindings;
		if (concept == null) {
			if (exp.isVariableValue() && varCtx.size() > 0
					&& varCtx.containsKey(((VariableValue) exp).getVarName())) {
				List<Value> primitiveVals = new ArrayList<Value>();
				primitiveVals
						.add(varCtx.get(((VariableValue) exp).getVarName()));
				return primitiveVals;
			}
			return new ArrayList<Value>();
		}
		//System.out.println("attribute name: " + attributeName);
		if (attributeName == null || attributeName.equals("")) {
			List<Value> complexVals = new ArrayList<Value>();
			complexVals.add(new TplObjRef(concept.getName()));
			//System.out.println("concept: " + concept.getName());
			return complexVals;
		}
		String identifier = CommonExpUtils.getBeliefOrFactAttribute(f, b,
				concept, attributeName);

		//System.out.println("identifier :" + identifier);

		List<Knowledge> k = CommonExpUtils.getBeliefsOrFactsFor(f, b,
				identifier);
		// could not find binding
		if (k == null || k.size() == 0) {
			//System.out.println("could not find binding");
			return new ArrayList<Value>();
		}

		if (k.get(0).getBeliefOrFactExp() instanceof MapExpression) {
			// it is a map expression
			MapExpression mapExp = ((MapExpression) k.get(0)
					.getBeliefOrFactExp());
			if(mapIndex != null) {
				return mapExp.getValue(mapIndex, b, f);
			}
			else {
				return getMapValuesIndexbyKeyVar(f, b, identifier, keyVar, varCtx);
			}
		}
		//System.out.println(k.get(0).toString() + "before returning");
		return k.get(0).getBeliefOrFactExp().getValue(b, f);
	}
	
	private List<Value> getMapValuesIndexbyKeyVar(Frame f, Basic currBasic,
			String index, Value keyVar, Map<String, Value> varCtx) {
		//System.out.println("currBasic is :" + currBasic.getBeliefs().toString());
		List<Knowledge> mapBelifOrFact = CommonExpUtils.getBeliefsOrFactsFor(f,
				currBasic, index);
		List<Value> vals = new ArrayList<Value>();
		for (Knowledge knowElem : mapBelifOrFact) {
			Expression exp = knowElem.getBeliefOrFactExp();
			if (!(exp instanceof MapExpression))
				continue;
			MapExpression mapExp = (MapExpression) exp;
			Map<String, Value> mapVals = mapExp.getMap();
			//System.out.println(mapVals.toString());
			if(keyVar != null) {
			Value otherVal = varCtx.get(keyVar.toString());
			for (String mapIndex : mapVals.keySet()) {
				if (!mapIndex.equals(otherVal.toString()))
					continue;
				vals.add(mapVals.get(mapIndex));
			}
			} else {
				for (String mapIndex : mapVals.keySet()) {
					vals.add(mapVals.get(mapIndex));
				}
			}
		}
		return vals;
	}

	public Variable getNextFreeVariable(Frame f, Map<String, Value> varCtx) {

		Variable lhsExpVar = CommonExpUtils.getVariable(lhsExp, varCtx, f);
		if (lhsExpVar != null)
			return lhsExpVar;
		
		if(this.keyVarL != null) {
			Variable lhsKeyVar = CommonExpUtils.getVariable(this.keyVarL, varCtx, f);
			if(lhsKeyVar != null) return lhsKeyVar;
		}

		Variable rhsExpVar = CommonExpUtils.getVariable(rhsExp, varCtx, f);
		
		if(rhsExpVar != null)
			return rhsExpVar;
		
		if(this.keyVarR != null) 
			return CommonExpUtils.getVariable(this.keyVarR, varCtx, f);
		
		return null;
	}

	public List<Value> getVariableBindings(Basic b, Frame f, Variable var,
			Map<String, Value> varCtx) {
		//System.out.println("coming to get variable bindings : " + var.getVarName());
		this.b = b;
		this.f = f;
		if (CommonExpUtils.variableMatchesExpression(b, f, lhsExp, var, varCtx)) {
			List<Value> bindings = CommonExpUtils.getVariableBindings(b, f,
													lhsExp, var, varCtx);
			if (bindings == null || bindings.size() == 0)
				return getPrimitiveVariableBindings(b, f,
													var, lhsExp, varCtx);
			return bindings;
		} 
		
		if(CommonExpUtils.variableMatchesExpression(b, f, keyVarL, var, varCtx)) {
			return CommonExpUtils.getKeyVarValues(b, f, lhsExp, lAtt, varCtx);
		}  
		
		if (CommonExpUtils.variableMatchesExpression(b, f, 
													rhsExp, var, varCtx)) {
			List<Value> bindings = CommonExpUtils.getVariableBindings(b, f,
													rhsExp, var, varCtx);
			if (bindings == null || bindings.size() == 0)
				return getPrimitiveVariableBindings(b, f, var, rhsExp, varCtx);
			return bindings;
		} 
		if(CommonExpUtils.variableMatchesExpression(b, f, keyVarR, var, varCtx)) {
			return CommonExpUtils.getKeyVarValues(b, f, rhsExp, rAtt, varCtx);
		} 
		
		return null;
	}
	
	public List<Value> getPrimitiveVariableBindings(Basic b, Frame f, Variable var, 
			Expression exp, Map<String, Value> varCtx) {
		
		//System.out.println("get primitive variables bindings");
		Expression otherExp;
		String otherAtt; 
		MapKey otherMapIndex;
		Value otherKeyVar;
		if(lhsExp == exp)  {// 
			otherExp = rhsExp;
			otherAtt = rAtt;
			otherMapIndex = mapIndexR;
			otherKeyVar = keyVarR;
		}
		else {
			otherExp = lhsExp;
			otherAtt = lAtt;
			otherMapIndex = mapIndexL;
			otherKeyVar = keyVarL;
		}
		
		if(otherAtt == null || otherAtt.equals(""))
			otherAtt = otherExp.getAttributeString();
		
		return getValueOfExp(otherExp, otherAtt, otherMapIndex, otherKeyVar, varCtx);
		
	}
	
	@Override
	public List<Value> getValue(Basic b, Frame f) {
		throw new RuntimeException("trying to call getValue in EvalValCompExpOpExp");
	}

	@Override
	public List<Value> getFactValue(Basic b, Frame f) {
		throw new RuntimeException("trying to call getValue in EvalValCompExpOpExp");

	}
	
	@Override
	public Value getKeyVarL(){
		return keyVarL;
	}
	@Override
	public Value getKeyVarR(){
		return keyVarR;
	}
	
	public void setKeyVarL(Value L){
		keyVarL = L;
	}
	
	public void setKeyVarR(Value R){
		keyVarR = R;
	}

	public void setLhsExpression(Expression e){
		lhsExp  = e;
	}
	
	public void setRhsExpression(Expression e){
		rhsExp  = e;
	}
	public void setLAtt(String a){
		lAtt  = a;
	}
	
	public void setRAtt(String a){
		rAtt  = a;
	}
	public String getType(){
		return type;
	}
	
	
	@Override
	public Object clone(){
		EvalValCompExpOpExp evceoe = new EvalValCompExpOpExp(
				 lhsExp, 
				 lAtt, 
				 mapIndexL, 
				 keyVarL,
				 mapKeysL,
				 mapIndexR,
				 keyVarR,
				 mapKeysR,
				 evalOp,
				 rhsExp, 
				 rAtt, 
				 type,
				 b,
				 f);
		return evceoe;
		
	}
	
}	
