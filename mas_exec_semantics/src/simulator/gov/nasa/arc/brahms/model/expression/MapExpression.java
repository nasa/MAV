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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.brahms.model.Expression;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.Frame;
import gov.nasa.arc.brahms.model.expression.MapKey;

public class  MapExpression extends Expression {
	String mapName;
	Map<String,Value> map;
	
	public MapExpression(String mapNm) {
		this.mapName = mapNm;
		map = new HashMap<String, Value>();
	}
	
	public String toString() {		
		StringBuilder retVal = new StringBuilder();
		 for (Map.Entry<String, Value> entry : map.entrySet())
		 {
			 retVal.append("key: " + entry.getKey() + "; value: " + entry.getValue() + "\n");
		 }
		 retVal.append("\n");
		return retVal.toString();
	}
	
	public String getMapName() {
		return mapName;
	}
	
	public Map<String, Value> getMap() {
		return map;
	}
	
	public List<Value> getValue(MapKey key, Basic b, Frame f) { 
		if(map.containsKey(key.toString())) {
			return map.get(key.toString()).getValue(b, f);
		} else {
			return new ArrayList<Value>();
		}
	}
	
	public List<Value> getFactValue(MapKey key, Basic b, Frame f) {
		return map.get(key.toString()).getFactValue(b, f);
	}
	
	public void add(MapKeyValPair keyval) {
		map.put(keyval.getKey().toString(), keyval.getValue());
	}
	
	public void clearMap() {
		map.clear();
	}
	
	public boolean keyExists(MapKey key) {
		return map.containsKey(key.toString());
	}
	
	public void removeKey(MapKey key) {
		map.remove(key.toString());
	}

	@Override
	public List<Value> getValue(Basic b, Frame f) {
		throw new NullPointerException("MapExpression doesn't have a belief value: RE " + b.getName() + " and frame " + f.getName());
	}

	@Override
	public List<Value> getFactValue(Basic b, Frame f) {
		throw new NullPointerException("MapExpression doesn't have a fact value: RE " + b.getName() + " and frame " + f.getName());
	}
}
