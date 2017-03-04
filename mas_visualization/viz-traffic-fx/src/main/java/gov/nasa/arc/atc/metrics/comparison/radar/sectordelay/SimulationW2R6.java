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

package gov.nasa.arc.atc.metrics.comparison.radar.sectordelay;

/**
 * @author ahamon
 */
public class SimulationW2R6 {

    private final String simName;

    private final int z118;
    private final int z112;
    private final int z114;
    private final int z27;
    private final int z29;
    private final int z28;
    private final int z110;
    private final int total;

    public SimulationW2R6(String name, int z118, int z112, int z114, int z27, int z29, int z28, int z110) {
        simName = name;
        this.z118 = z118;
        this.z112 = z112;
        this.z114 = z114;
        this.z27 = z27;
        this.z29 = z29;
        this.z28 = z28;
        this.z110 = z110;
        total = z118 + z112 + z114 + z27 + z29 + z28 + z110;
    }

    int getZ118() {
        return z118;
    }

    int getZ112() {
        return z112;
    }

    int getZ114() {
        return z114;
    }

    int getZ27() {
        return z27;
    }

    int getZ29() {
        return z29;
    }

    int getZ28() {
        return z28;
    }

    int getZ110() {
        return z110;
    }

    int getTotal() {
        return total;
    }


    @Override
    public String toString() {
        return simName;
    }
}
