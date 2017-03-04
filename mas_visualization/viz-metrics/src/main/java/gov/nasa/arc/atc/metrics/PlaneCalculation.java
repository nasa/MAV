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

package gov.nasa.arc.atc.metrics;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.airborne.TrafficType;
import gov.nasa.arc.atc.core.NewPlane;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds calculations, and other metrics to display, for each plane
 *
 * @author Kelsey
 */
public class PlaneCalculation {

    private final NewPlane plane;


    //TODO explain names
    private final Map<Integer, Integer> delayTimesMap;
    private final Map<Integer, Integer> cumDelayTimesMap;
    private final Map<Integer, Integer> meterPlotsMap;
    private final Map<Integer, Integer> cumMeterPlotsMap;

    private final Map<String, Integer> controllerDelays;

    //temp
    private TrafficType trafficType;


    //TODO explain names
    private String flightType;
    private int delayTime;
    private int meterCount;
    private int meterTime;

    // calculation variables
    private boolean meterPrevious = false;
    private int cumDelayValue = 0;
    private int currDelayValue = 0;
    private int currEta = 0;
    private int prevEta = 0;


    public PlaneCalculation(NewPlane aPlane) {
        plane = aPlane;
        delayTimesMap = new HashMap<>();
        cumDelayTimesMap = new HashMap<>();
        meterPlotsMap = new HashMap<>();
        cumMeterPlotsMap = new HashMap<>();
        controllerDelays = new HashMap<>();
        //
        calculate();

    }

    private void calculate() {
        // traffic type
        //// TEMP
        if (plane.getUpdates().isEmpty()) {
            trafficType = TrafficType.UNKNOWN;
        } else {
//            trafficType = plane.getUpdates().values().iterator().next().isDeparture() ? TrafficType.DEPARTURE : TrafficType.ARRIVAL;
            //tODO GET RID
            System.err.println("TODO PLANE CALCULATION traffic type");
            trafficType = TrafficType.ARRIVAL;
        }

        //
        plane.getUpdates().entrySet().stream().sorted((integerAfoUpdateEntry1, e2) -> integerAfoUpdateEntry1.getKey().compareTo(e2.getKey())).forEach(entry -> {
            // calculate metering information
            storeMeterInformation(entry.getKey(), entry.getValue());
            // calculate delays
            storeDelayInformation(entry.getKey(), entry.getValue());

        });

        delayTime = getDelayAt(plane.getLastUpdateTime());
//        System.err.println(" PLANE calculations " + plane.getSimpleName() + " total delay at t=" + plane.getLastUpdateTime() + " is " + delayTime);
    }

    /*
     * -----------------
     * Getters
     * -------------------
     */

    /**
     *
     * @return the flight's type
     */
    public TrafficType getFlightType() {
        //TODO: use airport for quering the type
        return trafficType;
    }


    public String getPlaneFullName() {
        return plane.getFullName();
    }

    public String getSimpleName() {
        return plane.getSimpleName();
    }

    public NewPlane getPlane() {
        return plane;
    }

    public int getMeteredTimesAt(int simulationTime) {
        if (cumMeterPlotsMap.containsKey(simulationTime)) {
            return cumMeterPlotsMap.get(simulationTime);
        }
        //TODO
        return -1;
    }

    public int getDelayAt(int simulationTime) {
        if (simulationTime < plane.getStartTime()) {
            return 0;
        }
        for (int i = simulationTime; i > 0; i--) {
            if (cumDelayTimesMap.containsKey(simulationTime)) {
                return cumDelayTimesMap.get(simulationTime);
            }
        }
        return 0;
    }

    public int getLastUpdateTime() {
        return plane.getLastUpdateTime();
    }

//    public void setDelayVsTimePlots(Map<Integer, Integer> delayTimes, Map<Integer, Integer> cumDelayTimes) {
//        this.delayTimesMap = delayTimes;
//        this.cumDelayTimesMap = cumDelayTimes;
//    }
//
//    public void setMeterVsTimePlots(Map<Integer, Integer> meter, Map<Integer, Integer> cumMeter) {
//        this.meterPlotsMap = meter;
//        this.cumMeterPlotsMap = cumMeter;
//    }

//    public void setDelayTime(int dTime) {
//        this.delayTime = dTime;
//    }

//    public void setMeterCount(int mCount) {
//        this.meterCount = mCount;
//    }
//
//    public void setMeterTime(int mTime) {
//        this.meterTime = mTime;
//    }

    public Map<Integer, Integer> getDelayTimesMap() {
        return Collections.unmodifiableMap(delayTimesMap);
    }

    public Map<Integer, Integer> getCumDelayTimesMap() {
        return Collections.unmodifiableMap(cumDelayTimesMap);
    }

    public Map<Integer, Integer> getMeterPlotsMap() {
        return Collections.unmodifiableMap(meterPlotsMap);
    }

    public Map<Integer, Integer> getCumMeterPlotsMap() {
        return Collections.unmodifiableMap(cumMeterPlotsMap);
    }


    public Map<String, Integer> getControllerDelays() {
        return Collections.unmodifiableMap(controllerDelays);
    }

    public int getDelayTime() {
        return delayTime;
    }

    public int getMeterCount() {
        return meterCount;
    }

    public int getMeterTime() {
        return meterTime;
    }



    /*
    * Private methods for calculation
     */

    private void storeMeterInformation(int time, AfoUpdate planeInfo) {
        int meterStatus = planeInfo.isMetering();
        if (meterStatus == 1) {
            meterTime++;
        }
        if (meterStatus == 1) {
            if (!meterPrevious) {
                meterCount++;
            }
            meterPrevious = true;
        } else if (meterStatus == 0) {
            meterPrevious = false;
        }
        meterPlotsMap.put(time, meterStatus);
        cumMeterPlotsMap.put(time, meterCount);
    }

    private void storeDelayInformation(int time, AfoUpdate planeInfo) {
        //only calculate delays for arrivals
        //TODO: fix is Departure
//        if (!planeInfo.isDeparture()) {
            currEta = (int) planeInfo.getEta();
            //TODO make it cleaner
            if (currEta == prevEta) {
                currDelayValue = 1;
                cumDelayValue += 1;
            } else {
                currDelayValue = 0;
            }
//        }
//        if(planeInfo.getAfoName().contains("JBU6365")) {
//            System.err.println(" -> t=" + time + "  currEta=" +currEta+"  currDelayValue=" + currDelayValue + "  cumDelayValue=" + cumDelayValue);
//        }
        delayTimesMap.put(time, currDelayValue); //add to plotDelays
        cumDelayTimesMap.put(time, cumDelayValue); //add to plotCumDelays
        //

        /// todo merge with previous
        // new part

        //TODO: fix is Departure
//        if (!planeInfo.isDeparture()) {
            if (currEta >= prevEta) {
                //add delay to delay/controller
                if (controllerDelays.containsKey(planeInfo.getController())) {
                    final int newDelay = controllerDelays.get(planeInfo.getController()) + currEta - prevEta;
                    controllerDelays.remove(planeInfo.getController());
                    controllerDelays.put(planeInfo.getController(), newDelay);
                } else {
                    final int newDelay = currEta - prevEta;
                    controllerDelays.put(planeInfo.getController(), newDelay);
                }
//            }
        }


        //
        prevEta = currEta;
    }

}
