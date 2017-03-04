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

package gov.nasa.arc.atc.brahms.parsers;

/**
 * @author ahamon
 */
public class BrahmsModelsDefaultAttributes {

    /*
 * by default here, trying to see if is better without config files
 */
    public static final String FULL_NAME = "fullName";
    public static final String AFO = "AFO";
    public static final String AIR_SPEED = " m_iAirSpeed";
    public static final String VERTICAL_SPEED = "m_iVerticalSpeed";
    public static final String LATITUDE = "m_dLatitude";
    public static final String LONGITUDE = "m_dLongitude";
    public static final String ALTITUDE = "m_dAltitude ";
    public static final String HEADING = "m_dBearing";
    public static final String STATUS = "iStatus";
    public static final String CONTROLLER_NAME = "controller";
    public static final String IS_METERING = "is_Metering";

    public static final String NAME = "name";
    public static final String AFO_NAME = "Name";
    public static final String TO_WAYPOINT = "toWaypoint";

    public static final String M_I_AIRSPEED = "m_iAirSpeed";
    public static final String M_D_VERTICAL_SPEED = "m_dVerticalSpeed";
    public static final String M_I_TIMESTAMP = "m_iTimeStamp";
    public static final String M_D_LATITUDE = "m_dLatitude";
    public static final String M_D_LONGITUDE = "m_dLongitude";
    public static final String M_D_ALTITUDE = "m_dAltitude";
    public static final String I_STATUS = "iStatus";
    public static final String I_CURRENT_SEGMENT = "iCurrentSegment";
    public static final String M_HEADING_ENUM = "m_headingEnum";
    public static final String M_D_BEARING = "m_dBearing";

    @Deprecated
    public static final String IS_DEPARTURE = "is_departure";
    public static final String FLIGHT_PLAN = "flightPlan";
    public static final String START_TIME = "startTime";
    public static final String ETA = "ETA";
    public static final String DTA = "DTA";

    private BrahmsModelsDefaultAttributes(){
        // private utility constructor
    }



}
