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


import java.util.Map;

import gov.nasa.arc.atc.tmpsimplification.IsDepartureDebug;
import org.apache.log4j.Logger;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.airborne.TrafficType;
import gov.nasa.arc.brahms.vm.api.common.IAgent;
import gov.nasa.arc.brahms.vm.api.common.IConcept;
import gov.nasa.arc.brahms.vm.api.common.IContext;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

public class CacheDepartureQueue extends AbstractExternalActivity {

	public final static Logger LOGGER = Logger.getLogger(CacheDepartureQueue.class);
	
	@Override
	public void doActivity() throws ExternalException {
		
		Utils instance = Utils.getInstance();
		synchronized (instance) {
			IAgent departureQueue = (IAgent) this.getParameterConcept("towerCtrl");
			System.out.println(departureQueue.getName() + " name of dep queueu");
			IContext ctx = this.getContext();
			final int time = departureQueue.getBeliefAttributeInt(departureQueue, "m_Time", ctx);
			populateDepartureCache(departureQueue, ctx,"departure_queue");
			Utils.logDepartureQueue(LOGGER,time);
		}
	}
	
	public static void populateDepartureCache(IConcept iMap,
			IContext ctx, String attributeName) {
	
		IAgent agt_map = (IAgent) iMap;

		try {
			assert (agt_map.hasBeliefAttributeAnyValue
                        (iMap, attributeName, true, ctx));
		} catch (ExternalException e) {
			e.printStackTrace();
		}

		Utils instance = Utils.getInstance();
		synchronized(instance) {
			Map<Object, Object> someTimeLine =
					null;
			try {
				someTimeLine = agt_map.getBeliefAttributeMap(iMap, attributeName, true, ctx);
			} catch (ExternalException e) {
				e.printStackTrace();
			}
			for (Object index : someTimeLine.keySet()) {
				assert (someTimeLine.get(index) instanceof IAgent);
				IAgent iDept = (IAgent) someTimeLine.get(index);
				
				AFO afoObject = Utils.initializeAFO(LOGGER, iDept, ctx, 1, Constants.ON_GROUND);	
				assert (afoObject.getFlightPlan().getCurrentSegmentIndex() >= 0);
				assert(IsDepartureDebug.isDeparture(afoObject));
				assert(afoObject.getStatus() == Constants.ON_GROUND);
//				afoObject.setTrafficType(TrafficType.DEPARTURE);
				Utils.getInstance().deptCache.add(afoObject);
				
			}
			
		}
	}

}

