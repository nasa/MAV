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

package gov.nasa.arc.atc.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.geography.ATCArea;
import gov.nasa.arc.atc.geography.Region;
import gov.nasa.arc.atc.geography.Sector;

/**
 * 
 * @author ahamon
 *
 */
public class SectorParser {

	private static final Logger LOG = Logger.getGlobal();

	private SectorParser() {
		// utility constructor
	}

	/**
	 * !! at the moment does not handle excluded regions
	 * 
	 * @param areaFile file to parse
	 * @return the parsed area populated with its sectors
	 */
	public static ATCArea parseATCArea(File areaFile) {
		LOG.log(Level.INFO, "# parseATCArea file :: {0}", areaFile);
		ATCArea result = null;
		//
		String line;
		StringBuilder discarded = new StringBuilder();
		try (FileReader fr = new FileReader(areaFile); BufferedReader br = new BufferedReader(fr)) {
			// reading header
			line = br.readLine();
			LOG.log(Level.INFO, "# file header :: {0}", line);
			// getting area name
			discarded.append(" & ").append(br.readLine());
			discarded.append(" & ").append(br.readLine());
			line = br.readLine();
			assert line != null;
			String name = line.replaceAll(" ", "").replace("\t", "");
			result = new ATCArea(name);
			//
			discarded.append(" & ").append(br.readLine());
			while ((line = br.readLine()) != null) {
				discarded.append(" & ").append(br.readLine());
				final Sector sector = parseSingleSector(br, line);
				result.addSector(sector);
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exception while generateFlightPlan; {0}", e);
		}
		LOG.log(Level.INFO, "Discarded:: {0}",discarded.toString());
		return result;
	}

	private static Sector parseSingleSector(BufferedReader br, String firstLine) throws IOException {
		String line;
		String[] splitLine;
		Sector sector;
		// sector name
		String name = firstLine;
		// get sector ID
		line = br.readLine();
		if (line.contains("#")) {
			// sometimes sectors have two names...
			name = line;
			line = br.readLine();
		}
		splitLine = line.replace("\t", " ").split(" ");
		int iD = Integer.parseInt(splitLine[2]);
		sector = new Sector(name, iD);
		//
		line = br.readLine();
		while (line != null && !line.isEmpty()) {
			if (!line.contains("region") || line.contains("exclude")) {
				throw new UnsupportedOperationException(" cannot parse region:: " + line);
			}
			//
			line = br.readLine();
			String altitude = line.replace("\t", " ").split(" ")[2];
			String[] spliAltitude = altitude.split("/");
			int minAlt = Integer.parseInt(spliAltitude[0]);
			int maxAlt = Integer.parseInt(spliAltitude[1]);
			//
			Region region = new Region(minAlt, maxAlt);
			while ((line = br.readLine()) != null) {
				if (line.isEmpty() || line.contains("region")) {
					// end of region
					break;
				}
				// get vertex
				final Coordinates vertex = parseVertexCoordinates(line);
				region.addVertex(vertex);
			}
			//
			sector.addRegion(region);
		}

		return sector;
	}

	private static Coordinates parseVertexCoordinates(String line) {
		String[] splitLine = line.split("\t");
		double latitude = toCoordinates(splitLine[1]);
		double longitude = toCoordinates(splitLine[2]) * -1.0;
		return new Coordinates(latitude, longitude);
	}

	private static double toCoordinates(String value) {
		return Double.valueOf(value.substring(0, 2)) + Double.valueOf(value.substring(2, 4)) / 60.0 + Double.valueOf(value.substring(4)) / 3600.0;
	}

}
