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

package gov.nasa.arc.atc.reports.tss;

import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.reports.TSSReporter;

/**
 * 
 * @author ahamon
 *
 */
public class SimpleInitializationConflictDetection implements InitializationConflictDetection {

	private final int simTime;
	private final ATCNode node;
	private final String lead;
	private final String trail;
	private final int initialDelay;
	private final int itemID;

	public SimpleInitializationConflictDetection(final int uniqueID, int simulationTime, String conflictNode, String leadSlot, String trailSlot, int delay) {
		itemID = uniqueID;
		simTime = simulationTime;
		node = new ATCNode(conflictNode, 0, 0, 0);
		lead = leadSlot;
		trail = trailSlot;
		initialDelay = delay;
	}

	@Override
	public int getID() {
		return itemID;
	}

	@Override
	public int getSimulationTime() {
		return simTime;
	}

	@Override
	public ATCNode getNode() {
		return node;
	}

	@Override
	public String getLead() {
		return lead;
	}

	@Override
	public String getTrail() {
		return trail;
	}

	@Override
	public int getDelay() {
		return initialDelay;
	}

	@Override
	public String getReason() {
		return SimpleInitializationConflictDetection.class.getSimpleName();
	}

	@Override
	public String getNodeName() {
		return node.getName();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(TSSReporter.TSS_REPORT_PREFIX);
		sb.append("t=");
		sb.append(simTime);
		sb.append(" INIT conflict detected at node ");
		sb.append(node.getName());
		sb.append(" between ");
		sb.append(lead);
		sb.append(" and ");
		sb.append(trail);
		sb.append(" trail ETA is  smaller than lead one, adding delay=");
		sb.append(initialDelay);
		return sb.toString();
	}

}
