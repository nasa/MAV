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

import org.apache.log4j.Logger;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.brahms.vm.api.common.IAgent;
import gov.nasa.arc.brahms.vm.api.common.IContext;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

public class SynchronizeSlotMarkerWithPlane extends AbstractExternalActivity {

	public final static Logger LOGGER = Logger.getLogger(SynchronizeSlotMarkerWithPlane.class);
	
	@Override
	public void doActivity() throws ExternalException {
		Utils instance = Utils.getInstance();
		synchronized(instance) {
			// Synchronize the slot marker with the plane
			IAgent i_AFO = (IAgent) this.getParameterConcept("i_AFO");
			IAgent i_slotAFO = (IAgent) this.getParameterConcept("i_slotAFO");

			LOGGER.info(
					"SynchronizeSlotMarkerWithPlane::doActivity(" + i_AFO.getName() + "," + i_slotAFO.getName() + ")");
			IContext ctx = this.getContext();

			// The '1' is not used since we are _not_ going to update
			// the position using this afoObject
			//
			// Not using the cache here because we need a new instance of the AFO.
			// TODO: create a copy constructor to do the same thing using the cached instance
			AFO afoObject = Utils.createAFOObject(i_AFO, ctx); 
			String name = i_slotAFO.getName();
			instance.addAFO(afoObject, name);		
			Utils.syncrhronizeAFOObject(afoObject, i_slotAFO, ctx);
			PersistentState.addInitialFlightParameters(afoObject.getName(), afoObject.getInitialParameters());
			System.out.println(PersistentState.getFlightParameters(afoObject.getName()) + " persistent state");
			Utils.dumpAFO(LOGGER, afoObject, name);
		}
		
	}
	
	
	
}
