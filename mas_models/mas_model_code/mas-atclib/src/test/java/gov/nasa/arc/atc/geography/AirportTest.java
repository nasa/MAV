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

package gov.nasa.arc.atc.geography;

import static org.junit.Assert.*;

import org.junit.Test;

import gov.nasa.arc.atc.factories.AirportFactory;

/**
 * @author ahamon
 */
public class AirportTest {

    private static final double EPSILON = 0.000001;

    @Test
    public void testLGA() {
        Airport lGA = AirportFactory.createLGA();
        assertNotNull(lGA);
        assertEquals(4, lGA.getRunways().size());
        for (Runway runway : lGA.getRunways()) {
            assertTrue(lGA.getRunways().stream().anyMatch(r -> ((r.getQFU() + 18) % 36) == runway.getQFU()));
        }
    }


    /**
     * Tests default constructors
     */
    @Test
    public void testNewInstance() {
        String icao = "LFML";
        String iata = "MRS";
        double lat = 43.436667;
        double lng = 5.215;
        int elev = 70;
        //
        Airport instance1 = new Airport(icao, iata, lat, lng, elev);
        assertNotNull(instance1.toString());
        assertEquals(icao, instance1.getName());
        assertEquals(icao, instance1.getIcaoCode());
        assertEquals(iata, instance1.getIataCode());
        assertEquals(lat, instance1.getLatitude(), EPSILON);
        assertEquals(lng, instance1.getLongitude(), EPSILON);
        assertEquals(elev, instance1.getAltitude(),EPSILON);
        //
        Airport instance2 = new Airport(icao,iata, lat, lng);
        assertNotNull(instance2.toString());
        assertEquals(icao, instance2.getName());
        assertEquals(icao, instance2.getIcaoCode());
        assertEquals(iata, instance2.getIataCode());
        assertEquals(lat, instance2.getLatitude(), EPSILON);
        assertEquals(lng, instance2.getLongitude(), EPSILON);
        assertEquals(Airport.DEFAULT_ELEVATION, instance2.getAltitude(),EPSILON);
        //
        Airport instance3 = new Airport(icao, lat, lng, elev);
        assertNotNull(instance3.toString());
        assertEquals(icao, instance3.getName());
        assertEquals(icao, instance3.getIcaoCode());
        assertEquals(icao, instance3.getIataCode());
        assertEquals(lat, instance3.getLatitude(), EPSILON);
        assertEquals(lng, instance3.getLongitude(), EPSILON);
        assertEquals(elev, instance3.getAltitude(),EPSILON);
        //
        Airport instance4 = new Airport(icao, lat, lng);
        assertNotNull(instance4.toString());
        assertEquals(icao, instance4.getName());
        assertEquals(icao, instance4.getIcaoCode());
        assertEquals(icao, instance4.getIataCode());
        assertEquals(lat, instance4.getLatitude(), EPSILON);
        assertEquals(lng, instance4.getLongitude(), EPSILON);
        assertEquals(Airport.DEFAULT_ELEVATION, instance4.getAltitude(),EPSILON);
    }


    /**
     * Tests adding runways
     */
    @Test
    public void testAddRunway() {
        Airport mP = new Airport("LFML", "MRS", 43.436667, 5.215, 70);
        Runway r13L = new Runway("13L", mP, 13);
        Runway r13R = new Runway("13R", mP, 13);
        mP.addRunway(r13L);
        mP.addRunway(r13R);
        assertTrue(mP.getRunways().contains(r13L));
        assertTrue(mP.getRunways().contains(r13R));
        assertEquals(2, mP.getRunways().size());
    }

    /**
     * Tests that a runway can only be added once and that adding it twice will not throw an exception
     */
    @Test
    public void testAddExistingRunway() {
        Airport mP = new Airport("LFML", "MRS", 43.436667, 5.215, 70);
        Runway r13L = new Runway("13L", mP, 13);
        mP.addRunway(r13L);
        mP.addRunway(r13L);
        assertTrue(mP.getRunways().contains(r13L));
        assertEquals(1, mP.getRunways().size());
    }


}
