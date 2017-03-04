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

package gov.nasa.arc.brahms.atmjava.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static gov.nasa.arc.atc.brahms.parsers.BrahmsModelsDefaultAttributes.*;

import gov.nasa.arc.atc.brahms.parsers.BrahmsModelsDefaultAttributes;
import gov.nasa.arc.atc.parsers.WeatherConditionParser;
import gov.nasa.arc.atc.physics.WeatherConditions;
import gov.nasa.arc.atc.tmpsimplification.IsDepartureDebug;
import gov.nasa.arc.atc.utils.XMLConstants;
import gov.nasa.arc.brahms.vm.api.exceptions.ExternalException;
import org.apache.log4j.Logger;

import gov.nasa.arc.atc.BearingIndicator;
import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.airborne.impl.AFOImpl;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.export.AfoUpdateLogger;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Runway;
import gov.nasa.arc.atc.geography.Waypoint;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.utils.AfoUtils;
import gov.nasa.arc.brahms.vm.api.JAPI;
import gov.nasa.arc.brahms.vm.api.common.IAgent;
import gov.nasa.arc.brahms.vm.api.common.IConcept;
import gov.nasa.arc.brahms.vm.api.common.IContext;
import gov.nasa.arc.brahms.vm.api.common.IObject;

/**
 * Synchronization class between Brahms' world and Java's world
 */
public class Utils {

    public static final String WEATHER_ENV_VARIABLE = "WEATHER_FILE";

    // TODO: use proper constants from correct packages

    //


    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getGlobal();

    private static volatile Utils instance = new Utils();

    private static SimulationContext context = null;


    protected Map<String, AFO> afoCache;
    protected List<AFO> deptCache;
    protected int lastArrivalTime = -17;
    protected double topETA = Double.MAX_VALUE;
    protected int landedPlanes = 0;

    private Utils() {
        afoCache = new HashMap<>();
        deptCache = new ArrayList<>();
    }

    public static Utils getInstance() {
        return instance;
    }

    public void setLastArrivalTime(int time) {
        Utils.getInstance().lastArrivalTime = time;
    }

    public int getLastArrivalTime() {
        return Utils.getInstance().lastArrivalTime;
    }

    /**
     * adding an afo to the cache
     *
     * @param iAFO            the AFO to be added to the cache
     * @param brahmsAgentName the afo's agent name
     */
    public void addAFO(AFO iAFO, String brahmsAgentName) {
        boolean containsAgent = afoCache.containsKey(brahmsAgentName);
        LOG.log(Level.FINE, "addAFO before add, containsAgent={0}", containsAgent);
        // ensures that the cache does NOT contain the agent before adding the agent to it
        assert !containsAgent;

        // adding the agent to the cache
        afoCache.put(brahmsAgentName, iAFO);
        containsAgent = afoCache.containsKey(brahmsAgentName);
        LOG.log(Level.FINE, "addAFO after add, containsAgent={0}", containsAgent);

        // ensures that the cache DOES contain the agent before adding the agent to it
        assert containsAgent;
    }

    /**
     * the afo instance controlled by an agent, and stored in the cache
     *
     * @param brahmsAgentName the agent's name controlling the afo
     * @return the afo controled by the agent
     */
    public AFO getAFO(String brahmsAgentName) {
        boolean containsAgent = afoCache.containsKey(brahmsAgentName);
        LOG.log(Level.FINE, "containsAgent={0}", containsAgent);
        assert containsAgent;
        return afoCache.get(brahmsAgentName);
    }

    public static int getLandedPlanes() {
        Utils instance = Utils.getInstance();
        synchronized (instance) {
            instance.landedPlanes = 0;
            for (String name : instance.afoCache.keySet()) {
                AFO afoObject = instance.afoCache.get(name);
                if (!IsDepartureDebug.isDeparture(afoObject) && afoObject.getStatus() == 3) {
                    //TODO: check impact of !afoObject.isDeparture() &&
                    instance.landedPlanes++;
                }
            }
            return instance.landedPlanes;
        }
    }

