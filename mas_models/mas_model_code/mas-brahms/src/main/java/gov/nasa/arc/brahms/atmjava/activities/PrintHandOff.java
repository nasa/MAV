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
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

/**
 * 
 * @author ahamon
 *
 */
public class PrintHandOff extends AbstractExternalActivity {

	// TODO: use constants

	public static final String CONTROLLER = "controller";
	public static final String TIME = "time";
	public static final String AFO = "afoName";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";

	private static final Logger LOGGER = Logger.getLogger(PrintHandOff.class);

	@Override
	public void doActivity() {

		Utils instance = Utils.getInstance();
		synchronized (instance) {
			IAgent sController = (IAgent) getParameterConcept("i_Controller");
			IAgent aircraft = (IAgent) getParameterConcept("i_Airplane");
			String name = aircraft.getName();
			AFO aircraftObject = instance.getAFO(name);
			int time = getParameterInt("i_Time");

			// System.out.println("@TIME="+time+" controller="+sController.getName()+" aircraft="+aircraft.getName());

			StringBuilder sb = new StringBuilder();
			sb.append("\n--- AFO PrintHandOff ---");
			//
			sb.append("\n ").append(TIME).append("   ").append(time);
			//
			sb.append("\n ").append(CONTROLLER).append("   ").append(sController.getName());
			//
			sb.append("\n ").append(AFO).append("   ").append(name);
			//
			sb.append("\n ").append(LATITUDE).append("   ").append(aircraftObject.getLatitude());
			//
			sb.append("\n ").append(LONGITUDE).append("   ").append(aircraftObject.getLongitude());
			//
			sb.append("\n--- END PrintHandOff block ---");
			//
			LOGGER.info(sb.toString());
		}

	}
}
