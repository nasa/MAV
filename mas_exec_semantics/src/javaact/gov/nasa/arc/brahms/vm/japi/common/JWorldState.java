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

package gov.nasa.arc.brahms.vm.japi.common;

import gov.nasa.arc.brahms.model.expression.BooleanValue;
import gov.nasa.arc.brahms.model.expression.DoubleValue;
import gov.nasa.arc.brahms.model.expression.EvaluationOperator;
import gov.nasa.arc.brahms.model.expression.IntegerValue;
import gov.nasa.arc.brahms.model.expression.SglObjRef;
import gov.nasa.arc.brahms.model.expression.Term;
import gov.nasa.arc.brahms.model.expression.ValueExpression;
import gov.nasa.arc.brahms.simulator.world.FactSet;
import gov.nasa.arc.brahms.vm.api.common.IConcept;
import gov.nasa.arc.brahms.vm.api.common.IContext;
import gov.nasa.arc.brahms.vm.api.components.IWorldState;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.exceptions.InvalidTypeException;
import gov.nasa.arc.brahms.vm.api.exceptions.UnknownValueException;

public class JWorldState implements IWorldState {

	public JWorldState() {
		//default constructor
	}
	
	public void setFactAttributeDouble(IConcept lhsConcept,
			String attributeName, double value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		Term t = new Term(lhsConcept.getName(), attributeName); 
		ValueExpression valExp = new ValueExpression(t,
				EvaluationOperator.EQ, new DoubleValue(value));
		FactSet.updateFacts(t, valExp);
	}

	public void setFactAttributeInt(IConcept lhsConcept,
			String attributeName, int value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		Term t = new Term(lhsConcept.getName(), attributeName); 
		ValueExpression valExp = new ValueExpression(t,
				EvaluationOperator.EQ, new IntegerValue(value));
		FactSet.updateFacts(t, valExp);
	}

	public void setFactAttributeConcept(IConcept lhsConcept,
			String attributeName, IConcept value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		Term t = new Term(lhsConcept.getName(), attributeName); 
		ValueExpression valExp = new ValueExpression(t,
				EvaluationOperator.EQ, new SglObjRef(value.getName()));
		FactSet.updateFacts(t, valExp);
	}

	@Override
	public boolean getFactAttributeBoolean(IConcept lhsConcept, String attributeName)
			throws UnknownValueException, InvalidTypeException, ExternalException {
		throw new RuntimeException("ERROR: implement getFactAttributeBoolean");
	}

	@Override
	public void setFactAttributeBoolean(IConcept lhsConcept, String attributeName, boolean value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		Term t = new Term(lhsConcept.getName(), attributeName); 
		ValueExpression valExp = new ValueExpression(t,
				EvaluationOperator.EQ, new BooleanValue(value));
		FactSet.updateFacts(t, valExp);
		
	}

}
