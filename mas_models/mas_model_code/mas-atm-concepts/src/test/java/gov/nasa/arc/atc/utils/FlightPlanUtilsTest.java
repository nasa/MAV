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

/********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.utils;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.airborne.impl.AFOImpl;
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightPlanTest;
import gov.nasa.arc.atc.utils.FlightPlanUtils;

/**
 * 
 * @author hamon
 *
 */
public class FlightPlanUtilsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Ignore
	@Test
	public void testGetTopOfDescent() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testCheckIfDepartureFlightPlan() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testFindMeterfix() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testCalculateSegmentTraverseTime() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testCalculateAverageSegmentTAS() {
		fail("Not yet implemented");
	}

	@Test
	public void testCalculateDistanceInTrackTo() {
		FlightPlan plan1 = FlightPlanTest.createTestPlanArrival1();
		FlightPlan plan2 = FlightPlanTest.createTestPlanArrival2();
		AFO afo1 = new AFOImpl("afo1", 0, 0);
		AFO afo2 = new AFOImpl("afo2", 0, 0);
		afo1.setFlightPlan(plan1);
		afo2.setFlightPlan(plan2);
		//
		afo1.setLatitude(39.5);
		afo1.setLongitude(-70);
		afo2.setLatitude(39.5);
		afo2.setLongitude(-70);
		//
		plan1.start();
		plan2.start();
		// basic checks
		assertEquals(0, plan1.getCurrentSegmentIndex());
		assertEquals(0, plan2.getCurrentSegmentIndex());
		//
		double distance1A = FlightPlanUtils.calculateDistanceInTrackTo(afo1, "A");
		double distance2C = FlightPlanUtils.calculateDistanceInTrackTo(afo2, "C");
		//
		System.err.println(" distance1A = " + distance1A);
		System.err.println(" distance2C = " + distance2C);
		//
		double distance1Arrival = FlightPlanUtils.calculateDistanceInTrackTo(afo1, "Arrival");
		double distance2Arrival = FlightPlanUtils.calculateDistanceInTrackTo(afo2, "Arrival");
		//
		System.err.println(" distance1Arrival = " + distance1Arrival);
		System.err.println(" distance2Arrival = " + distance2Arrival);
		//
		assertEquals(distance1A, distance2C, 0.0001);
		assertEquals(distance1Arrival, distance2Arrival, 0.0001);
	}

	@Test
	public void testCalculateETATo() {
		FlightPlan plan1 = FlightPlanTest.createTestPlanArrival1();
		FlightPlan plan2 = FlightPlanTest.createTestPlanArrival2();
		AFO afo1 = new AFOImpl("afo1", 0, 0);
		AFO afo2 = new AFOImpl("afo2", 0, 0);
		afo1.setFlightPlan(plan1);
		afo2.setFlightPlan(plan2);
		//
		afo1.setLatitude(39.5);
		afo1.setLongitude(-70);
		afo1.setAirSpeed(250);
		afo2.setLatitude(39.5);
		afo2.setLongitude(-70);
		afo2.setAirSpeed(250);
		//
		plan1.start();
		plan2.start();
		// basic checks
		assertEquals(0, plan1.getCurrentSegmentIndex());
		assertEquals(0, plan2.getCurrentSegmentIndex());
		//
		double eta1 = FlightPlanUtils.calculateETATo(afo1, "wpArrival");
		double eta2 = FlightPlanUtils.calculateETATo(afo2, "wpArrival");
		//
		System.err.println(" eta1 = " + eta1);
		System.err.println(" eta2 = " + eta2);
		//
		assertEquals(eta1, eta2, 0.0001);

	}

}
