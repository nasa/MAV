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

package gov.nasa.arc.atc.simulation;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.airborne.ArrivalSequence;
import gov.nasa.arc.atc.airborne.DepartureSequence;
import gov.nasa.arc.atc.factories.WeatherConditionsFactory;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import gov.nasa.arc.atc.physics.WeatherConditions;
import gov.nasa.arc.atc.tmpsimplification.IsDepartureDebug;
import javafx.util.Pair;

/**
 * @author ahamon
 */
public class SimulationContext {

    private static final Logger LOG = Logger.getGlobal();

    private final List<SimulatedSlotMarker> allSlots;
    //
    private final Map<String, List<SimulatedSlotMarker>> allArrivals;
    private final Map<String, List<SimulatedSlotMarker>> allDepartures;
    //
    private final Map<SimulatedSlotMarker, SimulatedTrajectory> allTrajectories;
    // TODO: define classes for these
    private final Map<String, List<Pair<SimulatedTrajectory, Integer>>> landings;
    private final Map<String, List<Pair<SimulatedTrajectory, Integer>>> takeOffs;
    //
    private final List<SimulatedSlotMarker> activeSlots;
    private final ATCGeography atcGeography;
    private final WeatherConditions weatherConditions;
    private final SimulationClock clock;
    //
    // Sequencing
    private final Map<ATCNode, ArrivalSequence> arrivalSequences;
    private final Map<ATCNode, DepartureSequence> departureSequences;

    // simulation duration for trajectory calculations
    private int simDuration = -1;

    /**
     * @param geography  the geography used for the simulation context so its elements can evolve in
     * @param conditions the weather conditions used for the simulation
     */
    public SimulationContext(ATCGeography geography, WeatherConditions conditions) {

//        try {
//            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
//            List<String> arguments = runtimeMxBean.getInputArguments();
//            arguments.forEach(a -> System.out.println(" &&& --------------- " + a));
//            //
//            System.getenv().forEach((e, v) -> System.out.println(" *** *** ------------- " + e + " // " + v));
//            //
//            System.getProperties().forEach((p, v) -> System.out.println(" $$$ --------------- " + p.toString() + " // " + v.toString()));
//            //
//            String current = new java.io.File(".").getCanonicalPath();
//            System.out.println("   @@ ! ------- Current dir:" + current);
//            String currentDir = System.getProperty("user.dir");
//            System.out.println("   @@ ! ------- Current dir using System:" + currentDir);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        LOG.log(Level.INFO, "Creation of new SimulationContext in progress");

        System.err.println("* Creating simulation context");

        allSlots = new ArrayList<>();
        allArrivals = new HashMap<>();
        allDepartures = new HashMap<>();
        allTrajectories = new HashMap<>();
        //
        landings = new HashMap<>();
        takeOffs = new HashMap<>();
        //
        activeSlots = new ArrayList<>();
        arrivalSequences = new HashMap<>();
        departureSequences = new HashMap<>();
        atcGeography = geography;
        weatherConditions = conditions;
        clock = new SimulationClock();
        initAllArrDeps();
        initLandingTakeOffs();

//        System.err.println(" departures: " + departureSequences.keySet().stream().map(ATCNode::getName).collect(Collectors.joining(", ")));
//        System.err.println(" arrivals: " + arrivalSequences.keySet().stream().map(ATCNode::getName).collect(Collectors.joining(", ")));

        System.err.println("** end of creation");
    }

    /**
     * @param geography the geography used for the simulation context so its elements can evolve in
     */
    public SimulationContext(ATCGeography geography) {
        this(geography, WeatherConditionsFactory.createDefaultConditions());
    }

    /**
     * @return the geography used for the simulation context
     */
    public ATCGeography getGeography() {
        return atcGeography;
    }

