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

import java.util.Enumeration;

import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.exceptions.InvalidTypeException;
import gov.nasa.arc.brahms.vm.api.exceptions.VariableBoundException;
import gov.nasa.arc.brahms.vm.api.exceptions.VariableNotBoundException;

public class JVariable implements IVariable {

	public IObject getConceptValue()  throws VariableNotBoundException,
											  InvalidTypeException,
											  ExternalException {
		throw new RuntimeException("need to implement");
	}

	public double getDoubleValue() throws VariableNotBoundException,
			InvalidTypeException, ExternalException {
		throw new RuntimeException("need to implement");
	}

	public int getIntValue() throws VariableNotBoundException,
			InvalidTypeException, ExternalException {
		throw new RuntimeException("need to implement");
	}

	public void addValue(int value) throws VariableBoundException,
			InvalidTypeException, ExternalException {
		throw new RuntimeException("need to implement");
		
	}

	public void addValue(double value) throws VariableBoundException,
			InvalidTypeException, ExternalException {
		throw new RuntimeException("need to implement");
		
	}

	public void addValue(boolean value) throws VariableBoundException,
			InvalidTypeException, ExternalException {
		throw new RuntimeException("need to implement");
		
	}

	public void addValue(String value) throws VariableBoundException,
			InvalidTypeException, ExternalException {
		throw new RuntimeException("need to implement");
		
	}

	public String getStringValue() throws VariableNotBoundException,
			InvalidTypeException, ExternalException {
		throw new RuntimeException("need to implement");
	}

	public boolean isBound() throws ExternalException {
		throw new RuntimeException("need to implement");
	}

	public Enumeration getValues() throws VariableNotBoundException,
			ExternalException {
		throw new RuntimeException("need to implement");
	}
	
}
