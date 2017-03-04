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

package gov.nasa.arc.atc.parsing.log.brahmssim;

import gov.nasa.arc.atc.core.DataModelInput;
import gov.nasa.arc.atc.parsing.DataBlocks;
import gov.nasa.arc.atc.parsing.log.LogDataBlock;
import gov.nasa.arc.atc.simulation.SeparationViolation;

import java.util.List;
import java.util.Properties;

/**
 * @author ahamon
 */
public class SeparationLossDataBlock implements LogDataBlock {

    public static final String SEPARATION_LOSS_PATTERN_PROPERTY = "separationPattern";

    // TODO when required separation is logged, parse it

    //--SeparationViolation(165)-----------------------------
    //	lead_plane     EGF4953
    //	trail_plane    TCF5786
    //	time           165
    //	separation     4.976272359465584

    private final String separationPattern;

    /**
     * @param properties the properties to configure the block parsing and matching pattern
     */
    public SeparationLossDataBlock(Properties properties) {
        separationPattern = properties.getProperty(SEPARATION_LOSS_PATTERN_PROPERTY);
    }

    @Override
    public void parseBlock(List<String> lines, int firstLine, DataModelInput inputs) {
        String leadPlane = DataBlocks.getLoggedAttributes(lines.get(firstLine + 1)).getValue();
        String trailPlane = DataBlocks.getLoggedAttributes(lines.get(firstLine + 2)).getValue();
        int time = Integer.parseInt(DataBlocks.getLoggedAttributes(lines.get(firstLine + 3)).getValue());
        double separation = Double.parseDouble(DataBlocks.getLoggedAttributes(lines.get(firstLine + 4)).getValue());
        SeparationViolation separationViolation = new SeparationViolation(-1, separation, leadPlane, trailPlane, time);
        inputs.addSeparationViolation(separationViolation);

    }

    @Override
    public boolean headerMatches(String nextLine) {
        return nextLine.contains(separationPattern);
    }

    @Override
    public int getNbLines() {
        return 5;
    }

}
