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
import gov.nasa.arc.atc.parsing.log.LogDataBlock;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class DepartureQueueDataBlock implements LogDataBlock {

    // data block structure
    //--Start_DepartureQueue(0)-----------------------------
    //            * dep 0 ASQ5573
    // * dep 1 AWI3191
    // ...
    // * dep 43 FFT732
    //-- End_DepartureQueue


    private static final String BLOCK_HEADER = "--Start_DepartureQueue";
    private static final String BLOCK_FOOTER = "-- End_DepartureQueue";


    private static final Logger LOG = Logger.getGlobal();

    //TODO: include in the return type
    private int nbLines = 1;


    public DepartureQueueDataBlock() {
        LOG.log(Level.FINE, "Creating DepartureQueueDataBlock");
    }

    @Override
    public void parseBlock(List<String> lines, int firstLineIndex, DataModelInput inputs) {

        // in the future, retrieve time if needed
        // in the future, log the departure node to differentiate between departure queues

        // at the moment logging is supposed to keep the departures order, so no need to parse the departure index

        List<String> departureNames = new LinkedList<>();
        nbLines = 1;
        String currentLine = lines.get(nbLines + firstLineIndex);
        while (!currentLine.contains(BLOCK_FOOTER)) {
            String[] splitCurrentLine = currentLine.split(" ");
            //TODO some checks
            departureNames.add(splitCurrentLine[4]);
            nbLines++;
            currentLine = lines.get(nbLines + firstLineIndex);
        }
        // needed to return the right value
        nbLines++;

        inputs.setInitDepartureQueue(Collections.unmodifiableList(departureNames));

    }

    @Override
    public boolean headerMatches(String nextLine) {
        return nextLine.contains(BLOCK_HEADER);
    }

    @Override
    public int getNbLines() {
        return nbLines;
    }


}
