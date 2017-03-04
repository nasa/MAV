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
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.simulator.Utils;
import gov.nasa.arc.brahms.model.Frame;

public class TplObjRef extends Value {
	
	String objRefName;
	
	public TplObjRef(String objRefName) {
		if (objRefName.equals("unknown"))
			throw new IllegalArgumentException("TplObjRef: cannot be unknown");
		this.objRefName = objRefName;
	}
	
	public List<Basic> getPossibleBindings(Basic b, Frame f) {
		List<Basic> bindings = new ArrayList<Basic>();
		Basic objRef;
		if (objRefName.equals("current"))
			objRef = b;
		else
			objRef = CommonExpUtils.findConcept(objRefName);
		bindings.add(objRef);
		return bindings;
	}
	
	public String getObjRefName() {
		return objRefName;
	}
	
	public void setObjRefName(String objRef) {
		this.objRefName = objRef;
	}
	
	public String toString() {
		return objRefName;
	}

	@Override
	public List<Value >getValue(Basic obj, Frame f) {
		List<Value> value = new ArrayList<Value>();
		value.add(this);
		return value;
	}
	
	@Override
	public List<Value> getFactValue(Basic obj, Frame f) {
		List<Value> value = new ArrayList<Value>();
		value.add(this);
		return value;
	}
	
	@Override
	protected Value addValues(Value val) {
		throw new ArithmeticException("can't add TplObjRef");
	}

	@Override
	protected Value subtractValues(Value val) {
		throw new ArithmeticException("can't subtract TplObjRef");
	}

	@Override
	protected Value multiplyValues(Value val) {
		throw new ArithmeticException("can't multiply TplObjRef");
	}

	@Override
	protected Value divideValues(Value val) {
		throw new ArithmeticException("can't divide TplObjRef");
	}

	@Override
	public boolean compareValues(Value val, EvaluationOperator eq) {
		if (val instanceof SglObjRef) {
			SglObjRef sgl = (SglObjRef) val;
			if (sgl.getObjRefName().equals("unknown")) {
				if (eq == EvaluationOperator.EQ)
					return false;
				if (eq == EvaluationOperator.NEQ)
					return true;
				throw new ArithmeticException("error in specifying evaluation operator");
			} else {
				TplObjRef tpl = new TplObjRef(sgl.getObjRefName());
				return this.compareValues(tpl, eq);
			}
		}
		if(!(val instanceof TplObjRef))
			return Utils.verifyClassGroupSameType(val, this, "");
		
		TplObjRef secondVal = (TplObjRef) val;
		
		if (eq == EvaluationOperator.EQ) {
			return (objRefName.equals(secondVal.getObjRefName()));
		} else if (eq == EvaluationOperator.GT) {
			throw new ArithmeticException("TplObjRef: cannot use GT with tplObjRefs");
		} else if (eq == EvaluationOperator.GTE) {
			throw new ArithmeticException("TplObjRef: cannot use GTE with tplObjRefs");
		} else if (eq == EvaluationOperator.LT) {
			throw new ArithmeticException("TplObjRef: cannot use LT with tplObjRefs");
		} else if (eq == EvaluationOperator.LTE) {
			throw new ArithmeticException("TplObjRef: cannot use LTE with tplObjRefs");
		} else if (eq == EvaluationOperator.NEQ) {
			return (!(objRefName.equals(secondVal.getObjRefName())));
		} else {
			throw new ArithmeticException("error in specifying evaluation operator");
		}
	}
	public String getTestVal() {
		return objRefName;
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
