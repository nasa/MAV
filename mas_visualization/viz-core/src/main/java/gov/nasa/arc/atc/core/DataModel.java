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
import gov.nasa.arc.atc.FlightPlanUpdate;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.algos.dsas.NamedArrivalGap;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Route;
import gov.nasa.arc.atc.simulation.SeparationViolation;
import gov.nasa.arc.atc.utils.Constants;

import java.util.*;

/**
 * @author aWallace
 * @author ahamon
 */
public class DataModel {

    // constructor parameters
    private final DataModelInput inputs;
    //
    private final List<ATCNode> allWaypoints;

    // data built on load
    private final List<NewPlane> allPlanes;
    private final List<NewPlane> departingPlanes;
    private final List<NewPlane> arrivingPlanes;
    private final List<NewSlot> slots;
    private final Map<Integer, List<DepartureInfo>> departuresInfos;
    private Map<Integer, List<NamedArrivalGap>> arrivalGaps;
    private List<Route> mainRoutes;
    private List<Route> allRoutes;

    // TO REFACTOR
    private final List<String> departureQueue;
    private final Map<String, NewPlane> departureMap;
    private final Map<String, Integer> planeDepartTime;
    private final Map<String, Double> planeETA;
    private final Map<String, NewSlot> arrivingSlots;
    private final List<Controller> atControllers;
    //
    private int maxTimeIndex;
    private int currentTimeValue = 0;
    // current index of timePoint list to set the simTime to
    private int timePointsIndex;

    private DataModel ghostModel = null;

    /**
     * @param dataInputs the inputs needed to build the {@link DataModel}
     */
    public DataModel(DataModelInput dataInputs) {
        inputs = dataInputs;
        inputs.lock();
        allWaypoints = SimulationManager.getATCGeography().getWaypoints();

        //calculations
//		setSimulationCalculations(new SimulationCalculations(Collections.unmodifiableMap(inputs.getAllUpdates()), createSectorMap(inputs.getControllerInitializationBlocks())) );
        //
        allPlanes = new ArrayList<>();
        departingPlanes = new ArrayList<>();
        arrivingPlanes = new ArrayList<>();
        slots = new ArrayList<>();
        departuresInfos = new HashMap<>();
        //
        departureQueue = new ArrayList<>();
        departureMap = new HashMap<>();
        planeDepartTime = new HashMap<>();
        planeETA = new HashMap<>();
        arrivingSlots = new HashMap<>();
        atControllers = new ArrayList<>();
        //
        timePointsIndex = 0;

        load();
    }


    private void load() {
        // hum....
        inputs.getSimulatedElements().forEach(element -> {
            if (element instanceof NewPlane) {
                NewPlane p = (NewPlane) element;
                allPlanes.add(p);
                //TODO
                System.err.println("TODO::inDataModel isDeparture");
//                if (p.isDeparture()) {
//                    addDepartingPlane(p);
//                } else {
//                    addArrivingPlane(p);
//                }
            } else if (element instanceof NewSlot) {
                NewSlot s = (NewSlot) element;
                addSlot(s);
            } else if (element instanceof Controller) {
                Controller c = (Controller) element;
                addController(c);
            }
        });
        //maxSimTime = inputs.getAllTimePoints().last();
        maxTimeIndex = inputs.getAllTimePoints().size();
        //
        buildDepartureQueue();
        arrivalGaps = DataCalculationUtilities.calculateArrivalGaps(this);
        //
        currentTimeValue = inputs.getStartTime();
        // build routes
        buildRoutes();
        buildFlightPlans();
    }


    //=======================================================
    // organize??
//	private void setSimulationCalculations(SimulationCalculations calculations) {
////		DisplayViewConfigurations.setSimulationCalculations(calculations);
//		//TODO
//	}


    //TODO: remove if works in simulation calculations
//	//note : this only works when a scenario that has controller init blocks is loaded
//	private Map<String,String> createSectorMap(List<ControllerInitBlock> controllerInitializationBlocks){
//		Map<String,String> controllerToSectorMap = new HashMap<>();
//		Map<String,String> waypointToControllerMap = new HashMap<>();
//		for(ControllerInitBlock block : controllerInitializationBlocks){
//			String controller = block.getControllerName();
//			String toWaypoint = block.getHandOffWaypoint();
//			waypointToControllerMap.put(toWaypoint,controller);
//		}
//		for(ATCNode waypoint : allWaypoints){
//			if (waypointToControllerMap.get(waypoint.getName()) != null){
//				Sector sector = calculateSectorFromCoordinate(waypoint.getLatitude(),waypoint.getLongitude());
//				String sectorName;
//				if(sector == null){
//					sectorName = "outside of sectors";
//				}else{
//					sectorName = sector.getName();
//				}
//				controllerToSectorMap.put( waypointToControllerMap.get(waypoint.getName()), sectorName );
//			}
//		}
//		return controllerToSectorMap;
//	}

//	private Sector calculateSectorFromCoordinate(double latitude, double longitude){
//		List<Sector> sectors = SimulationManager.getATCGeography().getSectors();
//		for(Sector sectorToCheck : sectors){
//			if(sectorToCheck.containsCoordinate(latitude,longitude)){
//				return sectorToCheck;
//			}
//		}
//		return null;
//	}
    //=======================================================


