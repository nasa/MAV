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

package gov.nasa.arc.atc.export;

import static org.junit.Assert.*;

import java.io.File;

import gov.nasa.arc.atc.scenarios.geographies.Geographies;
import org.junit.Test;

import gov.nasa.arc.atc.geography.ATCGeography;

/**
 * 
 * @author ahamon
 *
 */
public class ATCGeographyLoaderTest {

	@Test
	public void testParseXML() {
		File geographyFile = new File(Geographies.class.getResource("Geography1_1.xml").getPath());
		assertTrue(geographyFile.exists());
		ATCGeography geography = ATCGeographyLoader.parseXML(geographyFile);
		System.err.println("Nb waypoints: " + geography.getWaypoints().size());
		System.err.println("Nb Airports: " + geography.getAirports().size());
		geography.getAirports().forEach(airport -> System.err.println(" " + airport.getName() + " nb runways=" + airport.getRunways()));
		System.err.println("Nb Arrivals: " + geography.getArrivalNodes().size());
		System.err.println("Arrivals: " + geography.getArrivalNodes());
		System.err.println("Nb Departures: " + geography.getDepartureNodes().size());
		System.err.println("Departures: " + geography.getDepartureNodes());
		System.err.println("Nb Segments: " + geography.getSegments().size());
		// System.err.println("Segments: "+geography.getSegments());
	}

}
