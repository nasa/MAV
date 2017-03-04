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

/********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.airborne;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.Sequence;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.utils.Comparators;

/**
 * @author hamon
 */
public class ArrivalSequence implements Sequence {

    private static final Logger LOG = Logger.getGlobal();

    private final ATCNode arrivalNode;
    private final List<SimulatedTrajectory> arrivalTrajectories;

    /**
     * @param arrival the node where the sequence takes place
     */
    public ArrivalSequence(ATCNode arrival) {
        arrivalNode = arrival;
        arrivalTrajectories = new ArrayList<>();
    }

    /**
     * @param arrival      the node where the sequence takes place
     * @param trajectories the trajectories to add to the arrival sequence
     */
    public ArrivalSequence(ATCNode arrival, List<SimulatedTrajectory> trajectories) {
        arrivalNode = arrival;
        arrivalTrajectories = new ArrayList<>();
        addRelevantTrajectories(trajectories);
    }

    public void removeNotStartedAtTime(int currentSimTime) {
        final List<SimulatedTrajectory> tempToRemove = new ArrayList<>();
        arrivalTrajectories.forEach(traj -> {
            if (traj.getSlotMarker().getStartTime() > currentSimTime) {
                tempToRemove.add(traj);
            }
        });
        // remove
        arrivalTrajectories.removeAll(tempToRemove);
    }


    @Override
    public void clear() {
        arrivalTrajectories.clear();
    }

    @Override
    public ATCNode getATCNode() {
        return arrivalNode;
    }

    public ATCNode getArrivalNode() {
        return arrivalNode;
    }

    @Override
    public void addSimulatedTrajectory(SimulatedTrajectory arrivalTrajectory) {
        // TODO test...
        LOG.log(Level.FINE, "addSimulatedTrajectory {0}", arrivalTrajectory);
        if (arrivalTrajectory.getOriginalArrivalTimeAtNode(arrivalNode.getName()) < 0) {
            return;
        }
        arrivalTrajectories.add(arrivalTrajectory);
        //Collections.sort(arrivalTrajectories, Comparators.getSimulatedTrajectoryOrignalWptTimeComparator(arrivalNode));
        Collections.sort(arrivalTrajectories, Comparators.SIMULATED_TRAJECTORY_ORIGINAL_ARRIVAL_TIME_COMPARATOR);
    }

    @Override
    public List<SimulatedTrajectory> getSimulatedTrajectories() {
        return Collections.unmodifiableList(arrivalTrajectories);
    }

    @Override
    public SimulatedTrajectory getAtIndex(int arrivalIndex) {
        return arrivalTrajectories.get(arrivalIndex);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " at node " + arrivalNode.getName() + " nb afos=" + arrivalTrajectories.size();
    }

    private void addRelevantTrajectories(List<SimulatedTrajectory> trajectories) {
        trajectories.forEach(trajectory -> {
            if (trajectory.getSlotMarker().getFlightPlan().goesThrought(arrivalNode)) {
                addSimulatedTrajectory(trajectory);
            }
        });
    }

    public void reOrder() {
        Collections.sort(arrivalTrajectories, Comparators.SIMULATED_TRAJECTORY_ORIGINAL_ARRIVAL_TIME_COMPARATOR);
    }


}
