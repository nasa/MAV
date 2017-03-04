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

import gov.nasa.arc.atc.geography.Position;
import gov.nasa.arc.atc.utils.Constants;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Waypoint;
import gov.nasa.arc.atc.utils.ConsoleUtils;

/**
 * @author ahamon
 */
public class SimulatedSlotTrajectoryTest {

    private static final double DELTA_PRECISION = 0.05;
    private static final int DEFAULT_SIM_DURATION = 1200;

    private static Waypoint wpt0 = new Waypoint("WPT_0", 40.8, -71);
    private static Waypoint wpt1 = new Waypoint("WPT_1", 40.2, -70);
    private static Waypoint wpt2 = new Waypoint("WPT_2", 30, -70);

    @BeforeClass
    public static void setupClass() {
        ConsoleUtils.setLoggingLevel(Level.FINE);
    }

    //Are ignored since should be covered by the SlotTrajectory tests in gov.nasa.arc.atc.simulation.SlotTrajectoryTest

    @Ignore
    @Test
    public void testRecalculateRemainOnSegmentAt0Delay0() {
        SlotTrajectory oracleTraj = createTrajectory1(0);
        int oracleArrivalTime = oracleTraj.getArrivalTimeAtNode("WPT_2");
        //
        int startTime1 = 10;
        SlotTrajectory testTraj1 = createTrajectory1(startTime1);
        int traj1ArrivalTime = testTraj1.getArrivalTimeAtNode("WPT_2");
        //
        assertEquals(oracleArrivalTime, traj1ArrivalTime - startTime1);
        //
        FlightParameters oracleParam;
        FlightParameters traj1Param;
        String oracleWPT;
        String traj1WPT;
        // for (int i = 0; i < oracleArrivalTime; i++) {
        // oracleParam = oracleTraj.getParametersAtSimulationTime(i);
        // System.err.println("ORACLE :: "+oracleParam);
        // }
        //
        // for (int i = 0; i < oracleArrivalTime; i++) {
        // traj1Param = testTraj1.getParametersAtSimulationTime(i + startTime1);
        // System.err.println("TRAJ :: "+traj1Param);
        // }

        // test before recalculation
        for (int i = 0; i < oracleArrivalTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1.getParametersAtSimulationTime(i + startTime1);
            traj1WPT = testTraj1.getWaypointAimedAtSimulationTime(i + startTime1);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        // recalculation
        testTraj1.recalculateTrajectoryWithDelayAt(0, 0);
        //
//        traj1Param = testTraj1.getParametersAtSimulationTime(+startTime1);
        // System.err.println("TRAJ 2 :: "+traj1Param);
        // for (int i = 0; i < 21; i++) {
        // traj1Param = testTraj1.getParametersAtSimulationTime(i);
        // System.err.println("TRAJ 2 :: "+traj1Param);
        // }
        // test after recalculation
        for (int i = 0; i < oracleArrivalTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1.getParametersAtSimulationTime(i + startTime1);
            traj1WPT = testTraj1.getWaypointAimedAtSimulationTime(i + startTime1);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
    }


    @Ignore
    @Test
    public void testRecalculateRemainOnSegmentAt1000Delay800() {
        SlotTrajectory oracleTraj = createTrajectory1(0);
        int oracleArrivalTime = oracleTraj.getArrivalTimeAtNode("WPT_2");
        //
        int startTime1 = 10;
        SlotTrajectory testTraj1 = createTrajectory1(startTime1);
        int traj1ArrivalTime = testTraj1.getArrivalTimeAtNode("WPT_2");
        //
        assertEquals(oracleArrivalTime, traj1ArrivalTime - startTime1);
        //
        FlightParameters oracleParam;
        FlightParameters traj1Param;
        String oracleWPT;
        String traj1WPT;
        // test before recalculation
        for (int i = 0; i < oracleArrivalTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1.getParametersAtSimulationTime(i + startTime1);
            traj1WPT = testTraj1.getWaypointAimedAtSimulationTime(i + startTime1);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        //
        int calculStartTime = 811;
        int delay = 800;
        // recalculation
        testTraj1.recalculateTrajectoryWithDelayAt(calculStartTime, delay);
        // test after recalculation
        for (int i = calculStartTime; i < oracleArrivalTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i - delay);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i - delay);
            traj1Param = testTraj1.getParametersAtSimulationTime(i + startTime1);
            traj1WPT = testTraj1.getWaypointAimedAtSimulationTime(i + startTime1);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
    }


    @Ignore
    @Test
    public void testRecalculateRemainOnSegmentAtStartDelay0() {
        SlotTrajectory oracleTraj = createTrajectory1(0);
        int oracleArrivalTime = oracleTraj.getArrivalTimeAtNode("WPT_2");
        //
        int startTime1 = 10;
        SlotTrajectory testTraj1 = createTrajectory1(startTime1);
        int traj1ArrivalTime = testTraj1.getArrivalTimeAtNode("WPT_2");
        //
        assertEquals(oracleArrivalTime, traj1ArrivalTime - startTime1);
        //
        FlightParameters oracleParam;
        FlightParameters traj1Param;
        String oracleWPT;
        String traj1WPT;
        // test before recalculation
        for (int i = 0; i < oracleArrivalTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1.getParametersAtSimulationTime(i + startTime1);
            traj1WPT = testTraj1.getWaypointAimedAtSimulationTime(i + startTime1);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        // recalculation
        testTraj1.recalculateTrajectoryWithDelayAt(startTime1, 0);
        // test after recalculation
        for (int i = 0; i < oracleArrivalTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1.getParametersAtSimulationTime(i + startTime1);
            traj1WPT = testTraj1.getWaypointAimedAtSimulationTime(i + startTime1);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
    }


    @Ignore
    @Test
    public void testRecalculateRemainOnSegmentAt20Delay10() {
        SlotTrajectory oracleTraj = createTrajectory1(0);
        int oracleArrivalTime = oracleTraj.getArrivalTimeAtNode("WPT_2");
        //
        int startTime1 = 10;
        SlotTrajectory testTraj1 = createTrajectory1(startTime1);
        int traj1ArrivalTime = testTraj1.getArrivalTimeAtNode("WPT_2");
        //
        assertEquals(oracleArrivalTime, traj1ArrivalTime - startTime1);
        //
        FlightParameters oracleParam;
        FlightParameters traj1Param;
        String oracleWPT;
        String traj1WPT;
        // test before recalculation
        for (int i = 0; i < oracleArrivalTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1.getParametersAtSimulationTime(i + startTime1);
            traj1WPT = testTraj1.getWaypointAimedAtSimulationTime(i + startTime1);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        int calculStartTime = 30;
        int delay = 10;
        // recalculation
        testTraj1.recalculateTrajectoryWithDelayAt(calculStartTime, delay);
        // test after recalculation
        for (int i = startTime1; i < calculStartTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i - startTime1);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i - startTime1);
            traj1Param = testTraj1.getParametersAtSimulationTime(i);
            traj1WPT = testTraj1.getWaypointAimedAtSimulationTime(i);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        for (int i = calculStartTime; i < oracleArrivalTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i - delay);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i - delay);
            traj1Param = testTraj1.getParametersAtSimulationTime(i + startTime1);
            traj1WPT = testTraj1.getWaypointAimedAtSimulationTime(i + startTime1);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        traj1ArrivalTime = testTraj1.getArrivalTimeAtNode("WPT_2");
        assertEquals(oracleArrivalTime, traj1ArrivalTime - startTime1 - delay);
    }

    @Ignore
    @Test
    public void testRecalculateRemainOnSegmentAt20Delay0() {
        SlotTrajectory oracleTraj = createTrajectory1(0);
        int oracleArrivalTime = oracleTraj.getArrivalTimeAtNode("WPT_2");
        //
        int startTime1 = 10;
        SlotTrajectory testTraj1t2 = createTrajectory1(startTime1);
        int traj1ArrivalTime = testTraj1t2.getArrivalTimeAtNode("WPT_2");
        //
        assertEquals(oracleArrivalTime, traj1ArrivalTime - startTime1);
        //
        FlightParameters oracleParam;
        FlightParameters traj1Param;
        String oracleWPT;
        String traj1WPT;
        // test before recalculation
        for (int i = 0; i < oracleArrivalTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1t2.getParametersAtSimulationTime(i + startTime1);
            traj1WPT = testTraj1t2.getWaypointAimedAtSimulationTime(i + startTime1);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        // recalculation
        testTraj1t2.recalculateTrajectoryWithDelayAt(20, 0);
        // test after recalculation
        for (int i = 0; i < oracleArrivalTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1t2.getParametersAtSimulationTime(i + startTime1);
            traj1WPT = testTraj1t2.getWaypointAimedAtSimulationTime(i + startTime1);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        // recalculation
        testTraj1t2.recalculateTrajectoryWithDelayAt(30, 0);
        // test after recalculation
        for (int i = 0; i < oracleArrivalTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1t2.getParametersAtSimulationTime(i + startTime1);
            traj1WPT = testTraj1t2.getWaypointAimedAtSimulationTime(i + startTime1);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
        // recalculation
        testTraj1t2.recalculateTrajectoryWithDelayAt(158, 0);
        // test after recalculation
        for (int i = 0; i < oracleArrivalTime; i++) {
            oracleParam = oracleTraj.getParametersAtSimulationTime(i);
            oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
            traj1Param = testTraj1t2.getParametersAtSimulationTime(i + startTime1);
            traj1WPT = testTraj1t2.getWaypointAimedAtSimulationTime(i + startTime1);
            assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
            assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
            assertEquals(oracleWPT, traj1WPT);
        }
    }

    private static SlotTrajectory createTrajectory1(int startTime) {
        //
        String slotName = "Oracle_Slot";
        //
        SimulatedSlotMarker slotMarker;
        SlotTrajectory trajectory;
        FlightPlan flightPlan;
        //
        //
        FlightSegment segment0 = new FlightSegment("0->1", slotName, wpt0, wpt1, 12000, 300);
        FlightSegment segment1 = new FlightSegment("1->2", slotName, wpt1, wpt2, 10000, 250);
        //
        flightPlan = new FlightPlan("oracleFPL");
        flightPlan.addSegment(segment0);
        flightPlan.addSegment(segment1);
        flightPlan.setInitialSegment(segment1);
        //
        slotMarker = new SimulatedSlotMarker(slotName, new Position(40, -70, 10000), 250, 0, 180, startTime, startTime, Constants.NO_STARTED);
        slotMarker.setFlightPlan(flightPlan);
        //
//		trajectory = new SlotTrajectory(slotMarker, DEFAULT_SIM_DURATION);
//		return trajectory;
        throw new UnsupportedOperationException();
    }

}
