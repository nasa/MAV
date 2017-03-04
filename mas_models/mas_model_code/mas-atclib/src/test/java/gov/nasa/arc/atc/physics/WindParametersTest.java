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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ahamon
 */
public class WindParametersTest {


    @Test
    public void testCreateInstance() {
        int speed1 = 10;
        int direction1 = 270;
        WindParameters param1 = new WindParameters(direction1, speed1);
        assertEquals(direction1, param1.getDirection(), 0.0);
        assertEquals(speed1, param1.getSpeed(), 0.0);
    }

    @Test
    public void testLargeDirection() {
        int speed1 = 10;
        int direction1 = 360*2+90;
        WindParameters param1 = new WindParameters(direction1, speed1);
        assertEquals(90, param1.getDirection(), 0.0);
        //
        int speed2 = 10;
        int direction2 = 360;
        WindParameters param2 = new WindParameters(direction2, speed2);
        assertEquals(0, param2.getDirection(), 0.0);

    }

    @Test
    public void testNegativeSpeed() {
        int speed1 = -10;
        int direction1 = 0;
        WindParameters param1 = new WindParameters(direction1, speed1);
        assertEquals(180, param1.getDirection(), 0.0);
        assertEquals(-speed1, param1.getSpeed(), 0.0);
    }
}
