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

/********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.brahms.atmjava.activities;

import gov.nasa.arc.atc.tmpsimplification.IsDepartureDebug;
import org.apache.log4j.Logger;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.brahms.vm.api.common.IAgent;
import gov.nasa.arc.brahms.vm.api.common.IContext;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

public class UpdateAFOPosition extends AbstractExternalActivity {

	public final static Logger LOGGER = Logger.getLogger(UpdateAFOPosition.class);
	
	/**
	 * The java activity updates the position of the plane 
	 *
	 * @exception ExternalException
	 *                if an unrecoverable error occurs.
	 */
	public void doActivity() throws ExternalException {
		try {
			Utils instance = Utils.getInstance();
			synchronized(instance){
				IAgent i_AFO = (IAgent) this.getParameterConcept("i_AFO");
				//int TIME_INCREMENT = this.getParameterInt("i_TIME_INCREMENT");
				IContext ctx = this.getContext();
				String name = i_AFO.getName();
				AFO afoObject = instance.getAFO(name);
				// Get from Brahms: Airspeed, Controller, is_Metering
				Utils.synchronizeBrahmsControllerActions(i_AFO, ctx, afoObject);
				afoObject.updatePosition();
				//An arrival is has just landed
				//TODO: need to distinguish between slots and planes
				if(afoObject.getStatus() == Constants.FINISHED
										&& !IsDepartureDebug.isDeparture(afoObject)) {
					Utils.getInstance().setLastArrivalTime(afoObject.getSimulationTime());
					Utils.logPlaneLanded(LOGGER, afoObject, afoObject.getSimulationTime());
				}
				Utils.syncrhronizeAFOObject(afoObject, i_AFO, ctx);
				Utils.dumpAFO(LOGGER, afoObject, name);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} // end try
	}// doActivity

}// class UpdateAircraftPosition
