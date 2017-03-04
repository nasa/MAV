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

package gov.nasa.arc.brahms.vm.api.common;

import java.util.List;

import gov.nasa.arc.brahms.model.MultiAgentSystem;
import gov.nasa.arc.brahms.model.Parameter;
import gov.nasa.arc.brahms.model.activity.JavaActivity;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.expression.BooleanValue;
import gov.nasa.arc.brahms.model.expression.DoubleValue;
import gov.nasa.arc.brahms.model.expression.IntegerValue;
import gov.nasa.arc.brahms.model.expression.ParameterValue;
import gov.nasa.arc.brahms.model.expression.SglObjRef;
import gov.nasa.arc.brahms.model.expression.StringValue;
import gov.nasa.arc.brahms.model.expression.SymbolValue;
import gov.nasa.arc.brahms.model.expression.TplObjRef;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.exceptions.InvalidContentTypeException;

public class JParameter implements IParameter {
	
	public static MultiAgentSystem MAS;

	//brahms library interface to the parameter
	Parameter p;
	
	Basic b; 
	JavaActivity ja;
	
	public JParameter(Basic b, 
			JavaActivity ja, Parameter p, MultiAgentSystem MAS) {
		this.b = b;
		this.p = p;
		JParameter.MAS = MAS;
		this.ja = ja;
	}

	public Parameter getContainingParam() {
		return this.p;
	}
	
	public String getVariablePassedName() {
		return p.getVals().get(0).toString();
	}

	public int getContentType() throws ExternalException {
		List<Value> vals = p.getVals();
		if(vals.get(0) instanceof IntegerValue) {
			return INT;
		} else if (vals.get(0) instanceof DoubleValue) {
			return DOUBLE;
		} else if (vals.get(0) instanceof StringValue) {
			return STRING;
		} else if (vals.get(0) instanceof BooleanValue) {
			return BOOLEAN;
		} else if(vals.get(0) instanceof SglObjRef) {
			return CONCEPT;
		} else if(vals.get(0) instanceof TplObjRef) {
			return CONCEPT;
		} else if (vals.get(0) instanceof ParameterValue) {
			return CONCEPT;
		} else {
			throw new RuntimeException("printing the unhandled type in"
					+ "	 IParameter.getContentType() :" + p.getType().toString());
		}
	}


	public IConcept getConceptValue() throws InvalidContentTypeException,
			ExternalException {
		List<Value> vals = p.getVals();
		//System.out.println(vals.get(0).toString());
		if (vals.get(0) instanceof SglObjRef) {
			SglObjRef sglObjRef = (SglObjRef) vals.get(0);
			if(sglObjRef.getObjRefName().equals("current")) {
				return CommonUtils.findConcept(MAS, b.getName());
			}
			return CommonUtils.findConcept(MAS, sglObjRef.getObjRefName());
		} else if (vals.get(0) instanceof TplObjRef) {
			TplObjRef tplObjRef = (TplObjRef) vals.get(0);
			if(tplObjRef.getObjRefName().equals("current")) {
				return CommonUtils.findConcept(MAS, b.getName());
			}
			return CommonUtils.findConcept(MAS, tplObjRef.getObjRefName());
		} else if (vals.get(0) instanceof SymbolValue) {
			System.out.println("TODO: implement SymbolValue");
		} else if (vals.get(0) instanceof ParameterValue) {
			ParameterValue p = (ParameterValue) vals.get(0);
			if(p.getName().equals("current"))
				return CommonUtils.findConcept(MAS, b.getName());
			return CommonUtils.findConcept(MAS, p.getName());
		} else
			throw new RuntimeException("TODO://implement other "
					+ "					cases in getConceptValue");
		return null;
	}

	public IVariable getVariableValue() throws InvalidContentTypeException,
															ExternalException {
		throw new RuntimeException("TODO:// getVariableValue unimplemented");
		//return null;
	}
	


	public double getDoubleValue() throws InvalidContentTypeException,
			ExternalException {
		List<Value> vals = p.getVals();
		assert(vals.get(0) instanceof DoubleValue);
		return ((DoubleValue) vals.get(0)).getDblValue();
	}

	public int getIntValue() throws InvalidContentTypeException,
			ExternalException {
		List<Value> vals = p.getVals();
		assert(vals.get(0) instanceof IntegerValue);
		return ((IntegerValue) vals.get(0)).getIntValue();
	}

	public String getStringValue() throws InvalidContentTypeException,
			ExternalException {
		List<Value> vals = p.getVals();
		assert(vals.get(0) instanceof StringValue);
		return ((StringValue) vals.get(0)).getStringValue();
	}

	public String getSymbolValue() throws InvalidContentTypeException,
			ExternalException {
		List<Value> vals = p.getVals();
		assert(vals.get(0) instanceof SymbolValue);
		return ((SymbolValue) vals.get(0)).getVal();
	}

	public boolean getBooleanValue() throws InvalidContentTypeException,
			ExternalException {
		List<Value> vals = p.getVals();
		assert(vals.get(0) instanceof BooleanValue);
		return ((BooleanValue) vals.get(0)).getBooleanValue();
	}

	public Object getJavaObjectValue() throws InvalidContentTypeException,
			ExternalException {
		throw new RuntimeException("need to implement getJavaObjectValue()");
	}

	public long getLongValue() throws InvalidContentTypeException,
			ExternalException {
		throw new RuntimeException("need to implement getLongValue()");
	}

	public String getName() throws ExternalException {
		return p.getName();
	}
	
}
