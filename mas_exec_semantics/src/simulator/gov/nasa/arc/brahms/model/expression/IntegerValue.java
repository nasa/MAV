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

public class IntegerValue extends Value {
	int val;
	
	public IntegerValue(int val) {
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

	
	public int getIntValue() {
		return val;
	}
	
	public void setIntValue(int setVal) {
		this.val = setVal;
	}
	
	public String toString() {
		return Integer.toString(val);
	}
	
	public Object clone() {
		IntegerValue intValue = ((IntegerValue) super.clone());
		return intValue;
	}

	@Override
	protected Value addValues(Value otherVal) {
		if(!(otherVal instanceof IntegerValue))
			throw new IllegalArgumentException("trying to add incompatible int values");
		int addVal = this.getIntValue() + ((IntegerValue) otherVal).getIntValue();
		return new IntegerValue(addVal);
	}

	@Override
	protected Value subtractValues(Value otherVal) {
		if(!(otherVal instanceof IntegerValue))
			throw new IllegalArgumentException("trying to subtract incompatible int values");
		int subVal = this.getIntValue() - ((IntegerValue) otherVal).getIntValue();
		return new IntegerValue(subVal);
	}
	
	@Override
	protected Value multiplyValues(Value otherVal) {
		if(!(otherVal instanceof IntegerValue))
			throw new IllegalArgumentException("trying to multiply incompatible int values");
		int multVal = this.getIntValue() * ((IntegerValue) otherVal).getIntValue();
		return new IntegerValue(multVal);
	}
	
	@Override
	protected Value divideValues(Value otherVal) {
		if(!(otherVal instanceof IntegerValue))
			throw new IllegalArgumentException("trying to divide incompatible int values");
		int divideVal = this.getIntValue() / ((IntegerValue) otherVal).getIntValue();
		return new IntegerValue(divideVal);
	}
	
	
	public boolean compareValues(Value otherVal, EvaluationOperator eq) {
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
		if(!(otherVal instanceof IntegerValue))
			throw new IllegalArgumentException("trying to compare incompatible int values: " + this.toString() + " and " + otherVal );
		
		int secondVal = ((IntegerValue) otherVal).getIntValue();
		
		if(eq == EvaluationOperator.EQ) {
			return (val == secondVal);
		} else if (eq == EvaluationOperator.GT) {
			return (val > secondVal);
		} else if (eq == EvaluationOperator.GTE) {
			return (val >= secondVal);
		} else if (eq == EvaluationOperator.LT) {
			return (val < secondVal);
		} else if (eq == EvaluationOperator.LTE) {
			return (val <= secondVal);
		} else if (eq == EvaluationOperator.NEQ) {
			return (val  != secondVal);
		} else {
			throw new ArithmeticException("error in specifying evaluation operator");
		}
	}
	public int getTestVal() {
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
