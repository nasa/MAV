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

import gov.nasa.arc.atc.ControllerInitBlockImpl;
import gov.nasa.arc.atc.core.DataModelInput;
import gov.nasa.arc.atc.parsing.DataBlocks;
import gov.nasa.arc.atc.parsing.log.LogDataBlock;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ahamon
 */
public class ControllerInitializationBlock implements LogDataBlock {

    // data block structure
    //--- Controller init block ---
    //	controller   gov.nasa.arc.atm.atmmodel.scenarios.week2run6.agents.ZNY_27
    //	handoffTo   gov.nasa.arc.atm.atmmodel.scenarios.week2run6.agents.ZNY_110
    //	handoffWaypoint   gov.nasa.arc.atm.atmmodel.scenarios.week2run6.objects.BASYE
    //	nbWaypoints   4
    //	hasWaypoint   gov.nasa.arc.atm.atmmodel.scenarios.week2run6.objects.IGN
    //	hasWaypoint   gov.nasa.arc.atm.atmmodel.scenarios.week2run6.objects.VALRE
    //	hasWaypoint   gov.nasa.arc.atm.atmmodel.scenarios.week2run6.objects.BASYE
    //	hasWaypoint   gov.nasa.arc.atm.atmmodel.scenarios.week2run6.objects.BDL
    //--- END init block ---

    public static final String PATTERN = "--- Controller init block ---";

    private static final int MIN_NUMBER_OF_LINES = 6;

    private int nbLines = MIN_NUMBER_OF_LINES;

    @Override
    public void parseBlock(List<String> lines, int firstLineIndex, DataModelInput inputs) {
        String controllerName = getControllerName(lines.get(firstLineIndex + 1));
        String handOffController = getHandOffController(lines.get(firstLineIndex + 2));
        String handOffWaypoint = getWaypoint(lines.get(firstLineIndex + 3));
        int nbWaypoints = getNumberOfWayPoints(lines.get(firstLineIndex + 4));
        nbLines = MIN_NUMBER_OF_LINES + nbWaypoints;
        final List<String> waypoints = new ArrayList<>();
        for (int i = 0; i < nbWaypoints; i++) {
            String waypoint = getWaypoint(lines.get(firstLineIndex + 5 + i));
            waypoints.add(waypoint);
        }
        inputs.addControllerInitBlock(new ControllerInitBlockImpl(controllerName, handOffController, handOffWaypoint, Collections.unmodifiableList(waypoints)));
    }

    @Override
    public boolean headerMatches(String nextLine) {
        return nextLine.contains(PATTERN);
    }

    @Override
    public int getNbLines() {
        return nbLines;
    }

    private String getControllerName(String line) {
        Pair<String, String> beliefLog = DataBlocks.getLoggedAttributes(line);
        // check belief name
        return DataBlocks.getSimpleName(beliefLog.getValue());
    }

    private String getHandOffController(String line) {
        Pair<String, String> beliefLog = DataBlocks.getLoggedAttributes(line);
        // check belief name
        return DataBlocks.getSimpleName(beliefLog.getValue());

    }

    private int getNumberOfWayPoints(String line) {
        Pair<String, String> beliefLog = DataBlocks.getLoggedAttributes(line);
        // check belief name
        return Integer.parseInt(beliefLog.getValue());
    }

    private String getWaypoint(String line) {
        Pair<String, String> beliefLog = DataBlocks.getLoggedAttributes(line);
        // check belief name
        return DataBlocks.getSimpleName(beliefLog.getValue());
    }

}
