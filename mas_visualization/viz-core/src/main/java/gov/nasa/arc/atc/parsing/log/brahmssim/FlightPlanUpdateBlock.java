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

package gov.nasa.arc.atc.parsing.log.brahmssim;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.atc.FlightPlanUpdateImpl;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.core.DataModelInput;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Runway;
import gov.nasa.arc.atc.parsing.DataBlocks;
import gov.nasa.arc.atc.parsing.log.LogDataBlock;
import javafx.util.Pair;

/**
 * 
 * @author ahamon
 *
 */
public class FlightPlanUpdateBlock implements LogDataBlock {

	// data block structure
	//--- AFO FlightPlan ---
	//	afoName   gov.nasa.arc.atm.atmmodel.scenarios.week2run6.agents.plane_CPZ5753
	//	from   Runway LGA31
	//	to   null
	//	nbSegments   2
	//	hasSegment   LGA31_TO_KWANN_CPZ5753   LGA31   KWANN   3436.7   240.0
	//	hasSegment   KWANN_TO_THRON_CPZ5753   KWANN   THRON   9769.9   240.0
	//--- END FlightPlan block ---

	public static final String PATTERN = "--- AFO FlightPlan ---";

	private static final int MIN_NUMBER_OF_LINES = 6;

	// is there a need to subscribe to SimulationManager?
	private ATCGeography geography;
	private int nbLines = MIN_NUMBER_OF_LINES;

	@Override
	public void parseBlock(List<String> lines, int firstLineIndex, DataModelInput inputs) {
		// hum...
		geography = SimulationManager.getATCGeography();
		String afoName = getControllerName(lines.get(firstLineIndex + 1));
		Runway fromRunway = getRunway(lines.get(firstLineIndex + 2));
		Runway toRunway = getRunway(lines.get(firstLineIndex + 3));
		int nbSegments = getNumberOfSegments(lines.get(firstLineIndex + 4));
		nbLines = MIN_NUMBER_OF_LINES + nbSegments;
		final List<FlightSegment> segments = new ArrayList<>();
		for (int i = 0; i < nbSegments; i++) {
			FlightSegment segment = getWaypoint(lines.get(firstLineIndex + 5 + i));
			segments.add(segment);
		}
		inputs.addFlightPlanUpdate(new FlightPlanUpdateImpl(afoName, fromRunway, toRunway, segments));
	}

	@Override
	public boolean headerMatches(String nextLine) {
		return nextLine.contains(PATTERN);
	}

	@Override
	public int getNbLines() {
		return nbLines;
	}

	private String getControllerName(String line) {
		Pair<String, String> beliefLog = DataBlocks.getLoggedAttributes(line);
		// check belief name
		return DataBlocks.getSimpleName(beliefLog.getValue());
	}

	private int getNumberOfSegments(String line) {
		Pair<String, String> beliefLog = DataBlocks.getLoggedAttributes(line);
		// check belief name
		return Integer.parseInt(beliefLog.getValue());
	}

	private Runway getRunway(String line) {
		String[] splitLine = line.replaceAll("\\s+", " ").split(" ");

		if (splitLine.length == 2 || "null".equals(splitLine[2])) {
			return null;
		}

		String runwayName = splitLine[3];
		for (Airport airport : geography.getAirports()) {
			for (Runway runway : airport.getRunways()) {
				if (runway.getName().equals(runwayName)) {
					return runway;
				}
			}
		}
		throw new IllegalStateException(" Could not find Runway " + runwayName);
	}

	private FlightSegment getWaypoint(String line) {
		String[] splitLine = line.replaceAll("\\s+", " ").split(" ");

		String name = splitLine[2];
		// hum...
		String afoName = name.split("_")[2];
		ATCNode from = geography.getNodeByName(splitLine[3]);
		if (from == null) {
			throw new IllegalStateException(" Could not find ATCNode " + splitLine[3]);
		}
		ATCNode to = geography.getNodeByName(splitLine[4]);
		if (to == null) {
			throw new IllegalStateException(" Could not find ATCNode " + splitLine[4]);
		}
		double endAltitude = Double.parseDouble(splitLine[5]);
		double endSpeed = Double.parseDouble(splitLine[6]);
		return new FlightSegment(name, afoName, from, to, endAltitude, endSpeed);
	}

}
