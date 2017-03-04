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

package gov.nasa.arc.atc.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.geography.ATCNode;

public class DepartureQueue {
	
	private final ATCNode departureNode;
	private final List<AFO> departures;
	
	public DepartureQueue(ATCNode node) {
		departureNode= node;
		departures=new ArrayList<>();
	}

	public ATCNode getDepartureNode() {
		return departureNode;
	}
	
	public void addDeparture(AFO departure){
		if(!departure.getFlightPlan().getFirstWaypoint().getName().equals(departureNode.getName())){
			//TODO: remove when MACS export is fixed
			//throw new IllegalArgumentException("AFO: "+departure.getName()+" does NOT depart from "+departureNode.getName());
		}
		departures.add(departure);
		
		//TODO: remove when brahms file are fixed
        if(departure.getStartTime()==0){
    		departure.setDepartureTime(DepartureDelayer.getDelayAtIndex(departures.indexOf(departure)));
        }
	}
	
	public List<AFO> getDepartures() {
		return Collections.unmodifiableList(departures);
	}
	
	
}
