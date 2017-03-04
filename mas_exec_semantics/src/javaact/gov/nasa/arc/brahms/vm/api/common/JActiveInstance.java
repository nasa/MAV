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

import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.exceptions.InvalidTypeException;
import gov.nasa.arc.brahms.vm.api.exceptions.UnknownValueException;

import java.util.List;
import java.util.Map;

public class JActiveInstance implements IActiveInstance{

	public boolean hasBeliefAttributeAnyValue(IConcept obj, String beliefName,
			boolean b, IContext ctx) throws ExternalException {
		// TODO Auto-generated method stub
		return false;
	}

	public double getBeliefAttributeDouble(IConcept obj, String beliefName,
			IContext ctx) throws UnknownValueException, InvalidTypeException,
			ExternalException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getBeliefAttributeInt(IConcept obj, String beliefName,
			IContext ctx) throws UnknownValueException, InvalidTypeException,
			ExternalException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getBeliefAttributeString(IConcept lhsConcept,
			String attributeName, IContext ctx) throws UnknownValueException,
			InvalidTypeException, ExternalException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setBeliefAttributeInt(IConcept obj, String beliefName,
			int setVal, IContext ctx) throws InvalidTypeException,
			ExternalException {
		// TODO Auto-generated method stub
		
	}

	public void setBeliefAttributeDouble(IConcept lhsConcept,
			String attributeName, double value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		// TODO Auto-generated method stub
		
	}

	public void setBeliefAttributeSymbol(IConcept lhsConcept,
			String attributeName, String value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		// TODO Auto-generated method stub
		
	}

	public void setBeliefAttributeString(IConcept lhsConcept,
			String attributeName, String value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		// TODO Auto-generated method stub
		
	}

	public IConcept getBeliefAttributeConcept(IConcept lhsConcept,
			String attributeName, IContext ctx) throws UnknownValueException,
			InvalidTypeException, ExternalException {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<Object, Object> getBeliefAttributeMap(IConcept lhsConcept,
			String attributeName, boolean includeUnknown, IContext ctx)
			throws InvalidTypeException, ExternalException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setBeliefAttributeJava(IConcept lhsConcept,
			String attributeName, int index, Object value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		// TODO Auto-generated method stub
		
	}

	public boolean hasBeliefRelationAnyValue(IConcept lhsConcept,
			String relationName, boolean includeUnknown, IContext ctx)
			throws ExternalException {
		// TODO Auto-generated method stub
		return false;
	}

	public List<IConcept> getBeliefRelationConcepts(IConcept lhsConcept,
			String relationName, IContext ctx) throws ExternalException {
		// TODO Auto-generated method stub
		return null;
	}

	public void addBeliefRelationConcept(IConcept lhsConcept,
			String relationName, IConcept rhsConcept, IContext ctx)
			throws ExternalException {
		// TODO Auto-generated method stub
		
	}

	public void removeBeliefRelationConcept(IConcept lhsConcept,
			String relationName, IConcept rhsConcept, IContext ctx)
			throws ExternalException {
		// TODO Auto-generated method stub
		
	}

	public void setBeliefAttributeConcept(IConcept lhsConcept, String attributeName, IConcept value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		throw new ExternalException("ERROR: implement setBeliefAttributeConcept");
	}

	public void setBeliefAttributeConcept(IConcept lhsConcept,
			String attributeName, int index, IConcept value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getBeliefAttributeBoolean(IConcept lhsConcept, String attributeName, IContext ctx)
			throws UnknownValueException, InvalidTypeException, ExternalException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBeliefAttributeBoolean(IConcept lhsConcept, String attributeName, boolean value, IContext ctx)
			throws InvalidTypeException, ExternalException {
		// TODO Auto-generated method stub
		
	}
	
}
