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

package gov.nasa.arc.atc.core;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.ControllerHandOff;
import gov.nasa.arc.atc.ControllerInitBlock;
import gov.nasa.arc.atc.FlightPlanUpdate;
import gov.nasa.arc.atc.export.AfoUpdateLogger;
import gov.nasa.arc.atc.simulation.SeparationViolation;

import java.util.*;


/**
 * @author ahamon
 */
public class DataModelInput {

    private final List<SimulatedElement> simulatedElements;
    private final SortedSet<Integer> allTimePoints;
    private final Map<Integer, List<SeparationViolation>> allSeparationViolators;
    private final Map<String, Map<Integer, AfoUpdate>> allUpdates;
    private final List<ControllerInitBlock> controllerInitializationBlocks;
    private final List<FlightPlanUpdate> flightPlanUpdates;
    private final Map<Integer, List<ControllerHandOff>> handOffs;
    private final Map<String, Integer> departureTimes;

    private final Map<String, String> afoTypes;
    private DepartureQueue departureQueue;

    private List<String> initDepartureQueue = new LinkedList<>();

    private int startTime = Integer.MAX_VALUE;
    private int endTime = Integer.MIN_VALUE;

    private boolean locked;

    /**
     * creates a new data model input structure
     */
    public DataModelInput() {
        simulatedElements = new ArrayList<>();
        allTimePoints = new TreeSet<>();
        allSeparationViolators = new HashMap<>();
        allUpdates = new HashMap<>();
        controllerInitializationBlocks = new ArrayList<>();
        flightPlanUpdates = new ArrayList<>();
        handOffs = new HashMap<>();
        afoTypes = new HashMap<>();
        departureTimes = new HashMap<>();
        locked = false;
    }

    /*
    * Getters
     */

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public List<SimulatedElement> getSimulatedElements() {
        return Collections.unmodifiableList(simulatedElements);
    }

    public SortedSet<Integer> getAllTimePoints() {
        return Collections.unmodifiableSortedSet(allTimePoints);
    }

    public Map<Integer, List<SeparationViolation>> getAllSeparationViolators() {
        return Collections.unmodifiableMap(allSeparationViolators);
    }

    public Map<String, Map<Integer, AfoUpdate>> getAllUpdates() {
        return Collections.unmodifiableMap(allUpdates);
    }

    public List<ControllerInitBlock> getControllerInitializationBlocks() {
        return Collections.unmodifiableList(controllerInitializationBlocks);
    }

    public List<FlightPlanUpdate> getFlightPlanUpdates() {
        return Collections.unmodifiableList(flightPlanUpdates);
    }

    public Map<Integer, List<ControllerHandOff>> getHandOffs() {
        return Collections.unmodifiableMap(handOffs);
    }

    /**
     * this method locks the data inputs (finalize) to prevent further modification of the data it contains. It also creates the simulated elements based on the updates
     */
    public void lock() {
        if (!locked) {
            createSimulatedElements();
            // add time to time tree if does not exist yet
            allUpdates.forEach((name, elementUpdates) -> elementUpdates.forEach((time, list) -> addTime(time)));
            // create departureQueue
            createDepartureQueue();
            locked = true;
        }
    }

    public void addAgentUpdates(String agentNameElem, Map<Integer, AfoUpdate> agentUpdates) {
        checkLock();
        allUpdates.put(agentNameElem, agentUpdates);
    }

    public void addControllerInitBlock(ControllerInitBlock controllerInitBlock) {
        checkLock();
        controllerInitializationBlocks.add(controllerInitBlock);
    }

    public void addSeparationViolation(SeparationViolation separationViolation) {
        checkLock();
        int time = separationViolation.getTimeStamp();
        if (!allSeparationViolators.containsKey(time)) {
            allSeparationViolators.put(time, new ArrayList<>());
        }
        allSeparationViolators.get(time).add(separationViolation);
    }

    @Deprecated
    public void addAFOUpdate(AfoUpdate afoUpdate) {
        System.err.println(" %%%%%%%% adding afo update for name "+afoUpdate.getAfoName());
        checkLock();
        if (!allUpdates.containsKey(afoUpdate.getAfoName())) {
            allUpdates.put(afoUpdate.getAfoName(), new HashMap<>());
        }
        allUpdates.get(afoUpdate.getAfoName()).put(afoUpdate.getTimeStamp(), afoUpdate);
    }

