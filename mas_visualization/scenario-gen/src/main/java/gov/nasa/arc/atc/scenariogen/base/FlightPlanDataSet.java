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

package gov.nasa.arc.atc.scenariogen.base;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.scenariogen.core.ArrivalControllerConf;
import gov.nasa.arc.atc.scenariogen.core.RichFlightPlan;

/**
 * 
 * @author ahamon
 *
 */
public class FlightPlanDataSet {

	private final List<ATCNode> nodes;
	private final List<RichFlightPlan> flightPlans;
	private final List<ArrivalControllerConf> controllers;

	public FlightPlanDataSet() {
		nodes = new LinkedList<>();
		flightPlans = new LinkedList<>();
		controllers = new LinkedList<>();
		// TEMP: TODO remove
		nodes.add(new ATCNode("LGA31",40.76889,-73.88389,0));
		nodes.add(new ATCNode("LGA22",40.76889,-73.88389,0));
	}

	public void addFlightPlan(RichFlightPlan flightPlan) {
		flightPlans.add(flightPlan);
		//
		for(FlightSegment segment : flightPlan.getSegments()){
			addNode(segment.getFromWaypoint());
			addNode(segment.getToWaypoint());
		}
	}

	public void addNode(ATCNode node) {
		if (getNode(node.getName())==null) {
			nodes.add(node);
		}
	}

	public void addController(ArrivalControllerConf controller) {
		controllers.add(controller);
		boolean containsHandOff = false;
		for(ATCNode node : nodes){
			if(node.getName().equals(controller.getHandOffWaypoint())){
				containsHandOff=true;
				break;
			}
		}
		assert containsHandOff;

		// check all controlled nodes exist
		for(String nodeName :controller.getNodes()){
			boolean containsNode = false;
			for(ATCNode node : nodes){
				if(node.getName().equals(nodeName)){
					containsNode=true;
					break;
				}
			}
			if(!containsNode){
				System.err.println("Failed finding node :: "+nodeName);
				throw new IllegalStateException("Cannot find node "+nodeName+" controlled by "+controller.getName());
			}
		}
	}

	public List<RichFlightPlan> getFlightPlans() {
		return Collections.unmodifiableList(flightPlans);
	}

	public List<ATCNode> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public List<ArrivalControllerConf> getControllers() {
		return Collections.unmodifiableList(controllers);
	}

	public ATCNode getNode(String nodeName) {
		// not optimized
		for (ATCNode node : nodes) {
			if (node.getName().equals(nodeName)) {
				return node;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + nodes.size() + " nodes - " + flightPlans.size() + " FPLs";
	}

}