    public DataModelInput getInputs() {
        return inputs;
    }

    public List<ATCNode> getAllWaypoints() {
        return Collections.unmodifiableList(allWaypoints);
    }

    /**
     * @return all the {@link SimulatedElement} in the {@link DataModel}
     */
    public List<SimulatedElement> geSimulatedElements() {
        return Collections.unmodifiableList(inputs.getSimulatedElements());
    }

    public List<NewPlane> getAllPlanes() {
        return Collections.unmodifiableList(allPlanes);
    }

    public List<NewSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    public List<Controller> getATControllers() {
        return Collections.unmodifiableList(atControllers);
    }

    public SortedSet<Integer> getTimePoints() {
        return Collections.unmodifiableSortedSet(inputs.getAllTimePoints());
    }

//    public List<String> getDepartureQueue() {
//        return Collections.unmodifiableList(departureQueue);
//    }

    public Map<Integer, List<SeparationViolation>> getSeparationViolators() {
        return Collections.unmodifiableMap(inputs.getAllSeparationViolators());
    }

    public List<NewPlane> getDepartingPlanes() {
        return Collections.unmodifiableList(departingPlanes);
    }

    public List<NewPlane> getArrivingPlanes() {
        return Collections.unmodifiableList(arrivingPlanes);
    }

    public List<Route> getMainRoutes() {
        //already unmodifiable
        return mainRoutes;
    }

    public DepartureQueue getDepartureQueue() {
        return inputs.getDepartureQueue();
    }

    public List<Route> getAllRoutes() {
        return Collections.unmodifiableList(allRoutes);
    }

    public Map<String, Map<Integer, AfoUpdate>> getAllDataUpdates() {
        return Collections.unmodifiableMap(inputs.getAllUpdates());
    }

    public Map<Integer, List<NamedArrivalGap>> getArrivalGaps() {
        return Collections.unmodifiableMap(arrivalGaps);
    }

    public Map<Integer, List<ControllerHandOff>> getHandOffs() {
        return Collections.unmodifiableMap(inputs.getHandOffs());
    }

    public int getSimTime() {
        return currentTimeValue;
    }

    public int getMinSimTime() {
        return inputs.getStartTime();
    }

    public int getMaxSimTime() {
        return inputs.getEndTime();
    }

    public int getSimulationDuration() {
        return inputs.getAllTimePoints().last();
    }

    public int incrementTime() {
        if (timePointsIndex < maxTimeIndex) {
            timePointsIndex++;
        }
        currentTimeValue = (int) inputs.getAllTimePoints().toArray()[timePointsIndex];
        updateModel();
        return currentTimeValue;
    }

    public int decrementTime() {
        if (timePointsIndex > 0) {
            timePointsIndex--;
        }
        currentTimeValue = (int) inputs.getAllTimePoints().toArray()[timePointsIndex];
        updateModel();
        return currentTimeValue;
    }

    public int setSimTime(int newtime) {
        if (inputs.getAllTimePoints().contains(newtime)) {
            for (int i = 0; i < maxTimeIndex; i++) {
                // hum...
                int time = (int) inputs.getAllTimePoints().toArray()[i];
                if (time == newtime) {
                    currentTimeValue = time;
                    timePointsIndex = i;
                    updateModel();
                    return currentTimeValue;
                }
            }
        }
        return currentTimeValue;
    }

    public NewPlane getCorrespondingPlane(NewSlot slot) {
        // TODO: use a map
        for (NewPlane p : allPlanes) {
            if (p.getSimpleName().equals(slot.getSimpleName())) {
                return p;
            }
        }
        return null;
    }

    public List<DepartureInfo> getDeparturesQueue(int time) {
        return Collections.unmodifiableList(departuresInfos.get(time));
    }

	/*
     * PRIVATE METHODS
	 */

    private void buildDepartureQueue() {

        // for each simulation tine
        inputs.getAllTimePoints().forEach(time -> {
            final List<DepartureInfo> departures = new ArrayList<>();

            // for each plane
            departingPlanes.forEach(plane -> {
                // get departure time
                AfoUpdate update = inputs.getAllUpdates().get(plane.getFullName()).get(time);
                if (update != null) {
                    int startTime = update.getStartTime();
                    int status = update.getStatus();
                    // TODO take care of initial departure time
                    if (startTime >= time && status == Constants.ON_GROUND) {
                        departures.add(new DepartureInfo(plane.getSimpleName(), startTime, startTime, departures.size()));
                    }
                }
            });

            departuresInfos.put(time, departures);
        });
        //
    }

    private void addArrivingPlane(NewPlane plane) {
        int index = 0;
        for (int i = 0; i < arrivingPlanes.size(); i++) {
            NewPlane p = arrivingPlanes.get(i);
            if (plane.getEta() <= p.getEta()) {
                index = i;
                break;
            } else
                index = i + 1;
        }
        arrivingPlanes.add(index, plane);
        planeETA.put(plane.getFullName(), plane.getEta());
    }

