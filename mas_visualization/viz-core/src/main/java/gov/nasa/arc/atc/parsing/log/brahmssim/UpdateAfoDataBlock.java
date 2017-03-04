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

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.brahms.parsers.BrahmsModelsDefaultAttributes;
import gov.nasa.arc.atc.core.DataModelInput;
import gov.nasa.arc.atc.export.AfoUpdateLogger;
import gov.nasa.arc.atc.geography.Position;
import gov.nasa.arc.atc.parsing.DataBlocks;
import gov.nasa.arc.atc.parsing.log.LogDataBlock;
import gov.nasa.arc.atc.utils.SimulationProperties;
import javafx.util.Pair;

import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class UpdateAfoDataBlock implements LogDataBlock {

    // TODO: put it in mas_model for next lib packaging
    private static final String TIME_STAMP_KEY = "timeStamp";

    // data block structure
    //39139 INFO  [gov.nasa.arc.atm.atmmodel.scenarios.week2run6.agents.plane_DAL956] -
    //	***_START_AFOUpdate_*** plane-update
    //	AFO               DAL956
    //	m_iAirSpeed       260.0
    //	m_iVerticalSpeed  -9.0
    //	m_dLatitude       40.43334797757234
    //	m_dLongitude      74.9460198967631
    //	m_dAltitude       13293.29258199502
    //	m_dBearing        217.8444634212084
    //	m_headingEnum     South-South-West
    //	flightPlan
    //	iCurrentSegment   2
    //	toWaypoint        FOLAM
    //	iStatus           1
    //	m_iTimeStamp      287
    //	startTime         36
    //	is_departure      false
    //	ETA               1329.8071260695733
    //	controller        gov.nasa.arc.atm.atmmodel.scenarios.week2run6.agents.ZNY_114
    //	is_Metering       0
    //	***_END_AFOUpdate_***


    private static final Logger LOG = Logger.getGlobal();


    //TODO: include in the return type
    private int nbLines = 1;

    private final String speedPpty;
    private final String vSpeedPpty;
    private final String latitudePpty;
    private final String longitudePpty;
    private final String altitudePpty;
    private final String headingPpty;
    private final String currentSegmentPpty;
    private final String toWaypointPpty;
    private final String statusPpty;
    private final String startTimePpty;
    private final String isDeparturePpty;
    private final String etaPpty;
    private final String controllerPpty;
    private final String isMeteringPpty;
    private final String timeStampPpty;
    private final String afoPpty;
    private final String fullNamePpty;

    /**
     * @param properties the properties to configure the block parsing and matching pattern
     */
    public UpdateAfoDataBlock(Properties properties) {
        LOG.log(Level.FINE,"Creating UpdateAfoDataBlock using properties:: {0}", properties.propertyNames());
        speedPpty = properties.getProperty(SimulationProperties.SPEED_PPTY);
        vSpeedPpty = properties.getProperty(SimulationProperties.VERTICAL_SPEED_PPTY);
        latitudePpty = properties.getProperty(SimulationProperties.LATITUDE_PPTY);
        longitudePpty = properties.getProperty(SimulationProperties.LONGITUDE_PPTY);
        altitudePpty = properties.getProperty(SimulationProperties.ALTITUDE_PPTY);
        headingPpty = properties.getProperty(SimulationProperties.BEARING_PPTY);
        currentSegmentPpty = BrahmsModelsDefaultAttributes.I_CURRENT_SEGMENT; //TODO
        toWaypointPpty = BrahmsModelsDefaultAttributes.TO_WAYPOINT; //TODO
        statusPpty = BrahmsModelsDefaultAttributes.STATUS; //TODO
        startTimePpty = properties.getProperty(SimulationProperties.STARTED_TIME_PPTY);
        isDeparturePpty = properties.getProperty(SimulationProperties.DEPART_PPTY);
        etaPpty = properties.getProperty(SimulationProperties.ETA_PPTY);
        controllerPpty = BrahmsModelsDefaultAttributes.CONTROLLER_NAME; //TODO;
        isMeteringPpty = BrahmsModelsDefaultAttributes.IS_METERING; //TODO;
        if(properties.containsKey(TIME_STAMP_KEY)){
            System.err.println("!!!! Has  KEY "+properties.getProperty(TIME_STAMP_KEY));
            timeStampPpty = properties.getProperty(TIME_STAMP_KEY);
        }else{
            timeStampPpty = BrahmsModelsDefaultAttributes.M_I_TIMESTAMP;
            System.err.println("!!!! Has  NOT the key "+properties.getProperty(TIME_STAMP_KEY));
        }
        afoPpty = BrahmsModelsDefaultAttributes.AFO; //TODO;
        fullNamePpty = BrahmsModelsDefaultAttributes.FULL_NAME; //TODO
    }

    @Override
    public void parseBlock(List<String> lines, int firstLineIndex, DataModelInput inputs) {

        // determine type of agent
        String agentType = getAgentType(lines.get(firstLineIndex));
        //
        final ParsedAfoUpdate update = new ParsedAfoUpdate();
        String fullName = Long.toString(System.currentTimeMillis());
        //
        String attributeKey;

        nbLines = 1;
        Pair<String, String> attributeLogged;
        String currentLine = lines.get(nbLines + firstLineIndex);
        while (!currentLine.contains(AfoUpdateLogger.AFO_BLOCK_FOOTER)) {
            attributeLogged = DataBlocks.getLoggedAttributes(currentLine);
            //cannot use switch since not using variables
            attributeKey = attributeLogged.getKey();
            if (attributeKey.equals(speedPpty)) {
                update.setAirSpeed(Double.parseDouble(attributeLogged.getValue()));
            } else if (attributeKey.equals(vSpeedPpty)) {
                update.setVerticalSpeed(Double.parseDouble(attributeLogged.getValue()));
            } else if (attributeKey.equals(latitudePpty)) {
                update.setLatitude(Double.parseDouble(attributeLogged.getValue()));
            } else if (attributeKey.equals(longitudePpty)) {
                update.setLongitude(Double.parseDouble(attributeLogged.getValue()));//TODO fix longitude in models
            } else if (attributeKey.equals(altitudePpty)) {
                update.setAltitude(Double.parseDouble(attributeLogged.getValue()));
            } else if (attributeKey.equals(headingPpty)) {
                update.setHeading(Double.parseDouble(attributeLogged.getValue()));
            } else if (attributeKey.equals(currentSegmentPpty)) {
                update.setCurrentSegment(Integer.parseInt(attributeLogged.getValue()));
            } else if (attributeKey.equals(toWaypointPpty)) {
                update.setToWaypoint(attributeLogged.getValue());
            } else if (attributeKey.equals(statusPpty)) {
                update.setStatus(Integer.parseInt(attributeLogged.getValue()));
            } else if (attributeKey.equals(startTimePpty)) {
                update.setStartTime(Integer.parseInt(attributeLogged.getValue()));
            } else if (attributeKey.equals(isDeparturePpty)) {
//                update.setDeparture(Boolean.parseBoolean(attributeLogged.getValue()));
//                throw new IllegalArgumentException(" try t")
            } else if (attributeKey.equals(etaPpty)) {
                update.setEta(Double.parseDouble(attributeLogged.getValue()));
            } else if (attributeKey.equals(controllerPpty)) {
                update.setController(attributeLogged.getValue());
            } else if (attributeKey.equals(isMeteringPpty)) {
                update.setIsMetering(Integer.parseInt(attributeLogged.getValue()));
            } else if (attributeKey.equals(timeStampPpty)) {
                update.setTimeStamp(Integer.parseInt(attributeLogged.getValue()));
            } else if (attributeKey.equals(afoPpty)) {
                update.setAfoName(attributeLogged.getValue());
            } else if (attributeKey.equals(fullNamePpty)) {
                fullName = attributeLogged.getValue();
            }
            nbLines++;
            currentLine = lines.get(nbLines + firstLineIndex);
        }

        // needed to return the right value
        nbLines++;

        inputs.addAFOUpdate(fullName,agentType, update);
    }

    @Override
    public boolean headerMatches(String nextLine) {
        return nextLine.contains(AfoUpdateLogger.AFO_BLOCK_HEADER);
    }

    @Override
    public int getNbLines() {
        return nbLines;
    }

    private String getAgentType(String line) {
        // UGLY, should use patterns
        String formattedLine = line.replaceAll("[()]", " ").replaceAll("\\s+", " ");
        String[] splitLine = formattedLine.split(" ");
        return splitLine[splitLine.length - 1];
    }

    private static class ParsedAfoUpdate implements AfoUpdate {

        private String afoName;
        private int timeStamp;

        // for position
        private double latitude;
        private double longitude;
        private double altitude;

        private double airSpeed;
        private double vSpeed;
        private double heading;
        private int currentSegment;
        private String toWaypoint;
        private int status;
        private int startTime;
        private int departureTime = 0; //TODO
//        private boolean isDeparture;
        private double eta;
        private String controller;
        private int isMetering;

        private void setAfoName(String afoName) {
            this.afoName = afoName;
        }

        private void setTimeStamp(int timeStamp) {
            this.timeStamp = timeStamp;
        }

        private void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        private void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        private void setAltitude(double altitude) {
            this.altitude = altitude;
        }

        private void setAirSpeed(double airSpeed) {
            this.airSpeed = airSpeed;
        }

        private void setVerticalSpeed(double vSpeed) {
            this.vSpeed = vSpeed;
        }

        private void setHeading(double heading) {
            this.heading = heading;
        }

        private void setCurrentSegment(int currentSegment) {
            this.currentSegment = currentSegment;
        }

        private void setToWaypoint(String toWaypoint) {
            this.toWaypoint = toWaypoint;
        }

        private void setStatus(int status) {
            this.status = status;
        }

        private void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        private void setDepartureTime(int departureTime) {
            this.departureTime = departureTime;
        }

//        private void setDeparture(boolean departure) {
//            isDeparture = departure;
//        }

        private void setEta(double eta) {
            this.eta = eta;
        }

        private void setController(String controller) {
            this.controller = controller;
        }

        private void setIsMetering(int isMetering) {
            this.isMetering = isMetering;
        }

		/*
        * accessible part
		 */

        @Override
        public String getAfoName() {
            return afoName;
        }

        @Override
        public int getTimeStamp() {
            return timeStamp;
        }

        @Override
        public Position getPosition() {
            return new Position(latitude, longitude, altitude);
        }

        @Override
        public double getAirSpeed() {
            return airSpeed;
        }

        @Override
        public double getVSpeed() {
            return vSpeed;
        }

        @Override
        public double getHeading() {
            return heading;
        }

        @Override
        public int getCurrentSegment() {
            return currentSegment;
        }

        @Override
        public String getToWaypoint() {
            return toWaypoint;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public int getStartTime() {
            return startTime;
        }

        @Override
        public int getDepartureTime() {
            return departureTime;
        }
//
//        @Deprecated
//        @Override
//        public boolean isDeparture() {
//            return isDeparture;
//        }

        @Override
        public double getEta() {
            return eta;
        }

        @Override
        public String getController() {
            return controller;
        }

        @Override
        public int isMetering() {
            return isMetering;
        }


    }

}
