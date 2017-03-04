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
import gov.nasa.arc.atc.geography.Position;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author ahamon
 */
public class SimpleAltitudeConstantWindTest {

    //
    private static final int MIN_ALT_1 = 1000;
    private static final int MIN_ALT_2 = 2000;
    private static final int MIN_ALT_3 = 3000;
    private static final int MIN_ALT_4 = 4000;
    private static final int MIN_ALT_5 = 5000;

    // WINDS
    private static final WindParameters WIND_1 = new WindParameters(180, 25);
    private static final WindParameters WIND_2 = new WindParameters(90, 50);
    private static final WindParameters WIND_3 = new WindParameters(270, 5);

    //// simple layers
    private static final SimpleWindLayer LAYER_1 = new SimpleWindLayer(MIN_ALT_1, MIN_ALT_2, WIND_1);
    private static final SimpleWindLayer LAYER_2 = new SimpleWindLayer(MIN_ALT_2, MIN_ALT_3, WIND_2);
    private static final SimpleWindLayer LAYER_3 = new SimpleWindLayer(MIN_ALT_3, MIN_ALT_4, WIND_3);
    // large layer
    private static final SimpleWindLayer LAYER_LARGE = new SimpleWindLayer(MIN_ALT_4 - 500, MIN_ALT_5, WIND_1);


    /**
     * Test normal creation
     */
    @Test
    public void testCreateInstance() {
        List<SimpleWindLayer> windsIn = new LinkedList<>();
        windsIn.add(LAYER_1);
        windsIn.add(LAYER_2);
        windsIn.add(LAYER_3);
        SimpleAltitudeConstantWind instance = new SimpleAltitudeConstantWind(windsIn);
        assertNotNull(instance.toString());
        assertEquals(WindFactory.NO_WIND_PARAMETER,instance.getWindAt(new Position(0,0,MIN_ALT_1-10)));
        assertEquals(WIND_1,instance.getWindAt(new Position(0,0,MIN_ALT_1)));
        assertEquals(WIND_2,instance.getWindAt(new Position(0,0,MIN_ALT_2)));
        assertEquals(WIND_3,instance.getWindAt(new Position(0,0,MIN_ALT_3)));
        assertEquals(WindFactory.NO_WIND_PARAMETER,instance.getWindAt(new Position(0,0,MIN_ALT_4)));
    }

    /**
     * Tests no layer overlap
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateInstanceFail() {
        List<SimpleWindLayer> windsIn = new LinkedList<>();
        windsIn.add(LAYER_1);
        windsIn.add(LAYER_2);
        windsIn.add(LAYER_3);
        windsIn.add(LAYER_LARGE);
        SimpleAltitudeConstantWind instance = new SimpleAltitudeConstantWind(windsIn);
        fail(instance.toString());
    }

    /**
     * Test layers input reordered
     */
    @Test
    public void testLayerReordered() {
        List<SimpleWindLayer> windsIn = new LinkedList<>();
        windsIn.add(LAYER_3);
        windsIn.add(LAYER_2);
        windsIn.add(LAYER_1);
        SimpleAltitudeConstantWind instance = new SimpleAltitudeConstantWind(windsIn);
        assertNotNull(instance.toString());
        List<WindLayer> windsOut = instance.getLayers();
        assertEquals(0, windsOut.indexOf(LAYER_1));
        assertEquals(1, windsOut.indexOf(LAYER_2));
        assertEquals(2, windsOut.indexOf(LAYER_3));

    }

    /**
     * Test getLayers list is unmodifiable
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetLayers() {
        List<SimpleWindLayer> windsIn = new LinkedList<>();
        windsIn.add(LAYER_3);
        windsIn.add(LAYER_2);
        windsIn.add(LAYER_1);
        SimpleAltitudeConstantWind instance = new SimpleAltitudeConstantWind(windsIn);
        assertNotNull(instance.toString());
        List<WindLayer> windsOut = instance.getLayers();
        windsOut.add(LAYER_LARGE);

    }

}
