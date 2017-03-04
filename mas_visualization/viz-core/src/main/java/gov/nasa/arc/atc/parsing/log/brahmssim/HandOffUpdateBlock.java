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

import java.util.List;

import gov.nasa.arc.atc.ControllerHandOff;
import gov.nasa.arc.atc.core.DataModelInput;
import gov.nasa.arc.atc.parsing.DataBlocks;
import gov.nasa.arc.atc.parsing.log.LogDataBlock;
import javafx.util.Pair;

/**
 * 
 * @author ahamon
 *
 */
public class HandOffUpdateBlock implements LogDataBlock {

	// data block structure
	//--- AFO PrintHandOff ---
	//	time   175
	//	controller   gov.nasa.arc.atm.atmmodel.scenarios.week2run6.agents.ZNY_114
	//	afoName   gov.nasa.arc.atm.atmmodel.scenarios.week2run6.agents.plane_DAL927
	//	latitude   40.6984191140696
	//	longitude   74.00569272510299
	//			--- END PrintHandOff block ---

	public static final String PATTERN = "--- AFO PrintHandOff ---";

	private static final int NUMBER_OF_LINES = 7;

	@Override
	public void parseBlock(List<String> lines, int firstLineIndex, DataModelInput inputs) {
		final int simulationTime = getSimulationTime(lines.get(firstLineIndex + 1));
		String controllerName = getName(lines.get(firstLineIndex + 2));
		String afoName = getName(lines.get(firstLineIndex + 3));
		double latitude = getLatitude(lines.get(firstLineIndex + 4));
		double longitude = getLongitude(lines.get(firstLineIndex + 5));
		//
		inputs.addHandOff(new ControllerHandOff(simulationTime, controllerName, afoName, latitude, longitude));
	}

	@Override
	public boolean headerMatches(String nextLine) {
		return nextLine.contains(PATTERN);
	}

	@Override
	public int getNbLines() {
		return NUMBER_OF_LINES;
	}

	private String getName(String line) {
		Pair<String, String> beliefLog = DataBlocks.getLoggedAttributes(line);
		// check belief name
		return DataBlocks.getSimpleName(beliefLog.getValue());
	}

	private int getSimulationTime(String line) {
		Pair<String, String> beliefLog = DataBlocks.getLoggedAttributes(line);
		// check belief name
		return Integer.parseInt(beliefLog.getValue());
	}

	private double getLatitude(String line) {
		Pair<String, String> beliefLog = DataBlocks.getLoggedAttributes(line);
		// check belief name
		return Double.parseDouble(beliefLog.getValue());
	}

	private double getLongitude(String line) {
		Pair<String, String> beliefLog = DataBlocks.getLoggedAttributes(line);
		// check belief name
		return -Double.parseDouble(beliefLog.getValue());
	}

}
