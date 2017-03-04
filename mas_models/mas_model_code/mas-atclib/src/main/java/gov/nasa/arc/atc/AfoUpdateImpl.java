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

package gov.nasa.arc.atc;

import gov.nasa.arc.atc.geography.Position;

/**
 * @author ahamon
 */
public class AfoUpdateImpl implements AfoUpdate {

    private final String afoName;
    private final int timeStamp;

    private final Position position;

    private final double airSpeed;
    private final double vSpeed;
    private final double heading;
    private final int currentSegment;
    private final String toWaypoint;
    private final int status;
    private final int startTime;
    private final int departureTime;
    private final double eta;
    private final String controller;
    private final int isMetering;

    /**
     * @param afoName the afo name
     * @param timeStamp the time the update is taken at
     * @param position the afo position
     * @param airSpeed the afo airspeed in [kt]
     * @param vSpeed the afo vertical speed in [ft/s]
     * @param heading the afo heading in [degree]
     * @param currentSegment the segment index the afo is flying
     * @param toWaypoint the waypoint the afo is flying towards
     * @param status the afo status
     * @param startTime the afo start time in the simulation
     * @param departureTime the afo departure time
     * @param eta the afo ETA
     * @param controller the name of the controller controlling the afo
     * @param isMetering the metering status of the afo
     */
    public AfoUpdateImpl(String afoName, int timeStamp, Position position, double airSpeed, double vSpeed, double heading, int currentSegment, String toWaypoint, int status, int startTime, int departureTime, double eta, String controller, int isMetering) {
        super();
        this.afoName = afoName;
        this.timeStamp = timeStamp;
        this.position = position;
        this.airSpeed = airSpeed;
        this.vSpeed = vSpeed;
        this.heading = heading;
        this.currentSegment = currentSegment;
        this.toWaypoint = toWaypoint;
        this.status = status;
        this.startTime = startTime;
        this.departureTime = departureTime;
        this.eta = eta;
        this.controller = controller;
        this.isMetering = isMetering;
    }

    @Deprecated
    public AfoUpdateImpl(String afoName, int timeStamp, Position position, double airSpeed, double vSpeed, double heading, int currentSegment, String toWaypoint, int status, int startTime, double eta, String controller, int isMetering) {
        this(afoName, timeStamp, position, airSpeed, vSpeed, heading, currentSegment, toWaypoint, status, startTime, startTime, eta, controller, isMetering);
    }

    @Override
    public String getAfoName() {
        return afoName;
    }

    @Override
    public int getTimeStamp() {
        return timeStamp;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public double getAirSpeed() {
        return airSpeed;
    }

    @Override
    public double getVSpeed() {
        return vSpeed;
    }

    @Override
    public double getHeading() {
        return heading;
    }

    @Override
    public int getCurrentSegment() {
        return currentSegment;
    }

    @Override
    public String getToWaypoint() {
        return toWaypoint;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public int getStartTime() {
        return startTime;
    }

    @Override
    public int getDepartureTime() {
        return departureTime;
    }

    @Override
    public double getEta() {
        return eta;
    }

    @Override
    public String getController() {
        return controller;
    }

    @Override
    public int isMetering() {
        return isMetering;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AfoUpdateImpl)) return false;

        AfoUpdateImpl afoUpdate = (AfoUpdateImpl) o;

        if (timeStamp != afoUpdate.timeStamp) return false;
        if (Double.compare(afoUpdate.airSpeed, airSpeed) != 0) return false;
        if (Double.compare(afoUpdate.vSpeed, vSpeed) != 0) return false;
        if (Double.compare(afoUpdate.heading, heading) != 0) return false;
        if (currentSegment != afoUpdate.currentSegment) return false;
        if (status != afoUpdate.status) return false;
        if (startTime != afoUpdate.startTime) return false;
        if (departureTime != afoUpdate.departureTime) return false;
        if (Double.compare(afoUpdate.eta, eta) != 0) return false;
        if (isMetering != afoUpdate.isMetering) return false;
        if (!afoName.equals(afoUpdate.afoName)) return false;
        if (!position.equals(afoUpdate.position)) return false;
        if (toWaypoint != null && !toWaypoint.equals(afoUpdate.toWaypoint)) return false;
        return controller.equals(afoUpdate.controller);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = afoName.hashCode();
        result = 31 * result + timeStamp;
        result = 31 * result + position.hashCode();
        temp = Double.doubleToLongBits(airSpeed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(vSpeed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(heading);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + currentSegment;
        result = 31 * result + (toWaypoint != null ? toWaypoint.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + startTime;
        result = 31 * result + departureTime;
        temp = Double.doubleToLongBits(eta);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + controller.hashCode();
        result = 31 * result + isMetering;
        return result;
    }
}
