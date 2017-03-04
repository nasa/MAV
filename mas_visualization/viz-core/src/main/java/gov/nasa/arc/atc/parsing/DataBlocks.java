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

package gov.nasa.arc.atc.parsing;

import java.util.*;

import gov.nasa.arc.atc.parsing.log.LogDataBlock;
import gov.nasa.arc.atc.parsing.log.brahmssim.*;
import gov.nasa.arc.atc.parsing.trac.GeographyDataBlock;
import gov.nasa.arc.atc.parsing.trac.WaypointDataBlock;
import javafx.util.Pair;

/**
 * TODO: use service providers from NetBeans utils API
 *
 * @author ahamon
 */
public final class DataBlocks {

    private static final List<LogDataBlock> LOG_DATA_BLOCKS = new ArrayList<>();
    private static final List<GeographyDataBlock> GEOGRAPHY_DATA_BLOCKS = new ArrayList<>();

    private DataBlocks() {
        // private utility constructor
    }

    public static List<LogDataBlock> getLogDataBlocks(Properties properties) {
        // hum...
        if (LOG_DATA_BLOCKS.isEmpty()) {
            // not using init block anymore since it does not contain the afo type information
			LOG_DATA_BLOCKS.add(new AgentInitializationBlock(properties));
            LOG_DATA_BLOCKS.add(new UpdateAfoDataBlock(properties));
            LOG_DATA_BLOCKS.add(new ControllerInitializationBlock());
			LOG_DATA_BLOCKS.add(new SeparationLossDataBlock(properties));
			LOG_DATA_BLOCKS.add(new FlightPlanUpdateBlock());
            LOG_DATA_BLOCKS.add(new HandOffUpdateBlock());
            LOG_DATA_BLOCKS.add(new DepartureQueueDataBlock());
            LOG_DATA_BLOCKS.add(new DepartureClearedBlock());
            LOG_DATA_BLOCKS.add(new ArrivalLandedBlock());


            //For temporary compatibility
//			LOG_DATA_BLOCKS.add(new UpdateAfoDataBlockFixedEXE(properties));
//			LOG_DATA_BLOCKS.add(new SeparationLossDataBlock(properties));
//			LOG_DATA_BLOCKS.add(new FlightPlanUpdateBlock());
//			LOG_DATA_BLOCKS.add(new HandOffUpdateBlock());
            //
            // TODO: test headers do not collide

        }
        return Collections.unmodifiableList(LOG_DATA_BLOCKS);
    }

    public static List<GeographyDataBlock> getGeographyDataBlocks() {
        // hum...
        if (GEOGRAPHY_DATA_BLOCKS.isEmpty()) {
            GEOGRAPHY_DATA_BLOCKS.add(new WaypointDataBlock());
            // TODO: test headers do not collide
        }
        return Collections.unmodifiableList(GEOGRAPHY_DATA_BLOCKS);
    }

    public static Pair<String, String> getExeLogBeliefAttributes(String line) {
        String[] splitLine = line.replaceAll("\\s+", " ").split(" ");
        return new Pair<>(splitLine[2], splitLine[3]);
    }

    public static Pair<String, String> getLoggedAttributes(String line) {
        String[] splitLine = line.replaceAll("\\s+", " ").split(" ");
        switch (splitLine.length) {
            case 3:
                return new Pair<>(splitLine[1], splitLine[2]);
            case 2:
                return new Pair<>(splitLine[1], "");
            default:
                throw new IllegalArgumentException(" could not parse line:: " + line);
        }
    }

    public static String getSimpleName(String fullName) {
        String[] splitName = fullName.split("\\.");
        if (splitName.length > 0) {
            return splitName[splitName.length - 1];
        }
        return fullName;
    }

}
