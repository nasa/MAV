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

package gov.nasa.arc.atc.metrics;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.core.NewPlane;
import gov.nasa.arc.atc.geography.ATCNode;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kelsey
 * @author ahamon
 */
public class SectorCalculation {

    private static final int DEFAULT_MAX_CAPACITY = 15;

    //TODO revise constructor
    private final String sectorName;

    private final List<String> nodes;

    private final SimulationCalculations simCalculations;
    //private int sectorId;
    private int maxCapacity;
    private double perceptualWorkload;
    private double temporalWorkload;
    private double decisionWorkload;

    //
    private final int[] count;


    public SectorCalculation(String name, int maximumCapacity, List<ATCNode> sectorNodes, SimulationCalculations simulationCalculations) {
        sectorName = name;
        simCalculations = simulationCalculations;
        nodes = Collections.unmodifiableList(sectorNodes.stream().map(node -> node.getName()).collect(Collectors.toList()));
        maxCapacity = maximumCapacity;

        //TODO make it start at start sim time
        count = new int[simCalculations.getDataModel().getMaxSimTime() + 1];

        // init count at 0
        for (int i = 0; i < count.length; i++) {
            count[i] = 0;
        }

        //calculate capacity at each time
        synchronized (count) {
            for (NewPlane plane : simCalculations.getDataModel().getAllPlanes()) {
                for (AfoUpdate update : plane.getUpdates().values()) {
                    if (nodes.contains(update.getToWaypoint())) {
                        count[update.getTimeStamp()] += 1;
                    }
                }
            }
        }


        // temp debug values
        perceptualWorkload = 1;
        temporalWorkload = 2;
        decisionWorkload = 3;
    }

    public SectorCalculation(String name, List<ATCNode> sectorNodes, SimulationCalculations simulationCalculations) {
        this(name, DEFAULT_MAX_CAPACITY, sectorNodes, simulationCalculations);
    }

    public String getSectorName() {
        return this.sectorName;
    }


    public String toString() {
        return "SectorCalculations for: " + sectorName;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getnbAircrafts(int time) {
        return count[time];
    }

    //copied over methods

    public double getPerceptualWorkload() {
        return perceptualWorkload;
    }

    public double getTemporalWorkload() {
        return temporalWorkload;
    }

    public double getDecisionWorkload() {
        return decisionWorkload;
    }

    public int getPercentageCapacity(int time) {
        return getnbAircrafts(time) / maxCapacity;
    }

    public double getCurrentCapacity(int time) {
        return (double) getnbAircrafts(time) / maxCapacity;
    }

}