    public static void dumpAFO(Logger logger, AFO iAFO, String brahmsAgentName) {
        String sHeading = iAFO.getHeadingForDisplay();
        String wayPoint = iAFO.getFlightPlan().getCurrentSegment().getToWaypoint().getName();

        assert (iAFO.getController() instanceof IConcept);
        IConcept controller = (IConcept) iAFO.getController();
        //
        // TODO: use a stringbuild in a method

        //TODO: find a nicer way to determine type
        String afoType = AfoUpdateLogger.UNKNOWN_AFO_TYPE;
        if (brahmsAgentName.contains("plane_")) {
            afoType = AfoUpdateLogger.PLANE_UPDATE;
        } else if (brahmsAgentName.contains("slot_")) {
            afoType = AfoUpdateLogger.SLOT_UPDATE;
        }

        //TODO: use the constants in AfoUpdateLogger
        logger.info(
                "\n" +
                        AfoUpdateLogger.AFO_BLOCK_HEADER + " " + afoType +
//		"\n--" + brahmsAgentName + "(" + iAFO.getSimulationTime() + ")-----------------------------" + 
                        "\n   fullName          " + brahmsAgentName +
                        "\n   AFO               " + iAFO.getName() +
                        "\n   m_iAirSpeed       " + iAFO.getAirSpeed() +
                        "\n   m_dVerticalSpeed  " + iAFO.getVerticalSpeed() +
                        "\n   m_dLatitude       " + iAFO.getLatitude() +
                        "\n   m_dLongitude      " + iAFO.getLongitude() +
                        "\n   m_dAltitude       " + iAFO.getAltitude() +
                        "\n   m_dBearing        " + iAFO.getBearing() +
                        "\n   m_headingEnum     " + sHeading +
                        "\n   flightPlan        " +
                        "\n   iCurrentSegment   " + iAFO.getFlightPlan().getCurrentSegmentIndex() +
                        "\n   toWaypoint        " + wayPoint +
                        "\n   iStatus           " + iAFO.getStatus() +
                        "\n   m_iTimeStamp      " + iAFO.getSimulationTime() +
                        "\n   startTime         " + iAFO.getStartTime() +
//		"\n   is_departure      " + iAFO.isDeparture() +
                        "\n   ETA               " + iAFO.getETA() +
                        "\n   controller        " + controller.getName() +
                        "\n   is_Metering       " + iAFO.isMetering() +
                        "\n" + AfoUpdateLogger.AFO_BLOCK_FOOTER + "\n");

//		logger.info(
//				"\n"+
//		AfoUpdateLogger.AFO_BLOCK_HEADER+" "+afoType+
//		";" + brahmsAgentName +
//		";" + iAFO.getName() + 
//		";" + iAFO.getAirSpeed() + 
//		";" + iAFO.getVerticalSpeed() + 
//		";" + iAFO.getLatitude() + 
//		";" + iAFO.getLongitude() + 
//		";" + iAFO.getAltitude() + 
//		";" + iAFO.getBearing() + 
//		";" + sHeading + 
//		";" + 
//		";" + iAFO.getFlightPlan().getCurrentSegmentIndex() + 
//		";" + wayPoint + 
//		";" + iAFO.getStatus() + 
//		";" + iAFO.getSimulationTime() + 
//		";" + iAFO.getStartTime() + 
//		";" + iAFO.isDeparture() + 
//		";" + iAFO.getETA() + 
//		";" + controller.getName() + 
//		";" + iAFO.isMetering() +
//		"\n");

    }

    public static void logPlaneClearedToDepart(Logger logger, AFO departure, int time) {
        //TODO: add the data block for it.
        LOG.info(departure.getName() + " cleared to depart at " + time);
        logger.info("\n---Departure_for--- " + departure.getName() + " " + time);
    }

    public static void logPlaneLanded(Logger logger, AFO arrival, int time) {
        //TODO: add the data block for it.
        LOG.info(arrival.getName() + "landed at time " + time);
        logger.info("\n---Landing_for--- " + arrival.getName() + " " + time);
    }

    public static void logDepartureQueue(Logger logger, int time) {
        StringBuilder sb = new StringBuilder();
        List<AFO> departures = getInstance().deptCache;
        // try to keep list order, to validate
        for (int i = 0; i < departures.size(); i++) {
            sb.append(" * dep ").append(i).append(" ").append(departures.get(i).getName()).append("\n");
        }

        logger.info(
                "\n--" + "Start_DepartureQueue" + "(" + time + ")-----------------------------\n" +
                        sb.toString() +
                        "-- End_DepartureQueue\n");
    }

    // [java] --SeparationViolation(859)---------
    // [java] lead_plane plane_SWA1837
    // [java] trail_plane plane_TCF5584
    // [java] time 859
    // [java] separation 1.80849924992

    public static void logSeparation(Logger logger, AFO lead, AFO trail, double milesInTrail,
                                     double requiredSep) {
        logger.info(
                "\n--" + "SeparationViolation" + "(" + lead.getSimulationTime() + ")-----------------------------" +
                        "\n  lead_plane     " + lead.getName() +
                        "\n  trail_plane    " + trail.getName() +
                        "\n  time           " + lead.getSimulationTime() +
                        "\n  separation     " + milesInTrail);
    }

