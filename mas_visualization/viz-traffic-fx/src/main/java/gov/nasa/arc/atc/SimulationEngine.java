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

package gov.nasa.arc.atc;

import gov.nasa.arc.atc.core.DataModel;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 * At the moment, this engine is only capable of managing one simulation
 *
 * @author hamon
 *
 */
public final class SimulationEngine implements PropertyChangeListener {

    public static final String SIMULATION_READY = "simulationReady";

    public static final String PLAY = "play";
    public static final String PAUSE = "pause";
    public static final String PREVIOUS_STEP = "previousStep";
    public static final String NEXT_STEP = "nextStep";
    public static final String SPEED_CHANGE = "speedChange";
    public static final String SIMULATION_DURATION = "simulationDuration";
    public static final String SIMULATION_TIME_CHANGED = "simulationTimeChanged";
    public static final String SIMULATION_JUMP_TIME = "simulationJumpTime";

    private static final Logger LOG = Logger.getGlobal();

    private static final SimulationEngine INSTANCE = new SimulationEngine();
    private static final PropertyChangeSupport PPTY_CHANGE_SUPPORT = new PropertyChangeSupport(INSTANCE);
    private static final int BASE_DELAY = 1000;

    private static Timer engineTimer;
    private static double currentSpeed;
    private static DataModel simulationDataModel;

    private SimulationEngine() {
        SimulationManager.addPropertyChangeListener(SimulationEngine.this);
        currentSpeed = 1;
        engineTimer = new Timer(BASE_DELAY, SimulationEngine::processEngineTimeTick);
    }

    private static void processEngineTimeTick(ActionEvent event) {
        LOG.log(Level.FINE, "processEngineTimeTick {0}", event);
        final int time = simulationDataModel.getSimTime();
        if (time >= simulationDataModel.getMaxSimTime()) {
            stopEngine();
            return;
        }
        SimulationEngine.playNextStep();
    }

    public static SimulationEngine getInstance() {
        return INSTANCE;
    }

    private static void setSimulationDataModel(DataModel dataModel) {
        if(dataModel==null){
            throw  new IllegalArgumentException();
        }
        simulationDataModel = dataModel;
        retreiveSimulationTimes();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case SimulationManager.DATA_MODEL_CHANGED:
                setSimulationDataModel((DataModel) event.getNewValue());
                PPTY_CHANGE_SUPPORT.firePropertyChange(SIMULATION_READY, null, true);
                break;
            case PLAY:
                play();
                break;
            case PAUSE:
                pause();
                break;
            case PREVIOUS_STEP:
                playPreviousStep();
                break;
            case NEXT_STEP:
                playNextStep();
                break;
            case SPEED_CHANGE:
                changeSpeed((double) event.getNewValue());
                break;
            case SIMULATION_JUMP_TIME:
                int newTime = (int) event.getNewValue();
                jumpTime(newTime);
                break;
            default:
                // nothing to do
                break;
        }

    }

    public static int getSimTime() {
        return SimulationEngine.getEngineTime();
    }

    private static int getEngineTime() {
        return simulationDataModel.getSimTime();
    }

    public static void stopEngine() {
        // TODO: log and find a more cleaner way
        engineTimer.stop();
    }

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        PPTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        PPTY_CHANGE_SUPPORT.removePropertyChangeListener(listener);
    }

    private static void play() {
        PPTY_CHANGE_SUPPORT.firePropertyChange(PLAY, null, null);
        engineTimer.start();
    }

    private static void pause() {
        PPTY_CHANGE_SUPPORT.firePropertyChange(PAUSE, null, null);
        engineTimer.stop();
    }

    private static void changeSpeed(double newSpeed) {
        currentSpeed = newSpeed;
        engineTimer.setDelay((int) (BASE_DELAY / currentSpeed));
        PPTY_CHANGE_SUPPORT.firePropertyChange(SPEED_CHANGE, null, currentSpeed);
    }

    private static void retreiveSimulationTimes() {
        LOG.log(Level.INFO, "Loading simulation data in engine");
        PPTY_CHANGE_SUPPORT.firePropertyChange(SIMULATION_DURATION, null, simulationDataModel.getSimulationDuration());
    }

    private static void playNextStep() {
        final int time = simulationDataModel.incrementTime();
        LOG.log(Level.FINE, "Engines plays next step :: {0}", time);
        PPTY_CHANGE_SUPPORT.firePropertyChange(SIMULATION_TIME_CHANGED, null, time);
    }

    private static void playPreviousStep() {
        final int time = simulationDataModel.decrementTime();
        LOG.log(Level.FINE, "Engines plays previous step :: {0}", time);
        PPTY_CHANGE_SUPPORT.firePropertyChange(SIMULATION_TIME_CHANGED, null, time);
    }

    private static void jumpTime(int newTime) {
        final int time = simulationDataModel.setSimTime(newTime);
        LOG.log(Level.INFO, "Engines plays next step :: {0}", newTime);
        PPTY_CHANGE_SUPPORT.firePropertyChange(SIMULATION_TIME_CHANGED, null, time);
    }

    public static void jumpToTime(int time) {
        SimulationEngine.jumpTime(time);
    }

}
