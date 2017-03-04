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

package gov.nasa.arc.atc.simulation;

import static org.junit.Assert.*;

import java.util.logging.Level;

import gov.nasa.arc.atc.geography.*;
import gov.nasa.arc.atc.utils.Constants;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.utils.ConsoleUtils;

/**
 * @author ahamon
 */
public class DepartureTrajectoryTest {

    private static final double DELTA_PRECISION = 0.02;
    private static final int DEFAULT_SIM_DURATION = 2200;

    @BeforeClass
    public static void setupClass() {
        ConsoleUtils.setLoggingLevel(Level.FINE);
    }


    @Ignore
    @Test
    public void testRecalculateDelay0() {
        int startTimeOracle = 4;
        SlotTrajectory oracleTraj = createTrajectory1(startTimeOracle);
        int originalEndTime = oracleTraj.getOriginalArrivalTime();
        //
        int startTime1 = 10;
        SlotTrajectory testTraj1t2 = createTrajectory1(startTime1);
        int traj1originalEndTime = testTraj1t2.getArrivalTimeAtNode("WPT_2");
        //
        assertEquals(originalEndTime, traj1originalEndTime - startTime1 + startTimeOracle);
        //
        FlightParameters oracleParam;
        FlightParameters traj1Param;
        String oracleWPT;
        String traj1WPT;
        // test before recalculation
        for (int i = 0; i < originalEndTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1t2.getParametersAtSimulationTime(i + startTime1 - startTimeOracle);
            traj1WPT = testTraj1t2.getWaypointAimedAtSimulationTime(i + startTime1 - startTimeOracle);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        // recalculation
        testTraj1t2.recalculateTrajectoryWithDelayAt(5, 0);
        // test after recalculation
        for (int i = 0; i < originalEndTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1t2.getParametersAtSimulationTime(i + startTime1 - startTimeOracle);
            traj1WPT = testTraj1t2.getWaypointAimedAtSimulationTime(i + startTime1 - startTimeOracle);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        // recalculation
        testTraj1t2.recalculateTrajectoryWithDelayAt(10, 0);
        // test after recalculation
        for (int i = 0; i < originalEndTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1t2.getParametersAtSimulationTime(i + startTime1 - startTimeOracle);
            traj1WPT = testTraj1t2.getWaypointAimedAtSimulationTime(i + startTime1 - startTimeOracle);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
    }


    @Ignore
    @Test
    public void testRecalculateDelay6() {
        final int delay = 6;
        int startTimeOracle = 4;
        SlotTrajectory oracleTraj = createTrajectory1(startTimeOracle);
        int originalEndTime = oracleTraj.getOriginalArrivalTime();
        //
        int startTime1 = 10;
        SlotTrajectory testTraj1t2 = createTrajectory1(startTime1);
        int traj1originalEndTime = testTraj1t2.getArrivalTimeAtNode("WPT_2");
        //
        assertEquals(originalEndTime, traj1originalEndTime - startTime1 + startTimeOracle);
        //
        FlightParameters oracleParam;
        FlightParameters traj1Param;
        String oracleWPT;
        String traj1WPT;
        // test before recalculation
        for (int i = 0; i < originalEndTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1t2.getParametersAtSimulationTime(i + startTime1 - startTimeOracle);
            traj1WPT = testTraj1t2.getWaypointAimedAtSimulationTime(i + startTime1 - startTimeOracle);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        // recalculation
        testTraj1t2.recalculateTrajectoryWithDelayAt(5, delay);
        // test after recalculation
        for (int i = 0; i < testTraj1t2.getSlotMarker().getStartTime(); i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(0);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(0);
            traj1Param = testTraj1t2.getParametersAtSimulationTime(i);
            traj1WPT = testTraj1t2.getWaypointAimedAtSimulationTime(i);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        for (int i = oracleTraj.getSlotMarker().getStartTime(); i < originalEndTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1t2.getParametersAtSimulationTime(i + startTime1 - startTimeOracle + delay);
            traj1WPT = testTraj1t2.getWaypointAimedAtSimulationTime(i + startTime1 - startTimeOracle + delay);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        // recalculation
        testTraj1t2.recalculateTrajectoryWithDelayAt(12, delay);
        // test after recalculation
        for (int i = 0; i < testTraj1t2.getSlotMarker().getStartTime(); i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(0);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(0);
            traj1Param = testTraj1t2.getParametersAtSimulationTime(i);
            traj1WPT = testTraj1t2.getWaypointAimedAtSimulationTime(i);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        for (int i = oracleTraj.getSlotMarker().getStartTime(); i < originalEndTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1t2.getParametersAtSimulationTime(i + startTime1 - startTimeOracle + 2 * delay);
            traj1WPT = testTraj1t2.getWaypointAimedAtSimulationTime(i + startTime1 - startTimeOracle + 2 * delay);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
    }

    // now you can be delayed when is airborne
//	@Test(expected = IllegalStateException.class)
//	public void testRecalculateTooLate() {
//		int startTime1 = 10;
//		SlotTrajectory testTraj1t2 = createTrajectory1(startTime1);
//		testTraj1t2.recalculateTrajectoryWithDelayAt(startTime1 + 1, 0);
//	}


    @Ignore
    @Test(expected = IllegalStateException.class)
    public void testRecalculateNegativeDelay() {
        int startTime1 = 10;
        SlotTrajectory testTraj1t2 = createTrajectory1(startTime1);
        testTraj1t2.recalculateTrajectoryWithDelayAt(0, -1);
    }

    private static SlotTrajectory createTrajectory1(int startTime) {
        //
        String slotName = "Oracle_Slot";
        //
        SimulatedSlotMarker slotMarker;
        SlotTrajectory trajectory;
        FlightPlan flightPlan;
        //
        Airport airport = new Airport("Dep_airport", 40.9, -71.1);
        Runway runway = new Runway("R1", airport, 12);
        airport.addRunway(runway);
        Waypoint wpt0 = new Waypoint("WPT_0", 40.8, -71);
        Waypoint wpt1 = new Waypoint("WPT_1", 40.2, -70);
        Waypoint wpt2 = new Waypoint("WPT_2", 40, -70);
        //
        FlightSegment takeOff = new FlightSegment("dep->0", slotName, runway, wpt0, 12000, 300);
        FlightSegment segment0 = new FlightSegment("0->1", slotName, wpt0, wpt1, 12000, 300);
        FlightSegment segment1 = new FlightSegment("1->2", slotName, wpt1, wpt2, 10000, 250);
        //
        flightPlan = new FlightPlan("oracleFPL");
        flightPlan.addSegment(takeOff);
        flightPlan.addSegment(segment0);
        flightPlan.addSegment(segment1);
        flightPlan.setInitialSegment(takeOff);
        flightPlan.setDepartureRunway(runway);
        //
        slotMarker = new SimulatedSlotMarker(slotName, new Position(40, -70, 10000), 250, 0, 180, startTime, startTime, Constants.NO_STARTED);
        slotMarker.setFlightPlan(flightPlan);
        //
//		trajectory = new SlotTrajectory(slotMarker, DEFAULT_SIM_DURATION);
//		return trajectory;
        throw new UnsupportedOperationException();
    }

}
