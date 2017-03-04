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

import java.util.List;

import org.apache.log4j.Logger;

import gov.nasa.arc.brahms.vm.api.common.IAgent;
import gov.nasa.arc.brahms.vm.api.common.IConcept;
import gov.nasa.arc.brahms.vm.api.common.IContext;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import gov.nasa.arc.brahms.vm.api.jac.AbstractExternalActivity;

/**
 * 
 * @author ahamon
 *
 */
public class InitializeController extends AbstractExternalActivity {

	public static final String HAND_OFF_WAYPOINT = "handoffWaypoint";
	public static final String HAND_OFF_CONTROLLER = "handoffTo";

	public static final String NUMBER_WAYPOINTS = "nbWaypoints";
	public static final String HAS_WAYPOINT = "hasWaypoint";

	public static final String CONTROLLER = "controller";

	private static final Logger LOGGER = Logger.getLogger(InitializeController.class);

	/**
	 * Executes the java activity action.
	 *
	 * @exception ExternalException if an unrecoverable error occurs.
	 */
	public void doActivity() throws ExternalException {
		try {
			Utils instance = Utils.getInstance();
			synchronized (instance) {
				IAgent iController = (IAgent) this.getParameterConcept("iController");
				//
				IContext ctx = this.getContext();
				IAgent handOffController = (IAgent) iController.getBeliefAttributeConcept(iController, HAND_OFF_CONTROLLER, ctx);
				IConcept handOffWPT = (IConcept) iController.getBeliefAttributeConcept(iController, HAND_OFF_WAYPOINT, ctx);
				List<IConcept> waypoints = iController.getBeliefRelationConcepts(iController, HAS_WAYPOINT, ctx);

				// TODO use external class

				StringBuilder sb = new StringBuilder();
				sb.append("\n--- Controller init block ---");
				//
				sb.append("\n ").append(CONTROLLER).append("   ").append(iController.getName());
				//
				sb.append("\n ").append(HAND_OFF_CONTROLLER).append("   ").append(handOffController.getName());
				//
				sb.append("\n ").append(HAND_OFF_WAYPOINT).append("   ").append(handOffWPT.getName());
				//
				sb.append("\n ").append(NUMBER_WAYPOINTS).append("   ").append(waypoints.size());
				//
				waypoints.stream().forEach(waypoint -> sb.append("\n ").append(HAS_WAYPOINT).append("   ").append(waypoint.getName()));
				//
				sb.append("\n--- END init block ---");
				//
				LOGGER.info(sb.toString());

			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
