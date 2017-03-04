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

import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.scenariogen.base.FlightPlanDataSet;
import javafx.scene.paint.Color;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author ahamon
 */
public class SubFlow {

    private static final int NB_SECONDS_PER_QUARTER = 15 * 60;

    private static final int DEFAULT_BASE_RATE = 10;

    private final PropertyChangeSupport propertyChangeSupport;

    private final List<ScheduledFlightPlan> generatedFlightPlans;
    private final Random random;

    private String name;
    private SubFLowSelectionFunction identifierFunction;
    private String identifierWPT;
    private String deliveryPoint;
    private ATCNode deliveryNode;
    private String flowDestination;
    private FlowType flowType;
    private int[] quarterlyRates;
    private int defaultRate;

    private Color subFlowColor = Color.WHITESMOKE;

    private int offset = 0;


    private List<RichFlightPlan> flightPlans;


    //temp constructor
    public SubFlow(String flowName, SubFLowSelectionFunction idFunction, String identifierWPTName, String deliveryP, String destination, FlowType subFlowType) {
        propertyChangeSupport = new PropertyChangeSupport(SubFlow.this);
        generatedFlightPlans = new LinkedList<>();
        random = new Random();
        name = flowName;
        identifierFunction = idFunction;
        identifierWPT = identifierWPTName;
        deliveryPoint = deliveryP;
        flowDestination = destination;
        flowType = subFlowType;
        quarterlyRates = new int[0];
        defaultRate = DEFAULT_BASE_RATE;
    }


    public void addPropertyChangeListener(PropertyChangeListener listener){
        propertyChangeSupport.addPropertyChangeListener(listener);
    }


    /*
    * GETTERS
     */

    public int getRateAtQuarter(int quarterIndex) {
        if (quarterIndex < quarterlyRates.length) {
            return quarterlyRates[quarterIndex];
        }
        return 0;
    }

    public int[] getNbPerQuarters() {
        return quarterlyRates.clone();
    }

    public int getNbQuarters() {
        return quarterlyRates.length;
    }

    public Color getColor() {
        return subFlowColor;
    }

    public String getName() {
        return name;
    }

    public SubFLowSelectionFunction getIdentifierFunction() {
        return identifierFunction;
    }

    public List<ScheduledFlightPlan> getGeneratedFlightPlans() {
        return Collections.unmodifiableList(generatedFlightPlans);
    }


    /*
    * SETTERS
     */

    public void setNBQuarters(int numberOfQuarters) {
        int[] tempStorage = quarterlyRates.clone();
        int oldSize = tempStorage.length;
        quarterlyRates = new int[numberOfQuarters];
        // to be optimized
        if (oldSize <= numberOfQuarters) {
            for (int i = 0; i < oldSize; i++) {
                quarterlyRates[i] = tempStorage[i];
            }
            for (int i = oldSize; i < numberOfQuarters; i++) {
                quarterlyRates[i] = defaultRate;
            }
        } else {
            for (int i = 0; i < numberOfQuarters; i++) {
                quarterlyRates[i] = tempStorage[i];
            }
        }
        fireValueChanged();
    }

    public void setRateAtQuarter(int quarterIndex, int value) {
        if (quarterIndex >= quarterlyRates.length) {
            throw new IllegalArgumentException("Index out of bounds " + quarterIndex + " but nbQuarters=" + quarterlyRates.length);
        }
        quarterlyRates[quarterIndex] = value;
        fireValueChanged();
    }

    public void setBaseRate(int rate) {
        defaultRate = rate;
        for (int i = 0; i < quarterlyRates.length; i++) {
            quarterlyRates[i] = defaultRate;
        }
        fireValueChanged();
    }

    public void setColor(Color color) {
        subFlowColor = color;
    }

    public void setOffset(int newOffset) {
        offset = newOffset;
    }



    /*
    * GENERATION METHODS
     */

    /**
     * This method regenerate new flight plans each time it is invoked
     * Once generated the list can also be retrieved using the getGeneratedFlightPlans method.
     *
     * @param evenlySpaced if the traffic generated will arrival evenly at the delivery waypoint or randomly
     * @return an unmodifiable list containing the generated flight plans
     */
    public List<ScheduledFlightPlan> generate(boolean evenlySpaced) {
        System.err.println("Generating flow for " + this);
        generatedFlightPlans.clear();
        int afoId = 0;
        int poolSize = flightPlans.size();
        for (int i = 0; i < quarterlyRates.length; i++) {
            // defines the start of the quarter in [seconds]
            int start = i * 15 * 60;
            int rate = quarterlyRates[i];
            int space = (15 * 60 - offset) / rate;
            System.err.println("  ... rate[" + i + "] = " + rate);
            for (int j = 0; j < rate; j++) {
                final RichFlightPlan fpl = flightPlans.get(random.nextInt(poolSize));
                ScheduledFlightPlan scheduledFlightPlan;
                if (evenlySpaced) {
                    scheduledFlightPlan = new ScheduledFlightPlan(name + "_" + afoId, fpl, start + j * space + offset, deliveryNode);
                } else {
                    scheduledFlightPlan = new ScheduledFlightPlan(name + "_" + afoId, fpl, start + random.nextInt(NB_SECONDS_PER_QUARTER) + offset, deliveryNode);
                }
                generatedFlightPlans.add(scheduledFlightPlan);
                afoId++;
            }
        }
        System.err.println(" ->" + generatedFlightPlans.size());
        return Collections.unmodifiableList(generatedFlightPlans);
    }

    public void populate(FlightPlanDataSet dataSet) {
        flightPlans = dataSet.getFlightPlans().stream().filter(fpl -> identifierFunction.isCandidate(fpl)).collect(Collectors.toList());
        deliveryNode = dataSet.getNode(deliveryPoint);
        //TODO fire
    }


    @Override
    public String toString() {
        switch (flowType) {
            case ARRIVAL:
                return this.getClass().getSimpleName() + " " + name + " delivered at " + deliveryPoint + " arriving at " + flowDestination + " :: [...->" + identifierWPT + "->" + deliveryPoint + "->" + flowDestination + "]";
            case DEPARTURE:
                return this.getClass().getSimpleName() + " " + name + " delivered at " + deliveryPoint + " departing from " + flowDestination + " :: [" + flowDestination + "->" + deliveryPoint + "->" + identifierWPT + "->...]";
            case THROUGH:
                return this.getClass().getSimpleName() + " " + name + " delivered at " + deliveryPoint + " passing through at " + flowDestination + " :: [...->" + flowDestination + "->" + deliveryPoint + "->" + identifierWPT + "->...]";
            default:
                throw new IllegalStateException("unhandled flow type: " + flowType);
        }
    }


    /*
    * PRIVATE METHODS
     */

    private void fireValueChanged() {
        propertyChangeSupport.firePropertyChange(Flow.VALUE_CHANGED, null, this);
    }
}
