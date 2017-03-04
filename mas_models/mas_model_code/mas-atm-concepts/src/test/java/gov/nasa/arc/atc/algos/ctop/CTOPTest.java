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
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.utils.Constants;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author ahamon
 */
public class CTOPTest {


    @Test
    public void testCTOPstep1() {
        SimulationContext context = createContext1();
    }


    private static SimulationContext createContext1() {
        SimulationContext context = new SimulationContext(CTOPTestUtils.getSimpleGeography());
        context.setSimulationDuration(Integer.MAX_VALUE);

        List<SimulatedSlotMarker> afos = new ArrayList<>();
        int depTime1 = 15;
        int depTime2 = depTime1 + Constants.ARR_ARR_MIN / 2;
        int depTime3 = depTime2 + Constants.ARR_ARR_MIN / 2;
        int depTime4 = depTime3 + Constants.ARR_ARR_MIN / 2;
        int depTime5 = depTime4 + Constants.ARR_ARR_MIN / 2;
        int depTime6 = depTime5 + Constants.ARR_ARR_MIN / 2;
        int depTime7 = depTime6 + Constants.ARR_ARR_MIN / 2;
        int depTime8 = depTime7 + Constants.ARR_ARR_MIN / 2;
        int depTime9 = depTime8 + Constants.ARR_ARR_MIN / 2;
        //
        int startTime1 = depTime1-5;
        int startTime2 = depTime2-5;
        int startTime3 = depTime3-5;
        int startTime4 = depTime4-5;
        int startTime5 = depTime5-5;
        int startTime6 = depTime6-5;
        int startTime7 = depTime7-5;
        int startTime8 = depTime8-5;
        int startTime9 = depTime9-5;
        //
        SimulatedSlotMarker arr1 = CTOPTestUtils.createArrivalAA(1, startTime1, depTime1);
        SimulatedSlotMarker arr2 = CTOPTestUtils.createArrivalAA(2, startTime2, depTime2);
        SimulatedSlotMarker arr3 = CTOPTestUtils.createArrivalAA(3, startTime3, depTime3);
        SimulatedSlotMarker arr4 = CTOPTestUtils.createArrivalAA(4, startTime4, depTime4);
        SimulatedSlotMarker arr6 = CTOPTestUtils.createArrivalAA(6, startTime5, depTime6);
        SimulatedSlotMarker arr5 = CTOPTestUtils.createArrivalAA(5, startTime6, depTime5);
        SimulatedSlotMarker arr7 = CTOPTestUtils.createArrivalAA(7, startTime7, depTime7);
        SimulatedSlotMarker arr8 = CTOPTestUtils.createArrivalAA(8, startTime8, depTime8);
        SimulatedSlotMarker arr9 = CTOPTestUtils.createArrivalAA(9, startTime9, depTime9);
        afos.add(arr1);
        afos.add(arr2);
        afos.add(arr3);
        afos.add(arr4);
        afos.add(arr5);
        afos.add(arr6);
        afos.add(arr7);
        afos.add(arr8);
        afos.add(arr9);
        context.addSlots(afos);
        context.getClock().setTime(depTime9-1);
        context.calculateReferenceTrajectories();
        context.updateActiveSlots();
        //
        final int arrivalTime1 = context.getArrivalSequences().get(CTOPTestUtils.A4_R2).getSimulatedTrajectories().get(0).getArrivalTime();
        System.err.println(" Arrival 1 @t=" + arrivalTime1);
        final int arrivalTime2 = context.getArrivalSequences().get(CTOPTestUtils.A4_R2).getSimulatedTrajectories().get(1).getArrivalTime();
        System.err.println(" Arrival 2 @t=" + arrivalTime2);
        final int arrivalTime3 = context.getArrivalSequences().get(CTOPTestUtils.A4_R2).getSimulatedTrajectories().get(2).getArrivalTime();
        System.err.println(" Arrival 3 @t=" + arrivalTime3);
        final int arrivalTime4 = context.getArrivalSequences().get(CTOPTestUtils.A4_R2).getSimulatedTrajectories().get(3).getArrivalTime();
        System.err.println(" Arrival 4 @t=" + arrivalTime4);
        final int arrivalTime5 = context.getArrivalSequences().get(CTOPTestUtils.A4_R2).getSimulatedTrajectories().get(4).getArrivalTime();
        System.err.println(" Arrival 5 @t=" + arrivalTime5);
        final int arrivalTime6 = context.getArrivalSequences().get(CTOPTestUtils.A4_R2).getSimulatedTrajectories().get(5).getArrivalTime();
        System.err.println(" Arrival 6 @t=" + arrivalTime6);
        final int arrivalTime7 = context.getArrivalSequences().get(CTOPTestUtils.A4_R2).getSimulatedTrajectories().get(6).getArrivalTime();
        System.err.println(" Arrival 7 @t=" + arrivalTime7);
        final int arrivalTime8 = context.getArrivalSequences().get(CTOPTestUtils.A4_R2).getSimulatedTrajectories().get(7).getArrivalTime();
        System.err.println(" Arrival 8 @t=" + arrivalTime8);
        final int arrivalTime9 = context.getArrivalSequences().get(CTOPTestUtils.A4_R2).getSimulatedTrajectories().get(8).getArrivalTime();
        System.err.println(" Arrival 9 @t=" + arrivalTime9);


        CTOP ctop = new CTOP(400, 10 * 60, 4);
        ctop.setSimulationConfiguration("test CTOP", 22000, 1);
        ctop.initializeData(context, CTOPTestUtils.A4_R2);

        System.err.println(" CTOP @t=0");
        context.getClock().setTime(0);
        context.updateActiveSlots();
        ctop.execute(0);
        System.err.println(ctop.bucketDebugLight());
        System.err.println("\n");

        System.err.println(" CTOP @start1 t="+startTime1);
        context.getClock().setTime(startTime1);
        context.updateActiveSlots();
        ctop.execute(startTime1);
        System.err.println(ctop.bucketDebugLight());
        System.err.println("\n");

        System.err.println(" CTOP @start2 t="+startTime2);
        context.getClock().setTime(startTime2);
        context.updateActiveSlots();
        ctop.execute(startTime2);
        System.err.println(ctop.bucketDebugLight());
        System.err.println("\n");

        System.err.println(" CTOP @start3 t="+startTime3);
        context.getClock().setTime(startTime3);
        context.updateActiveSlots();
        ctop.execute(startTime3);
        System.err.println(ctop.bucketDebugLight());
        System.err.println("");

        System.err.println(" CTOP @start4 t="+startTime4);
        context.getClock().setTime(startTime4);
        context.updateActiveSlots();
        ctop.execute(startTime4);
        System.err.println(ctop.bucketDebugLight());
        System.err.println("");

        System.err.println(" CTOP @start5 t="+startTime5);
        context.getClock().setTime(startTime5);
        context.updateActiveSlots();
        ctop.execute(startTime5);
        System.err.println(ctop.bucketDebugLight());
        System.err.println("");

        System.err.println(" CTOP @start6 t="+startTime6);
        context.getClock().setTime(startTime6);
        context.updateActiveSlots();
        ctop.execute(startTime6);
        System.err.println(ctop.bucketDebugLight());
        System.err.println("");

        System.err.println(" CTOP @start7 t="+startTime7);
        context.getClock().setTime(startTime7);
        context.updateActiveSlots();
        ctop.execute(startTime7);
        System.err.println(ctop.bucketDebugLight());
        System.err.println("");

        System.err.println(" CTOP @start8 t="+startTime8);
        context.getClock().setTime(startTime8);
        context.updateActiveSlots();
        ctop.execute(startTime8);
        System.err.println(ctop.bucketDebugLight());
        System.err.println("");

        System.err.println(" CTOP @start9 t="+startTime9);
        context.getClock().setTime(startTime9);
        context.updateActiveSlots();
        ctop.execute(startTime9);
        System.err.println(ctop.bucketDebugLight());
        System.err.println("");




        return context;
    }


}
