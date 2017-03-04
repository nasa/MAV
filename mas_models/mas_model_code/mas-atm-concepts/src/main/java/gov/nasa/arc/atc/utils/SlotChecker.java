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

package gov.nasa.arc.atc.utils;

import gov.nasa.arc.atc.airborne.AFO;

public class SlotChecker {
	
	private SlotChecker(){
		// private utility constructor
	}
	
	public static void checkSlotValidity(AFO slot){
		// check name
		if(slot.getName()==null|| "".equals(slot.getName())){
			throw new IllegalArgumentException("found invalid slot name ::"+slot);
		}
		// check startTime
		if(slot.getStartTime()<0){
			throw new IllegalArgumentException("negative start time ::"+slot);
		}
		// check flight plans
		//// exists
		if(slot.getFlightPlan()==null){
			throw new IllegalArgumentException("flight plan is null for slot "+slot);
		}
		//// valid flight plan
		if(!slot.getFlightPlan().isValid()){
			throw new IllegalArgumentException("Flight plan for slot:"+slot.getName()+" is not valid ::"+slot.getFlightPlan());
		}
		//// valid traffic type
//		switch (slot.getTrafficType()) {
//		case ARRIVAL:
//			// OK
//			break;
//		case  DEPARTURE :
//			// OK
//			break;
//		case TRANSIT :
//			// OK
//			break;
//		default:
//			throw new IllegalArgumentException("Unsupported traffic type ::"+slot);
//		}
		// initial parameter
	}

}
