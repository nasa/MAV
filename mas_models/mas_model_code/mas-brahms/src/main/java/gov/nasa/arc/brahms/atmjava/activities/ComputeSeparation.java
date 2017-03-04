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

package gov.nasa.arc.brahms.atmjava.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import gov.nasa.arc.atc.tmpsimplification.IsDepartureDebug;
import gov.nasa.arc.atc.utils.AfoUtils;
import org.apache.log4j.Logger;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.airborne.TrafficType;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.utils.CalculationTools;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

public class ComputeSeparation extends AbstractExternalActivity {

	protected final static Logger LOGGER = Logger.getLogger(ComputeSeparation.class);

	protected List<AFO> arrivals;
	protected List<AFO> departures;
	protected List<AFO> transits;

	protected void sortPlanes() {
		Map<String, AFO> cache = Utils.getInstance().afoCache;
		arrivals = new ArrayList<AFO>();
		departures = new ArrayList<AFO>();
		transits = new ArrayList<AFO>();

		for (Map.Entry<String, AFO> i : cache.entrySet()) {
			if (isSlot(i.getKey())) {
				continue;
			}
			AFO plane = i.getValue();
			if (plane.getStatus() != Constants.IS_FLYING) {
				continue;
			}
//			TrafficType tt = plane.getTrafficType();
//			if (tt == TrafficType.ARRIVAL) {
//				arrivals.add(plane);
//			} else if (tt == TrafficType.DEPARTURE) {
//				departures.add(plane);
//			} else if (tt == TrafficType.TRANSIT) {
//				transits.add(plane);
//			} else {
//				assert (false);
//			}

			if(IsDepartureDebug.isDeparture(plane)){
                departures.add(plane);
            }else if(IsDepartureDebug.isDArrival(plane)){
                arrivals.add(plane);
            }else{
                transits.add(plane);
            }
		}

		arrivals.sort((AFO a, AFO b) -> {
			double diff = a.getETA() - b.getETA();
			if (diff < 0)
				return -1;
			if (diff == 0)
				return 0;
			return 1;
		});
	}

	protected boolean isSlot(String name) {
		if (name.contains("slot"))
			return true;
		return false;
	}

	@Override
	public void doActivity() throws ExternalException {
		Utils instance = Utils.getInstance();
		
		synchronized (instance) {
			sortPlanes();
			ListIterator<AFO> i = arrivals.listIterator();
			Utils.getInstance().topETA = Double.MAX_VALUE;
			while (i.hasNext()) {
				AFO lead = i.next();
				if(lead.getStatus() != Constants.IS_FLYING) continue;
				//TODO this is a hack!! fixme!
				if(Utils.getInstance().topETA > lead.getETA())
					Utils.getInstance().topETA = lead.getETA();
				ListIterator<AFO> j = arrivals.listIterator(i.nextIndex());
				j.forEachRemaining((AFO trail) -> checkInTrailSeparation(lead, trail));
			}
			checkSlotPlaneETAs();
		}
	}

	private void checkSlotPlaneETAs() {
		Object[] cache = Utils.getInstance().afoCache.values().toArray();
		for (int i = 0 ; i < cache.length ; ++i) {
			for (int j = i ; j < cache.length ; ++j) {
				AFO a = (AFO) cache[i];
				AFO b = (AFO) cache[j];
				if (!a.getName().equals(b.getName())) continue;
				double diff = Math.abs(a.getETA() - b.getETA());
				if (diff <= 10) continue;
				LOGGER.info("METERING: " + a.getName() + "(" + diff + ")");
			}
		}
	}

	protected void checkInTrailSeparation(AFO lead, AFO trail) {
		//LOGGER.info("checkInTrailSeparation(" + lead.getName() + ", " + trail.getName() + ")");
		if (trail.getStatus() != Constants.IS_FLYING) return;
		
		ATCNode toWptLead = lead.getFlightPlan().getCurrentSegment().getToWaypoint();
		ATCNode fromWptLead = lead.getFlightPlan().getCurrentSegment().getFromWaypoint();
		ATCNode toWptTrail = trail.getFlightPlan().getCurrentSegment().getToWaypoint();
		
		if (toWptLead.equals(toWptTrail)) {
			double separation = CalculationTools.minumumRequiredSeparation(AfoUtils.getDTA(trail));
			double milesInTrail = CalculationTools.distanceFromTo(trail.getLatitude(), trail.getLongitude(), 
					lead.getLatitude(), lead.getLongitude()); 
			//LOGGER.info("CHECK: "+ milesInTrail + " >= " + separation + " = " + (milesInTrail >= separation));
			//assert(milesInTrail >= separation);
			logSeparationViolation(lead, trail, milesInTrail, separation);
			return;
		}
		
		if (fromWptLead.equals(toWptTrail)) {
			double separation = CalculationTools.minumumRequiredSeparation(AfoUtils.getDTA(trail));
			double milesInTrail = CalculationTools.distanceFromTo(trail.getLatitude(), trail.getLongitude(), 
					toWptTrail.getLatitude(), toWptTrail.getLongitude()) +
					CalculationTools.distanceFromTo(toWptTrail.getLatitude(), toWptTrail.getLongitude(), 
							lead.getLatitude(), lead.getLongitude());
			//LOGGER.info("CHECK: " + milesInTrail + " >= " + separation + " = " + (milesInTrail >= separation));
			logSeparationViolation(lead, trail, milesInTrail, separation);
		}		
	}
	
	protected void logSeparationViolation(AFO lead, AFO trail, double milesInTrail, double separation) {
		if(milesInTrail < separation) {
			Utils.logSeparation(LOGGER, lead, trail, milesInTrail, separation);
		}
	}

}
