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

import java.util.List;

import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.expression.MapKey;

public class  MapKeyValPair extends Expression {
	private MapKey key;
	private Value value;
	
	public MapKeyValPair(MapKey key, Value val) {
		this.key = key;
		this.value = val;
	}
	
	public MapKey getKey() {
		return key;
	}
	
	public Value getValue() {
		return value;
	}
	
	public String toString() {
		String retVal = key.toString() + " = " + value.toString();
		return retVal;
	}

	@Override
	public List<Value> getValue(Basic b, Frame f) {
		throw new NullPointerException("MapKeyValPair doesn't have a value");
	}

	@Override
	public List<Value> getFactValue(Basic b, Frame f) {
		throw new NullPointerException("MapKeyValPair doesn't have a value");
	}
}
