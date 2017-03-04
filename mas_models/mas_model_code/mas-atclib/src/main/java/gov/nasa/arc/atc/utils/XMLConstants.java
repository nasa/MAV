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

package gov.nasa.arc.atc.utils;

/**
 * @author ahamon
 */
public final class XMLConstants {


    // XML Elements
    public static final String ROOT_ELEMENT = "MODEL_DATA";
    public static final String META_DATA_ELEMENT = "META_DATA";
    public static final String AGENTS_GROUP_ELEMENT = "AGENTS";
    public static final String AGENT_ELEMENT = "AGENT";
    public static final String BELIEF_UPDATES_GROUP_ELEMENT = "BELIEF_UPDATES";
    public static final String BELIEF_UPDATE_ELEMENT = "BELIEF_UPDATE";
    public static final String DEPARTURE_ELEMENT = "DEPARTURE";
    public static final String WORLD_GEOGRAPHY_ELEMENT = "WORLD_GEOGRAPHY";
    public static final String WAYPOINT_GEOGRAPHY_ELEMENT = "WAYPOINT_GEOGRAPHY";
    public static final String WAYPOINT_ELEMENT = "WAYPOINT";
    public static final String DURATION_ELEMENT = "DURATION";
    public static final String TIME_STEPS_ELEMENT = "TIME_STEPS";
    public static final String FACTS_ELEMENT = "FACTS";
    public static final String EVENTS_ELEMENT = "EVENTS";

    // XML attributes

    public static final String ATTRIBUTE_ATTRIBUTE = "attribute";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String ORDER_ATTRIBUTE = "order";
    public static final String PLANE_ATTRIBUTE = "plane";
    public static final String REFERENCE_ATTRIBUTE = "reference";
    public static final String TIME_ATTRIBUTE = "time";
    public static final String VALUE_ATTRIBUTE = "value";

    public static final String LATITUDE_ATTRIBUTE = "latitude";
    public static final String LONGITUDE_ATTRIBUTE = "longitude";

    public static final String START_ATTRIBUTE = "start";
    public static final String END_ATTRIBUTE = "end";
    public static final String STEPS_ATTRIBUTE = "steps";

    //Prefixes
    public static final String SLOT_PREFIX = "slot_";
    // dep
    public static final String DEPARTURE_PREFIX = "plane_";
    public static final String TRANSIT_PREFIX = "tr_";

    private XMLConstants() {
        // private constructor for utility class
    }


}