    public void addAFOUpdate(String fullName, String afoType, AfoUpdate afoUpdate) {
        checkLock();
        if (!afoTypes.containsKey(fullName)) {
            afoTypes.put(fullName, afoType);
        }
        if (!allUpdates.containsKey(fullName)) {
            allUpdates.put(fullName, new HashMap<>());
        }
        allUpdates.get(fullName).put(afoUpdate.getTimeStamp(), afoUpdate);
    }

    public void addFlightPlanUpdate(FlightPlanUpdate flightPlanUpdate) {
        checkLock();
        flightPlanUpdates.add(flightPlanUpdate);
    }

    public void addHandOff(ControllerHandOff handOff) {
        checkLock();
        if (!handOffs.containsKey(handOff.getSimulationTime())) {
            handOffs.put(handOff.getSimulationTime(), new ArrayList<>());
        }
        handOffs.get(handOff.getSimulationTime()).add(handOff);
    }

    public void addLanding(String name, int time) {
        //TODO
    }

    public void addDepartureCleared(String name, int time) {
        departureTimes.put(name, time);
    }

    public void setInitDepartureQueue(List<String> departures) {
        checkLock();
        initDepartureQueue = departures;
    }

    public DepartureQueue getDepartureQueue() {
        return departureQueue;
    }

	/*
    * Private methods
	 */

    private void checkLock() {
        if (locked) {
            throw new IllegalStateException("cannot modify DataModelInput once it is locked");
        }
    }

    private void addTime(int timeStamp) {
        checkLock();
        if (!allTimePoints.contains(timeStamp)) {
            allTimePoints.add(timeStamp);
        }
        startTime = Math.min(startTime, timeStamp);
        endTime = Math.max(endTime, timeStamp);
    }

    private List<SimulatedElement> createSimulatedElements() {
        // create ATController updates
        Map<String, Map<Integer, List<String>>> controllersUpdates = createControllersUpdates();

        // TEMP UGLY CODE
        allUpdates.forEach((name, updates) -> {
            final String simpleName;
            switch (afoTypes.get(name)) {
                case AfoUpdateLogger.PLANE_UPDATE:
                    simpleName = updates.values().iterator().next().getAfoName();
                    simulatedElements.add(new NewPlane(name, simpleName, updates,getFlightPlanUpdate(simpleName)));
                    break;
                case AfoUpdateLogger.SLOT_UPDATE:
                    simpleName = updates.values().iterator().next().getAfoName();
                    simulatedElements.add(new NewSlot(name, simpleName, updates,getFlightPlanUpdate(simpleName)));
                    break;
                default:
                    System.err.println("Not creating simulatedElement for agent:: " + name);
                    break;
            }

        });

        return simulatedElements;
    }

    private FlightPlanUpdate getFlightPlanUpdate(String name){
        //no point in changing data structure to map since has to work for both planes and slots
        for(FlightPlanUpdate update : flightPlanUpdates){
            if(update.getAFOName().contains(name)){
                return update;
            }
        }
        System.err.println(" +> return null");
        return null;
    }

    private Map<String, Map<Integer, List<String>>> createControllersUpdates() {
        Map<String, Map<Integer, List<String>>> allControllersUpdates = new HashMap<>();
        allUpdates.forEach((name, updates) -> {
                    if (AfoUpdateLogger.PLANE_UPDATE.equals(afoTypes.get(name))) {
                        updates.forEach((time, update) -> {
                            String controllerName = update.getController();
                            if (!allControllersUpdates.containsKey(controllerName)) {
                                allControllersUpdates.put(controllerName, new HashMap<>());
                            }
                            Map<Integer, List<String>> controllerUpdates = allControllersUpdates.get(controllerName);
                            if (!controllerUpdates.containsKey(time)) {
                                controllerUpdates.put(time, new ArrayList<>());
                            }
                            controllerUpdates.get(time).add(name);
                        });
                    }
                }

        );
        return allControllersUpdates;
    }

    private void createDepartureQueue() {
        List<NewPlane> departurePlanes = new LinkedList<>();
        // to be made cleaner
        for (int i = 0; i < initDepartureQueue.size(); i++) {

            String departureName = initDepartureQueue.get(i);
            for (SimulatedElement element : simulatedElements) {
                if (element instanceof NewPlane && ((NewPlane) element).getSimpleName().equals(departureName)) {
                    departurePlanes.add((NewPlane) element);
                    break;
                }
            }
        }
        departureQueue = new DepartureQueue(departurePlanes, departureTimes);
    }

}
