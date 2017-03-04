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

import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.concept.Basic;

import java.util.ArrayList;
import java.util.List;


public class MapKey implements Cloneable{
	Object key;
	
	public MapKey(String key) {
		this.key = key;
	}
	
	public MapKey(int index) {
		this.key = index;
	}

	public Object getKey() {
		return key;
	}
	
	public List<Value> getValue(Basic b, Frame f) { 
		List<Value> vals = new ArrayList<Value>();
		if (key instanceof String)
			vals.add(new StringValue((String) key));
		else
			vals.add(new IntegerValue((Integer) key));
		return vals;
	}
	
	public List<Value> getFactValue(Basic b, Frame f) {
		return getValue(b, f);
	} 
	
	public String toString() {
		StringBuilder retVal = new StringBuilder();
		if (key instanceof String)
			retVal.append("\"");
		retVal.append(key.toString());
		if (key instanceof String)
			retVal.append("\"");
		return retVal.toString();
	}
	
	public Object clone() {
		MapKey copy = null;
		try {
			copy = (MapKey) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(this.key instanceof String) {
			copy.key = new String ((String) this.key);
		}
		else if (this.key instanceof Integer) {
			copy.key = new Integer((Integer) this.key);
		}
		return copy;

	}
	
}
