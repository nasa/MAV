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

package gov.nasa.arc.atc.algos;

import gov.nasa.arc.atc.airborne.ArrivalSequence;
import gov.nasa.arc.atc.airborne.DepartureSequence;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.simulation.SlotTrajectory;
import gov.nasa.arc.atc.simulation.SimulatedSlotMarker;

public final class BasicSequencer {

    private BasicSequencer() {
        // private utility class
    }

    /**
     * @param node
     * @param context
     * @param simulationDuration
     * @return the newly created sequence
     */
    public static ArrivalSequence createArrivalSequence(ATCGeography atcGeography, ATCNode node, SimulationContext context, int simulationDuration) {
        ArrivalSequence sequence = new ArrivalSequence(node);
        // for each slot in the simulation context ...
        context.getActiveSlots().forEach(slot -> updateArrivalSequence(atcGeography, sequence, slot, simulationDuration));
        return sequence;
    }

    public static void updateArrivalSequence(ATCGeography atcGeography, ArrivalSequence sequence, SimulatedSlotMarker slot, int simulationDuration) {
        // ... simulate the slot's trajectory
        final SlotTrajectory trajectory = new SlotTrajectory(atcGeography, slot, simulationDuration);
        sequence.addSimulatedTrajectory(trajectory);
    }

	/*
     * DEPARTURES
	 */

    public static DepartureSequence createDepartureSequence(ATCGeography atcGeography, ATCNode node, SimulationContext context, int simulationDuration) {
        System.err.println(" @@@ createDepartureSequence for node " + node.getName());
        DepartureSequence sequence = new DepartureSequence(node);
        // for each slot in the simulation context ...
        context.getActiveSlots().forEach(slot -> updateDepartureSequence(atcGeography, sequence, slot, simulationDuration));
        return sequence;
    }

    public static void updateDepartureSequence(ATCGeography atcGeography, DepartureSequence sequence, SimulatedSlotMarker slot, int simulationDuration) {
        // ... simulate the slot's trajectory
        System.err.println(" @@@ new departure ");
        final SlotTrajectory trajectory = new SlotTrajectory(atcGeography, slot, simulationDuration);
        System.err.println(" @@@ new departure ::" + trajectory);
        sequence.addSimulatedTrajectory(trajectory);
    }

}
