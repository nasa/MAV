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

package gov.nasa.arc.atc.simulation.wrapper;

import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.airborne.ArrivalSequence;
import gov.nasa.arc.atc.algos.DepartureArrivalAlgorithm;
import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.algos.dsas.DSAS;
import gov.nasa.arc.atc.algos.dsas.DSASConcept;
import gov.nasa.arc.atc.algos.dsas.FixedDepartureTime;
import gov.nasa.arc.atc.algos.dsas.ParametricDSAS;
import gov.nasa.arc.atc.algos.tss.TSS;
import gov.nasa.arc.atc.export.ReportXMLExport;
import gov.nasa.arc.atc.functions.DistanceSeparationFunction;
import gov.nasa.arc.atc.functions.MeterFixCandidateFunction;
import gov.nasa.arc.atc.functions.SimpleGapScheludingFunction;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.reports.DSASReporter;
import gov.nasa.arc.atc.reports.TSSReporter;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.utils.ConsoleUtils;
import gov.nasa.arc.atc.utils.SlotChecker;
import gov.nasa.arc.brahms.atmjava.activities.Utils;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class BrahmsWrapper {

    private static final Logger LOG = Logger.getGlobal();

    private static final boolean IS_LOG = false;
    private static final Map<String, Map<String, TSS>> TSS_INSTANCES = new HashMap<>();
    private static final Map<String, DepartureArrivalAlgorithm> DEP_ARR_ALGO_INSTANCES = new HashMap<>();

    private BrahmsWrapper() {
        // private utility constructor
        ConsoleUtils.setLoggingLevel(Level.INFO);
    }

    /**
     * Clumbsy invokerClassName parameter to store invokers and re-use TSS instances
     *
     * @param invokerClassName the name of the class invoking this method
     * @param slots            the slots to run the TSS on
     * @param simulationTime   the simulation time
     * @return the slots input in the method
     */
    public static List<AFO> invokeTSS(final String invokerClassName, final List<AFO> slots, final int simulationTime) {
        if (IS_LOG) {
            LOG.log(Level.INFO, " invoking TSS at t={0}", simulationTime);
        }
        ConsoleUtils.setLoggingLevel(Level.FINE);
        // check inputs
        checkInputsValidity(slots, simulationTime);

        SimulationContext context;
        if (Utils.getContext() == null) {
            Utils.initializeContext(slots);
        }
        context = Utils.getContext();
        // update simulation context with current simulation time
        context.getClock().setTime(simulationTime);
        context.updateActiveSlots(slots);
        // arrival sequences
        Map<ATCNode, ArrivalSequence> allSequences = context.getArrivalSequences();

        // calculate reference trajectories
        // context.calculateReferenceTrajectories();

        allSequences.values().forEach(arrivalsequence -> arrivalsequence.getSimulatedTrajectories().forEach(SimulatedTrajectory::printDebug));

        // create TSS at arrival nodes
        context.getArrivalSequences().keySet().stream().map(node -> {
            //
            final TSS tss;
            //
            if (!TSS_INSTANCES.containsKey(invokerClassName)) {
                tss = new TSS(new DistanceSeparationFunction(node));
                tss.initializeData(context, node);
                final Map<String, TSS> map = new HashMap<>();
                map.put(node.getName(), tss);
                TSS_INSTANCES.put(invokerClassName, map);
            } else if (!TSS_INSTANCES.get(invokerClassName).containsKey(node.getName())) {
                tss = new TSS(new DistanceSeparationFunction(node));
                tss.initializeData(context, node);
                TSS_INSTANCES.get(invokerClassName).put(node.getName(), tss);
            } else {
                tss = TSS_INSTANCES.get(invokerClassName).get(node.getName());
            }
            return tss;
        }).forEach(tss -> {
            // final TSS tss = new TSS(new DistanceSeparationFunction(node));
            // invoke TSS
            tss.execute(simulationTime);
        });

        // update slots parameters and flight plan
        allSequences.values().forEach(arrivalsequence -> processSequenceForSlotUpdate(arrivalsequence, slots, simulationTime));

        return slots;
    }

    /**
     * Clumbsy invokerClassName parameter to store invokers and re-use TSS instances
     *
     * @param invokerClassName the name of the class invoking this method
     * @param slots            the slots to run the TSS on
     * @param simulationTime   the simulation time
     * @return the slots input in the method
     */
    public static List<AFO> invokeDSAS1(final String invokerClassName, final List<AFO> slots, final int simulationTime) {
        if (IS_LOG)
            LOG.log(Level.INFO, " invoking DSAS1 at t={0}", simulationTime);
        // check inputs
        checkInputsValidity(slots, simulationTime);
        // context
        SimulationContext context;
        if (Utils.getContext() == null) {
            Utils.initializeContext(slots);
        }
        context = Utils.getContext();
        // update simulation context with current simulation time
        context.getClock().setTime(simulationTime);
        context.updateActiveSlots(slots);
        //
        // TODO: remove ugly to to determine arrival and departure runway
        if (context.getArrivalSequences().isEmpty()) {
            LOG.log(Level.SEVERE, " NO ARRIVAL SEQUENCE at t = {0}", simulationTime);
            throw new IllegalStateException(" NO ARRIVAL SEQUENCE at t = " + simulationTime);
        } else if (context.getArrivalSequences().size() > 1) {
            LOG.log(Level.SEVERE, " MORE THAN ONE ARRIVAL SEQUENCE at t = {0}", simulationTime);
            context.getArrivalSequences().forEach((node, sequence) -> LOG.log(Level.SEVERE, "sequence arriving at = {0}", node));
            throw new IllegalStateException(" MORE THAN ONE ARRIVAL SEQUENCE at t = " + simulationTime);
        }
        // TODO: parametrize
        // TODO : remove ACK
        final FixedDepartureTime fixedDepartureTime;
        if (!DEP_ARR_ALGO_INSTANCES.containsKey(invokerClassName)) {
            ATCNode arrivalR = context.getArrivalSequences().keySet().iterator().next();
            ATCNode departureR = context.getDepartureSequences().keySet().iterator().next();
            fixedDepartureTime = new FixedDepartureTime(new DistanceSeparationFunction(context.getGeography().getAirports().get(0)));
            fixedDepartureTime.initializeData(context, departureR, arrivalR);
            DEP_ARR_ALGO_INSTANCES.put(invokerClassName, fixedDepartureTime);
        } else {
            fixedDepartureTime = (FixedDepartureTime) DEP_ARR_ALGO_INSTANCES.get(invokerClassName);
        }
        fixedDepartureTime.execute(simulationTime);
        //
        return slots;
    }

    public static List<AFO> invokeDSAS2(final String invokerClassName, final List<AFO> slots, final int simulationTime) {
        if (IS_LOG) {
            LOG.log(Level.INFO, " invoking DSAS2 at t={0}", simulationTime);
        }
        System.err.println(" I AM INVOKING DSAS 2");
        // check inputs
        checkInputsValidity(slots, simulationTime);
        // context
        SimulationContext context;
        if (Utils.getContext() == null) {
            Utils.initializeContext(slots);
        }
        context = Utils.getContext();
        // update simulation context with current simulation time
        context.getClock().setTime(simulationTime);
        context.updateActiveSlots(slots);
        // TODO: remove ugly to to determine arrival and departure runway
        if (context.getArrivalSequences().isEmpty()) {
            LOG.log(Level.SEVERE, " NO ARRIVAL SEQUENCE at t = {0}", simulationTime);
            throw new IllegalStateException(" NO ARRIVAL SEQUENCE at t = " + simulationTime);
        } else if (context.getArrivalSequences().size() > 1) {
            LOG.log(Level.SEVERE, " MORE THAN ONE ARRIVAL SEQUENCE at t = {0}", simulationTime);
            context.getArrivalSequences().forEach((node, sequence) -> LOG.log(Level.SEVERE, "sequence arriving at = {0}", node));
            throw new IllegalStateException(" MORE THAN ONE ARRIVAL SEQUENCE at t = " + simulationTime);
        }

        final DSASConcept dsas2;
        if (!DEP_ARR_ALGO_INSTANCES.containsKey(invokerClassName)) {
            ATCNode arrivalR = context.getArrivalSequences().keySet().iterator().next();
            ATCNode departureR = context.getDepartureSequences().keySet().iterator().next();
            dsas2 = new DSASConcept();
            dsas2.initializeData(context, departureR, arrivalR);
            DEP_ARR_ALGO_INSTANCES.put(invokerClassName, dsas2);
        } else {
            dsas2 = (DSASConcept) DEP_ARR_ALGO_INSTANCES.get(invokerClassName);
        }
        dsas2.execute(simulationTime);
        return slots;
    }

    public static List<AFO> invokeParametricDSAS(final String invokerClassName, final List<AFO> slots, final int simulationTime, final int dsasHorizon) {
        if (IS_LOG) {
            LOG.log(Level.INFO, " invoking Parametric DSAS at t={0}", simulationTime);
        }
        // check inputs
        checkInputsValidity(slots, simulationTime);
        // context
        SimulationContext context;
        if (Utils.getContext() == null) {
            Utils.initializeContext(slots);
        }
        context = Utils.getContext();
        // update simulation context with current simulation time
        context.getClock().setTime(simulationTime);
        context.updateActiveSlots(slots);
        // TODO: remove ugly to to determine arrival and departure runway
        if (context.getArrivalSequences().isEmpty()) {
            LOG.log(Level.SEVERE, " NO ARRIVAL SEQUENCE at t = {0}", simulationTime);
            throw new IllegalStateException(" NO ARRIVAL SEQUENCE at t = " + simulationTime);
        } else if (context.getArrivalSequences().size() > 1) {
            LOG.log(Level.SEVERE, " MORE THAN ONE ARRIVAL SEQUENCE at t = {0}", simulationTime);
            context.getArrivalSequences().forEach((node, sequence) -> LOG.log(Level.SEVERE, "sequence arriving at = {0}", node));
            throw new IllegalStateException(" MORE THAN ONE ARRIVAL SEQUENCE at t = " + simulationTime);
        }

        final DSAS parametricDSAS;
        if (!DEP_ARR_ALGO_INSTANCES.containsKey(invokerClassName)) {
            ATCNode arrivalR = context.getArrivalSequences().keySet().iterator().next();
            ATCNode departureR = context.getDepartureSequences().keySet().iterator().next();
            parametricDSAS = new ParametricDSAS(new MeterFixCandidateFunction(context.getGeography(), dsasHorizon), (traj, time) -> true, new SimpleGapScheludingFunction());
            parametricDSAS.initializeData(context, departureR, arrivalR);
            DEP_ARR_ALGO_INSTANCES.put(invokerClassName, parametricDSAS);
        } else {
            parametricDSAS = (DSAS) DEP_ARR_ALGO_INSTANCES.get(invokerClassName);
        }
        parametricDSAS.execute(simulationTime);
        return slots;
    }

    /**
     * Save the simulation report in a file name: Report_DATE-TIME.xml
     *
     * @param folderPath         the name of the folder which the report shall be saved in
     * @param simulationDuration duration of the simulation
     * @param timeIncrement      time step for the simulation
     * @param scenarioName       name of the scenario ran
     */
    public static void exportReport(String folderPath, final int simulationDuration, final int timeIncrement, final String scenarioName) {
        // TODO : improve
        String filePath = folderPath + File.separator + "Report_" + LocalDate.now().toString() + ".xml";
        LOG.log(Level.WARNING, "Exporting TSS Report to file {0}", filePath);
        File reportFile = new File(filePath);
        // not necessarily the best place to do it...
        TSSReporter.getReportInputs().forEach(reportInput -> {
            reportInput.setScenarioName(scenarioName);
            reportInput.setSimulationDuration(simulationDuration);
            reportInput.setStepDuration(timeIncrement);
        });
        DSASReporter.getReportInputs().forEach(reportInput -> {
            reportInput.setScenarioName(scenarioName);
            reportInput.setSimulationDuration(simulationDuration);
            reportInput.setStepDuration(timeIncrement);
        });
        boolean exportOK = ReportXMLExport.exportReports(reportFile);
        if (!exportOK) {
            LOG.log(Level.WARNING, "Error while exporting TSS Report to file {0}", filePath);
        }
    }

	/*
     * TSS related private methods
	 */

    private static void checkInputsValidity(List<AFO> slots, int simulationTime) {
        // check simulation time
        if (simulationTime < 0) {
            throw new IllegalArgumentException("Simulation time cannot be negative. Given value=" + simulationTime);
        }
        // check slots are well defined
        slots.forEach(SlotChecker::checkSlotValidity);

        // check name unicity
        for (int i = 0; i < slots.size(); i++) {
            for (int j = 0; j < slots.size(); j++) {
                if (i != j && slots.get(i).getName().equals(slots.get(j).getName())) {
                    throw new IllegalArgumentException("Two slots with identical names :: " + slots.get(i) + "  and  " + slots.get(j));
                }
            }
        }
    }

    private static void processSequenceForSlotUpdate(ArrivalSequence arrivalsequence, List<AFO> slots, int simTime) {
        arrivalsequence.getSimulatedTrajectories().forEach(simulatedTrajectory -> processTrajectoryForSlotUpdate(simulatedTrajectory, slots, simTime));
    }

    private static void processTrajectoryForSlotUpdate(SimulatedTrajectory simulatedTrajectory, List<AFO> slots, int simTime) {
        simulatedTrajectory.printDebug();
        // find corresponding slot
        AFO slot = null;
        for (AFO s : slots) {
            if (s.getName().equals(simulatedTrajectory.getSlotMarker().getName())) {
                slot = s;
                break;
            }
        }
        if (slot == null) {
            throw new IllegalStateException("did not find slot to update");
        }
        // update parameters
        FlightParameters parameters = simulatedTrajectory.getParametersAtSimulationTime(simTime);
        slot.setParameters(parameters);
        // update flight plan current segment
        // clumsy, change API?s
        slot.getFlightPlan().setCurrentSegment(simulatedTrajectory.getWaypointAimedAtSimulationTime(simTime));
    }

	/*
	 * DSAS related private methods
	 */
}
