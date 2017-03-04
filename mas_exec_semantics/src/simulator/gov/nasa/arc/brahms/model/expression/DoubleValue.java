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

public class DoubleValue extends Value {
	double val;
	
	public DoubleValue(double val) {
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
	
	public double getDblValue() {
		return val;
	}
	
	public void setDblValue(double value) {
		val = value;
	}
	
	public String toString() {
		return Double.toString(val);
	}
	
	public Object clone() {
		DoubleValue dblValue = ((DoubleValue) super.clone());
		return dblValue;
	}

	@Override
	protected Value addValues(Value val) {
		if(val instanceof IntegerValue){
			val = convertToDouble(val);
		}
		if(!(val instanceof DoubleValue))
			throw new IllegalArgumentException("trying to add incompatible double values");
		double dblValue = this.getDblValue() + ((DoubleValue) val).getDblValue();
		return new DoubleValue(dblValue);
	}

	@Override
	protected Value subtractValues(Value val) {
		if(val instanceof IntegerValue){
				val = convertToDouble(val);
		}
		if(!(val instanceof DoubleValue))
			throw new IllegalArgumentException("trying to add incompatible double values");
		double dblValue = this.getDblValue() - ((DoubleValue) val).getDblValue();
		return new DoubleValue(dblValue);
	}
	
	@Override
	protected Value multiplyValues(Value otherVal) {
		if(otherVal instanceof IntegerValue){
			otherVal = convertToDouble(otherVal);
		}
		if(!(otherVal instanceof DoubleValue))
			throw new IllegalArgumentException("trying to divide incompatible double values");
		double multVal = this.getDblValue() * ((DoubleValue) otherVal).getDblValue();
		return new DoubleValue(multVal);
	}
	
	@Override
	protected Value divideValues(Value otherVal) {
		if(otherVal instanceof IntegerValue){
			otherVal = convertToDouble(otherVal);
		}
		if(!(otherVal instanceof DoubleValue))
			throw new IllegalArgumentException("trying to divide incompatible double values");
		double divideVal = this.getDblValue() / ((DoubleValue) otherVal).getDblValue();
		return new DoubleValue(divideVal);
	}

	@Override
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
		if((!(otherVal instanceof DoubleValue)) && (!(otherVal instanceof VariableValue)))
			throw new IllegalArgumentException("trying to compare incompatible double " +
					"values" + this.toString() + ", " + otherVal.toString() + otherVal.getClass());
		
		double secondVal;

		if (otherVal instanceof VariableValue) {
			System.err.println("DoubleValue: trying to compare to variable: implement");
			System.exit(1);
		}
		secondVal = ((DoubleValue) otherVal).getDblValue();
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
	
	public double getTestVal() {
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
	public DoubleValue convertToDouble(Value val){
		DoubleValue newVal;
		try{
			Double dblVal = Double.parseDouble(val.toString());
			newVal = new DoubleValue(dblVal);
		}
		catch(Exception e){
			throw new IllegalArgumentException("Error converting to double when performing double*int");
		}
		return newVal;
	}
	
}
