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

package gov.nasa.arc.atc.algos.ctop;

import gov.nasa.arc.atc.airborne.ArrivalSequence;
import gov.nasa.arc.atc.algos.ArrivalAlgorithm;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.utils.CTOPUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ahamon
 */
public class CTOP implements ArrivalAlgorithm {


    private final int externalFixDistance;
    private final int bucketDuration;
    private final int bucketRate;


    // in the future, may need to add the offset
    private final int bucketOffset = 0;


    private SimulationContext context;
    private ATCNode arrivalNode;
    private ArrivalSequence arrivalSequence;

    private final List<SimulatedTrajectory> scheduledArrivals;

    private String scenarioName;
    private int simulationDuration;
    private int stepDuration;
    private boolean isSet = false;

    private Bucket[] buckets;


    public CTOP(int externalMeterFixDistance, int bDuration, int bRate) {
        externalFixDistance = externalMeterFixDistance;
        bucketDuration = bDuration;
        bucketRate = bRate;
        scheduledArrivals = new LinkedList<>();
    }


    @Override
    public void setSimulationConfiguration(String scenarioName, int simulationDuration, int stepDuration) {
        this.scenarioName = scenarioName;
        this.simulationDuration = simulationDuration;
        this.stepDuration = stepDuration;
        isSet = true;
        createBuckets();
    }

    @Override
    public void initializeData(SimulationContext context, ATCNode aNode) {
        setContext(context);
        arrivalNode = aNode;
    }


    @Override
    public boolean execute(int simulationTime) {
        if (!isSet) {
            throw new IllegalStateException(" CTOP not set :: " + this);
        }

        // update list of slots
        arrivalSequence = context.getArrivalSequences().get(arrivalNode);

        // reset the scheduled arrivals
        scheduledArrivals.clear();

        // get current bucket
        int currentBucketIndex = CTOPUtils.getCurrentBucketIndex(simulationTime, bucketDuration, bucketOffset);
        Bucket currentBucket = buckets[currentBucketIndex];

        //remove planes that have not crossed the meter line
        List<SimulatedTrajectory> currentBTraj = currentBucket.getTrajectories();
        currentBTraj.forEach(t -> {
            if (t.getCrossingTime(arrivalNode, externalFixDistance) > simulationTime) {
                currentBucket.remove(t);
            }
        });
        //cleaning future buckets
        for (int i = currentBucketIndex + 1; i < buckets.length; i++) {
            buckets[i].removeAll();
        }

        System.err.println(bucketDebugLight());


        // rebuilding scheduled arrivals
        for (int i = 0; i < currentBucketIndex + 1; i++) {
            scheduledArrivals.addAll(buckets[i].getTrajectories());
        }

        //to be optimized
        List<SimulatedTrajectory> toBeScheduled = arrivalSequence.getSimulatedTrajectories().stream().filter(t -> !scheduledArrivals.contains(t)).collect(Collectors.toList());


        for (SimulatedTrajectory t : toBeScheduled) {
            final int crossingTime = t.getCrossingTime(arrivalNode, externalFixDistance);
            int correspondingBucketIndex = CTOPUtils.getCurrentBucketIndex(crossingTime, bucketDuration, bucketOffset);
            if (correspondingBucketIndex < buckets.length) {

                while (correspondingBucketIndex < buckets.length && buckets[correspondingBucketIndex].getSize() >= bucketRate) {
                    correspondingBucketIndex++;
                }

                if (correspondingBucketIndex < buckets.length && buckets[correspondingBucketIndex].getSize() < bucketRate) {
                    Bucket b = buckets[correspondingBucketIndex];
                    b.add(t);
                    if (b.getStartTime() > crossingTime) {
                        int delay = b.getStartTime() - crossingTime + 1;
                        t.recalculateTrajectoryWithDelayAt(simulationTime, delay);
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return true;
    }

    public String getDisplayName() {
        return "CTOP";
    }

    public String bucketDebugLight() {
        StringBuilder sb = new StringBuilder();
        for (Bucket b : buckets) {
            sb.append("[").append(b.getSize()).append("] ");
        }
        return sb.toString();
    }


    //
    // INITIALIZATION RELATED METHODS
    //

    private void setContext(SimulationContext simulationContext) {
        if (simulationContext == null) {
            throw new IllegalArgumentException("SimulationContext cannot be null");
        }
        context = simulationContext;
    }

    private void createBuckets() {
        if (bucketOffset == 0) {
            int nbBuckets = simulationDuration / bucketDuration + 1;
            buckets = new Bucket[nbBuckets];
            for (int i = 0; i < nbBuckets; i++) {
                buckets[i] = new Bucket(i * bucketDuration, bucketDuration);
            }
        } else {
            throw new UnsupportedOperationException(" not done yet");
        }
    }


    private void processBucket(int bucketStartTime) {

    }
}
