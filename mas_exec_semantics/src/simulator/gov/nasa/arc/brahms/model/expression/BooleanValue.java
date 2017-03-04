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

import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.Frame;

public class BooleanValue extends Value {
	boolean val;
	
	public BooleanValue(boolean val) {
		this.val = val;
	}
	
	public List<Value> getValue(Basic b, Frame f) {
		List<Value> value = new ArrayList<Value>();
		value.add(this);
		return value;
	}
	
	public List<Value> getFactValue(Basic b, Frame f) {
		List<Value> value = new ArrayList<Value>();
		value.add(this);
		return value;
	}
	
	public Value getVarValue(Basic b, Frame f) {
		return this;
	}
	
	public boolean getBooleanValue() {
		return val;
	}
	
	public String toString() {
		return Boolean.toString(val);
	}
	
	public Object clone() {
		BooleanValue boolVal = ((BooleanValue) super.clone());
		return boolVal;
		
	}

	@Override
	protected Value addValues(Value val) {
		throw new ArithmeticException("cannot add boolean values");
	}

	@Override
	protected Value subtractValues(Value val) {
		throw new ArithmeticException("cannot subtract boolean values");
	}
	
	@Override
	protected Value multiplyValues(Value val) {
		throw new ArithmeticException("cannot multiply boolean values");
	}
	
	@Override
	protected Value divideValues(Value val) {
		throw new ArithmeticException("cannot divide boolean values");
	}
	

	@Override
	public boolean compareValues(Value otherVal, EvaluationOperator eq) {
		if(otherVal.toString().equals("?"))
			return false;
		if (otherVal instanceof SglObjRef) {
			SglObjRef sgl = (SglObjRef) otherVal;
			if (sgl.getObjRefName().equals("unknown")) {
				if (eq == EvaluationOperator.EQ)
					return false;
				if (eq == EvaluationOperator.NEQ)
					return true;
				throw new ArithmeticException("error in specifying evaluation operator");
			}
		}
		if (!(otherVal instanceof BooleanValue))
			throw new IllegalArgumentException("trying to compare compatible boolean values");
		
		boolean secondVal = ((BooleanValue) otherVal).getBooleanValue(); //cast it to boolean
		if (eq == EvaluationOperator.EQ){
			return (val == secondVal);
		} else if (eq == EvaluationOperator.NEQ) {
			return (val  != secondVal);
		} else {
			throw new ArithmeticException("error in specifying evaluation operator");
		}
	}
	
	public boolean getTestVal() {
		return val;
	}
	
	@Override
	public Expression getLhs(){
		return this;
	}
	@Override
	public Expression getRhs(){
		return this;
	}
}