    /**
     * @param slotsToAdd simulatedSlots to populate the context
     */
    public void addSlots(List<SimulatedSlotMarker> slotsToAdd) {

//        System.err.println(" -- adding slots " + slotsToAdd.stream().map(slot -> "[" + slot.getName() + " s=" + slot.getStartTime() + " d=" + slot.getDepartureTime() + "]").collect(Collectors.joining(", ")));
        // TODO: optimize
        slotsToAdd.forEach(this::updateGeographyOnNewSlot);
        slotsToAdd.forEach(allSlots::add);
        slotsToAdd.forEach(slot -> {
            // TODO: add some checks on size...


            String arrRName = slot.getFlightPlan().getArrivalRunway() != null ? slot.getFlightPlan().getArrivalRunway().getName() : null;
            if (arrRName != null) {
                if (!allArrivals.containsKey(arrRName)) {
                    allArrivals.put(arrRName, new LinkedList<>());
                }
                allArrivals.get(arrRName).add(slot);
            }
            String depRName = slot.getFlightPlan().getDepartureRunway() != null ? slot.getFlightPlan().getDepartureRunway().getName() : null;
            if (depRName != null) {
                if (!allDepartures.containsKey(depRName)) {
                    allDepartures.put(depRName, new LinkedList<>());
                }
                allDepartures.get(depRName).add(slot);
            }


        });
        //
        arrivalSequences.clear();
        // creating arrival sequences
        atcGeography.getArrivalNodes().forEach(node -> arrivalSequences.put(node, new ArrivalSequence(node)));
        // creating departure sequences
        atcGeography.getDepartureNodes().forEach(node -> departureSequences.put(node, new DepartureSequence(node)));

//        // ??
//        calculateReferenceTrajectories();


//        System.err.println(" departures: " + departureSequences.keySet().stream().map(ATCNode::getName).collect(Collectors.joining(", ")));
//        System.err.println(" arrivals: " + arrivalSequences.keySet().stream().map(ATCNode::getName).collect(Collectors.joining(", ")));
//        departureSequences.forEach((node, seq) -> System.err.println(" Dep @" + node + " :: " + seq.getSimulatedTrajectories().stream().map(t -> t.getSlotMarker().getName()).collect(Collectors.joining(", "))));
//        arrivalSequences.forEach((node, seq) -> System.err.println(" Arr @" + node + " :: " + seq.getSimulatedTrajectories().stream().map(t -> t.getSlotMarker().getName()).collect(Collectors.joining(", "))));
//        System.err.println(" ---- end adding slots ");
    }

    public void setSimulationDuration(final int simulationDuration) {
        simDuration = simulationDuration;
    }

    public void calculateReferenceTrajectories() {
        //
        if (simDuration < 0) {
            throw new IllegalStateException("simulation duration has not been set in context");
        }
        //
        allTrajectories.clear();
        allSlots.stream().filter(slot -> slot.getStartTime() <= simDuration).forEach(slot -> {
            allTrajectories.put(slot, new SlotTrajectory(atcGeography, slot, simDuration));
            String arrRName = slot.getFlightPlan().getArrivalRunway() != null ? slot.getFlightPlan().getArrivalRunway().getName() : null;
            if (arrRName != null) {
                allArrivals.get(arrRName).add(slot);
            }
            String depRName = slot.getFlightPlan().getDepartureRunway() != null ? slot.getFlightPlan().getDepartureRunway().getName() : null;
            if (depRName != null) {
                allDepartures.get(depRName).add(slot);
            }

        });
    }

    // TODO clean API (this is a temp fix)
    public void addCalculatedTrajectory(SimulatedSlotMarker slot, SimulatedTrajectory trajectory) {

        String arrRName = slot.getFlightPlan().getArrivalRunway() != null ? slot.getFlightPlan().getArrivalRunway().getName() : null;
        if (arrRName != null) {
            allArrivals.get(arrRName).add(slot);
        }
        String depRName = slot.getFlightPlan().getDepartureRunway() != null ? slot.getFlightPlan().getDepartureRunway().getName() : null;
        if (depRName != null) {
            allDepartures.get(depRName).add(slot);
        }
        allTrajectories.put(slot, trajectory);
    }

    /**
     * @return all the simulated slots of the context, whether they are active or not
     */
    public List<SimulatedSlotMarker> getAllSlots() {
        return Collections.unmodifiableList(allSlots);
    }

    /**
     * @return the list of the active slots, depending on the context's clock simulation time
     */
    public List<SimulatedSlotMarker> getActiveSlots() {
        return Collections.unmodifiableList(activeSlots);
    }

    public Map<String, List<SimulatedSlotMarker>> getAllArrivals() {
        return Collections.unmodifiableMap(allArrivals);
    }

    public Map<String, List<SimulatedSlotMarker>> getAllDepartures() {
        return Collections.unmodifiableMap(allDepartures);
    }

    public boolean updateActiveSlots(List<AFO> afos) {
        //
        if (simDuration < 0) {
            throw new IllegalStateException("simulation duration has not been set in context");
        }
        // process each slot to see if already exists
        for (AFO afo : afos) {
            boolean contains = false;
            for (SimulatedSlotMarker simSlotMarker : allSlots) {
                if (simSlotMarker.getName().equals(afo.getName())) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                final SimulatedSlotMarker newSimSlotMarker = new SimulatedSlotMarker(afo);
                updateGeographyOnNewSlot(newSimSlotMarker);
                allSlots.add(newSimSlotMarker);
                SlotTrajectory trajectory = new SlotTrajectory(atcGeography, newSimSlotMarker, simDuration);
                allTrajectories.put(newSimSlotMarker, trajectory);
            }
        }
        return updateActiveSlots();
    }

