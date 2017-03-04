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

package gov.nasa.arc.atc.algos.dsas;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.Runway;

/**
 * 
 * @author ahamon
 *
 */
public class StandardDepartureAlgoTest {

	@Ignore
	@Test
	public void test() {
		SimulationContext context = new SimulationContext(createSimpleGeography());
		context.setSimulationDuration(Integer.MAX_VALUE);
		StandardDepartureAlgo departureAlgo = new StandardDepartureAlgo();
		ATCNode runway = context.getGeography().getAirports().get(0).getRunways().get(0);
		departureAlgo.initializeData(context, runway, runway);

		departureAlgo.execute(10);
		assertTrue(true);
		// TODO: code the test
	}

	private static ATCGeography createSimpleGeography() {
		ATCGeography geography = new ATCGeography("testingG");
		Airport airport = new Airport("Airport", 0, 0);
		Runway r = new Runway("R", airport, 12);
		airport.addRunway(r);
		geography.addAirport(airport);
		return geography;
	}

}
