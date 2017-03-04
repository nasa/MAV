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

package gov.nasa.arc.atc.geography;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.utils.CalculationTools;

/**
 * @author ahamon
 */
public class Segment {

    private static final Logger LOG = Logger.getGlobal();

    private final ATCNode fromNode;
    private final ATCNode toNode;
    private final double dSegmentDistance;


    /**
     * @param newFromNode the segment start node
     * @param newToNode   the segment end node
     */
    public Segment(ATCNode newFromNode, ATCNode newToNode) {
        fromNode = newFromNode;
        toNode = newToNode;
        dSegmentDistance = calculateDistanceOfSegment();
        LOG.log(Level.FINE, "Created Flight Segment {0}", Segment.this);
    }

    public ATCNode getFromWaypoint() {
        return fromNode;
    }

    public ATCNode getToWaypoint() {
        return toNode;
    }

    public double getSegmentDistance() {
        return dSegmentDistance;
    }

    /**
     * CalculateDistanceOfSegment. Method for calculating the distance of the
     * segment.
     *
     * @return The distance of the segment in Nautical Miles.
     */
    private double calculateDistanceOfSegment() {
        // TODO: take into account the altitude difference !!!
        // LOG.log(Level.WARNING, "TODO: take into account the altitude
        // difference in the calculateDistanceOfSegment!!!");
        double dToLat = getToWaypoint().getLatitude();
        double dToLong = getToWaypoint().getLongitude();
        double dFromLat = getFromWaypoint().getLatitude();
        double dFromLong = getFromWaypoint().getLongitude();
        return CalculationTools.distanceFromTo(dFromLat, dFromLong, dToLat, dToLong);
    }

    public boolean hasSameEdges(Segment segment) {
        return segment.getFromWaypoint().getName().equals(fromNode.getName())
                && segment.getToWaypoint().getName().equals(toNode.getName());
    }

    @Override
    public String toString() {
        return "Segment from: " + getFromWaypoint() + " -> To: " + getToWaypoint();
    }
}
