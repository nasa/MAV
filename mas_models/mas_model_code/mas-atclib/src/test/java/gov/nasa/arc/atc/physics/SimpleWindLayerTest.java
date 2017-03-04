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

package gov.nasa.arc.atc.physics;

import gov.nasa.arc.atc.factories.WindFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ahamon
 */
public class SimpleWindLayerTest {


    @Test
    public void testCreateInstance() {
        int minAlt = 1000;
        int maxAlt = 2000;
        SimpleWindLayer layer = new SimpleWindLayer(minAlt, maxAlt, WindFactory.NO_WIND_PARAMETER);
        assertEquals(minAlt, layer.getMinimumAltitude());
        assertEquals(maxAlt, layer.getMaximumAltitude());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInstanceFail() {
        SimpleWindLayer layer = new SimpleWindLayer(10, 9, WindFactory.NO_WIND_PARAMETER);
        fail(layer.toString());
    }


    @Test
    public void testContains() {
        int minAlt = 1000;
        int maxAlt = 2000;
        SimpleWindLayer layer = new SimpleWindLayer(minAlt, maxAlt, WindFactory.NO_WIND_PARAMETER);
        assertTrue(layer.contains(minAlt));
        assertFalse(layer.contains(maxAlt));
    }


}
