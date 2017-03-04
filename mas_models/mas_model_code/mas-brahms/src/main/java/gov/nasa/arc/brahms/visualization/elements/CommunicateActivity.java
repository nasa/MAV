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

public class CommunicateActivity {

	private String commType;	// either send or receive
	private String whoWith;		// who the comm is with
	private String attribute;	// what was communicated / got updated?
	private String value;		// value of comm object
	
	public CommunicateActivity(String comm, String who, String attr, String val) {
		this.commType = comm;
		this.whoWith = who;
		this.attribute = attr;
		this.value = val;
	}

	public String getCommType() {
		return commType;
	}

	public String getWhoWith() {
		return whoWith;
	}

	public String getAttribute() {
		return attribute;
	}

	public String getValue() {
		return value;
	}
}
