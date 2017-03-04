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

package gov.nasa.arc.atc.brahms.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.brahms.BrahmsFlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;

/**
 * 
 * @author ahamon
 *
 */
public class FlightPlanBrahmsParser {

	private static final Logger LOG = Logger.getGlobal();

	private FlightPlanBrahmsParser() {
		// private utility constructor
	}

	/**
	 * 
	 * @param fplFile the Brahms file containing the flight plans
	 * @param flightSegments the flight segments availables
	 * @return the parsed {@link BrahmsFlightPlan}s
	 */
	public static final Map<String, BrahmsFlightPlan> parseBrahmsFPLs(File fplFile, Map<String, FlightSegment> flightSegments) {
		Map<String, BrahmsFlightPlan> plans = new HashMap<>();
		String line;
		String[] splitLine;

		try (FileReader fr = new FileReader(fplFile); BufferedReader br = new BufferedReader(fr);) {
			while ((line = br.readLine()) != null) {
				// Removing the end ); at the end of the line
				line = line.replaceAll("\\);", "").replaceAll("[()]", " ");
				// Splitting the line at spaces
				splitLine = line.split(" ");
				// Retrieve the name of the segment
				if (line.contains("object") && (line.contains("flightPlan"))) {
					String fplName = splitLine[1];
					// Get the name of the corresponding slot
					String[] fplNameSplit = fplName.split("_");
					String slotName = fplNameSplit[1];
					BrahmsFlightPlan bFPL = parseSingleFlightPlan(fplName, slotName, br, flightSegments);
					testFlightPlanExistence(plans, slotName);
					plans.put(slotName, bFPL);
				}
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exception while generateFlightPlan; {0}", e);
		}
		return Collections.unmodifiableMap(plans);
	}

	private static BrahmsFlightPlan parseSingleFlightPlan(String planName, String slotName, BufferedReader bufferedReader, Map<String, FlightSegment> flightSegments) throws IOException {
		final BrahmsFlightPlan bFPL = new BrahmsFlightPlan(planName, slotName);
		String line;
		String[] splitLine;
		int segmentID;
		String segmentName;
		//
		while ((line = bufferedReader.readLine()) != null && !line.contains("}") && !line.contains("initial_facts:")) {
			// Removing the end ); at the end of the line
			if (!line.contains("initial_beliefs")) {
				line = line.replaceAll("\\)", " ");
				line = line.replaceAll("\\(", " ");
				splitLine = line.split(" ");
				segmentID = Integer.parseInt(splitLine[2]);
				segmentName = splitLine[5];
				final FlightSegment segment = flightSegments.get(segmentName);
				bFPL.addSegment(segmentID, segment);
			}
		}
		LOG.log(Level.FINE, "Found FlightPlan:: {0}", bFPL);
		return bFPL;
	}

	private static void testFlightPlanExistence(Map<String, BrahmsFlightPlan> plans, String slotName) {
		if (plans.containsKey(slotName)) {
			LOG.log(Level.SEVERE, "duplicate BrahmsFlightPlan found for slot {0}", slotName);
			throw new IllegalStateException("duplicate BrahmsFlightPlan found for slot " + slotName);
		}
	}

}
