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

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.geography.*;
import gov.nasa.arc.atc.utils.Constants;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.utils.CustomRecordFormatter;

/**
 * @author ahamon
 */
public class SimulatedSlotTrajectoryTestScenario {

    private static final double DELTA_PRECISION = 0.0001;
    private static final int DEFAULT_SIM_DURATION = 1200;
    private static final Logger LOG = Logger.getGlobal();
    private static final ConsoleHandler CONSOLE_HANDLER = new ConsoleHandler();

    public static final double EGF4314_LAT = 41.55028;
    public static final double EGF4314_LONG = -73.85116;
    public static final double EGF4314_SPEED = 279.88;
    public static final double EGF4314_ALTITUDE = 13670.07;

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


    @Ignore
    @Test
    public void testRecalculateRemainOnSegmentAt0Delay0() {
        SlotTrajectory oracleTraj = createEGF4314(0, EGF4314_LAT, EGF4314_LONG, EGF4314_ALTITUDE, EGF4314_SPEED);
        int oracleArrivalTime = oracleTraj.getArrivalTimeAtNode("LGA22");
        //
        int startTime1 = 1596;
        SlotTrajectory testTraj1 = createEGF4314(startTime1, EGF4314_LAT, EGF4314_LONG, EGF4314_ALTITUDE, EGF4314_SPEED);
        int traj1ArrivalTime = testTraj1.getArrivalTimeAtNode("LGA22");
        //
        System.err.println("FPL=" + oracleTraj.getSlotMarker().getFlightPlan());
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
        System.err.println("oracleArrivalTime=" + oracleArrivalTime);
        traj1ArrivalTime = testTraj1.getArrivalTimeAtNode("LGA22");
        System.err.println("traj1ArrivalTime=" + traj1ArrivalTime);
        int delay = 1475;
        delay = 375;
        int calculationStart = 1596;
        System.err.println("delay=" + delay);
        System.err.println("should arrive at=" + (traj1ArrivalTime + delay));
        CONSOLE_HANDLER.setLevel(Level.INFO);
        // create Dummy Context
        SimulationContext context = Mockito.mock(SimulationContext.class);
        ATCGeography geography = Mockito.mock(ATCGeography.class);
        Mockito.when(context.getGeography()).thenReturn(geography);
        Mockito.when(geography.isADepartureNode(Matchers.<String>any())).thenReturn(false);
        // recalculation
        CONSOLE_HANDLER.setLevel(Level.OFF);
        ReverseSimulationUtils.introduceDelay(context, calculationStart, testTraj1, delay);
        traj1ArrivalTime = testTraj1.getArrivalTimeAtNode("LGA22");
        System.err.println("traj1ArrivalTime=" + traj1ArrivalTime);
        CONSOLE_HANDLER.setLevel(Level.OFF);

        System.err.println("traj1 current segment=" + testTraj1.getSlotMarker().getFlightPlan().getCurrentSegmentIndex());
        System.err.println("traj1 current position=" + testTraj1.getSlotMarker().getInitialParameters());
        //
        // testTraj1.recalculateTrajectoryWithDelayAt(calculationStart, 0);
        // traj1ArrivalTime = testTraj1.getArrivalTimeAtNode("LGA22");
        // System.err.println("traj1ArrivalTime="+traj1ArrivalTime);
        // //
        // System.err.println("traj1 current segment="+testTraj1.getAFO().getFlightPlan().getCurrentSegmentIndex());
        // System.err.println("traj1 current position="+testTraj1.getAFO().getInitialParameters());
        // test after recalculation
        // for (int i = 0; i < oracleArrivalTime; i++) {
        // oracleParam = oracleTraj.getParametersAtSimulationTime(i);
        // oracleWPT = oracleTraj.getWaypointAimedAtSimulationTime(i);
        // traj1Param = testTraj1.getParametersAtSimulationTime(i + startTime1);
        // traj1WPT = testTraj1.getWaypointAimedAtSimulationTime(i + startTime1);
        // assertEquals(oracleParam.getLatitude(), traj1Param.getLatitude(), DELTA_PRECISION);
        // assertEquals(oracleParam.getLongitude(), traj1Param.getLongitude(), DELTA_PRECISION);
        // assertEquals(oracleWPT, traj1WPT);
        // }
    }

    // @Test
    public void testwithFlyBack() {
        SlotTrajectory oracleTraj = createEGF4314(1596, EGF4314_LAT, EGF4314_LONG, EGF4314_ALTITUDE, EGF4314_SPEED);
        int oracleArrivalTime = oracleTraj.getArrivalTimeAtNode("LGA22");
        SlotTrajectory traj = createEGF4314Late(1596, 46.16805809444167, -73.72485963355682, 14640.94, 290.0);
        int trajArrivalTime = traj.getArrivalTimeAtNode("LGA22");
        System.err.println("oracleTraj FPL=" + oracleTraj.getSlotMarker().getFlightPlan());
        System.err.println("traj FPL=" + traj.getSlotMarker().getFlightPlan());
        System.err.println("oracleArrivalTime=" + oracleArrivalTime);
        System.err.println("1475+oracleArrivalTime = " + (1475 + oracleArrivalTime));
        System.err.println("traj1ArrivalTime=" + trajArrivalTime);
        //
        // for(int i = oracleTraj.getAFO().getStartTime(); i<oracleArrivalTime+1;i++){
        // System.err.println("@"+i+" "+oracleTraj.getParametersAtSimulationTime(i));
        // }
        // for(int i = traj.getAFO().getStartTime(); i<trajArrivalTime+1;i++){
        // System.err.println("@"+i+" "+traj.getParametersAtSimulationTime(i));
        // }
    }

