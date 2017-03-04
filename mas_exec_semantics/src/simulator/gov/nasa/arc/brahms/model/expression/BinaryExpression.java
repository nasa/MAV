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

package gov.nasa.arc.brahms.model.expression;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.brahms.model.Expression;

public class BinaryExpression extends numberExpression {
	
	public BinaryExpression(Expression lhs, MathOperator mathOp,
			Expression rhs){
		super(lhs, mathOp, rhs, "", "");
	}
	// LHS is var.att and RHS is simple
	public BinaryExpression(Expression lhs, String new_lAtt , MathOperator mathOp,
			Expression rhs){
		super(lhs, mathOp, rhs, new_lAtt, "");
	}
	
	//LHS is simple and RHS is var.att
	public BinaryExpression(Expression lhs, MathOperator mathOp,
			Expression rhs, String new_rAtt){
		super(lhs, mathOp, rhs, "", new_rAtt);
	}
	//Both are var.att
	public BinaryExpression(Expression lhs, String new_lAtt, MathOperator mathOp,
			Expression rhs, String new_rAtt){
		super(lhs, mathOp, rhs, new_lAtt, new_rAtt);
	}
	
	public Object clone() {
		BinaryExpression exp = ((BinaryExpression) super.clone());
		return exp;
	}
	
	public MathOperator getMathOperator() {
		return this.mathOp;
	}
	
	@Override
	public String getRAtt() {
		return this.rAtt;
	}
	
	@Override
	public String getLAtt() { 
		return this.lAtt;
	}
	
	public List<Value> computeValues(List<Value> lhsVals, List<Value> rhsVals,
			MathOperator op) {
		List<Value> results = new ArrayList<Value>();
		if (op == MathOperator.PLUS) {
			results.add(lhsVals.get(0).addValues(rhsVals.get(0)));
		} else if(op == MathOperator.MINUS) {
			results.add(lhsVals.get(0).subtractValues(rhsVals.get(0)));
		} else if(op == MathOperator.MULTIPLY) {
			results.add(lhsVals.get(0).multiplyValues(rhsVals.get(0)));
		} else if(op == MathOperator.DIVIDE) {
			results.add(lhsVals.get(0).divideValues(rhsVals.get(0)));
		}
		return results;
	}
	
}
