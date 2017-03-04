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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ahamon
 */
public class SegmentTest {

    private static final ATCNode A = new ATCNode("A", 10, 15, 1000);
    private static final ATCNode B = new ATCNode("B", 11, 15, 750);
    private static final ATCNode C = new ATCNode("C", 12, 15, 15000);
    private static final ATCNode D = new ATCNode("D", 13, 15, 2400);

    @Test
    public void testNewInstance() {
        Segment instance = new Segment(A, B);
        assertEquals(A, instance.getFromWaypoint());
        assertEquals(B, instance.getToWaypoint());
        assertNotNull(instance.toString());
    }



    @Test
    public void hasSameEdges() throws Exception {
        Segment instance1 = new Segment(A, B);
        //
        Segment instance2 = new Segment(A, B);
        assertTrue(instance1.hasSameEdges(instance2));
        //
        Segment instance3 = new Segment(C, B);
        assertFalse(instance1.hasSameEdges(instance3));
        //
        Segment instance4 = new Segment(D, B);
        assertFalse(instance1.hasSameEdges(instance4));
        //
        Segment instance5 = new Segment(B, A);
        assertFalse(instance1.hasSameEdges(instance5));
        //
    }


}
