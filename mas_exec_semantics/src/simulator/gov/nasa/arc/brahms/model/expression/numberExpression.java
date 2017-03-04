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
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.activity.CompositeActivity;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.simulator.Utils;

public class numberExpression extends Expression {

	Expression lhs;
	MathOperator mathOp;
	Expression rhs;
	
	//if performing calculation on a variable
	String lAtt = "";
	String rAtt = "";

	public numberExpression(Expression new_lhs, MathOperator new_mathOp, Expression new_rhs,
			String new_lAtt, String new_rAtt){
		lhs = new_lhs;
		mathOp = new_mathOp;
		rhs = new_rhs;
		lAtt = new_lAtt;
		rAtt = new_rAtt;
	}
	
	@Override
	public Expression getLhs() {
		return lhs;
	}
	
	@Override
	public Expression getRhs() {
		return rhs;
	}
	
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		retVal.append(lhs.toString());
		retVal.append(mathOp.toString());
		retVal.append(rhs.toString());
		
		return retVal.toString();
	}
	
	public List<Value> getSideValues(Basic b, Frame f, Expression side, String leftRightAtt){
		List<Value> lhsVal = null;
		try{
			if (side instanceof VariableValue){
				if(leftRightAtt.equals("")){
					VariableValue var = (VariableValue) side;
					lhsVal = f.getVarInstanceVal(var.getVarName());
					if(lhsVal == null){
						lhsVal = f.getVarValues(var.getVarName());
					}
				}
				else{
					VariableValue var = (VariableValue) side;
					List <Value> tempVal = f.getVarInstanceVal(var.getVarName());
					List <Value> tempLhsVal = new ArrayList<Value>();
					for(int i = 0; i<tempVal.size() ;i++){
						Expression tempVarExpression = new Term(tempVal.get(i).toString(), leftRightAtt);
						tempLhsVal.addAll(tempVarExpression.getValue(b, f));
					}
					lhsVal = tempLhsVal;
				}
			}
			else if(side instanceof ParameterValue){
				//Find which composite activities the workframe belongs to
				List<CompositeActivity> compParents = Utils.findFramesCompositeParents2(b, f);
				//Loop through all CAs and find the corresponding parameter value and set it to the LhsVal 
				for(int i = 0; i < compParents.size(); i++){
					CompositeActivity ca = compParents.get(i);
					List<Parameter> params = ca.getParams();
					for(int j = 0; j < params.size(); j++){
						Parameter p = params.get(j);
						if(p.getName().equals(side.toString())){
							lhsVal = p.getVals();
						}
					}
				}
			}
			else{
				lhsVal = side.getValue(b, f);
			}
		} catch (Exception e) {
			VariableValue var = (VariableValue) side;
			try{
				lhsVal = f.getVarValues(var.getVarName());
			}
			catch(Exception ex){
				throw new IllegalArgumentException("Value can not be found for " + this.toString());
			}
		}
		
		
		return lhsVal;
	}
	
	
	@Override
	public List<Value> getValue(Basic b, Frame f) {
		List<Value> lhsVal, rhsVal;
		lhsVal = getSideValues(b, f, lhs, lAtt);
		rhsVal = getSideValues(b, f, rhs, rAtt);
		List<Value> result = new ArrayList<Value>();
		for (int i = 0; i < lhsVal.size(); i++) {
			for (int j = 0; j < rhsVal.size(); j++) {
				if(mathOp.equals(MathOperator.PLUS)) {
					result.add(lhsVal.get(i).addValues(rhsVal.get(j)));
				} else if (mathOp.equals(MathOperator.MINUS)) {
					result.add(lhsVal.get(i).subtractValues(rhsVal.get(j)));
				} else if (mathOp.equals(MathOperator.MULTIPLY)) {
					result.add(lhsVal.get(i).multiplyValues(rhsVal.get(j)));
				} else if (mathOp.equals(MathOperator.DIVIDE)) {
					result.add(lhsVal.get(i).divideValues(rhsVal.get(j)));
				} else {
					// add support for other operators
					throw new ArithmeticException("todo: add support for the other math " 
							+ "operators in DoubleExpression");
				}
			}
		}
		return result;
	}
	
	@Override
	public List<Value> getFactValue(Basic b, Frame f) {
		List<Value> lhsVal, rhsVal;
		try {
			lhsVal = lhs.getFactValue(b, f);
		} catch (Exception e) {
			VariableValue var = (VariableValue) lhs;
			lhsVal = f.getVarValues(var.getVarName());
		}
		try {
			rhsVal = rhs.getFactValue(b, f);

		} catch (Exception e2) {
			VariableValue var = (VariableValue) rhs;
			rhsVal = f.getVarValues(var.getVarName());
		}
		List<Value> result = new ArrayList<Value>();
		for (int i = 0; i < lhsVal.size(); i++) {
			for (int j = 0; j < rhsVal.size(); j++) {
				if(mathOp.equals(MathOperator.PLUS)) {
					result.add(lhsVal.get(i).addValues(rhsVal.get(j)));
				} else if (mathOp.equals(MathOperator.MINUS)) {
					result.add(lhsVal.get(i).subtractValues(rhsVal.get(j)));
				} else if (mathOp.equals(MathOperator.MULTIPLY)) {
					result.add(lhsVal.get(i).multiplyValues(rhsVal.get(j)));
				} else if (mathOp.equals(MathOperator.DIVIDE)) {
					result.add(lhsVal.get(i).divideValues(rhsVal.get(j)));
				} else
					// add support for other operators
					throw new ArithmeticException("todo: add support for the other math operators");
			}
		}
		return result;
	}
	
}
