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
import gov.nasa.arc.brahms.model.Frame;

public class StringValue extends Value {
	String val;
	
	public StringValue(String val) {
		this.val = val;
	}
	
	@Override
	public String getVal() {
		return val;
	}

	public String getStringValue() {
		return val;
	}
	
	public void setStringValue(String value) {
		this.val = value;
	}
	
	public String toString(){ 
		return "\"" + val + "\"";
	}
	
	public Object clone() {
		StringValue strValue = ((StringValue) super.clone());
		strValue.val = new String(val);
		return strValue;
	}

	@Override
	public List<Value> getValue(Basic b, Frame f) {
		List<Value> value = new ArrayList<Value>();
		value.add(this);
		return value;
	}
	
	@Override
	public List<Value> getFactValue(Basic b, Frame f) {
		List<Value> value = new ArrayList<Value>();
		value.add(this);
		return value;
	}
	
	@Override
	protected Value addValues(Value val) {
		throw new ArithmeticException("cannot add string values");
	}

	@Override
	protected Value subtractValues(Value val) {
		throw new ArithmeticException("cannot subtract string values");

	}
	
	@Override
	protected Value multiplyValues(Value val) {
		throw new ArithmeticException("cannot multiply string values");
	}

	@Override
	protected Value divideValues(Value val) {
		throw new ArithmeticException("cannot divide string values");
	}
	
	
	@Override
	public boolean compareValues(Value otherVal, EvaluationOperator eq) {
		if (otherVal instanceof SglObjRef) {
			SglObjRef sgl = (SglObjRef) otherVal;
			String secondVal = sgl.toString();
			if (sgl.getObjRefName().equals("unknown")) {
				if (eq == EvaluationOperator.EQ)
					return false;
				if (eq == EvaluationOperator.NEQ)
					return true;
				throw new ArithmeticException("error in specifying evaluation operator");
			}
			if(eq == EvaluationOperator.EQ) {
				return (val.equals(secondVal));
			} else if (eq == EvaluationOperator.NEQ) {
				return (!(val.equals(secondVal)));
			} else {
				throw new ArithmeticException("error in specifying evaluation operator");
			}
		}
		if (otherVal instanceof TplObjRef) {
			TplObjRef tpl = (TplObjRef) otherVal;
			String secondVal = tpl.toString();
			if (tpl.getObjRefName().equals("unknown")) {
				if (eq == EvaluationOperator.EQ)
					return false;
				if (eq == EvaluationOperator.NEQ)
					return true;
				throw new ArithmeticException("error in specifying evaluation operator");
			}
			if(eq == EvaluationOperator.EQ) {
				return (val.equals(secondVal));
			} else if (eq == EvaluationOperator.NEQ) {
				return (!(val.equals(secondVal)));
			} else {
				throw new ArithmeticException("error in specifying evaluation operator");
			}
		}
		if(!(otherVal instanceof StringValue))
			throw new IllegalArgumentException("trying to compare incompatible string values " + otherVal + " is instanceof " + otherVal.getClass());
		
		String secondVal = ((StringValue) otherVal).getStringValue();
		
		if(eq == EvaluationOperator.EQ) {
			return (val.equals(secondVal));
		} else if (eq == EvaluationOperator.NEQ) {
			return (!(val.equals(secondVal)));
		} else {
			throw new ArithmeticException("error in specifying evaluation operator");
		}
	}
	public String getTestVal() {
		return val;
	}
}
