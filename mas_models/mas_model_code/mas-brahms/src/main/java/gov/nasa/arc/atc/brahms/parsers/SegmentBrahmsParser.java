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

/*
* *******************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.brahms.parsers;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.brahms.BrahmsInstanceInitialConfiguration;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.FlightSegment;

/**
 * @author hamon
 */
public class SegmentBrahmsParser {

    public static final String FROM_WAYPOINT = "fromWaypoint";
    public static final String TO_WAYPOINT = "toWaypoint";
    public static final String END_ALTITUDE = "end_altitude";
    public static final String END_SPEED = "end_speed";

    private static final Logger LOG = Logger.getGlobal();

    private SegmentBrahmsParser() {
        // private utility constructor
    }

    /**
     * @param segmentsFile the Brahms file containing the segments information
     * @param geography    the geography containing the waypoints, airports...
     * @return the parsed {@link FlightSegment}s
     */
    public static Map<String, FlightSegment> parseSegments(File segmentsFile, ATCGeography geography) {
        Map<String, FlightSegment> segments = new HashMap<>();
        BrahmsAgentInitializationParser.parseBrahmsInstances(segmentsFile).forEach(segmentInstance -> {
            final FlightSegment segment = parseSingleSegment(segmentInstance, geography);
            segments.put(segment.getSegmentName(), segment);

        });
        return Collections.unmodifiableMap(segments);
    }

    private static FlightSegment parseSingleSegment(BrahmsInstanceInitialConfiguration segmentInstance, ATCGeography geography) {
        final String afoName = segmentInstance.getName().split("_")[3];
        //
        final ATCNode from = geography.getNodeByName(segmentInstance.getInitialFacts().get(FROM_WAYPOINT));
        final ATCNode to = geography.getNodeByName(segmentInstance.getInitialFacts().get(TO_WAYPOINT));
        final double dEndAltitude = Double.parseDouble(segmentInstance.getInitialFacts().get(END_ALTITUDE));
        final int iEndSpeed = (int) Double.parseDouble(segmentInstance.getInitialFacts().get(END_SPEED));
        //
        final FlightSegment segment = new FlightSegment(segmentInstance.getName(), afoName, from, to, dEndAltitude, iEndSpeed);
        LOG.log(Level.FINE, "Found segment:: {0}", segment);
        return segment;
    }

}
