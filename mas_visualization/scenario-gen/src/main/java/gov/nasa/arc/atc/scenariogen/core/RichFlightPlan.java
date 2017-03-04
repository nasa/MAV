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

import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Position;
import gov.nasa.arc.atc.simulation.SlotTrajectory;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ahamon
 */
public class RichFlightPlan {

    private final String scenarioName;
    private final String fplName;
    private final Position initPosition;
    private final double initSpeed;
    private final FlightSegment[] segments;
    private final Map<String, Integer> times;

    public RichFlightPlan(String scenario, String name, FlightPlan fpl, SlotTrajectory trajectory, Position initialPosition, double initialSpeed) {
        scenarioName = scenario;
        fplName = name;
        initPosition = initialPosition;
        initSpeed = initialSpeed;
        times = new HashMap<>();
        //
        segments = new FlightSegment[fpl.getPath().size()];
        for (int i = 0; i < segments.length; i++) {
            final FlightSegment s = fpl.getSegment(i);
            segments[i] = s;
            times.put(s.getToWaypoint().getName(), trajectory.getArrivalTimeAtNode(s.getToWaypoint().getName()));
        }
        if(segments.length>0){
            times.put(segments[0].getFromWaypoint().getName(),0);
        }
    }

    public RichFlightPlan(String scenario, String name, FlightSegment[] flightSegments, Map<String, Integer> crossingTimes, Position initialPosition, double initialSpeed) {
        scenarioName = scenario;
        fplName = name;
        times = Collections.unmodifiableMap(crossingTimes);
        segments = flightSegments.clone();
        initPosition = initialPosition;
        initSpeed = initialSpeed;
    }

    public String getFplName() {
        return fplName;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public FlightSegment[] getSegments() {
        return segments.clone();
    }

    public Position getInitPosition() {
        return initPosition;
    }

    public double getInitSpeed() {
        return initSpeed;
    }

    public Map<String, Integer> getTimes() {
        return Collections.unmodifiableMap(times);
    }

    public boolean crosses(String nodeName) {
        return times.containsKey(nodeName);
    }

    public boolean arrivesAt(String nodeName) {
        return segments.length != 0 && segments[segments.length - 1].getToWaypoint().getName().equals(nodeName);

    }

    public boolean departsFrom(String nodeName) {
        return segments.length > 0 && segments[0].getFromWaypoint().getName().equals(nodeName);
    }

    public int getCrossingTime(String nodeName) {
        if(!times.containsKey(nodeName)){
            System.out.println(" ? crossing time for "+fplName+" at "+nodeName);
            System.out.println( " : fpl -> "+segmentsToString());
            System.out.println( " : times -> "+times);
            System.out.println( " : return => "+times.get(nodeName));
        }
        return times.get(nodeName);
    }

    public int getArrivalTime() {
        if (times.isEmpty()) {
            return -1;
        }
        return times.get(segments[segments.length - 1].getToWaypoint().getName());
    }

    public String getArrival() {
        if (times.isEmpty()) {
            return null;
        }
        return segments[segments.length - 1].getToWaypoint().getName();
    }

    private String segmentsToString(){
       return Arrays.stream(segments).map(segment -> "["+ segment.getFromWaypoint().getName()+"-"+segment.getToWaypoint().getName()+"]").collect(joining(" - "));
    }

}
