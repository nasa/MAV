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

package gov.nasa.arc.atc.atc2dviz;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

/**
 * @author ahamon
 */
public final class HighlightManager {

    /**
     * The default highlight color
     */
    public static final Color HIGHLIGHT_COLOR = Color.CHARTREUSE;

    private static final Map<String, Slot2D> SLOTS = new HashMap<>();
    private static final Map<String, Aircraft2D> AIRCRAFTS = new HashMap<>();
    private static final Map<String, Waypoint2D> WAYPOINTS = new HashMap<>();

    public static final Color WHITE_COLOR = Color.WHITE;

    private HighlightManager() {
        // private utility constructor
    }

    /**
     * @param slot2d {@link Slot2D} to register for potential highlight
     */
    public static void registerSlot2D(Slot2D slot2d) {
        SLOTS.put(slot2d.getSlotName(), slot2d);
    }

    /**
     * @param aircraft2d {@link Aircraft2D} to register for potential highlight
     */
    public static void registerAircraft2D(Aircraft2D aircraft2d) {
        AIRCRAFTS.put(aircraft2d.getAircraftName(), aircraft2d);
    }

    /**
     * @param slotName  the slot's name
     * @param highlight the new highlight property value
     */
    public static void highlightSlot(String slotName, boolean highlight) {
        Slot2D slot2d = SLOTS.get(slotName);
        if (slot2d != null) {
            slot2d.setHighlighted(highlight);
        }
    }

    /**
     * @param aircraftName the aircraft's name
     * @param highlight    the new highlight property value
     */
    public static void highlightAircraft(String aircraftName, boolean highlight) {
        Aircraft2D aircraft2d = AIRCRAFTS.get(aircraftName);
        if (aircraft2d != null) {
            aircraft2d.setHighlighted(highlight);
        }
    }

    /**
     * @param waypointName the waypoints's name
     * @param highlight    the new highlight property value
     */
    public static void highlightWaypoint(String waypointName, boolean highlight) {
        Waypoint2D waypoint2d = WAYPOINTS.get(waypointName);
        if (waypoint2d != null) {
            waypoint2d.setHighlighted(highlight);
        }
    }

    public static void registerWaypoint2D(Waypoint2D waypoint) {
        WAYPOINTS.put(waypoint.getName(), waypoint);
    }

}