    public static SlotTrajectory createEGF4314() {
        return createEGF4314(1596, EGF4314_LAT, EGF4314_LONG, EGF4314_ALTITUDE, EGF4314_SPEED);
    }

    private static SlotTrajectory createEGF4314(int startTime, double latitude, double longitude, double altitude, double speed) {
        // IGN VALRE BASYE FODAK DOSWL ELZEE HAARP FAMMA YOMAN OMAAR GREKO KYLIE LGA22
        String slotName = "EGF4314";
        //
        SimulatedSlotMarker slotMarker;
        FlightPlan flightPlan;
        //
        Waypoint CYUL = new Waypoint("CYUL", 45.47055, -73.74084);
        Waypoint IGN = new Waypoint("IGN", 41.66555, -73.82222);
        Waypoint VALRE = new Waypoint("VALRE", 41.43528, -73.88167);
        Waypoint BASYE = new Waypoint("BASYE", 41.34361, -73.79861);
        Waypoint FODAK = new Waypoint("FODAK", 41.27139, -73.73583);
        Waypoint DOSWL = new Waypoint("DOSWL", 41.23472, -73.70028);
        Waypoint ELZEE = new Waypoint("ELZEE", 41.19278, -73.6625);
        Waypoint HAARP = new Waypoint("HAARP", 41.11639, -73.59361);
        Waypoint FAMMA = new Waypoint("FAMMA", 41.01, -73.68195);
        Waypoint YOMAN = new Waypoint("YOMAN", 40.93694, -73.745);
        Waypoint OMAAR = new Waypoint("OMAAR", 40.90055, -73.77528);
        Waypoint GREKO = new Waypoint("GREKO", 40.86555, -73.80444);
        Waypoint KYLIE = new Waypoint("KYLIE", 40.81361, -73.84722);
        Waypoint LGA22 = new Waypoint("LGA22", 40.78528, -73.87055);
        //
        FlightSegment seg0 = new FlightSegment("Seg_0", slotName, CYUL, IGN, 14640.94, 290);
        FlightSegment seg1 = new FlightSegment("Seg_1", slotName, IGN, VALRE, 11211.41, 280);
        FlightSegment seg2 = new FlightSegment("Seg_2", slotName, VALRE, BASYE, 10000.0, 240);
        FlightSegment seg3 = new FlightSegment("Seg_3", slotName, BASYE, FODAK, 9017.1, 240);
        FlightSegment seg4 = new FlightSegment("Seg_4", slotName, FODAK, DOSWL, 8500.0, 240);
        FlightSegment seg5 = new FlightSegment("Seg_5", slotName, DOSWL, ELZEE, 7760.04, 240);
        FlightSegment seg6 = new FlightSegment("Seg_6", slotName, ELZEE, HAARP, 6462.54, 240);
        FlightSegment seg7 = new FlightSegment("Seg_7", slotName, HAARP, FAMMA, 5100.0, 180);
        FlightSegment seg8 = new FlightSegment("Seg_8", slotName, FAMMA, YOMAN, 3300.0, 180);
        FlightSegment seg9 = new FlightSegment("Seg_9", slotName, YOMAN, OMAAR, 2525.46, 180);
        FlightSegment seg10 = new FlightSegment("Seg_10", slotName, OMAAR, GREKO, 1900.0, 160);
        FlightSegment seg11 = new FlightSegment("Seg_11", slotName, GREKO, KYLIE, 800.0, 160);
        FlightSegment seg12 = new FlightSegment("Seg_12", slotName, KYLIE, LGA22, 21.0, 125);
        //
        flightPlan = new FlightPlan("FPL_EGF4314");
        flightPlan.addSegment(seg0);
        flightPlan.addSegment(seg1);
        flightPlan.addSegment(seg2);
        flightPlan.addSegment(seg3);
        flightPlan.addSegment(seg4);
        flightPlan.addSegment(seg5);
        flightPlan.addSegment(seg6);
        flightPlan.addSegment(seg7);
        flightPlan.addSegment(seg8);
        flightPlan.addSegment(seg9);
        flightPlan.addSegment(seg10);
        flightPlan.addSegment(seg11);
        flightPlan.addSegment(seg12);
        flightPlan.setInitialSegment(seg1);
        //
        slotMarker = new SimulatedSlotMarker(slotName, new Position(latitude, longitude, altitude), speed, 100, 180, startTime, startTime, Constants.NO_STARTED);
        slotMarker.setFlightPlan(flightPlan);
        //
//		return new SlotTrajectory(slotMarker, DEFAULT_SIM_DURATION);
        throw new UnsupportedOperationException();
    }

