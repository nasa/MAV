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

import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.simulation.SlotTrajectory;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ahamon
 */
public class CTOPTrajectoryPerfoTest {

    public static void main(String[] args) {

        int nbTrajectories = 10;

        System.err.println(" Running performance test for n = " + nbTrajectories + " ...");

        printMemory();

        List<SimulatedTrajectory> trajectories = new LinkedList<>();

        long startA = System.currentTimeMillis();

        for (int i = 0; i < nbTrajectories; i++) {
            final SimulatedSlotMarker slot = CTOPTestUtils.createArrivalA(i, i * 40, i * 50);
            final SimulatedTrajectory trajectory = new SlotTrajectory(CTOPTestUtils.getSimpleGeography(), slot, Integer.MAX_VALUE);
            trajectories.add(trajectory);
        }

        long startAA = System.currentTimeMillis();
        for (int i = 0; i < nbTrajectories; i++) {
            final SimulatedSlotMarker slot = CTOPTestUtils.createArrivalAA(i, i * 40, i * 50);
            final SimulatedTrajectory trajectory = new SlotTrajectory(CTOPTestUtils.getSimpleGeography(), slot, Integer.MAX_VALUE);
            trajectories.add(trajectory);
        }

        long startAAA = System.currentTimeMillis();
        for (int i = 0; i < nbTrajectories; i++) {
            final SimulatedSlotMarker slot = CTOPTestUtils.createArrivalAAA(i, i * 40, i * 50);
            final SimulatedTrajectory trajectory = new SlotTrajectory(CTOPTestUtils.getSimpleGeography(), slot, Integer.MAX_VALUE);
            trajectories.add(trajectory);
        }

        long end = System.currentTimeMillis();

        long createA = startAA - startA;
        long createAA = startAAA - startAA;
        long createAAA = end - startAAA;

        System.err.println(" avg. create A   :: " + createA / nbTrajectories);
        System.err.println(" avg. create AA  :: " + createAA / nbTrajectories);
        System.err.println(" avg. create AAA :: " + createAAA / nbTrajectories);


        printMemory();


        System.err.println("trajectories:: " + trajectories.stream().map(t -> t.getSlotMarker().getName() + "-> @t=" + t.getArrivalTime()).collect(Collectors.joining(", ")));

        for (int i = 0; i < Math.min(10, trajectories.size()); i++) {
            SimulatedTrajectory t = trajectories.get(i);
            System.err.println(" " + t.getSlotMarker().getName() + " enters ctop freeze at " + t.getCrossingTime(CTOPTestUtils.AP_4, 400));
        }

        System.err.println("...done");

    }

    private static void printMemory() {

        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();
        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: ").append(format.format(freeMemory / 1024)).append(" - ");
        sb.append("allocated memory: ").append(format.format(allocatedMemory / 1024)).append(" - ");
        sb.append("max memory: ").append(format.format(maxMemory / 1024)).append(" - ");
        sb.append("total free memory: ").append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
        System.err.println("");
        System.err.println(sb.toString());
        System.err.println("");
    }

    // results on dev machine for n=100
//     Running performance test ...
//    avg. create A   :: 596
//    avg. create AA  :: 604
//    avg. create AAA :: 605
//            ...done
}
