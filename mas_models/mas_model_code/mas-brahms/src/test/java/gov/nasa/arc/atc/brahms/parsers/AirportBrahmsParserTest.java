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

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.scenarios.testconf.testdata.TestData;

/**
 * 
 * @author ahamon
 *
 */
public class AirportBrahmsParserTest {

	@Test
	public void testParseNY() {
		File airportsFile = new File(TestData.class.getResource("allAirports.b").getPath());
		Map<String, Airport> airports = AirportBrahmsParser.parseAirports(airportsFile);
		assertEquals(2, airports.values().size());
		airports.forEach((name, airport) -> {
			System.err.println("Airport ::" + name);
			System.err.println(" > toString ::" + airport);
			airport.getRunways().stream().forEach(runway -> System.err.println("  -> " + runway));
		});
	}

}
