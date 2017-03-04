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

import gov.nasa.arc.atc.brahms.BrahmsKeyWords;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.simulation.DepartureQueue;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author ahamon
 *
 */
public class DepartureQueueParser {

	private static final Logger LOG = Logger.getGlobal();

	private DepartureQueueParser() {
		// private utility constructor
	}

	public static DepartureQueue parseDepartureQueue(File file, ATCGeography geography, List<SimulatedSlotMarker> allFlights) {
		DepartureQueue departureQueue = null;
		String line;
		String[] splitLine;
        String runwayName;
		String planeFullName;
		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr);) {
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("[()]", " ");
				line = line.replaceAll("\\;", "");
				if (line.contains(BrahmsKeyWords.DEPARTURE_QUEUE)) {
					splitLine = line.split(" ");
					planeFullName = splitLine[5];
					final String tagName = planeFullName.split("_")[1];
					addDepartureToQueue(departureQueue, tagName, allFlights);
				} else if (line.contains(BrahmsKeyWords.HAS_WAYPOINT)) {
					splitLine = line.split(" ");
					runwayName = splitLine[3];
					departureQueue = new DepartureQueue(geography.getNodeByName(runwayName));
				} else if (line.contains(BrahmsKeyWords.INIT_FACTS)) {
					break;
				}
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exception while parseDepartureQueue; {0}", e);
			e.printStackTrace();
		}

		return departureQueue;
	}

	private static void addDepartureToQueue(DepartureQueue queue, String name, List<SimulatedSlotMarker> allFlights) {
		for (SimulatedSlotMarker slot : allFlights) {
			if (slot.getName().contains(name)) {
				queue.addDeparture(slot);
				return;
			}
		}
		throw new IllegalStateException("Could not find departure : " + name);
	}

}
