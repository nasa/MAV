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

package gov.nasa.arc.atc.simulation.newfpl;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.simulation.SlotTrajectory;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.simulation.SimulatedSlotTrajectoryTestScenario;
import gov.nasa.arc.atc.simulation.TimedFlightParameters;
import gov.nasa.arc.atc.utils.AfoUtils;
import gov.nasa.arc.atc.utils.CustomRecordFormatter;
import gov.nasa.arc.atc.utils.FlightPlanUtils;
import gov.nasa.arc.atc.utils.SlotUpdateUtils;

/**
 * 
 * @author ahamon
 *
 */
public class ModifiedFPLTest {

	private static final Logger LOG = Logger.getGlobal();
	private static final ConsoleHandler CONSOLE_HANDLER = new ConsoleHandler();

	@BeforeClass
	public static void setupClass() {
		LOG.setUseParentHandlers(false);
		CustomRecordFormatter formatter = new CustomRecordFormatter();

		CONSOLE_HANDLER.setFormatter(formatter);
		Handler[] handlers = LOG.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			LOG.removeHandler(handlers[i]);
		}
		CONSOLE_HANDLER.setLevel(Level.OFF);
		LOG.addHandler(CONSOLE_HANDLER);
		LOG.log(Level.CONFIG, "LOG Level is {0}", Level.ALL);
	}

	@Test
	public void test() {
		System.err.println(" TEST TODO");

//		// creating the baseline trajectory based on the EGF_4314 plane
//		SlotTrajectory oracle = SimulatedSlotTrajectoryTestScenario.createEGF4314();
//		SlotTrajectory originalTraj = SimulatedSlotTrajectoryTestScenario.createEGF4314();
//
//		System.err.println("original FPL: " + originalTraj.getSlotMarker().getFlightPlan());
//
//		// creating the modified flight plan
//		FlightPlan modifiedFlightPlan = FlightPlanUtils.createSlotModifiedFlightPlan(originalTraj.getSlotMarker());
//		assertNotNull(modifiedFlightPlan);
//
//		// compare both flight plans
//		//// TODO
//
//		System.err.println("modified FPL: " + modifiedFlightPlan);
//
//		List<TimedFlightParameters> reference = SlotUpdateUtils.calculateArrivalRefTrajectory(originalTraj.getSlotMarker(), modifiedFlightPlan);
//		// check t=0 at origin
//		ATCNode oriWPT = modifiedFlightPlan.getFirstWaypoint();
//		System.err.println("  oriWPT = " + oriWPT);
//		System.err.println("  oriPOS = " + reference.get(0) + " with time stamp =" + reference.get(0).getTimeStamp());
//		int timeAtOrigin = SlotUpdateUtils.getFirstTimeAtCoordinates(oriWPT.getLatitude(), oriWPT.getLongitude(), reference);
//		System.err.println("Arrives at origin at t=" + timeAtOrigin);
//		// get time at initial position
//		int timeAtStart = SlotUpdateUtils.getFirstTimeAtCoordinates(SimulatedSlotTrajectoryTestScenario.EGF4314_LAT, SimulatedSlotTrajectoryTestScenario.EGF4314_LONG, reference);
//		System.err.println("Arrives at initial position at t=" + timeAtStart);
//		int arrivalTime = reference.size();
//		int arrivalTime2 = reference.get(reference.size() - 1).getTimeStamp();
//		System.err.println("Arrives at destination at =" + arrivalTime);
//		System.err.println("Arrives2 at destination at =" + arrivalTime2);
//		// 1596
//		System.err.println("oracle.getArrivalTime()=" + oracle.getArrivalTime());
//		int flyTime = oracle.getArrivalTime() - 1596;
//		System.err.println("oracle flyTime=" + flyTime);
//		int flyTimeREF = arrivalTime2 - timeAtStart;
//		System.err.println("oracle flyTimeREF=" + flyTimeREF);
//
//		// LGA22 (current.latitude = 40.78528)(current.longitude = 73.87055)
//
//		for (int i = 1596; i < oracle.getArrivalTime(); i++) {
//			double distance = AfoUtils.getHorizontalDistance(oracle.getParametersAtSimulationTime(i).getLatitude(), oracle.getParametersAtSimulationTime(i).getLongitude(), reference.get(timeAtStart + i - 1596).getLatitude(), reference.get(timeAtStart + i - 1596).getLongitude());
//			boolean dOK = distance < 0.001;
//			assertTrue(dOK);
//		}
	}

}
