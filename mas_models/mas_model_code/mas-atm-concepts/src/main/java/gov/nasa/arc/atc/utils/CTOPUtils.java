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

package gov.nasa.arc.atc.utils;

import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.simulation.SlotTrajectory;

/**
 * @author ahamon
 */
public class CTOPUtils {

    private CTOPUtils() {
        // private utility constructor
    }


    public static int getCurrentBucketIndex(int time, int bucketDuration, int bucketOffset) {
        if (time < bucketOffset) {
            return 0;
        }
        if (bucketOffset == 0) {
            return (time - bucketOffset) / bucketDuration;
        }
        return (time - bucketOffset) / bucketDuration + 1;
    }

    public static int getCurrentBucketStartTime(int time, int bucketDuration, int bucketOffset){
        if(time<bucketOffset){
            return bucketOffset-bucketDuration;
        }
        return bucketOffset + (getCurrentBucketIndex(time,bucketDuration,bucketOffset)-1)*bucketDuration;
    }

}
