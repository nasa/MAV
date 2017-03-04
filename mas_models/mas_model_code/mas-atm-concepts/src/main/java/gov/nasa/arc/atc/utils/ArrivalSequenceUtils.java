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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gov.nasa.arc.atc.airborne.ArrivalSequence;
import gov.nasa.arc.atc.algos.dsas.SimpleArrivalGap;

public class ArrivalSequenceUtils {
	
	private ArrivalSequenceUtils(){
		// private utility constructor
	}
	
	public static List<SimpleArrivalGap> calculateGaps(ArrivalSequence sequence){
		List<SimpleArrivalGap> gaps = new ArrayList<>();
		for(int i =0; i<sequence.getSimulatedTrajectories().size()-1;i++){
			gaps.add(new SimpleArrivalGap(sequence.getAtIndex(i).getArrivalTime(), sequence.getAtIndex(i+1).getArrivalTime()));
		}
		return Collections.unmodifiableList(gaps);
	}

}
