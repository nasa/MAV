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

package gov.nasa.arc.atc.charts;


import gov.nasa.arc.atc.metrics.ArgmaxInfo;

/**
 * Stores info for the simple metric box in SimpleSimulationMetrics {total, average, maximum metric, and ArgmaxInfo}
 * current boxes are created during SimulationCalculation
 * BoxInfo should be stored in MetricsUtil in order to load in the Simulation Metrics display
 *
 * @author Kelsey
 */
public class BoxInfo {

    private String name;
    private double totalValue;
    private double maxValue;
    private double avgValue;
    private ArgmaxInfo argmax = new ArgmaxInfo();

    public BoxInfo(String boxName) {
        setName(boxName);
    }

    public void setTotalValue(double total) {
        totalValue = total;
    }

    public void setMaxValue(double max) {
        maxValue = max;
    }

    public void setAvgValue(double avg) {
        avgValue = avg;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getAverageValue() {
        return avgValue;
    }

    public String getTotalString() {
        return Double.toString(totalValue);
    }

    public String getMaxString() {
        return Double.toString(maxValue);
    }

    public String getAverageString() {
        return Double.toString(avgValue);
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public void setAllValues(int total, int avg, int max) {
        totalValue = (double) total;
        avgValue = (double) avg;
        maxValue = (double) max;
    }

    public void setAllValues(double total, double avg, double max) {
        totalValue = total;
        avgValue = avg;
        maxValue = max;
    }

    public void setArgmax(ArgmaxInfo argmaxDelay) {
        argmax = argmaxDelay;
    }

    public ArgmaxInfo getArgmax() {
        return argmax;
    }

}
