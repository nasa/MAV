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

package gov.nasa.arc.atc.metrics.comparison;

import java.util.*;

/**
 * @author ahamon
 */
public class DelayDistribution {

    private final Map<Integer, Integer> delayCounts =new HashMap<>();

    private int maxCount = 0;
    private int max = 0;

    public void addDelayValue(int delay) {
        if (delayCounts.containsKey(delay)) {
            final int newCount = delayCounts.get(delay) + 1;
            delayCounts.remove(delay);
            delayCounts.put(delay, newCount);
            maxCount = Math.max(maxCount, newCount);
        } else {
            delayCounts.put(delay, 1);
            maxCount = Math.max(maxCount, 1);
        }
        max = Math.max(max, delay);

    }

    public int getMax() {
        return max;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public Map<Integer, Integer> getDelayCounts() {
        return Collections.unmodifiableMap(new TreeMap<>(delayCounts));
    }
}
