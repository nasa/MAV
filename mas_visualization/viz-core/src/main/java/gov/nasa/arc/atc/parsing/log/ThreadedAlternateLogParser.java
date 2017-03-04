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

package gov.nasa.arc.atc.parsing.log;

import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.core.DataModelInput;
import gov.nasa.arc.atc.parsing.DataBlocks;
import gov.nasa.arc.atc.utils.SimulationProperties;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahamon
 *
 */
public class ThreadedAlternateLogParser extends Thread {

    public static final String OPENING_FILE = "openingFile";
    public static final String READING_LINE_PERCENTAGE = "readingLinePercentage";
    public static final String CREATING_AGENT_PERCENTAGE = "creatingAgentsPercentage";
    public static final String DATA_MODEL_PARSED = "dataModelParsed";
    public static final String LOADING_DATA_MODEL = "loadingDataModel";
    public static final String ERROR_PARSING = "errorParsing";

    // TODO: figure out where to put these
//    public static final String PLANE_AGENT_PREFIX = "plane_";
//    public static final String SLOT_AGENT_PREFIX = "slot_";
    public static final String CONTROLLER_AGENT_PREFIX = "ZNY_";

    private static final Logger LOG = Logger.getGlobal();

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private List<LogDataBlock> dataBlocks;

    private final File logFile;
    private final File configurationFile;
    private final PropertyChangeListener listener;

    private DataModel dataModel;

    /**
     *
     * @param logFile the log to parse (brahms-sim)
     * @param configurationFile the configuration file providing the parser configuration for data block matching patterns
     * @param listener the progress listener
     */
    public ThreadedAlternateLogParser(File logFile, File configurationFile, PropertyChangeListener listener) {
        super();
        this.logFile = logFile;
        this.configurationFile = configurationFile;
        this.listener = listener; // listener from viewer config controller ----
    }

    @Override
    public void run() {
        //super.run();
        parseBrahmsSimFile(logFile, configurationFile, listener);
    }

    /**
     * Parses a log file produced by the brahms-sim command
     *
     * @param logFile file to parse
     * @param configurationFile the file describing the parsing configuration for the logger
     * @param listener a listener to be notified of progress
     * @return a dataModel containing the log information
     */
    private void parseBrahmsSimFile(File logFile, File configurationFile, PropertyChangeListener listener) {
        LOG.log(Level.INFO, "starting to parse Brahms-sim log file: {0} ...", logFile);
        //
        addListener(listener);
        // add parameter to get the config file
        SimulationProperties.parseProperties(configurationFile);
        // TODO: some tests on the config properties

        // add parameter to get the data block
        dataBlocks = DataBlocks.getLogDataBlocks(SimulationProperties.getProperties());
        FileReader fileReader;
        propertyChangeSupport.firePropertyChange(OPENING_FILE, null, logFile.getAbsolutePath());
        try {
            fileReader = new FileReader(logFile);
            DataModelInput inputs;
            try (BufferedReader br = new BufferedReader(fileReader)) {
                List<String> lines = Files.readAllLines(logFile.toPath());
                //
                inputs = new DataModelInput();
                //
                boolean ok = parseLines(lines, inputs);
                if (!ok) {
                    br.close();
                    fileReader.close();
                    return;
                }
            }
            fileReader.close();

            propertyChangeSupport.firePropertyChange(LOADING_DATA_MODEL, logFile, dataModel);
            // LOAD DATA MODEL
            // get waypoints from simulation data model
            dataModel = new DataModel(inputs);
            // TODO
            // move calculations here to give more accurate user updates while loading

            LOG.log(Level.INFO, "... done parsing Brahms-sim log file: {0}", logFile);
            propertyChangeSupport.firePropertyChange(DATA_MODEL_PARSED, logFile, dataModel);
            removeListener(listener);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Exception: {0}", e);
        }
        removeListener(listener);
    }

    public DataModel getDataModel() {
        return this.dataModel;
    }

    private boolean parseLines(List<String> lines, DataModelInput inputs) {
        final double nbLines = lines.size();
        int blockSize;
        int nbLineToRead = 0;
        String lineToRead;
        int lastPercentage = 0;
        int iPercentage;
        while (nbLineToRead < nbLines) {

            final double percentage = nbLineToRead / nbLines;
            iPercentage = (int) (100 * percentage);
            // not to update progress every line but every new percentage done (perfo)
            if (iPercentage != lastPercentage) {
                lastPercentage = iPercentage;
                propertyChangeSupport.firePropertyChange(READING_LINE_PERCENTAGE, null, percentage);
            }
            blockSize = 1;
            lineToRead = lines.get(nbLineToRead);
            for (LogDataBlock block : dataBlocks) {
                if (block.headerMatches(lineToRead)) {
                    try {
                        block.parseBlock(lines, nbLineToRead, inputs);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOG.log(Level.SEVERE, "error while trying to parse log file, for block starting at line: {0} :: {1}", new Object[]{lineToRead, e});
                        List<String> errorLines = new ArrayList<>();
                        for (int i = 0; i < block.getNbLines(); i++) {
                            errorLines.add(lines.get(nbLineToRead + i));
                        }
                        propertyChangeSupport.firePropertyChange(ERROR_PARSING, e, Collections.unmodifiableList(errorLines));
                        return false;
                    }
                    blockSize = block.getNbLines();
                    break;
                }
            }
            nbLineToRead += blockSize;
        }
        return true;
    }

    private void addListener(PropertyChangeListener listener) {
        if (listener != null) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }
    }

    private void removeListener(PropertyChangeListener listener) {
        if (listener != null) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }
    }

    // /**
    // *
    // * @param args launch arguments
    // */
    // public static void main(String[] args) {
    // ConsoleUtils.setLoggingLevel(Level.FINE);
    // File logFile = new File(ATCParserTest.class.getResource("log_small2A2D.txt").getPath());
    // File configFile = new File(ThreadedAlternateLogParser.class.getResource("config.properties").getPath());
    // ThreadedAlternateLogParser threadedAlternateLogParser = new ThreadedAlternateLogParser(logFile, configFile, null);
    // threadedAlternateLogParser.start();
    // }
}
