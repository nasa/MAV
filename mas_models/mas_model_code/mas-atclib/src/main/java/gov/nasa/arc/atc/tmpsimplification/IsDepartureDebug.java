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

package gov.nasa.arc.atc.tmpsimplification;

import gov.nasa.arc.atc.airborne.AFO;
import gov.nasa.arc.atc.geography.FlightPlan;

/**
 * Created by ahamon on 10/17/16.
 */
public class IsDepartureDebug {

    private IsDepartureDebug(){
        // private utility constructor
    }

    //BAD... but temporary

    public static boolean isDArrival(AFO afo){
        FlightPlan fpl = afo.getFlightPlan();
       String lastWPT =  fpl.getLastWaypoint().getName();
        return lastWPT.contains("LGA");
    }

    public static boolean isDeparture(AFO afo){
        FlightPlan fpl = afo.getFlightPlan();
        String firstWPT =  fpl.getFirstWaypoint().getName();
        return firstWPT.contains("LGA");
    }

}
