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
package gov.nasa.arc.atc.geography;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Waypoint;

/**
 * 
 * @author hamon
 *
 */
public class FlightPlanTest {

	private static FlightPlan instance;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		instance = createTestPlanArrival2();
		instance = createTestPlanDeparture1();
		instance = createTestPlanArrival1();
	}

	@Test
	public void testGetAfoName() {
		String afoName = "testName";
		FlightPlan plan = new FlightPlan(afoName);
		assertEquals(afoName, plan.getAfoName());
	}

	@Test
	public void testGetPath() {
		List<FlightSegment> path = instance.getPath();
		assertEquals(6, path.size());
	}

	@Test
	public void testIsValid() {
		FlightPlan plan = new FlightPlan("empty");
		assertTrue(plan.isValid());
		//
		assertTrue(instance.isValid());
	}

	@Test
	public void testGetFirstWaypoint() {
		assertEquals("wpDeparture", instance.getFirstWaypoint().getName());
	}

	@Test
	public void testGetLastWaypoint() {
		assertEquals("LGA", instance.getLastWaypoint().getName());
	}

	@Test
	public void testGetWayPoints() {
		assertEquals(7, instance.getWayPoints().size());
	}

	@Test
	public void testGetCurrentSegmentIndex() {
		instance.setCurrentSegment("LGA", "A");
		assertEquals(0, instance.getCurrentSegmentIndex());
		instance.setCurrentSegment("I", "LGA");
		assertEquals(5, instance.getCurrentSegmentIndex());
	}

	@Test
	public void testToString() {
		assertNotNull(instance.toString());
	}

	@Test
	public void testIsCurrentSegment() {
		instance.setCurrentSegment("wpDeparture", "A");
		assertTrue(instance.isCurrentSegment(instance.getPath().get(0)));
		instance.setCurrentSegment("I", "wpArrival");
		assertTrue(instance.isCurrentSegment(instance.getPath().get(5)));
	}

	@Test
	public void testGetPreviousSegment() {
		instance.setCurrentSegment("wpDeparture", "A");
		assertNull(instance.getPreviousSegment(instance.getPath().get(0)));
		assertEquals(instance.getPath().get(2), instance.getPreviousSegment(instance.getPath().get(3)));
	}

	public static final FlightPlan createTestPlanArrival1() {
		FlightPlan flightPlan = new FlightPlan("testFlightPlan");
		// Waypoints
		Waypoint wpDeparture = new Waypoint("wpDeparture", 39.5, -70);
		Waypoint wpA = new Waypoint("A", 40, -70.5);
		Waypoint wpB = new Waypoint("B", 39.5, -70.5);
		Waypoint wpE = new Waypoint("E", 39, -70);
		Waypoint wpH = new Waypoint("H", 38.5, -70);
		Waypoint wpI = new Waypoint("I", 38, -70);
		Airport airport = new Airport("airport", 37.5, -70);
		Runway arrivalR = new Runway("LGA", airport, 12);
		airport.addRunway(arrivalR);
		// Waypoint wpArrival = new Waypoint("wpArrival", 37.5, -70);
		// Segments
		FlightSegment seg0 = new FlightSegment("Departure_A", "testPlane", wpDeparture, wpA, 5000, 250);
		FlightSegment seg1 = new FlightSegment("A_B", "testPlane", wpA, wpB, 10000, 300);
		FlightSegment seg2 = new FlightSegment("B_E", "testPlane", wpB, wpE, 10000, 350);
		FlightSegment seg3 = new FlightSegment("E_H", "testPlane", wpE, wpH, 10000, 350);
		FlightSegment seg4 = new FlightSegment("H_I", "testPlane", wpH, wpI, 5000, 300);
		FlightSegment seg5 = new FlightSegment("I_LGA", "testPlane", wpI, arrivalR, 0, 250);
		//
		flightPlan.addSegment(seg5);
		flightPlan.addSegment(seg3);
		flightPlan.addSegment(seg4);
		flightPlan.addSegment(seg2);
		flightPlan.addSegment(seg1);
		flightPlan.addSegment(seg0);
		//
		flightPlan.setInitialSegment(seg0);
		flightPlan.setArrivalRunway(arrivalR);
		//
		return flightPlan;
	}

	public static final FlightPlan createTestPlanArrival2() {
		FlightPlan flightPlan = new FlightPlan("testFlightPlan");
		// Waypoints
		Waypoint wpDeparture = new Waypoint("wpDeparture", 39.5, -70);
		Waypoint wpC = new Waypoint("C", 40, -69.5);
		Waypoint wpD = new Waypoint("D", 39.5, -69.5);
		Waypoint wpE = new Waypoint("E", 39, -70);
		Waypoint wpH = new Waypoint("H", 38.5, -70);
		Waypoint wpI = new Waypoint("I", 38, -70);
		Airport airport = new Airport("airport", 37.5, -70);
		Runway arrivalR = new Runway("LGA", airport, 12);
		airport.addRunway(arrivalR);
		// Waypoint wpArrival = new Waypoint("wpArrival", 37.5, -70);
		// Segments
		FlightSegment seg0 = new FlightSegment("Departure_C", "testPlane", wpDeparture, wpC, 5000, 250);
		FlightSegment seg1 = new FlightSegment("C_D", "testPlane", wpC, wpD, 10000, 300);
		FlightSegment seg2 = new FlightSegment("D_E", "testPlane", wpD, wpE, 10000, 350);
		FlightSegment seg3 = new FlightSegment("E_H", "testPlane", wpE, wpH, 10000, 350);
		FlightSegment seg4 = new FlightSegment("H_I", "testPlane", wpH, wpI, 5000, 300);
		FlightSegment seg5 = new FlightSegment("I_LGA", "testPlane", wpI, arrivalR, 0, 250);
		//
		flightPlan.addSegment(seg5);
		flightPlan.addSegment(seg3);
		flightPlan.addSegment(seg4);
		flightPlan.addSegment(seg2);
		flightPlan.addSegment(seg1);
		flightPlan.addSegment(seg0);
		//
		flightPlan.setInitialSegment(seg0);
		flightPlan.setArrivalRunway(arrivalR);
		//
		return flightPlan;
	}

	public static final FlightPlan createTestPlanDeparture1() {
		FlightPlan flightPlan = new FlightPlan("testFlightPlan");
		// Waypoints
		Airport airport = new Airport("airport", 37.5, -70);
		Runway runway = new Runway("LGA", airport, 12);
		airport.addRunway(runway);
		Waypoint wpZ = new Waypoint("Z", 37, -70);
		Waypoint wpY = new Waypoint("Y", 36.5, -70);
		// Segments
		FlightSegment seg0 = new FlightSegment("LGA_Z", "testPlane", runway, wpZ, 5000, 400);
		FlightSegment seg1 = new FlightSegment("Z_Y", "testPlane", wpZ, wpY, 10000, 450);
		//
		flightPlan.addSegment(seg1);
		flightPlan.addSegment(seg0);
		//
		flightPlan.setInitialSegment(seg0);
		flightPlan.setCurrentSegment(seg0);
		flightPlan.setDepartureRunway(runway);
		//
		return flightPlan;
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNonExistingInitSegment() {
		String afoName = "testPlane";
		FlightPlan flightPlan = new FlightPlan("testFlightPlan");

		// Waypoints
		Waypoint wpDeparture = new Waypoint("LGA", 39.5, -70);
		Waypoint wpC = new Waypoint("C", 40, -69.5);
		Waypoint wpD = new Waypoint("D", 39.5, -69.5);
		// Segments
		FlightSegment seg0 = new FlightSegment("LGA_C", afoName, wpDeparture, wpC, 5000, 250);
		//
		flightPlan.addSegment(seg0);
		//
		FlightSegment seg1 = new FlightSegment("C_D", "testPlane", wpC, wpD, 10000, 300);
		flightPlan.setInitialSegment(seg1);
	}

	/*
	 * Tests to ensure proper equals methods is used
	 */

	@Test
	public void testBuildVariousInstances() {
		String afoName = "testPlane";
		FlightPlan flightPlan = new FlightPlan("testFlightPlan");

		// Waypoints
		Waypoint wpDeparture = new Waypoint("LGA31", 39.5, -70);
		Waypoint wpC = new Waypoint("C", 40, -69.5);
		Waypoint wpD = new Waypoint("D", 39.5, -69.5);
		Waypoint wpE = new Waypoint("E", 39, -70);
		Waypoint wpH = new Waypoint("H", 38.5, -70);
		Waypoint wpI = new Waypoint("I", 38, -70);
		Waypoint wpArrival = new Waypoint("LGA22", 37.5, -70);
		// Waypoints BIS
		Waypoint wpDepartureBIS = new Waypoint("LGA31", 39.5, -70);
		Waypoint wpCBIS = new Waypoint("C", 40, -69.5);
		Waypoint wpDBIS = new Waypoint("D", 39.5, -69.5);
		Waypoint wpEBIS = new Waypoint("E", 39, -70);
		Waypoint wpHBIS = new Waypoint("H", 38.5, -70);
		Waypoint wpIBIS = new Waypoint("I", 38, -70);
		// Segments
		FlightSegment seg0 = new FlightSegment("LGA_C", afoName, wpDeparture, wpC, 5000, 250);
		FlightSegment seg1 = new FlightSegment("C_D", afoName, wpCBIS, wpD, 10000, 300);
		FlightSegment seg2 = new FlightSegment("D_E", afoName, wpDBIS, wpE, 10000, 350);
		FlightSegment seg3 = new FlightSegment("E_H", afoName, wpEBIS, wpH, 10000, 350);
		FlightSegment seg4 = new FlightSegment("H_I", afoName, wpHBIS, wpI, 5000, 300);
		FlightSegment seg5 = new FlightSegment("I_LGA", afoName, wpIBIS, wpArrival, 0, 250);
		//
		flightPlan.addSegment(seg5);
		flightPlan.addSegment(seg3);
		flightPlan.addSegment(seg4);
		flightPlan.addSegment(seg2);
		flightPlan.addSegment(seg1);
		flightPlan.addSegment(seg0);
		//
		FlightSegment initialSeg = new FlightSegment("LGA_C", afoName, wpDepartureBIS, wpCBIS, 5000, 250);
		flightPlan.setInitialSegment(initialSeg);
		assertTrue(flightPlan.isValid());
		assertEquals(6, flightPlan.getPath().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddMisMatchInitSegSpeed() {
		String afoName = "testPlane";
		FlightPlan flightPlan = new FlightPlan("testFlightPlan");

		// Waypoints
		Waypoint wpDeparture = new Waypoint("LGA", 39.5, -70);
		Waypoint wpC = new Waypoint("C", 40, -69.5);
		// Waypoints BIS
		Waypoint wpDepartureBIS = new Waypoint("LGA", 39.5, -70);
		Waypoint wpCBIS = new Waypoint("C", 40, -69.5);
		// Segments
		FlightSegment seg0 = new FlightSegment("Departure_C", afoName, wpDeparture, wpC, 5000, 250);
		//
		flightPlan.addSegment(seg0);
		//
		FlightSegment initialSeg = new FlightSegment("Departure_C", afoName, wpDepartureBIS, wpCBIS, 5000, 251);
		flightPlan.setInitialSegment(initialSeg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddMisMatchInitSegAltitude() {
		String afoName = "testPlane";
		FlightPlan flightPlan = new FlightPlan("testFlightPlan");

		// Waypoints
		Waypoint wpDeparture = new Waypoint("wpDeparture", 39.5, -70);
		Waypoint wpC = new Waypoint("C", 40, -69.5);
		// Waypoints BIS
		Waypoint wpDepartureBIS = new Waypoint("wpDeparture", 39.5, -70);
		Waypoint wpCBIS = new Waypoint("C", 40, -69.5);
		// Segments
		FlightSegment seg0 = new FlightSegment("Departure_C", afoName, wpDeparture, wpC, 5000, 250);
		//
		flightPlan.addSegment(seg0);
		//
		FlightSegment initialSeg = new FlightSegment("Departure_C", afoName, wpDepartureBIS, wpCBIS, 5001, 250);
		flightPlan.setInitialSegment(initialSeg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddMisMatchInitSegName() {
		String afoName = "testPlane";
		FlightPlan flightPlan = new FlightPlan("testFlightPlan");

		// Waypoints
		Waypoint wpDeparture = new Waypoint("wpDeparture", 39.5, -70);
		Waypoint wpC = new Waypoint("C", 40, -69.5);
		// Waypoints BIS
		Waypoint wpDepartureBIS = new Waypoint("wpDeparture", 39.5, -70);
		Waypoint wpCBIS = new Waypoint("C", 40, -69.5);
		// Segments
		FlightSegment seg0 = new FlightSegment("Departure_C", afoName, wpDeparture, wpC, 5000, 250);
		//
		flightPlan.addSegment(seg0);
		//
		FlightSegment initialSeg = new FlightSegment("Departure_C", "foo", wpDepartureBIS, wpCBIS, 5000, 250);
		flightPlan.setInitialSegment(initialSeg);
	}

}
