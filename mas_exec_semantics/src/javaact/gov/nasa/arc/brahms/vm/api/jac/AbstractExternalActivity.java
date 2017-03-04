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

package gov.nasa.arc.brahms.vm.api.jac;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import gov.nasa.arc.brahms.model.expression.TplObjRef;
import gov.nasa.arc.brahms.model.expression.Value;
import gov.nasa.arc.brahms.vm.api.common.CommonUtils;
import gov.nasa.arc.brahms.vm.api.common.IConcept;
import gov.nasa.arc.brahms.vm.api.common.IContext;
import gov.nasa.arc.brahms.vm.api.common.IParameter;
import gov.nasa.arc.brahms.vm.api.common.JParameter;
import gov.nasa.arc.brahms.vm.api.components.IVMController;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.exceptions.InvalidTypeException;

public abstract class AbstractExternalActivity {
	
	static public MultiAgentSystem MAS;
	static public Map<String, IConcept> allConcepts = new 
							HashMap<String, IConcept>();
	static Basic b;
	static Map<String, JParameter> params = new 
						HashMap<String, JParameter>();
	static int firstElem = 0;
	static JavaActivity ja;
	
	IContext context;//this is a dummy variable
	
	
	public abstract void doActivity() throws ExternalException;
	
	public static void initialize(MultiAgentSystem MAS, JavaActivity ja, Basic b) {
		AbstractExternalActivity.MAS = MAS;
		AbstractExternalActivity.b = b;
		AbstractExternalActivity.ja = ja;
		params.clear();
		allConcepts.clear();
	}
	
	public static void addParameter(Parameter p) {
		JParameter iParam = new JParameter(b, ja, p, MAS);
		params.put(p.getName(), iParam);
	}
	
	public String getParameterString(String name) {
		List<Value> vals = getParamVals(name);
		assert(vals.get(0) instanceof StringValue);
		StringValue val = (StringValue) vals.get(firstElem);
		return val.getStringValue();
	}
	
	

	public int getParameterInt(String name) {
		List<Value> vals = getParamVals(name);
		assert(vals.get(0) instanceof IntegerValue);
		IntegerValue val = (IntegerValue) vals.get(firstElem);
		return val.getIntValue();
	}
	
	public double getParameterDouble(String name) {
		List<Value> vals = getParamVals(name);
		assert(vals.get(0) instanceof DoubleValue);
		DoubleValue val = (DoubleValue) vals.get(0);
		return val.getDblValue();		
	}
	
	private List<Value> getParamVals(String name){
		AbstractExternalActivity.paramCheck(name);
		Parameter p = params.get(name).getContainingParam();
		return p.getVals();
	}
	

	public void setParameterInt(String name, int val) {
		List<Value> vals = getParamVals(name);
		vals.clear();
		vals.add(new IntegerValue(val));
	}
	
	public void setParameterDouble(String name, double dblVal) {
		List<Value> vals = getParamVals(name);
		vals.clear();
		vals.add(new DoubleValue(dblVal));
	}
	
	public void setParameterBoolean(String name, boolean boolVal) {
		List<Value> vals = getParamVals(name);
		vals.clear();
		vals.add(new BooleanValue(boolVal));
	}
	
	// TODO
	public void setParameterUnknown(java.lang.String name) {
	}
 
	// TOOD
	public void setParameterConcept(java.lang.String name, IConcept value) {
		
	}
	 
	public static void paramCheck(String name) {
		if(!params.containsKey(name)) 
			throw new RuntimeException("could not find parameter :" + name);
	}
	
	public IParameter getParameter(String name) {
		return params.get(name);
	}
	
	public IContext getContext() {
		if(context == null) 
			return new Context();
		else 
			return context;
	}
	
	public final String getName() throws ExternalException {
		 return ja.getName();
	}
	
	protected final void setBeliefAttributeDouble(IConcept lhsConcept, String attributeName, double value) 
			throws InvalidTypeException, ExternalException {
		throw new RuntimeException("need to implement setBeliefAttributeDouble()");
	}
	
	protected final IConcept getPerformedBy()
            throws ExternalException {
		return CommonUtils.findConcept(MAS, b.getName());
	}
	
	protected final IVMController getVMController() throws ExternalException {
		throw new RuntimeException("need to implement IVMController");
	}

	protected final IConcept getParameterConcept(String name)  {
		AbstractExternalActivity.paramCheck(name);
		Parameter p = params.get(name).getContainingParam();
		List<Value> vals = p.getVals();
		if (vals.get(0) instanceof TplObjRef) {
			TplObjRef tplObjRef = (TplObjRef) vals.get(0);
			if(tplObjRef.getObjRefName().equals("current")) 
				return CommonUtils.findConcept(MAS, b.getName());
			else 
				return CommonUtils.findConcept(MAS, tplObjRef.getObjRefName());
		} else if(vals.get(0) instanceof SglObjRef) {
			SglObjRef sglObjRef = (SglObjRef) vals.get(0);
			if(sglObjRef.getObjRefName().equals("current")) 
				return CommonUtils.findConcept(MAS, b.getName());
			else
				return CommonUtils.findConcept(MAS, sglObjRef.getObjRefName());
		} else if (vals.get(0) instanceof ParameterValue) {
			ParameterValue parVal = (ParameterValue) vals.get(0);
			if(parVal.getName().equals("current")) 
				return CommonUtils.findConcept(MAS, b.getName());
			else
				return CommonUtils.findConcept(MAS, parVal.getName());
		}
		throw new RuntimeException("need to implement JavaExternalActivity::"
				+ "getParameterConcept(String name)" + vals.get(0).getClass().getName());
	}
	
	private class Context implements IContext {
		
	}
}
