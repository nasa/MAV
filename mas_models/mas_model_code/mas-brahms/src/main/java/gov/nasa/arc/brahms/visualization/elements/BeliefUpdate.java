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

package gov.nasa.arc.brahms.visualization.elements;

public class BeliefUpdate {

	private final int time;
	private final String reference;
	private final String attribute;
	private final String value;

	public BeliefUpdate(int time, String ref, String attr, String val) {
		this.time = time;
		this.reference = ref;
		this.attribute = attr;
		this.value = val;
	}

	public int getTime() {
		return time;
	}

	public String getReference() {
		return reference;
	}

	public String getAttribute() {
		return attribute;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(": time=");
		sb.append(time);
		sb.append(" reference=");
		sb.append(reference);
		sb.append(" attribute=");
		sb.append(attribute);
		sb.append(" value=");
		sb.append(value);
		
		return sb.toString();
	}
}
