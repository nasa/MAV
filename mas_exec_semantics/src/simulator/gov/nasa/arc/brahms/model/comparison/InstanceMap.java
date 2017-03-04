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

package gov.nasa.arc.brahms.model.comparison;

import gov.nasa.arc.brahms.model.expression.Value;

import java.util.ArrayList;
import java.util.List;

public class InstanceMap {
		String objectName; // this is the object
		String objectAttribute; // this is the attribute (object.attribute)
		List<Value> values;
		List<String> valNames;
	
		public InstanceMap(String tupleObjRef, String attrName,
									List<Value> values) {
			this.objectName = tupleObjRef;
			this.objectAttribute = attrName;
			this.values = values;
			valNames = new ArrayList<String>();
			for(Value val : values) {
				valNames.add(val.getLhs().toString());
			}
		}
		
		public String getName() {
			return objectName;
		}
		
		public List<Value> getVals() {
			return values;
		}
		
		public List<String> getBoundValueVarNames() {
			return valNames;
		}
		
		public String toString() {
			String retVal = "";
			retVal = "v(" + objectName + ") := {" + valNames.toString() + "}";
			return retVal;
		}
	}
