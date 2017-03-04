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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ahamon
 */
public class CTOPUtilsTest {


    @Test
    public void testGetCurrentBucketIndex() throws Exception {

        int offset = 5;
        int duration = 23;

        // before offset
        int result0 = CTOPUtils.getCurrentBucketIndex(offset - 1, duration, offset);
        assertEquals(0, result0);

        // at offset
        int result1 = CTOPUtils.getCurrentBucketIndex(offset, duration, offset);
        assertEquals(1, result1);

        // at offset + 1// at offset
        int result2 = CTOPUtils.getCurrentBucketIndex(offset + 1, duration, offset);
        assertEquals(1, result2);

        // start second bucket
        int result3 = CTOPUtils.getCurrentBucketIndex(offset + duration, duration, offset);
        assertEquals(2, result3);

        // at n
        for (int i = 1; i < 10; i++) {
            assertEquals(i, CTOPUtils.getCurrentBucketIndex(offset + duration * i - 1, duration, offset));
            assertEquals(i + 1, CTOPUtils.getCurrentBucketIndex(offset + duration * i, duration, offset));
            assertEquals(i + 1, CTOPUtils.getCurrentBucketIndex(offset + duration * i + 1, duration, offset));
        }
    }

    @Test
    public void testGetCurrentBucketStartTime() {

        int offset = 7;
        int duration = 31;


        // before offset
        int result0 = CTOPUtils.getCurrentBucketStartTime(offset - 1, duration, offset);
        assertEquals(offset - duration, result0);

        // at offset
        int result1 = CTOPUtils.getCurrentBucketStartTime(offset, duration, offset);
        assertEquals(offset, result1);


        // at n
        for (int i = 1; i < 10; i++) {
            assertEquals((i - 1) * duration + offset, CTOPUtils.getCurrentBucketStartTime(offset + duration * i - 1, duration, offset));
            assertEquals(i * duration + offset, CTOPUtils.getCurrentBucketStartTime(offset + duration * i, duration, offset));
            assertEquals(i * duration + offset, CTOPUtils.getCurrentBucketStartTime(offset + duration * i + 1, duration, offset));
        }
    }

}
