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

import gov.nasa.arc.atc.simulation.SimulatedTrajectory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ahamon
 */
public class Bucket {

    private final int startTime;
    private final int duration;
    private final List<SimulatedTrajectory> trajectories;

    public Bucket(int sTime, int dur) {
        startTime = sTime;
        duration = dur;
        trajectories = new LinkedList<>();
    }

    public void add(SimulatedTrajectory trajectory) {
        trajectories.add(trajectory);
    }

    public void remove(SimulatedTrajectory trajectory) {
        trajectories.remove(trajectory);
    }

    public void removeAll() {
        trajectories.clear();
    }

    public int getSize() {
        return trajectories.size();
    }

    public List<SimulatedTrajectory> getTrajectories() {
        return Collections.unmodifiableList(trajectories);
    }

    public int getStartTime() {
        return startTime;
    }
}
