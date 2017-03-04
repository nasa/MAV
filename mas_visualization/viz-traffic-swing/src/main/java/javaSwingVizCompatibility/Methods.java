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

package javaSwingVizCompatibility;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nasa.arc.atc.utils.SimulationProperties;
import gov.nasa.arc.brahms.visualization.elements.Agent;
import gov.nasa.arc.brahms.visualization.elements.BeliefUpdate;
import gov.nasa.arc.brahms.visualization.elements.Plane;
import gov.nasa.arc.brahms.visualization.elements.Slot;

/**
 * 
 * @author ahamon
 *
 */
public class Methods {

	public static final String AGENT_PLANE_PREFIX = "plane_";

	private Methods() {
		//
	}

	public static Plane createPlane(Agent agent) {
		if (!agent.getName().startsWith(AGENT_PLANE_PREFIX)) {
			throw new IllegalStateException("This agent is not an aircraft: " + agent);
		}
		String alt = "";
		boolean depart = false;
		String speed = "";
		String latitude = "";
		String longitude = "";
		String flying = "";
		String controller = "";
		String startTime = "";
		String metering = "";
		Map<Integer, List<BeliefUpdate>> beUp = agent.getBeliefs();
		// for loop retrieves all beliefUpdates from time 0

		// beUp.get(0).forEach(beliefUpdate -> {
		// switch (beliefUpdate.getAttribute()) {
		// //TODO: implement switch case, when using ppty map
		// }
		// });

		// System.err.println("nextUpdateList =" + nextUpdateList);
		// System.err.println(" :: " + SimulationProperties.getProperty(SimulationProperties.LATITUDE_PPTY));
		// System.err.println(" :: " + SimulationProperties.getProperty(SimulationProperties.LONGITUDE_PPTY));
		// System.err.println(" :: " + SimulationProperties.getProperty(SimulationProperties.ALTITUDE_PPTY));
		// System.err.println(" :: " + SimulationProperties.getProperty(SimulationProperties.SPEED_PPTY));

		boolean isInit = false;
		Set<Integer> keys = beUp.keySet();
		Iterator<Integer> iter = keys.iterator();
		int nextUpdateList;
		while (!isInit && iter.hasNext()) {
			nextUpdateList = iter.next();
			for (BeliefUpdate bu : beUp.get(nextUpdateList)) {
				// if(bu.getAttribute().contains("ltitude")){
				// System.err.println("#" + agent.getName() + " " + bu.getAttribute() + " => " + bu.getValue() + "_");
				// }
				// System.err.println("#" + agent.getName() + " " + bu.getAttribute() + " => " + bu.getValue() + "_");
				if ("".equals(latitude) && bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.LATITUDE_PPTY))) {
					latitude = bu.getValue();
				}
				if ("".equals(longitude) && bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.LONGITUDE_PPTY))) {
					longitude = bu.getValue();
				}
				if ("".equals(alt) && bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.ALTITUDE_PPTY))) {
					alt = bu.getValue();
				}
				if ("".equals(speed) && bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.SPEED_PPTY))) {
					speed = bu.getValue();
				}
				if ("".equals(controller) && bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.CONTROLLER_PPTY))) {
					controller = bu.getValue();
				}
				if ("".equals(flying) && bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.FLYING_PPTY))) {
					flying = bu.getValue();
				}
				if ("".equals(depart) && bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.DEPART_PPTY))) {
					depart = Boolean.valueOf(bu.getValue());
				}
				if ("".equals(startTime) && bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.STARTED_TIME_PPTY))) {
					startTime = bu.getValue();
				}
				if ("".equals(metering) && bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.METERING_PPTY))) {
					metering = bu.getValue();
				}
				// if (!alt.equals("") && !speed.equals("") && !latitude.equals("") && !longitude.equals("") && !flying.equals("") && !controller.equals("") && !startTime.equals("") && !metering.equals("")) {
				// break;
				// }
				isInit = !alt.equals("") && !speed.equals("") && !latitude.equals("") && !longitude.equals("") && !flying.equals("") && !controller.equals("") && !startTime.equals("") && !metering.equals("");
			}

		}

		// todo: check integrity

		// aircraft creation
		return new Plane(agent, alt, depart, speed, controller, latitude, longitude, flying, startTime, metering);
	}

	public static Slot createSlot(Agent agent) {
		// TODO: ensure
		String latitude = "";
		String longitude = "";
		String speed = "";
		String flying = "";
		String metering = "";
		String altitude = "";
		Map<Integer, List<BeliefUpdate>> beUp = agent.getBeliefs();
		for (BeliefUpdate bu : beUp.get(0)) {
			// System.err.println("#"+agent.getName()+ " "+bu);
			if (bu.getAttribute().contains("ltitude")) {
				System.err.println("#" + agent.getName() + " " + bu.getAttribute() + " => " + bu.getValue() + "_");
			}
			if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.LATITUDE_PPTY))) {
				latitude = bu.getValue();
			} else if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.LONGITUDE_PPTY))) {
				longitude = bu.getValue();
			} else if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.SPEED_PPTY))) {
				speed = bu.getValue();
			} else if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.FLYING_PPTY))) {
				flying = bu.getValue();
			} else if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.METERING_PPTY))) {
				metering = bu.getValue();
			} else if (bu.getAttribute().equals(SimulationProperties.getProperty(SimulationProperties.ALTITUDE_PPTY))) {
				altitude = bu.getValue();
			}
		}

		return new Slot(agent, latitude, longitude, flying, speed, metering, altitude);
	}

}