    private void addDepartingPlane(NewPlane plane) {
        // TODO Refactor based on departure Time
        int index = 0;
        for (int i = 0; i < departingPlanes.size(); i++) {
            NewPlane p = departingPlanes.get(i);
            if (plane.getStartTime() <= p.getStartTime()) {
                index = i;
                break;
            } else
                index = i + 1;
        }
        //
        departureMap.put(plane.getFullName(), plane);
        departingPlanes.add(index, plane);
        planeDepartTime.put(plane.getFullName(), plane.getStartTime());
    }

    private void addSlot(NewSlot s) {
        arrivingSlots.put(s.getFullName(), s);
        slots.add(s);
    }

    private void addController(Controller c) {
        atControllers.add(c);
    }

    private void updateModel() {
        getAllPlanes().forEach(plane -> plane.update(currentTimeValue));
        getSlots().forEach(slot -> slot.update(currentTimeValue));
        if (hasGhostModel()) {
            getGhostModel().getAllPlanes().forEach(plane -> plane.update(currentTimeValue));
            getGhostModel().getSlots().forEach(slot -> slot.update(currentTimeValue));
        }
    }

    public void setGhostModel(DataModel ghostDataModel) {
        ghostModel = ghostDataModel;
    }

    public boolean hasGhostModel() {
        return ghostModel != null;
    }

    public Controller getGhostController(Controller c) {
        if (hasGhostModel()) {
            for (Controller atC : ghostModel.atControllers) {
                if (atC.getName().equals(c.getName())) {
                    return atC;
                }
            }
        }
        return null;
    }

    public NewSlot getGhostSlot(NewSlot s) {
        if (hasGhostModel()) {
            return ghostModel.arrivingSlots.get(s.getFullName());
        }
        return null;
    }

    public NewPlane getGhostPlane(NewPlane p) {
        if (hasGhostModel()) {
            for (NewPlane plane : ghostModel.allPlanes) {
                if (plane.getFullName().equals(p.getFullName())) {
                    return plane;
                }
            }
        }
        return null;
    }

    public DataModel getGhostModel() {
        return ghostModel;
    }


    public List<FlightPlanUpdate> getFlighPlanUpdates() {
        return Collections.unmodifiableList(inputs.getFlightPlanUpdates());
    }

	/*
    Private methods
	 */

    private void buildRoutes() {
        allRoutes = new LinkedList<>();
        for (FlightPlanUpdate flightPlanUpdate : inputs.getFlightPlanUpdates()) {
            final Route route = createRoute(flightPlanUpdate.getFlightSegments());
            if (!allRoutes.contains(route)) {
                allRoutes.add(route);
            }
        }
        List<Route> filteredRoutes = new LinkedList<>(allRoutes);
        for (Route r1 : allRoutes) {
            for (Route r2 : allRoutes) {
                if (r1.isContained(r2) && !r1.equals(r2) && filteredRoutes.contains(r1)) {
                    filteredRoutes.remove(r1);
                }
            }
        }
        mainRoutes = Collections.unmodifiableList(filteredRoutes);
    }

    private Route createRoute(List<FlightSegment> segments) {
        Route route = new Route();
        // if no segment
        if (segments.isEmpty()) {
            return route;
        }
        //not performance optimized?
        List<FlightSegment> clone = new LinkedList<>(segments);
        List<FlightSegment> orderedSegments = new ArrayList<>();
        FlightSegment s0 = segments.get(0);
        orderedSegments.add(s0);
        clone.remove(s0);
        while (!clone.isEmpty()) {
            for (int i = 1; i < segments.size(); i++) {
                final FlightSegment seg = segments.get(i);

                //test at the start
                if (orderedSegments.get(0).getFromWaypoint().getName().equals(seg.getToWaypoint().getName())) {
                    orderedSegments.add(0, seg);
                    clone.remove(seg);
                    break;
                }
                //test at the end
                if (orderedSegments.get(orderedSegments.size() - 1).getToWaypoint().getName().equals(seg.getFromWaypoint().getName())) {
                    orderedSegments.add(seg);
                    clone.remove(seg);
                }
            }
        }

        // creating the route
        FlightSegment startSeg = orderedSegments.get(0);
        route.addAtStart(startSeg.getFromWaypoint());
        route.addAtEnd(startSeg.getToWaypoint());
        for (int i = 1; i < segments.size() - 1; i++) {
            FlightSegment seg = orderedSegments.get(i);
            route.addAtEnd(seg.getToWaypoint());
        }
        route.addAtEnd(orderedSegments.get(orderedSegments.size() - 1).getToWaypoint());
        return route;
    }

    private void buildFlightPlans() {
        inputs.getFlightPlanUpdates().forEach(flightPlanUpdate -> {
            System.err.println(flightPlanUpdate);
//            fli.get

        });
    }

}
