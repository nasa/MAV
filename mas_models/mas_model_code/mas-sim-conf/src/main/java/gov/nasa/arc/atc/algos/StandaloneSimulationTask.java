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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Runway;
import gov.nasa.arc.atc.simulation.*;
import gov.nasa.arc.atc.utils.SimulatedTrajectoryFactory;

/**
 * @author ahamon
 */
public class StandaloneSimulationTask extends SimulationTask {

    private static final Logger LOG = Logger.getGlobal();

    private final SimulationContext context;
    private final SimulationClock clock;
    private final int nbSteps;
    private final int stepDuration;
    private final List<Algorithm> algorithms;

    public StandaloneSimulationTask(SimulationContext scenario, int nbSimulationSteps, int simulationStepDuration, List<Algorithm> algorithmsSequence) {
        context = scenario;
        nbSteps = nbSimulationSteps;
        stepDuration = simulationStepDuration;
        clock = context.getClock();
        algorithms = algorithmsSequence;
    }

    @Override
    public SimulationContext getContext() {
        // TODO: refine API
        return context;
    }

    @Override
    public int getNbSteps() {
        return nbSteps;
    }

    @Override
    public int getStepDuration() {
        return stepDuration;
    }

    @Override
    protected Void call() throws Exception {
        System.err.println("executing service JavaStandalone");
        boolean sleepOK;
        try {
            long startTime = System.currentTimeMillis();
            System.err.println("startTime=" + startTime);

            // Calculating reference trajectories
            updateMessage(getClass().getSimpleName() + ": calculating reference trajectories");
            System.err.println(getClass().getSimpleName() + ": calculating reference trajectories");

            // initialize reference trajectories
            // TODO: find a way not to crash at gov.nasa.arc.atc.simulation.SimulationContext.lambda$11(SimulationContext.java:364)
            context.setSimulationDuration(Integer.MAX_VALUE);

            final int simDuration = nbSteps * stepDuration;
            List<SimulatedSlotMarker> slots = context.getAllSlots().stream().filter(slot -> slot.getStartTime() <= simDuration).collect(Collectors.toList());
            double size = slots.size();
            double nbDone = 0.0;

            for (SimulatedSlotMarker slot : slots) {
                // ensure slot flight plan consistency
                //TODO find right place to put it / or/and fix flight plan creation
                ATCNode depNode = slot.getFlightPlan().getFirstWaypoint();
                if (depNode != null && context.getGeography().isRunway(depNode.getName())) {
                    slot.getFlightPlan().setDepartureRunway((Runway) context.getGeography().getNodeByName(depNode.getName()));
                }
                ATCNode arrNode = slot.getFlightPlan().getLastWaypoint();
                if (arrNode != null && context.getGeography().isRunway(arrNode.getName())) {
                    slot.getFlightPlan().setArrivalRunway((Runway) context.getGeography().getNodeByName(arrNode.getName()));
                }

                final SimulatedTrajectory trajectory = new SlotTrajectory(context.getGeography(), slot, Integer.MAX_VALUE);

                context.addCalculatedTrajectory(slot, trajectory);
                nbDone++;
                final double progress = nbDone / size;
                updateProgress(progress, 1.0);
                firePropertyChange(SimulatedTrajectoryFactory.PROGRESS_UPDATE, null, progress);
            }

            // context.calculateReferenceTrajectories();

            System.err.println("updateActiveSlots");
            // update context
            // context.updateActiveSlots();

            // Generating the arrival and departure sequences
            updateMessage("Generating arrival and departure sequences");

            // set simulation configuration
            algorithms.forEach(algo -> algo.setSimulationConfiguration("??", nbSteps, stepDuration));

            // main simulation loop
            for (int i = 0; i < nbSteps; i = i + stepDuration) {
                // ???? for what??
                System.err.println("---  Loop:: " + i);
                LOG.log(Level.INFO, "Simulation LOOP : {0}", i);
                clock.incrSimulationTime(1);
                // update context
                final boolean needAlgo = context.updateActiveSlots();
                //
                if (needAlgo) {
                    LOG.log(Level.INFO, "> t={0}  needAlgo... : ", i);
                    invokeAlgorithms(i);
                }
                updateProgress(i, nbSteps);
                firePropertyChange(NEXT_SIMULATION_STEP, null, i);
                // sleep so HMI is responsive
                sleepOK = sleep();
                // check if ok and optimize
                if (!sleepOK) {
                    return null;
                }
            }
            long stopTime = System.currentTimeMillis();
            double runDuration = ((double) (stopTime - startTime)) / 1000.0;
            firePropertyChange(SIMULATION_COMPLETED, null, runDuration);
        } catch (RuntimeException e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "RuntimeException in simuation:: {0}", e);
            updateMessage("Cancelled");
            failed();
        }
        System.err.println("DONE");
        return null;
    }

    private void invokeAlgorithms(final int simulationTime) {
        boolean isInvocationOK;
        for (Algorithm algorithm : algorithms) {
            System.err.println(" invoking algorithm :: " + algorithm);
            isInvocationOK = algorithm.execute(simulationTime);
            if (!isInvocationOK) {
                LOG.log(Level.WARNING, "Invocation of {0} is NOT ok", algorithm);
                throw new IllegalArgumentException("Invocation of " + algorithm + " is NOT ok");
            }
        }
    }

    private boolean sleep() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException interrupted) {
            LOG.log(Level.SEVERE, "InterruptedException: {0}", interrupted);
            if (isCancelled()) {
                updateMessage("Cancelled");
                return false;
            }
            // Restore the interrupted status
            Thread.currentThread().interrupt();
        }
        return true;
    }

}