    public static AFO initializeAFO(Logger logger, IAgent bAFO, IContext ctx, final int timeIncrement, final int status) {
        try {
            // Attributes with default facts and beliefs
            String name =
                    bAFO.getBeliefAttributeString(bAFO, AFO_NAME, ctx);
            int mIAirSpeed =
                    bAFO.getBeliefAttributeInt(bAFO, M_I_AIRSPEED, ctx);
            double mDLatitude =
                    bAFO.getBeliefAttributeDouble(bAFO, M_D_LATITUDE, ctx);
            double mDLongitude =
                    bAFO.getBeliefAttributeDouble(bAFO, M_D_LONGITUDE, ctx);
            double mDAltitude =
                    bAFO.getBeliefAttributeDouble(bAFO, M_D_ALTITUDE, ctx);
            boolean isDeparture =
                    bAFO.getBeliefAttributeBoolean(bAFO, IS_DEPARTURE, ctx);

            IConcept flightPlan = bAFO.getBeliefAttributeConcept(bAFO, FLIGHT_PLAN, ctx);

            assert (flightPlan instanceof IObject);
            IObject iObjFlightPlan = (IObject) flightPlan;
            FlightPlan iFlightPlan = Utils.constructFlightPlan(name, iObjFlightPlan, isDeparture, ctx);

            int iCurrentSegment = bAFO.getBeliefAttributeInt(bAFO, I_CURRENT_SEGMENT, ctx);
            iFlightPlan.setInitialSegment(iCurrentSegment);
            // not true for generated scenarios
//		assert (iCurrentSegment == 0);

            //neha: the value of the iStatus has to be assigned the value passed in
            //this allows us to switch the status from not flying (status = 0) to
            //flying (status = 1) for arrivals. Should _never_ use getBeliefAttribe here.
            int iStatus = status;

            int startTime =
                    bAFO.getBeliefAttributeInt(bAFO, START_TIME, ctx);

            IConcept controller = bAFO.getBeliefAttributeConcept(bAFO, CONTROLLER_NAME, ctx);

            int tmpHeading = BearingIndicator.NORTH.to_int();

            AFO afoImpl = new AFOImpl(name,
                    (double) mIAirSpeed, 0.0,
                    mDLatitude,
                    mDLongitude,
                    mDAltitude,
                    0.0,
                    tmpHeading,
                    iFlightPlan,
                    iCurrentSegment,
                    iStatus,
                    startTime,
                    startTime,
//			isDeparture,
                    0,
                    (Object) controller,
                    0,
                    timeIncrement,
                    null);

            afoImpl.setVerticalSpeed(AfoUtils.calculateVerticalSpeed(afoImpl, iFlightPlan.getCurrentSegment().getdEndAltitude()));
            //TODO Check if update OK
//	afoImpl.updateBearing();
//	afoImpl.updateETA();
//	afoImpl.updateDTA();

            LOG.log(Level.SEVERE, " INIT AFO {0}", afoImpl);
            LOG.log(Level.SEVERE, " is departure {0}", IsDepartureDebug.isDeparture(afoImpl));

            //
            System.err.println(" ################ INIT AFO " + afoImpl.getName() + " " + IsDepartureDebug.isDeparture(afoImpl));
            System.err.println("  ====> " + afoImpl.getFlightPlan().getFirstWaypoint());
            System.err.println("  ====> " + afoImpl.getFlightPlan().getPath());
            System.err.println("  ====> " + afoImpl.getFlightPlan().getLastWaypoint());

            //TODO: log departure
            if (IsDepartureDebug.isDeparture(afoImpl) &&
                    afoImpl.getStatus() == Constants.IS_FLYING) {
                logPlaneClearedToDepart(logger, afoImpl, afoImpl.getStartTime());
            }


            return afoImpl;
        } catch (ExternalException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void syncrhronizeAFOObject(AFO jAFO, IAgent bAFO, IContext ctx) {
        try {
            // TODO correct typo "AFO O Object"

            // Name is _not_ synchronized (an initial belief and fact in planes and slots)

            bAFO.setBeliefAttributeInt(bAFO, M_I_AIRSPEED,
                    (int) jAFO.getAirSpeed(), ctx);
            JAPI.getWorldState().setFactAttributeInt(bAFO, M_I_AIRSPEED,
                    (int) jAFO.getAirSpeed(), ctx);

            bAFO.setBeliefAttributeDouble(bAFO, M_D_VERTICAL_SPEED,
                    jAFO.getVerticalSpeed(), ctx);
            JAPI.getWorldState().setFactAttributeDouble(bAFO, M_D_VERTICAL_SPEED,
                    jAFO.getVerticalSpeed(), ctx);

            bAFO.setBeliefAttributeDouble(bAFO, M_D_LATITUDE,
                    jAFO.getLatitude(), ctx);
            JAPI.getWorldState().setFactAttributeDouble(bAFO, M_D_LATITUDE,
                    jAFO.getLatitude(), ctx);

            bAFO.setBeliefAttributeDouble(bAFO, M_D_LONGITUDE,
                    jAFO.getLongitude(), ctx);
            JAPI.getWorldState().setFactAttributeDouble(bAFO, M_D_LONGITUDE,
                    jAFO.getLongitude(), ctx);

            bAFO.setBeliefAttributeDouble(bAFO, M_D_ALTITUDE,
                    jAFO.getAltitude(), ctx);
            JAPI.getWorldState().setFactAttributeDouble(bAFO, M_D_ALTITUDE,
                    jAFO.getAltitude(), ctx);

            bAFO.setBeliefAttributeDouble(bAFO, M_D_BEARING,
                    jAFO.getBearing(), ctx);
            JAPI.getWorldState().setFactAttributeDouble(bAFO, M_D_BEARING,
                    jAFO.getBearing(), ctx);

            bAFO.setBeliefAttributeInt(bAFO, M_HEADING_ENUM,
                    jAFO.getBearingIndicator().to_int(), ctx);
            JAPI.getWorldState().setFactAttributeInt(bAFO, M_HEADING_ENUM,
                    jAFO.getBearingIndicator().to_int(), ctx);

            // flightPlan is _not_ synchronized (an intial belief and fact in planes and slots)
            int currentSegment = jAFO.getFlightPlan().getCurrentSegmentIndex();
            bAFO.setBeliefAttributeInt(bAFO, I_CURRENT_SEGMENT,
                    currentSegment, ctx);
            JAPI.getWorldState().setFactAttributeInt(bAFO,
                    I_CURRENT_SEGMENT, currentSegment, ctx);

            //if (jAFO.getStatus() == 0)
            //	jAFO.setStatus(1);

            // setting the belief and fact about the iStatus
            bAFO.setBeliefAttributeInt(bAFO, I_STATUS,
                    jAFO.getStatus(), ctx);
            JAPI.getWorldState().setFactAttributeInt(bAFO, I_STATUS,
                    jAFO.getStatus(), ctx);

            // toWaypoint is not used in AFO, but needs to be synchronized
            // because the currentSegment may change
            IConcept toWaypoint = Utils.getToWayPoint(currentSegment, bAFO, ctx);
            bAFO.setBeliefAttributeConcept(bAFO, TO_WAYPOINT,
                    toWaypoint, ctx);
            JAPI.getWorldState().setFactAttributeConcept(bAFO, TO_WAYPOINT,
                    toWaypoint, ctx);

            bAFO.setBeliefAttributeInt(bAFO, M_I_TIMESTAMP,
                    jAFO.getSimulationTime(), ctx);
            JAPI.getWorldState().setFactAttributeInt(bAFO, M_I_TIMESTAMP,
                    jAFO.getSimulationTime(), ctx);

            bAFO.setBeliefAttributeInt(bAFO, START_TIME,
                    jAFO.getStartTime(), ctx);
            JAPI.getWorldState().setFactAttributeInt(bAFO, START_TIME,
                    jAFO.getStartTime(), ctx);

//		bAFO.setBeliefAttributeBoolean(bAFO, IS_DEPARTURE,
//				jAFO.isDeparture(), ctx);
//		JAPI.getWorldState().setFactAttributeBoolean(bAFO, IS_DEPARTURE,
//				jAFO.isDeparture(), ctx);
            bAFO.setBeliefAttributeBoolean(bAFO, IS_DEPARTURE,
                    IsDepartureDebug.isDeparture(jAFO), ctx);
            JAPI.getWorldState().setFactAttributeBoolean(bAFO, IS_DEPARTURE,
                    IsDepartureDebug.isDeparture(jAFO), ctx);


            bAFO.setBeliefAttributeDouble(bAFO, ETA,
                    jAFO.getETA(), ctx);
            JAPI.getWorldState().setFactAttributeDouble(bAFO, ETA,
                    jAFO.getETA(), ctx);

            bAFO.setBeliefAttributeDouble(bAFO, DTA,
                    AfoUtils.getDTA(jAFO), ctx);
            JAPI.getWorldState().setFactAttributeDouble(bAFO, DTA,
                    AfoUtils.getDTA(jAFO), ctx);

            bAFO.setBeliefAttributeConcept(bAFO, CONTROLLER_NAME, (IConcept)
                    jAFO.getController(), ctx);
            JAPI.getWorldState().setFactAttributeConcept(bAFO, CONTROLLER_NAME,
                    (IConcept) jAFO.getController(), ctx);

            // is_Metering is _not_ synchronized (only used for planes and not controllers)

        } catch (ExternalException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param bAFO
     * @param ctx
     * @param timeIncrement
     * @deprecated use getAFO
     */
    @Deprecated
    public static AFO createAFOObject(IAgent bAFO, IContext ctx, int timeIncrement) {
        try {
            String name =
                    bAFO.getBeliefAttributeString(bAFO, AFO_NAME, ctx);
            int mIAirSpeed =
                    bAFO.getBeliefAttributeInt(bAFO, M_I_AIRSPEED, ctx);
            double mDVerticalSpeed =
                    bAFO.getBeliefAttributeDouble(bAFO, M_D_VERTICAL_SPEED, ctx);
            double mDLatitude =
                    bAFO.getBeliefAttributeDouble(bAFO, M_D_LATITUDE, ctx);
            double mDLongitude =
                    bAFO.getBeliefAttributeDouble(bAFO, M_D_LONGITUDE, ctx);
            double mDAltitude =
                    bAFO.getBeliefAttributeDouble(bAFO, M_D_ALTITUDE, ctx);
            double mDBearing =
                    bAFO.getBeliefAttributeDouble(bAFO, M_D_BEARING, ctx);
            int mHeadingEnum =
                    bAFO.getBeliefAttributeInt(bAFO, M_HEADING_ENUM, ctx);
            boolean isDeparture =
                    bAFO.getBeliefAttributeBoolean(bAFO, IS_DEPARTURE, ctx);

            IConcept flightPlanConcept = bAFO.getBeliefAttributeConcept(bAFO, FLIGHT_PLAN, ctx);
            assert (flightPlanConcept instanceof IObject);
            IObject iObjFlightPlan = (IObject) flightPlanConcept;
            FlightPlan flightPlan = constructFlightPlan(name, iObjFlightPlan, isDeparture, ctx);

            int iCurrentSegment =
                    bAFO.getBeliefAttributeInt(bAFO, I_CURRENT_SEGMENT, ctx);

            // We do not have a notion of an "initial segment" in the Brahms model
            // so this is hard-coded to 0 (the first segment in the list)
            flightPlan.setInitialSegment(0);

            // toWaypoint is not used by AFO, but it will be synchronized on the
            // way out using the flightPlan
            //
            // IConcept toWaypoint =
            // bAFO.getBeliefAttributeConcept(bAFO, "toWaypoint", ctx);

            int iStatus =
                    bAFO.getBeliefAttributeInt(bAFO, I_STATUS, ctx);
            int mITimeStamp =
                    bAFO.getBeliefAttributeInt(bAFO, M_I_TIMESTAMP, ctx);

            // boolean flying
            // The flying attribute is _not_ synchronized from Java. Rather
            // there is a dedicated thought frame in AFO.b that changes the
            // flying attributed dependent on iStatus.

            int startTime =
                    bAFO.getBeliefAttributeInt(bAFO, START_TIME, ctx);

            double eTA = bAFO.getBeliefAttributeDouble(bAFO, ETA, ctx);

            // TODO: add code to update DTA if this is not phased out...
            // should be deprecated though!

            IConcept controller =
                    bAFO.getBeliefAttributeConcept(bAFO, CONTROLLER_NAME, ctx);
            int isMetering = bAFO.getBeliefAttributeInt(bAFO, BrahmsModelsDefaultAttributes.IS_METERING, ctx);

            FlightParameters initialParams = null;

            if (PersistentState.containsInitialParameters(name)) {
                initialParams = PersistentState.getFlightParameters(name);
            }

            AFO afoImpl = new AFOImpl(name,
                    (double) mIAirSpeed,
                    mDVerticalSpeed,
                    mDLatitude,
                    mDLongitude,
                    mDAltitude,
                    mDBearing,
                    mHeadingEnum,
                    flightPlan,
                    iCurrentSegment,
                    iStatus,
                    mITimeStamp,
                    startTime,
//				isDeparture,
                    eTA,
                    (Object) controller,
                    isMetering,
                    timeIncrement,
                    initialParams);

            return afoImpl;
        } catch (ExternalException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static FlightPlan constructFlightPlan(String afoName, IObject flightPlanObj,
                                                 boolean isDeparture, IContext ctx) {
        try {
            Map<Object, Object> flightPlanMap = flightPlanObj.getBeliefAttributeMap(flightPlanObj, "flightPlanMap", true,
                    ctx);
            assert (flightPlanMap != null && "ERROR: flightPlanMap is null" != null);

            FlightPlan flightPlan = new FlightPlan(afoName);
            int counter = 0;
            for (Object itr : flightPlanMap.keySet()) {
                counter++;
                Object flightSegObj = flightPlanMap.get(itr);
                assert (flightSegObj != null && flightSegObj instanceof IObject);
                IObject iObj = (IObject) flightSegObj;
                assert (iObj != null);
                FlightSegment iFlightSeg;
                if (isDeparture && counter == 1) {
                    iFlightSeg = constructFlightSegement(afoName, iObj, ctx, true, isDeparture);
                    flightPlan.addSegment(iFlightSeg);
                    assert (iFlightSeg.getFromWaypoint() instanceof Runway);
                    flightPlan.setDepartureRunway((Runway) iFlightSeg.getFromWaypoint());
                    continue;
                }
                if (!isDeparture && counter == flightPlanMap.size()) {
                    // this is the last flight segement
                    iFlightSeg = constructFlightSegement(afoName, iObj, ctx, true, isDeparture);
                    assert (iFlightSeg.getToWaypoint() instanceof Runway);
                    flightPlan.setArrivalRunway((Runway) iFlightSeg.getToWaypoint());
                } else {
                    iFlightSeg = constructFlightSegement(afoName, iObj, ctx, false, isDeparture);

                }
                flightPlan.addSegment(iFlightSeg);

            }

            return flightPlan;
        } catch (ExternalException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FlightSegment constructFlightSegement(String afoName,
                                                        IObject flightSeg, IContext ctx, boolean isRunway, boolean isDeparture) {
        try {
            IConcept fWaypoint = flightSeg.getBeliefAttributeConcept(flightSeg, "fromWaypoint", ctx);
            IConcept tWaypoint = flightSeg.getBeliefAttributeConcept(flightSeg, TO_WAYPOINT, ctx);
            assert (fWaypoint != null && tWaypoint != null);

            ATCNode fromWaypoint;
            if (isDeparture && isRunway)
                fromWaypoint = constructRunway((IObject) fWaypoint, ctx);
            else
                fromWaypoint = constructWaypoint((IObject) fWaypoint, ctx);

            ATCNode toWaypoint;
            if (!isDeparture && isRunway)
                toWaypoint = constructRunway((IObject) tWaypoint, ctx);
            else
                toWaypoint = constructWaypoint((IObject) tWaypoint, ctx);

            String flightSegName = fromWaypoint.getName() + "_TO_" + toWaypoint.getName() + "_" + afoName;
            double dEndAltitude = flightSeg.getBeliefAttributeDouble(flightSeg, "end_altitude", ctx);
            double iEndSpeed = (int) flightSeg.getBeliefAttributeDouble(flightSeg, "end_speed", ctx);
            return new FlightSegment(flightSegName, afoName, fromWaypoint, toWaypoint, dEndAltitude, iEndSpeed);
        } catch (ExternalException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ATCNode constructWaypoint(IObject wpObj, IContext ctx) {
        double latitude = 0.0;
        double longitude = 0.0;
        String name = "";
        try {
            if (wpObj.hasBeliefAttributeAnyValue(wpObj, XMLConstants.LATITUDE_ATTRIBUTE, true, ctx)) {
                latitude = wpObj.getBeliefAttributeDouble(wpObj, XMLConstants.LATITUDE_ATTRIBUTE, ctx);
            }
            if (wpObj.hasBeliefAttributeAnyValue(wpObj, XMLConstants.LONGITUDE_ATTRIBUTE, true, ctx)) {
                longitude = wpObj.getBeliefAttributeDouble(wpObj, XMLConstants.LONGITUDE_ATTRIBUTE, ctx);
            }
            if (wpObj.hasBeliefAttributeAnyValue(wpObj, NAME, true, ctx)) {
                name = wpObj.getBeliefAttributeString(wpObj, NAME, ctx);
            }
            return new Waypoint(name, latitude, longitude);
        } catch (ExternalException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ATCNode constructRunway(IObject wpObj, IContext ctx) {
        double latitude = 0.0;
        double longitude = 0.0;
        String name = "";
        try {
            if (wpObj.hasBeliefAttributeAnyValue(wpObj, XMLConstants.LATITUDE_ATTRIBUTE, true, ctx)) {
                latitude = wpObj.getBeliefAttributeDouble(wpObj, XMLConstants.LATITUDE_ATTRIBUTE, ctx);
            }

            if (wpObj.hasBeliefAttributeAnyValue(wpObj, XMLConstants.LONGITUDE_ATTRIBUTE, true, ctx)) {
                longitude = wpObj.getBeliefAttributeDouble(wpObj, XMLConstants.LONGITUDE_ATTRIBUTE, ctx);
            }

            if (wpObj.hasBeliefAttributeAnyValue(wpObj, NAME, true, ctx)) {
                name = wpObj.getBeliefAttributeString(wpObj, NAME, ctx);
            }

            // TODO remove hard coded constant for arrival
            Airport airport = new Airport("LGA", latitude, longitude);
            return new Runway(name, airport, 22);
        } catch (ExternalException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AFO createAFOObject(IAgent bAFO, IContext ctx) {
        return createAFOObject(bAFO, ctx, 1);
    }

    public static Map<Object, Object> readSomeTimeLineMapData(IObject iTSS,
                                                              IContext ctx, Map<AFO, IAgent> agentMap,
                                                              List<AFO> lstOfAgents, String attributeName) {
        try {
            if (!iTSS.hasBeliefAttributeAnyValue(iTSS, attributeName, true, ctx))
                return new HashMap<>();

            Utils instance = Utils.getInstance();
            synchronized (instance) {
                Map<Object, Object> someTimeLine = iTSS.getBeliefAttributeMap(iTSS, attributeName, true, ctx);
                for (Object index : someTimeLine.keySet()) {
                    assert (someTimeLine.get(index) instanceof IAgent);
                    IAgent islotAFO = (IAgent) someTimeLine.get(index);

                    // AFO afoObject = Utils.createAFOObject(i_slotAFO, ctx);
                    String name = islotAFO.getName();
                    AFO afoObject = instance.getAFO(name);
                    assert (afoObject.getFlightPlan().getCurrentSegmentIndex() >= 0);
                    assert (afoObject.getStatus() != 0);
//				if (afoObject.isDeparture())
//					afoObject.setTrafficType(TrafficType.DEPARTURE);
//				else
//					afoObject.setTrafficType(TrafficType.ARRIVAL);

                    // Landed planes are not added but
                    // TODO: what about planes that are _not_ flying?
                    if (afoObject.getStatus() == Constants.FINISHED)
                        continue;

                    agentMap.put(afoObject, islotAFO);
                    lstOfAgents.add(afoObject);
                }
                return someTimeLine;
            }
        } catch (ExternalException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IConcept getToWayPoint(int currentSegment, IAgent iAFO, IContext ctx) {
        try {
            IConcept flightPlan = iAFO.getBeliefAttributeConcept(iAFO, FLIGHT_PLAN, ctx);
            boolean isIObject = flightPlan instanceof IObject;
            LOG.log(Level.FINE, "isIObject {0}", isIObject);
            assert isIObject;

            IObject flightPlanObj = (IObject) flightPlan;

            Map<Object, Object> flightPlanMap = flightPlanObj.
                    getBeliefAttributeMap(flightPlanObj, "flightPlanMap", true, ctx);
            assert (flightPlanMap != null && "ERROR: flightPlanMap is null" != null);

            if (flightPlanMap.size() < currentSegment) {
                currentSegment -= 1;
            }

            Integer iCurrentSegment = new Integer(currentSegment);
            boolean containsSegment = flightPlanMap.containsKey(iCurrentSegment);
            LOG.log(Level.FINE, "containsSegment {0}", containsSegment);
            assert containsSegment;

            Object segment = flightPlanMap.get(iCurrentSegment);
            assert (segment instanceof IObject);
            IObject segmentObj = (IObject) segment;
            return segmentObj.getBeliefAttributeConcept(segmentObj, TO_WAYPOINT, ctx);
        } catch (ExternalException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void synchronizeBrahmsControllerActions(IAgent bAFO, IContext ctx, AFO afoObject) {
        try {
            int mIAirSpeed =
                    bAFO.getBeliefAttributeInt(bAFO, M_I_AIRSPEED, ctx);
            IConcept controller =
                    bAFO.getBeliefAttributeConcept(bAFO, CONTROLLER_NAME, ctx);
            int isMetering = bAFO.getBeliefAttributeInt(bAFO, BrahmsModelsDefaultAttributes.IS_METERING, ctx);
            //
            afoObject.setAirSpeed(mIAirSpeed);
            afoObject.setController(controller);
            afoObject.setMetering(isMetering);
        } catch (ExternalException e) {
            e.printStackTrace();
        }
    }

	/*
     * Part to cache simulation context (geography, simulated trajectories for the slots)
	 */

    public static SimulationContext getContext() {
        return context;
    }

    /**
     * Creates the Java context necessary for the scheduling algorithms
     *
     * @param slots the slots objects representing the agents in the simulation
     * @return the {@link SimulationContext} corresponding to the simulation in progress
     */
    public static SimulationContext initializeContext(final List<AFO> slots) {

        // create geography
        ATCGeography geography = new ATCGeography("");
        //// retrieve all waypoints

        // arrivals departures
        Map<AFO, SimulatedSlotMarker> arrivals = new HashMap<>();
        List<AFO> departures = new ArrayList<>();

        // sort departures and arrivals
        slots.forEach(slot -> sortSlots(slot, arrivals, departures, geography));

        // update atc geography to add waypoints, airports...
        slots.forEach(afo -> updateGeography(afo, geography));

        if (geography.getAirports().isEmpty()) {
            LOG.log(Level.SEVERE, "there are no airports initialized");
            throw new IllegalStateException("no airport found when initializing context");
        }

        // retrieve the weather file
        String weatherPath = System.getenv(WEATHER_ENV_VARIABLE);


        // create context
        if (weatherPath != null) {
            WeatherConditions weatherConditions = WeatherConditionParser.parseWeatherConditions(weatherPath);
            if (weatherConditions == null) {
                throw new IllegalStateException("Weather parsed in null for " + weatherPath);
            }
            context = new SimulationContext(geography, weatherConditions);
        } else {
            context = new SimulationContext(geography);
        }

        if (context.getLandings().isEmpty()) {
            LOG.log(Level.SEVERE, "the size of landings is {0}", context.getLandings().size());
            throw new IllegalStateException("no landings sequences found when initializing context");
        }
        // TODO: find a way to get the simulation duration
        context.setSimulationDuration(Integer.MAX_VALUE);

        // add simulated slots to the context
        List<SimulatedSlotMarker> simulatedSlotMarkers = new ArrayList<>();
        simulatedSlotMarkers.addAll(arrivals.values());
        context.addSlots(simulatedSlotMarkers);

        // calculate ref trajectories
        context.calculateReferenceTrajectories();
        //
        LOG.log(Level.INFO, "@@@@@@@ End of context init");
        context.getAllArrivals().keySet().forEach(name -> LOG.log(Level.INFO, " arrival node :: " + name));

        return context;
    }

    public static void logHandOff() {

    }

    private static void sortSlots(AFO slot, Map<AFO, SimulatedSlotMarker> arrivals, List<AFO> departures, ATCGeography geography) {
//		switch (slot.getTrafficType()) {
//		case ARRIVAL:
//			// creating corresponding simulatedSlotMarker
//			SimulatedSlotMarker simulatedSlotMarker = new SimulatedSlotMarker(slot);
//			arrivals.put(slot, simulatedSlotMarker);
//			geography.addArrivalNode(slot.getFlightPlan().getLastWaypoint());
//			break;
//		case DEPARTURE:
//			// not needed for TSS
//			departures.add(slot);
//			geography.addDepartureNode(slot.getFlightPlan().getFirstWaypoint());
//			break;
//		case TRANSIT:
//			System.err.println(" && UTILS :: sortSlots >> transit : " + slot.getName());
//			// TODO handle this
//			break;
//		default:
//			throw new IllegalStateException(" slot type not handled ::" + slot);
//		}
        if (IsDepartureDebug.isDeparture(slot)) {
            departures.add(slot);
            geography.addDepartureNode(slot.getFlightPlan().getFirstWaypoint());
        } else if (IsDepartureDebug.isDArrival(slot)) {
            // creating corresponding simulatedSlotMarker
            SimulatedSlotMarker simulatedSlotMarker = new SimulatedSlotMarker(slot);
            arrivals.put(slot, simulatedSlotMarker);
            geography.addArrivalNode(slot.getFlightPlan().getLastWaypoint());
        } else {
            throw new IllegalStateException(" slot type not handled ::" + slot);
        }
    }

    private static void updateGeography(AFO afo, ATCGeography geography) {
        afo.getFlightPlan().getPath().forEach(flightSegment -> {
            // not so nice...
            // from node
            addNodeToGeography(flightSegment.getFromWaypoint(), geography);
            // to node
            addNodeToGeography(flightSegment.getToWaypoint(), geography);
            // add segment to geography
            geography.addSegment(flightSegment);
        });
    }

    private static void addNodeToGeography(ATCNode node, ATCGeography geography) {
        if (node instanceof Runway) {
            Runway runway = (Runway) node;
            Airport airport = runway.getAirport();
            boolean containsRunway = airport.getRunways().contains(runway);
            if (!containsRunway) {
                airport.addRunway(runway);
            }
            containsRunway = airport.getRunways().contains(runway);
            LOG.log(Level.FINER, "Airport contains runway? -> {0}", containsRunway);
            assert containsRunway;
            geography.addAirport(airport);
            return;
        }
        if (node instanceof Waypoint) {
            geography.addWaypoint((Waypoint) node);
        }
    }


}
