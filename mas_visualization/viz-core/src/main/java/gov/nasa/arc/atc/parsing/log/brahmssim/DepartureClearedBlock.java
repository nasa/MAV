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

import gov.nasa.arc.atc.core.DataModelInput;
import gov.nasa.arc.atc.parsing.log.LogDataBlock;

import java.util.List;
import java.util.Properties;

/**
 * 
 * @author ahamon
 *
 */
public class DepartureClearedBlock implements LogDataBlock {

	private static final String START = "---Departure_for---";

	// INFO : Starting engine for 'gov.nasa.arc.atm.atmmodel.scenarios.small2plane2depart.agents.ZNY_28'


	@Override
	public void parseBlock(List<String> lines, int firstLine, DataModelInput inputs) {
		String line = lines.get(firstLine);
		if (line.contains(START)) {
			String[] splitLine = line.split(" ");
			inputs.addDepartureCleared(splitLine[1],Integer.parseInt(splitLine[2]));
		}
	}

	@Override
	public boolean headerMatches(String nextLine) {
		return nextLine.contains(START);
	}

	@Override
	public int getNbLines() {
		return 1;
	}



}