    private static SlotTrajectory createEGF4314Late(int startTime, double latitude, double longitude, double altitude, double speed) {
        // IGN VALRE BASYE FODAK DOSWL ELZEE HAARP FAMMA YOMAN OMAAR GREKO KYLIE LGA22
        String slotName = "EGF4314";
        //
        SimulatedSlotMarker slotMarker;
        FlightPlan flightPlan;
        //
        Waypoint GHOST = new Waypoint("GHOST", latitude, longitude);
        Waypoint CYUL = new Waypoint("CYUL", 45.47055, -73.74084);
        Waypoint IGN = new Waypoint("IGN", 41.66555, -73.82222);
        Waypoint VALRE = new Waypoint("VALRE", 41.43528, -73.88167);
        Waypoint BASYE = new Waypoint("BASYE", 41.34361, -73.79861);
        Waypoint FODAK = new Waypoint("FODAK", 41.27139, -73.73583);
        Waypoint DOSWL = new Waypoint("DOSWL", 41.23472, -73.70028);
        Waypoint ELZEE = new Waypoint("ELZEE", 41.19278, -73.6625);
        Waypoint HAARP = new Waypoint("HAARP", 41.11639, -73.59361);
        Waypoint FAMMA = new Waypoint("FAMMA", 41.01, -73.68195);
        Waypoint YOMAN = new Waypoint("YOMAN", 40.93694, -73.745);
        Waypoint OMAAR = new Waypoint("OMAAR", 40.90055, -73.77528);
        Waypoint GREKO = new Waypoint("GREKO", 40.86555, -73.80444);
        Waypoint KYLIE = new Waypoint("KYLIE", 40.81361, -73.84722);
        Waypoint LGA22 = new Waypoint("LGA22", 40.78528, -73.87055);
        //
        FlightSegment segGHOST = new FlightSegment("Seg_0", slotName, GHOST, CYUL, 14640.94, (int) speed);
        FlightSegment seg0 = new FlightSegment("Seg_0", slotName, CYUL, IGN, 14640.94, 290);
        FlightSegment seg1 = new FlightSegment("Seg_1", slotName, IGN, VALRE, 11211.41, 280);
        FlightSegment seg2 = new FlightSegment("Seg_2", slotName, VALRE, BASYE, 10000.0, 240);
        FlightSegment seg3 = new FlightSegment("Seg_3", slotName, BASYE, FODAK, 9017.1, 240);
        FlightSegment seg4 = new FlightSegment("Seg_4", slotName, FODAK, DOSWL, 8500.0, 240);
        FlightSegment seg5 = new FlightSegment("Seg_5", slotName, DOSWL, ELZEE, 7760.04, 240);
        FlightSegment seg6 = new FlightSegment("Seg_6", slotName, ELZEE, HAARP, 6462.54, 240);
        FlightSegment seg7 = new FlightSegment("Seg_7", slotName, HAARP, FAMMA, 5100.0, 180);
        FlightSegment seg8 = new FlightSegment("Seg_8", slotName, FAMMA, YOMAN, 3300.0, 180);
        FlightSegment seg9 = new FlightSegment("Seg_9", slotName, YOMAN, OMAAR, 2525.46, 180);
        FlightSegment seg10 = new FlightSegment("Seg_10", slotName, OMAAR, GREKO, 1900.0, 160);
        FlightSegment seg11 = new FlightSegment("Seg_11", slotName, GREKO, KYLIE, 800.0, 160);
        FlightSegment seg12 = new FlightSegment("Seg_12", slotName, KYLIE, LGA22, 21.0, 125);
        //
        flightPlan = new FlightPlan("FPL_EGF4314");
        flightPlan.addSegment(seg0);
        flightPlan.addSegment(seg1);
        flightPlan.addSegment(seg2);
        flightPlan.addSegment(seg3);
        flightPlan.addSegment(seg4);
        flightPlan.addSegment(seg5);
        flightPlan.addSegment(seg6);
        flightPlan.addSegment(seg7);
        flightPlan.addSegment(seg8);
        flightPlan.addSegment(seg9);
        flightPlan.addSegment(seg10);
        flightPlan.addSegment(seg11);
        flightPlan.addSegment(seg12);
        flightPlan.addSegment(segGHOST);
        flightPlan.setInitialSegment(segGHOST);
        //
        slotMarker = new SimulatedSlotMarker(slotName, new Position(latitude, longitude, altitude), speed, 100, 180, startTime, startTime, Constants.NO_STARTED);
        slotMarker.setFlightPlan(flightPlan);
        //
//		return new SlotTrajectory(slotMarker, DEFAULT_SIM_DURATION);
        throw new UnsupportedOperationException();
    }

}
