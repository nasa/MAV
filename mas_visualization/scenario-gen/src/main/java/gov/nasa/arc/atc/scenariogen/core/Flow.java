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

package gov.nasa.arc.atc.scenariogen.core;


import gov.nasa.arc.atc.scenariogen.base.FlightPlanDataSet;
import javafx.scene.paint.Color;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author ahamon
 */
public class Flow {

    public static final String FLOW_CHANGED = "flowChanged";
    public static final String VALUE_CHANGED = "valueChanged";
    public static final String TRAFFIC_GENERATED = "trafficGenerated";
    public static final String SCHEDULE_GENERATED = "scheduleGenerated";


    private static final int DEFAULT_BASE_RATE = 28;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(Flow.this);

    private final List<SubFlow> subFlows;
    private final List<ScheduledFlightPlan> scheduledFlightPlans;

    private FlightPlanDataSet dataSet;
    private List<ScheduledAFO> scheduledAFOs;

    private String deliveryPoint = "";
    //todo rename
    private String runway = "";
    private FlowType flowType;

    // at the moment only handles a constant rate
    private int baseRate = DEFAULT_BASE_RATE;
    private int simulationDuration = 0;

    private boolean scheduleUpToDate = false;

    public Flow() {
        subFlows = new LinkedList<>();
        scheduledFlightPlans = new LinkedList<>();
        flowType = FlowType.THROUGH;
    }


    // GETTERS

    public int getBaseRate() {
        return baseRate;
    }

    public int getNbQuarters() {
        return simulationDuration;
    }

// SETTERS

    public void setBaseRate(int rate) {
        baseRate = rate;
        if (!subFlows.isEmpty()) {
            int individualRate = baseRate / subFlows.size();
            int singleOffset = 60 * 60 / baseRate;
            subFlows.forEach(sF -> {
                sF.setBaseRate(individualRate);
                sF.setOffset(subFlows.indexOf(sF) * singleOffset);
            });
        }
        scheduleUpToDate = false;
    }

    public void setSimulationDuration(int duration) {
        simulationDuration = duration;
        subFlows.forEach(subFlow -> subFlow.setNBQuarters(duration));
        scheduleUpToDate = false;
    }

    public void setDataSet(FlightPlanDataSet flightPlanDataSet) {
        dataSet = flightPlanDataSet;
        subFlows.forEach(subFlow -> subFlow.populate(dataSet));
        scheduleUpToDate = false;
    }

    public void setDeliveryPoint(String deliveryPointName) {
        deliveryPoint = deliveryPointName;
        scheduleUpToDate = false;
    }

    public void setRunway(String runwayName) {
        runway = runwayName;
        scheduleUpToDate = false;
    }

    public void setFlowType(FlowType type) {
        flowType = type;
        scheduleUpToDate = false;
    }

    // OTHER METHODS

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public SubFlow addSubFlow(String subFlowName, String nodeName, Color color) {
        SubFlow subFlow;
        switch (flowType) {
            case ARRIVAL:
                subFlow = new SubFlow(subFlowName, new CombinedFunction(new SimpleFunction(FlowType.THROUGH, nodeName), new SimpleFunction(FlowType.ARRIVAL, runway), CombinedFunction.CombineType.AND), nodeName, deliveryPoint, runway, flowType);
                break;
            case DEPARTURE:
                subFlow = new SubFlow(subFlowName, new CombinedFunction(new SimpleFunction(FlowType.THROUGH, nodeName), new SimpleFunction(FlowType.DEPARTURE, runway), CombinedFunction.CombineType.AND), nodeName, deliveryPoint, runway, flowType);
                break;
            case THROUGH:
                subFlow = new SubFlow(subFlowName, new SimpleFunction(FlowType.THROUGH, nodeName), nodeName, deliveryPoint, runway, flowType);
                break;
            default:
                throw new IllegalStateException("unhandled flow type: " + flowType);
        }
        subFlow.setColor(color);
        subFlows.add(subFlow);
        // TEMP TODO: remove / factor
        int individualRate = baseRate / subFlows.size();
        int singleOffset = 60 * 60 / baseRate;
        subFlows.forEach(sF -> {
            sF.setBaseRate(individualRate);
            sF.setOffset(subFlows.indexOf(sF) * singleOffset);
        });
        subFlow.setNBQuarters(simulationDuration);
        if (dataSet != null) {
            subFlow.populate(dataSet);
        }
        scheduleUpToDate = false;
        subFlow.addPropertyChangeListener(event -> {
            scheduleUpToDate = false;
            propertyChangeSupport.firePropertyChange(event);
        });
        return subFlow;

    }

    public void createSchedule() {
        subFlows.forEach(subFlow -> scheduledFlightPlans.addAll(subFlow.generate(true)));
        scheduleUpToDate = true;
        propertyChangeSupport.firePropertyChange(SCHEDULE_GENERATED, null, this);
    }

    public void generateTraffic() {
        scheduledFlightPlans.clear();
        if (!scheduleUpToDate) {
            createSchedule();
        }
        scheduledAFOs = scheduledFlightPlans.stream().map(scheduledFlightPlan -> scheduledFlightPlan.generateAFO(0, dataSet.getControllers())).collect(Collectors.toList());
        scheduledAFOs.forEach(a -> System.err.println(a));
        //
        propertyChangeSupport.firePropertyChange(TRAFFIC_GENERATED, null, this);
    }


    public String getDisplayName() {
        switch (flowType) {
            case ARRIVAL:
                return "Arr " + deliveryPoint + " -> " + runway;
            case DEPARTURE:
                return "Dep " + runway + " -> " + deliveryPoint;
            case THROUGH:
                return "<-> " + runway + " -> " + deliveryPoint;
            default:
                throw new IllegalStateException("unhandled flow type: " + flowType);
        }
    }

    public List<SubFlow> getSubFlows() {
        return Collections.unmodifiableList(subFlows);
    }

    public String getDeliveryPoint() {
        return deliveryPoint;
    }

    public String getRunway() {
        return runway;
    }

    public FlowType getFlowType() {
        return flowType;
    }

    public List<ScheduledAFO> getScheduledAFOs() {
        return Collections.unmodifiableList(scheduledAFOs);
    }

    // PRIVATE METHODS

    private void fireFlowChanged() {
        propertyChangeSupport.firePropertyChange(FLOW_CHANGED, null, null);
    }


    // TO STRING

    @Override
    public String toString() {
        return getDisplayName();
    }


}
