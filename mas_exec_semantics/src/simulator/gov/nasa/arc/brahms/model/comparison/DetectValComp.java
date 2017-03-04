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

import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.Variable;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.EvaluationOperator;
import gov.nasa.arc.brahms.model.expression.SglObjRef;
import gov.nasa.arc.brahms.model.expression.StringValue;
import gov.nasa.arc.brahms.model.expression.SymbolValue;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.simulator.world.FactSet;

/**
 * 
 * @author jhunter
 * detect-val-comp 	::= obj-attr |
 * 						obj-attr BEL.evaluation-operator PRE.expression |
 * 						obj-attr BEL.evaluation-operator obj-attr |
 * 						obj-attr BEL.equality-operator ID.literal-symbol |
 * 						obj-attr BEL.equality-operator ID.literal-string |
 * 						obj-attr BEL.equality-operator sgl-object-ref
 *
 */
public class DetectValComp implements Comparison {
	Expression lhsExp;
	String lAtt = null;
	EvaluationOperator evalOp; //can be null
	Expression rhsExp; //term, string, symbol, bool, int, double, SglObjRef, 
					//variable, null
	
	/**
	 * Detect value for obj.att, match no matter what it is
	 * @param t
	 */
	public DetectValComp(Term t) {
		this.lhsExp = t;
		this.evalOp = null;
		this.rhsExp = null;
	}
	
	/**
	 * Detect value, match a specific value
	 * @param t
	 * @param evalOp
	 * @param exp
	 */
	public DetectValComp(Term t, EvaluationOperator evalOp, Expression exp) {
		this.lhsExp = t;
		this.evalOp = evalOp;
		this.rhsExp = exp;
		
		if (((rhsExp instanceof SglObjRef) || (rhsExp instanceof StringValue) || 
				(rhsExp instanceof SymbolValue)) && ((evalOp != EvaluationOperator.EQ)) 
				&& (evalOp != EvaluationOperator.NEQ))
			throw new RuntimeException("DetectValComp: SglObjRef/String/Symbol" +
					" must only be compared to = or !=");
	}
	
	public DetectValComp(Expression lexp, String att, EvaluationOperator 
			ev, Expression rexp) {
		this.lhsExp = lexp;
		this.lAtt = att;
		this.evalOp = ev;
		this.rhsExp = rexp;
	}
	
	public Expression getLhs() {
		return lhsExp;
	}
	
	public EvaluationOperator getEvalOp() {
		return evalOp;
	}
	
	public Expression getRhs() {
		return rhsExp;
	}
	
	public String getLAtt() {
		return lAtt;
	}
	
	public String toString() {
		return lhsExp.toString() + lAtt + " " + evalOp + " " + rhsExp;
	}
	
	public boolean compareEvaluatesTrue(Basic b, Frame f) {
		if (evalOp == null && rhsExp == null && lhsExp instanceof Term)
			return b.beliefExists(((Term) lhsExp).getObjRefName(), ((Term) lhsExp).getAttrName());
		EvalValCompExpOpExp newExp = new EvalValCompExpOpExp(lhsExp, lAtt, evalOp, 
				rhsExp);
		try {
			return newExp.beliefIsTrue(b, f);//, 0);
		} catch (NullPointerException npe) {
			return false;
		}
	}
	
	public boolean factEvaluatesTrue(Basic b, Frame f) {
		if (evalOp == null && rhsExp == null && rhsExp instanceof Term)
			return FactSet.factExists(((Term) lhsExp).toString());
		EvalValCompExpOpExp newExp = new EvalValCompExpOpExp(lhsExp, lAtt, evalOp, 
				rhsExp);
		try {
			return newExp.factIsTrue(b, f);//, 0);
		} catch (NullPointerException npe) {
			return false;
		}
	}
	
	@Override
	public Object clone(){
		DetectValComp dvc = new DetectValComp(lhsExp, lAtt, evalOp, rhsExp);

		return dvc;
		
	}

	public Expression getExp() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean eval(Basic b, Frame f, Map<String, Value> varCtx) {
		if(f.operatesOnFacts())
			return factEvaluatesTrue(b, f);
		return compareEvaluatesTrue(b, f);
	}

	public Variable getNextFreeVariable(Frame f, Map<String, Value> varCtx) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Value> getVariableBindings(Basic b, Frame f, Variable var,
			Map<String, Value> varCtx) {
		// TODO Auto-generated method stub
		return null;
	}
}
