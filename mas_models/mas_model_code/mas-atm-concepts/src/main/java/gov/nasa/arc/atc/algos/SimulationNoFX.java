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
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.simulation.SimulationClock;
import gov.nasa.arc.atc.simulation.SimulationContext;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author ahamon
 *
 */
public final class SimulationNoFX {

	/**
	 * Name of the event sent when the simulation proceeds to the next step
	 */
	public static final String NEXT_SIMULATION_STEP = "nextSimulationStep";

	/**
	 * Name of the event sent when the simulation is completed
	 */
	public static final String SIMULATION_COMPLETED = "simulationCompleted";

	private static final Logger LOG = Logger.getGlobal();

	private final PropertyChangeSupport propertyChangeSupport;

	private final SimulationClock clock;
	private final SimulationContext context;
	private final List<Algorithm> algorithms;
	//
	private int nbSimulationSteps;

	/**
	 * Creates an empty simulation, that will last 0 steps.
	 * 
	 * @param simuContext the SimulationContext used for the simulation
   	 */
	public SimulationNoFX(SimulationContext simuContext) {
		propertyChangeSupport = new PropertyChangeSupport(SimulationNoFX.this);
		context = simuContext;
		clock = context.getClock();
		algorithms = new ArrayList<>();
		//
		nbSimulationSteps = 0;
		//
	}

	/**
	 * 
	 * @param newNumberOfSteps the number of steps that will be executed during the simulation
	 */
	public final void setSimulationNbSteps(int newNumberOfSteps) {
		if (newNumberOfSteps < 0) {
			throw new IllegalArgumentException("Cannot ask for a negative number of steps " + newNumberOfSteps);
		}
		nbSimulationSteps = newNumberOfSteps;
	}

	/**
	 * 
	 * @return the number of steps that will be executed during the simulation
	 */
	public final int getSimulationNbSteps() {
		return nbSimulationSteps;
	}

	/**
	 * 
	 * @return the simulation context used for the simulation
	 */
	public final SimulationContext getSimulationContext() {
		return context;
	}

	/**
	 * Starts the simulation
	 */
	public void runSimulation() {
		start();
	}

	/**
	 * 
	 * @param listener the listener to subscribe to the changes
	 */
	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * 
	 * @param listener the listener stopping the subscription to the changes
	 */
	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * 
	 * @return the simulation clock to access the current simulation step step
	 */
	protected final SimulationClock getClock() {
		return clock;
	}

	public ArrivalSequence getArrivalSequenceAt(ATCNode node) {
		return context.getArrivalSequences().get(node);
	}

	public ATCGeography getGeography() {
		return context.getGeography();
	}

	public final void start() {
		long startTime = System.currentTimeMillis();
		context.setSimulationDuration(nbSimulationSteps);
		context.calculateReferenceTrajectories();

		// update context
		context.updateActiveSlots();

		// // initialize algorithms
		// for (Algorithm algo : algorithms) {
		// algo.initiliaze(context, context.getArrivalSequences());
		// }
		// main simulation loop
		for (int i = 0; i < nbSimulationSteps; i++) {
			LOG.log(Level.INFO, "Simulation LOOP : {0}", i);
			clock.incrSimulationTime(1);

			// update context
			final boolean needAlgo = context.updateActiveSlots();
			LOG.log(Level.FINE, "Simulation needAlgo : {0}", needAlgo);
			//
			if (needAlgo) {
                //TODO: use index
				for (Algorithm algo : algorithms) {
					algo.execute(clock.getCurrentSimTime());// , context.getArrivalSequences()
				}
			}
			propertyChangeSupport.firePropertyChange(NEXT_SIMULATION_STEP, null, i);
		}
		long stopTime = System.currentTimeMillis();
		double runDuration = ((double) (stopTime - startTime)) / 1000.0;
		propertyChangeSupport.firePropertyChange(SIMULATION_COMPLETED, null, runDuration);
	}

	public void addAlgorithm(Algorithm algo) {
		algorithms.add(algo);
	}

}
