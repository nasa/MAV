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
import gov.nasa.arc.brahms.model.Frame;

public class SglObjRef extends Value {
	
	private String objRefName;
	
	public SglObjRef(String objRefName) {
		this.objRefName = objRefName;
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

	@Override
	public List<Value> getValue(Basic obj, Frame f) {
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
		throw new ArithmeticException("***ERROR in SglObjRef: cannot add " +
				"objects");
	}

	@Override
	protected Value subtractValues(Value val) {
		throw new ArithmeticException("***ERROR in SglObjRef: cannot subtract " +
				"objects");
	}

	@Override
	protected Value multiplyValues(Value val) {
		throw new ArithmeticException("***ERROR in SglObjRef: cannot multiply " +
				"objects");
	}

	@Override
	protected Value divideValues(Value val) {
		throw new ArithmeticException("***ERROR in SglObjRef: cannot divide " +
				"objects");
	}

	@Override
	public boolean compareValues(Value val, EvaluationOperator eq) {
		if(val.equals("unknown") || objRefName.equals("unknown"))
			return false;
		if (val instanceof TplObjRef) {
			TplObjRef tpl = (TplObjRef) val;
			SglObjRef sgl = new SglObjRef(tpl.objRefName);
			return this.compareValues(sgl, eq);
		}
		String stringValue = "";
		if(val instanceof SglObjRef){
			SglObjRef secondVal = (SglObjRef) val;
			stringValue = secondVal.objRefName;
		}
		else if(val instanceof StringValue){
			stringValue = val.toString();
		}
		else{
			throw new IllegalArgumentException("trying to compare incompatible sglObjRef values");
		}
		if (eq == EvaluationOperator.EQ) {
			return (objRefName.equals(stringValue));
		} else if (eq == EvaluationOperator.GT) {
			throw new ArithmeticException("SglObjRef: cannot use GT with sglObjRefs");
		} else if (eq == EvaluationOperator.GTE) {
			throw new ArithmeticException("SglObjRef: cannot use GTE with sglObjRefs");
		} else if (eq == EvaluationOperator.LT) {
			throw new ArithmeticException("SglObjRef: cannot use LT with sglObjRefs");
		} else if (eq == EvaluationOperator.LTE) {
			throw new ArithmeticException("SglObjRef: cannot use LTE with sglObjRefs");
		} else if (eq == EvaluationOperator.NEQ) {
			return (!(objRefName.equals(stringValue)));
		} else {
			throw new ArithmeticException("error in specifying evaluation operator");
		}
	}
	@Override
	public Expression getLhs(){
		return this;
	}
	@Override
	public Expression getRhs(){
		return this;
	}
	public String getTestVal() {
		return objRefName;
	}
}
