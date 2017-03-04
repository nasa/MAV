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

package gov.nasa.arc.atc.algos.dsas;

import gov.nasa.arc.atc.utils.Constants;
import gov.nasa.arc.atc.utils.GapUtils;

/**
 * @author ahamon
 */
public class SimpleDepartureGap implements Gap {

    private int startTime;
    private int endTime;
    private int gapDuration;
    private int nbArrivals;

    public SimpleDepartureGap(final int sTime, final int eTime) {
        if (eTime < sTime) {
            throw new IllegalArgumentException("End time (" + eTime + ") cannot be strictly inferior to start time(" + sTime + ")");
        }
        startTime = sTime;
        endTime = eTime;
        updateGapAttributes();
    }

    @Override
    public int getStartTime() {
        return startTime;
    }

    @Override
    public int getEndTime() {
        return endTime;
    }

    @Override
    public int getGapDuration() {
        return gapDuration;
    }

    public int getNbArrivalsPossible() {
        return nbArrivals;
    }

    public void setStartTime(int newStartTime) {
        if (endTime < newStartTime) {
            throw new IllegalArgumentException("Start time (" + newStartTime + ") cannot be strictly superior to end time(" + endTime + ")");
        }
        startTime = newStartTime;
        updateGapAttributes();
    }

    public void setEndTime(int newEndTime) {
        if (newEndTime < startTime) {
            throw new IllegalArgumentException("End time (" + newEndTime + ") cannot be strictly inferior to start time(" + startTime + ")");
        }
        endTime = newEndTime;
        updateGapAttributes();
    }

    public int getEarliestTimeAtNumber(int arrivalIndex) {
        if (arrivalIndex < 0) {
            throw new IllegalArgumentException("Index cannot be negative: " + arrivalIndex);
        }
        if (arrivalIndex > nbArrivals) {
            throw new IllegalArgumentException("Index (" + arrivalIndex + ") is greater and nbArrivalsPossible (" + nbArrivals + ")");
        }
        if (arrivalIndex == 1) {
            return startTime + Constants.DEP_ARR_MIN;
        } else {
            return startTime + Constants.DEP_ARR_MIN + (arrivalIndex - 1) * Constants.ARR_ARR_MIN;
        }
    }

    private void updateGapAttributes() {
        gapDuration = endTime - startTime;
        nbArrivals = GapUtils.calculateNbArrivals(SimpleDepartureGap.this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(" nbArrivals=");
        sb.append(nbArrivals);
        sb.append(" starting at t=");
        sb.append(startTime);
        sb.append(" duration=");
        sb.append(gapDuration);
        return sb.toString();
    }

}