    /**
     * @return true is the activated slots have changed during the update
     */
    public boolean updateActiveSlots() {
        LOG.log(Level.FINE, "updateActiveSlots in progress");
        final int oldNbPanes = activeSlots.size();
        // in case the simulation time is set to a previous time or a later time
        activeSlots.clear();
        landings.forEach((node, list) -> list.clear());
        takeOffs.forEach((node, list) -> list.clear());
        arrivalSequences.values().forEach(this::cleanArrivalSequence);
        departureSequences.values().forEach(this::cleanDepartureSequence);
        //
        allSlots.forEach(this::processSlotStatus);
        //
        // re order (needed since update(slots) needed for Brahms)
        arrivalSequences.forEach((node, sequence) -> sequence.reOrder());
        departureSequences.forEach((node, sequence) -> sequence.reOrder());
//        rebuildSequences();
        // hum
        // what if minus one arrival plus one departure... possible?
        final int newNbPlanes = activeSlots.size();
        return newNbPlanes != oldNbPanes;
    }

    public Map<String, List<Pair<SimulatedTrajectory, Integer>>> getLandings() {
        return Collections.unmodifiableMap(landings);
    }

    public Map<String, List<Pair<SimulatedTrajectory, Integer>>> getTakeOffs() {
        return Collections.unmodifiableMap(takeOffs);
    }

    private void initAllArrDeps() {
        // TODO: once ATCLib is refactored, merge with method initLandingtakeoffs
        atcGeography.getAirports().forEach(airport -> airport.getRunways().forEach(runway -> {
            allArrivals.put(runway.getName(), new ArrayList<>());
            allDepartures.put(runway.getName(), new ArrayList<>());

        }));
    }

    private void initLandingTakeOffs() {
        atcGeography.getAirports().forEach(airport -> airport.getRunways().forEach(runway -> {
            if (!landings.keySet().contains(runway.getName())) {
                landings.put(runway.getName(), new ArrayList<>());
            }
            if (!takeOffs.keySet().contains(runway.getName())) {
                takeOffs.put(runway.getName(), new ArrayList<>());
            }
        }));
    }

    private void updateGeographyOnNewSlot(SimulatedSlotMarker slot) {
        if (IsDepartureDebug.isDeparture(slot)) {
            atcGeography.addDepartureNode(slot.getFlightPlan().getFirstWaypoint());
        } else if (IsDepartureDebug.isDArrival(slot)) {
            atcGeography.addArrivalNode(slot.getFlightPlan().getLastWaypoint());
        }
        // TODO
        //

        atcGeography.getAirports().forEach(airport -> airport.getRunways().forEach(runway -> {
            if (!landings.keySet().contains(runway.getName())) {
                landings.put(runway.getName(), new ArrayList<>());
            }
            if (!takeOffs.keySet().contains(runway.getName())) {
                takeOffs.put(runway.getName(), new ArrayList<>());
            }
        }));
    }

    private void processSlotStatus(SimulatedSlotMarker slot) {

        final int currentSimTime = clock.getCurrentSimTime();
        boolean addToActive = false;

//        System.err.println("----");
//        System.err.println(" @t=" + currentSimTime);
//        System.err.println(" Processing slot " + slot.getName());

        if (slot.getFlightPlan().getArrivalRunway() != null && allTrajectories.containsKey(slot) && allTrajectories.get(slot).getArrivalTime() < currentSimTime) {
//            System.err.println(" > is landed   at " + allTrajectories.get(slot).getArrivalTime());
            addSlotToLanded(slot);
        } else if (slot.getStartTime() <= currentSimTime) {
            // active but not landed
            addToActive = true;
            // populate the correct arrival queue
            arrivalSequences.forEach((node, sequence) -> {
                if (node.getName().equals(slot.getFlightPlan().getLastWaypoint().getName()) && allTrajectories.containsKey(slot) && allTrajectories.get(slot).getArrivalTimeAtNode(node.getName()) > clock.getCurrentSimTime()) {
                    sequence.addSimulatedTrajectory(allTrajectories.get(slot));
                }
            });
//            System.err.println(" > is active 1");
        }
        if (slot.getFlightPlan().getDepartureRunway() != null && slot.getDepartureTime() >= 0 && slot.getDepartureTime() < currentSimTime) {
//            System.err.println(" > is took off depT=" + slot.getDepartureTime());
            addSlotToTookOff(slot);
        } else if (slot.getStartTime() <= currentSimTime) {
            // active but did not take off
            addToActive = true;
            // populate the correct departure queue
            departureSequences.forEach((node, sequence) -> {
                // if slot departs form this node
                if (node.getName().equals(slot.getFlightPlan().getFirstWaypoint().getName())) {
                    sequence.addSimulatedTrajectory(allTrajectories.get(slot));
                }
            });

//            System.err.println(" > is active 2");
        }
        if (addToActive) {
            activeSlots.add(slot);
        }
//        System.err.println("----");


    }


