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

package gov.nasa.arc.atc.scenariogen.core;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.AfoUpdateImpl;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Position;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.simulation.SlotTrajectory;
import gov.nasa.arc.atc.simulation.TimedFlightParameters;
import gov.nasa.arc.atc.utils.Constants;

import java.util.List;

/**
 * @author ahamon
 */
public class ScheduledFlightPlan {

    /**
     * in [knot]
     */
    private static final int DEFAULT_TAKE_OFF_SPEED = 160;

    private final String name;
    private final int timeAtDeliveryWPT;
    private final ATCNode deliveryWPT;
    private final RichFlightPlan flightPlan;
    private final FlowType deliveryNodeFlowType;


    public ScheduledFlightPlan(String name, RichFlightPlan flightPlan, int timeAtDeliveryWPT, ATCNode deliveryWPT) {
        this.name = name;
        this.timeAtDeliveryWPT = timeAtDeliveryWPT;
        this.deliveryWPT = deliveryWPT;
        this.flightPlan = flightPlan;
        if (flightPlan.arrivesAt(deliveryWPT.getName())) {
            deliveryNodeFlowType = FlowType.ARRIVAL;
        } else if (flightPlan.departsFrom(deliveryWPT.getName())) {
            deliveryNodeFlowType = FlowType.DEPARTURE;
        } else {
            deliveryNodeFlowType = FlowType.THROUGH;
        }
    }

    public FlowType getDeliveryNodeFlowType() {
        return deliveryNodeFlowType;
    }

    public String getName() {
        return name;
    }

    public int getTimeAtDeliveryWPT() {
        return timeAtDeliveryWPT;
    }


    public RichFlightPlan getFlightPlan() {
        return flightPlan;
    }


    public ScheduledAFO generateAFO(int simulationStartTime, List<ArrivalControllerConf> controllers) {
        if (flightPlan.departsFrom(deliveryWPT.getName())) {
            return generateDepartureAFO(controllers);
        }

        return generateFlyingAFO(simulationStartTime, controllers);

    }

    private ScheduledAFO generateDepartureAFO(List<ArrivalControllerConf> controllers) {
        FlightSegment[] segments = flightPlan.getSegments();
        Position initPosition = new Position(deliveryWPT.getLatitude(), deliveryWPT.getLongitude(), deliveryWPT.getAltitude());
        double eta = flightPlan.getArrivalTime() - timeAtDeliveryWPT;
        String toWaypoint = segments[0].getToWaypoint().getName();
        String controllerName = null;
        for (ArrivalControllerConf controller : controllers) {
            if (controller.getNodes().contains(toWaypoint)) {
                controllerName = controller.getName();
                break;
            }
        }
        if (controllerName == null) {
            throw new IllegalStateException("Could not find controller for " + toWaypoint);
        }
        AfoUpdate initUpdate = new AfoUpdateImpl(name, timeAtDeliveryWPT, initPosition, DEFAULT_TAKE_OFF_SPEED, 0, 0, 0, toWaypoint, Constants.ON_GROUND, timeAtDeliveryWPT, -1, eta, controllerName, 0);

        // no need for copy since getSegments returns a clone already
        return new ScheduledAFO(initUpdate, segments);
    }

    private ScheduledAFO generateFlyingAFO(int simulationStartTime, List<ArrivalControllerConf> controllers) {
        FlightSegment[] segments = flightPlan.getSegments();
        // calculate initial AFOUpdate parameters
        Position initPosition;
        double initSpeed;
        int segmentIndex = -1;
        String toWaypoint;
        int eta;
        String controllerName = null;
        double vSpeed = 0;
        double heading = 0;
        //
        int refTimeAtWPT = flightPlan.getCrossingTime(deliveryWPT.getName());
        int startTime = timeAtDeliveryWPT - refTimeAtWPT;
        if (startTime >= simulationStartTime) {
            initPosition = flightPlan.getInitPosition();
            initSpeed = flightPlan.getInitSpeed();
            segmentIndex = 0;
            toWaypoint = segments[segmentIndex].getToWaypoint().getName();
        } else {
            Position pos = new Position(flightPlan.getInitPosition().getLatitude(), flightPlan.getInitPosition().getLongitude(), flightPlan.getInitPosition().getAltitude());
            SimulatedSlotMarker sMarker = new SimulatedSlotMarker(name, pos,flightPlan.getInitSpeed(), 0, 0, 0, 0, 0);
            FlightPlan simpleFPL = new FlightPlan(flightPlan.getFplName());
            for (FlightSegment s : segments) {
                simpleFPL.addSegment(s);
            }
            simpleFPL.setInitialSegment(0);
            sMarker.setFlightPlan(simpleFPL);
            SlotTrajectory trajectory = new SlotTrajectory(SimulationManager.getATCGeography(),sMarker, Integer.MAX_VALUE);
            TimedFlightParameters timedFlightParameters = trajectory.getParametersAtSimulationTime(simulationStartTime - startTime);
            initPosition = timedFlightParameters.getPosition();
            initSpeed = timedFlightParameters.getAirSpeed();
            vSpeed = timedFlightParameters.getVerticalSpeed();
            heading = timedFlightParameters.getHeading();
            toWaypoint = timedFlightParameters.getToWPT();
            for (int i = 0; i < segments.length; i++) {
                if (toWaypoint.equals(segments[i].getToWaypoint().getName())) {
                    segmentIndex = i;
                }
            }
        }
        eta = flightPlan.getArrivalTime() - Math.max(0, startTime);
        for (ArrivalControllerConf controller : controllers) {
            if (controller.getNodes().contains(toWaypoint)) {
                controllerName = controller.getName();
                break;
            }
        }
        if (controllerName == null) {
            throw new IllegalStateException("Could not find controller for " + toWaypoint);
        }
        if (segmentIndex < 0) {
            throw new IllegalStateException("Could not find segment for " + toWaypoint);
        }

        AfoUpdate initUpdate = new AfoUpdateImpl(name, Math.max(simulationStartTime, startTime), initPosition, initSpeed, vSpeed, heading, 0, toWaypoint, 0, Math.max(simulationStartTime, startTime), -1, eta, controllerName, 0);
        FlightSegment[] flownFPL = new FlightSegment[segments.length - segmentIndex];
        for (int i = 0; i < segments.length - segmentIndex; i++) {
            flownFPL[i] = segments[i + segmentIndex];
        }
        return new ScheduledAFO(initUpdate, flownFPL);
    }


    @Override
    public String toString() {
        return name + " delivered at t=" + timeAtDeliveryWPT + " at " + deliveryWPT;
    }
}
