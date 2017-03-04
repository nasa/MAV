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

/*
* *******************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.geography;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author hamon
 */
public class Airport extends ATCNode {

    static final int DEFAULT_ELEVATION = 0;


    private final String iataCode;
    private final String icaoCode;

    private final List<Runway> runways;

    /**
     * @param codeICAO         the airport ICAO code
     * @param codeIATA         the airport IATA code
     * @param airportLatitude  the airport latitude
     * @param airportLongitude the airport longitude
     * @param airportElevation the airport elevation in [ft]
     */
    public Airport(String codeICAO, String codeIATA, double airportLatitude, double airportLongitude, double airportElevation) {
        super(codeICAO, airportLatitude, airportLongitude, airportElevation);
        runways = new LinkedList<>();
        iataCode = codeIATA;
        icaoCode = codeICAO;
    }

    /**
     * Places the airport at Elevation = DEFAULT_ELEVATION
     *
     * @param codeICAO         the airport ICAO code
     * @param codeIATA         the airport IATA code
     * @param airportLatitude  the airport latitude
     * @param airportLongitude the airport longitude
     */
    public Airport(String codeICAO, String codeIATA, double airportLatitude, double airportLongitude) {
        this(codeICAO, codeIATA, airportLatitude, airportLongitude, DEFAULT_ELEVATION);
    }

    /**
     * !! does not have the ICAO / IATA tags, by default will assume ICAO
     * Places the airport at Elevation = DEFAULT_ELEVATION
     *
     * @param codeICAO         the airport ICAO code
     * @param airportLatitude  the airport latitude
     * @param airportLongitude the airport longitude
     */
    @Deprecated
    public Airport(String codeICAO, double airportLatitude, double airportLongitude) {
        this(codeICAO, codeICAO, airportLatitude, airportLongitude, DEFAULT_ELEVATION);
    }

    /**
     *
     * !! does not have the ICAO / IATA tags, by default will assume ICAO
     *
     * @param codeICAO         the airport ICAO code
     * @param airportLatitude  the airport latitude
     * @param airportLongitude the airport longitude
     * @param airportElevation the airport elevation in [ft]
     */
    @Deprecated
    public Airport(String codeICAO , double airportLatitude, double airportLongitude, double airportElevation) {
        super(codeICAO, airportLatitude, airportLongitude, airportElevation);
        runways = new LinkedList<>();
        iataCode = codeICAO;
        icaoCode = codeICAO;
    }


    public void addRunway(Runway runway) {
        for (Runway r : runways) {
            if (r.getName().equals(runway.getName())) {
                return;
            }
        }
        runways.add(runway);
    }

    public String getIataCode() {
        return iataCode;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public List<Runway> getRunways() {
        return Collections.unmodifiableList(runways);
    }

    @Override
    public String toString() {
        return "Airport [Name=" + getName() + ", Latitude=" + getLatitude() + ", Longitude=" + getLongitude() + "] has " + runways.size() + " runways";
    }

}
