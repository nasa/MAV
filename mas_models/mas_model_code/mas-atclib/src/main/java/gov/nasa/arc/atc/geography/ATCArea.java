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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An {@link ATCArea} is composed of {@link Sector}s
 * @author ahamon
 */
public class ATCArea {

    private final String name;
    private final List<Sector> sectors;

    /**
     *
     * @param areaName the name of the area
     */
    public ATCArea(String areaName) {
        name = areaName;
        sectors = new LinkedList<>();
    }


    public void addSector(Sector sector) {
        sectors.add(sector);
    }

    public String getName() {
        return name;
    }

    public List<Sector> getSectors() {
        return Collections.unmodifiableList(sectors);
    }

    @Override
    public String toString() {
        return "Area: " + name + " nb sectors=" + sectors.size();
    }

}
