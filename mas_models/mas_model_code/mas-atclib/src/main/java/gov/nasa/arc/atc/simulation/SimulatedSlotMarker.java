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

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.airborne.impl.SlotMarkerImpl;
import gov.nasa.arc.atc.factories.FlightPlanFactory;
import gov.nasa.arc.atc.geography.Position;

/**
 * @author ahamon
 */
public class SimulatedSlotMarker extends SlotMarkerImpl {

    public SimulatedSlotMarker(String afoName, Position position, double speed, double verticalSpeed, double bearing, int startTime, int departureTime, int status) {
        super(afoName, speed, verticalSpeed, position.getLatitude(), position.getLongitude(), position.getAltitude(), bearing, startTime);
        setStatus(status);
        setDepartureTime(departureTime);
    }

    public SimulatedSlotMarker(AFO slot) {
        this(slot.getName(), slot.getInitialParameters().getPosition(), slot.getInitialParameters().getAirSpeed(), slot.getInitialParameters().getVerticalSpeed(), slot.getInitialParameters().getHeading(), slot.getStartTime(), slot.getDepartureTime(), slot.getInitialParameters().getStatus());
        setFlightPlan(FlightPlanFactory.duplicateFPL(slot.getFlightPlan()));
    }

    @Override
    public String toString() {
        return "SlotMarker: " +
                getName() + " - " +
                getAirSpeed() + " kts, vertical rate:" +
                getVerticalSpeed() + " feet/sec at latitude:" +
                String.format("%.3f", getLatitude()) +
                ", longitude:" + String.format("%.3f", getLongitude()) +
                ", altitude:" + String.format("%.3f", getAltitude()) +
                ", feet heading:" + String.format("%.3f", getBearing()) +
                ", degrees:" + getHeadingForDisplay();
    }

}