    private void addSlotToLanded(SimulatedSlotMarker slot) {
        ATCNode arrNode = slot.getFlightPlan().getArrivalRunway();
        final SimulatedTrajectory traj = allTrajectories.get(slot);
        landings.get(arrNode.getName()).add(new Pair<>(traj, traj.getArrivalTime()));
    }

    private void addSlotToTookOff(SimulatedSlotMarker slot) {
        ATCNode depNode = slot.getFlightPlan().getDepartureRunway();
//        System.err.println("addSlotToTookOff +> ");
//        System.err.println("    depNode : " + depNode);
//        System.err.println("    depNode : " + depNode);
//        System.err.println("    depNode : " + depNode);
//        System.err.println("    depNode getName: " + depNode.getName());
        getTakeOffsAtNode(depNode.getName()).add(new Pair<>(allTrajectories.get(slot), slot.getDepartureTime()));
    }

    public List<Pair<SimulatedTrajectory, Integer>> getLandingsAtNode(String name) {
        for (Map.Entry<String, List<Pair<SimulatedTrajectory, Integer>>> entry : landings.entrySet()) {
            if (entry.getKey().equals(name)) {
                return entry.getValue();
            }
        }
        return Collections.emptyList();
    }

    private List<Pair<SimulatedTrajectory, Integer>> getTakeOffsAtNode(String name) {
        for (Map.Entry<String, List<Pair<SimulatedTrajectory, Integer>>> entry : takeOffs.entrySet()) {
            if (entry.getKey().equals(name)) {
                return takeOffs.get(entry.getKey());
            }
        }
        return Collections.emptyList();
    }

    /**
     * removes the slots that should not be in this sequence due to greater starting time
     *
     * @param sequence the sequence to cleaned
     */
    private void cleanArrivalSequence(ArrivalSequence sequence) {
        sequence.clear();
    }

    /**
     * @param sequence the sequence to cleaned
     */
    private void cleanDepartureSequence(DepartureSequence sequence) {
        sequence.clear();
    }

//    private void addSlotToSequences(SimulatedSlotMarker simSlot) {
//        // TODO: test on traffic type, reformat stream for more testing inside and optimization
//        arrivalSequences.forEach((node, sequence) -> {
//            if (node.getName().equals(simSlot.getFlightPlan().getLastWaypoint().getName()) && allTrajectories.containsKey(simSlot) && allTrajectories.get(simSlot).getArrivalTimeAtNode(node.getName()) > clock.getCurrentSimTime()) {
//                sequence.addSimulatedTrajectory(allTrajectories.get(simSlot));
//            }
//        });
//
//        departureSequences.forEach((node, sequence) -> {
//            // if slot departs form this node
//            if (node.getName().equals(simSlot.getFlightPlan().getFirstWaypoint().getName())) {
//                sequence.addSimulatedTrajectory(allTrajectories.get(simSlot));
//            }
//        });
//    }

    public List<SimulatedTrajectory> getAllArrivalTrajectories() {
        return allTrajectories.entrySet().stream().filter(e -> e.getKey().getFlightPlan().getArrivalRunway() != null).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public List<SimulatedTrajectory> getAllDepartureTrajectories() {
        return allTrajectories.entrySet().stream().filter(e -> e.getKey().getFlightPlan().getDepartureRunway() != null).map(Map.Entry::getValue).collect(Collectors.toList());
    }


    /**
     * @param name slot's name to find
     * @return the actual slot
     */
    public SimulatedSlotMarker getSlot(String name) {
        for (SimulatedSlotMarker slot : allSlots) {
            if (slot.getName().equals(name)) {
                return slot;
            }
        }
        return null;
    }

    /**
     * @return a map containing all the arrival nodes of the context's geography, and for each node, its arrival sequence
     */
    public final Map<ATCNode, ArrivalSequence> getArrivalSequences() {
        return Collections.unmodifiableMap(arrivalSequences);
    }

    /**
     * @return a map containing all the departure nodes of the context's geography, and for each node, its departure sequence
     */
    public final Map<ATCNode, DepartureSequence> getDepartureSequences() {
        return Collections.unmodifiableMap(departureSequences);
    }

    /**
     * @return the simulation clock representing the time of the simulation
     */
    public SimulationClock getClock() {
        return clock;
    }

}
