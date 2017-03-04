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

import java.util.Collections;
import java.util.Map;

import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.reports.ReportItemProperties;
import gov.nasa.arc.atc.reports.TSSReporter;
import gov.nasa.arc.atc.simulation.SlotTrajectory;

/**
 * 
 * @author ahamon
 *
 */
public class FullScheduleConflictResolution implements ScheduleConflictResolution {

	private final Map<String, Object> values;
	private final int simTime;
	private final ATCNode node;
	private final SlotTrajectory lead;
	private final SlotTrajectory trail;
	private final double reqSep;
	private final double actSep;
	private final int additionalDelay;
	private final int itemID;

	public FullScheduleConflictResolution(final int uniqueID, final Map<String, Object> reportValues) {
		itemID = uniqueID;
		values = reportValues;
		//
		simTime = (int) values.get(ReportItemProperties.SIMULATION_TIME);
		reqSep = (double) values.get(ReportItemProperties.MIN_H_SEPARATION_REQUIRED);
		actSep = (double) values.get(ReportItemProperties.INITIAL_H_SEPARATION);
		additionalDelay = (int) values.get(ReportItemProperties.DELAY_NEEDED);
		node = (ATCNode) values.get(ReportItemProperties.NODE);
		lead = (SlotTrajectory) values.get(ReportItemProperties.LEAD);
		trail = (SlotTrajectory) values.get(ReportItemProperties.TRAIL);
	}

	@Override
	public int getID() {
		return itemID;
	}

	@Override
	public String getLead() {
		return lead.getSlotMarker().getName();
	}

	@Override
	public String getTrail() {
		return trail.getSlotMarker().getName();
	}

	@Override
	public ATCNode getNode() {
		return node;
	}

	@Override
	public double getRequiredSeparation() {
		return reqSep;
	}

	@Override
	public double getActualSeparation() {
		return actSep;
	}

	@Override
	public Map<String, Object> getValues() {
		return Collections.unmodifiableMap(values);
	}

	@Override
	public int getDelay() {
		return additionalDelay;
	}

	@Override
	public int getSimulationTime() {
		return simTime;
	}

	@Override
	public String getNodeName() {
		return node.getName();
	}

	@Override
	public String getReason() {
		return ScheduleConflictResolution.class.getSimpleName();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(TSSReporter.TSS_REPORT_PREFIX);
		sb.append("t=");
		sb.append(simTime);
		sb.append(" conflict detected at node ");
		sb.append(node.getName());
		sb.append(" between ");
		sb.append(lead.getSlotMarker().getName());
		sb.append(" and ");
		sb.append(trail.getSlotMarker().getName());
		sb.append(" minSep=");
		sb.append(reqSep);
		sb.append(" but actualSep=");
		sb.append(actSep);
		return sb.toString();
	}

}
