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

package gov.nasa.jpf.mas.extract;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.VM;

public class ExtractAgent {
	
	public static String getAgentId (VM vm, StackFrame sf, int id) {
		  int arg0Ref = sf.peek(id);
		  ElementInfo ei0 = vm.getModifiableElementInfo(arg0Ref);
		  System.out.println(ei0.getClassInfo().getType() + " class info type");
		  System.out.println(ei0.getType().toString() +
				  		" this is what calls it the updateBelief");
		  String agentId = ei0.getType().toString();
		  return agentId;
	}
	
}
