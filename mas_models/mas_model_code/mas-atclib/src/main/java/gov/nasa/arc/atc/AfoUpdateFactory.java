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

package gov.nasa.arc.atc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.arc.atc.geography.Position;

/**
 * @author ahamon
 */
public class AfoUpdateFactory {

    public static final String AFO_UPDATE_ELEMENT = "AFO_UPDATE";

    public static final int FALLBACK_DEPARTURE_TIME = -1;

    // TODO merge with other xml attributes
    public static final String NAME_ATTRIBUTE = "name";
    public static final String TIME_STAMP_ATTRIBUTE = "t";
    public static final String LATITUDE_ATTRIBUTE = "lat";
    public static final String LONGITUDE_ATTRIBUTE = "lng";
    public static final String ALTITUDE_ATTRIBUTE = "alt";
    public static final String AIR_SPEED_ATTRIBUTE = "airS";
    public static final String VERTICAL_SPEED_ATTRIBUTE = "vS";
    public static final String HEADING_ATTRIBUTE = "h";
    public static final String CURRENT_SEGMENT_ATTRIBUTE = "curSeg";
    public static final String TO_WAYPOINT_ATTRIBUTE = "toWPT";
    public static final String STATUS_ATTRIBUTE = "status";
    public static final String START_TIME_ATTRIBUTE = "startTime";
    public static final String DEPARTURE_TIME_ATTRIBUTE = "departureTime";
    //	public static final String IS_DEPARTURE_ATTRIBUTE = "dep";
    public static final String ETA_ATTRIBUTE = "eta";
    public static final String CONTROLLER_ATTRIBUTE = "atc";
    public static final String IS_METERING_ATTRIBUTE = "meter";
//	public static final String SECTOR_ATTRIBUTE = "curSector";

    /**
     * An empty update bloc
     */
    public static final AfoUpdate EMPTY_UPDATE = new AfoUpdateImpl("", 0, new Position(0, 0, 0), 0, 0, 0, 0, "", 0, 0, 0, 0, "", 0);

    private AfoUpdateFactory() {
        // private utility constructor
    }

    /**
     * @param doc    the document to contain the element
     * @param update the update to export
     * @return the corresponding xml element
     */
    public static Element createXML(Document doc, AfoUpdate update) {
        Element updateElemt = doc.createElement(AFO_UPDATE_ELEMENT);
        updateElemt.setAttribute(NAME_ATTRIBUTE, update.getAfoName());
        updateElemt.setAttribute(TIME_STAMP_ATTRIBUTE, Integer.toString(update.getTimeStamp()));
        updateElemt.setAttribute(LATITUDE_ATTRIBUTE, Double.toString(update.getPosition().getLatitude()));
        updateElemt.setAttribute(LONGITUDE_ATTRIBUTE, Double.toString(update.getPosition().getLongitude()));
        updateElemt.setAttribute(ALTITUDE_ATTRIBUTE, Double.toString(update.getPosition().getAltitude()));
        updateElemt.setAttribute(AIR_SPEED_ATTRIBUTE, Double.toString(update.getAirSpeed()));
        updateElemt.setAttribute(VERTICAL_SPEED_ATTRIBUTE, Double.toString(update.getVSpeed()));
        updateElemt.setAttribute(HEADING_ATTRIBUTE, Double.toString(update.getHeading()));
        updateElemt.setAttribute(CURRENT_SEGMENT_ATTRIBUTE, Integer.toString(update.getCurrentSegment()));
        updateElemt.setAttribute(TO_WAYPOINT_ATTRIBUTE, update.getToWaypoint());
        updateElemt.setAttribute(STATUS_ATTRIBUTE, Integer.toString(update.getStatus()));
        updateElemt.setAttribute(START_TIME_ATTRIBUTE, Integer.toString(update.getStartTime()));
        updateElemt.setAttribute(DEPARTURE_TIME_ATTRIBUTE, Integer.toString(update.getDepartureTime()));
        updateElemt.setAttribute(ETA_ATTRIBUTE, Double.toString(update.getEta()));
        updateElemt.setAttribute(CONTROLLER_ATTRIBUTE, update.getController());
        updateElemt.setAttribute(IS_METERING_ATTRIBUTE, Integer.toString(update.isMetering()));
        return updateElemt;
    }

    /**
     * @param element xml element to parse
     * @return the {@link AfoUpdate} containing the element's values
     */
    public static AfoUpdate parseElement(Element element) {
        final String name = element.getAttribute(NAME_ATTRIBUTE);
        final int timeStamp = Integer.parseInt(element.getAttribute(TIME_STAMP_ATTRIBUTE));
        final double latitude = Double.parseDouble(element.getAttribute(LATITUDE_ATTRIBUTE));
        final double longitude = Double.parseDouble(element.getAttribute(LONGITUDE_ATTRIBUTE));
        final double altitude = Double.parseDouble(element.getAttribute(ALTITUDE_ATTRIBUTE));
        final double airSpeed = Double.parseDouble(element.getAttribute(AIR_SPEED_ATTRIBUTE));
        final double vSpeed = Double.parseDouble(element.getAttribute(VERTICAL_SPEED_ATTRIBUTE));
        final double heading = Double.parseDouble(element.getAttribute(HEADING_ATTRIBUTE));
        final int currentSegment = Integer.parseInt(element.getAttribute(CURRENT_SEGMENT_ATTRIBUTE));
        final String toWaypoint = element.getAttribute(TO_WAYPOINT_ATTRIBUTE);
        final int status = Integer.parseInt(element.getAttribute(STATUS_ATTRIBUTE));
        final int startTime = Integer.parseInt(element.getAttribute(START_TIME_ATTRIBUTE));
        final int departureTime;
        if (element.hasAttribute(DEPARTURE_TIME_ATTRIBUTE)) {
            departureTime = Integer.parseInt(element.getAttribute(DEPARTURE_TIME_ATTRIBUTE));
        } else {
            departureTime = FALLBACK_DEPARTURE_TIME;
        }
        final double eta = Double.parseDouble(element.getAttribute(ETA_ATTRIBUTE));
        final String controller = element.getAttribute(CONTROLLER_ATTRIBUTE);
        final int isMetering = Integer.parseInt(element.getAttribute(IS_METERING_ATTRIBUTE));
        //
        return new AfoUpdateImpl(name, timeStamp, new Position(latitude, longitude, altitude), airSpeed, vSpeed, heading, currentSegment, toWaypoint, status, startTime, departureTime, eta, controller, isMetering);
    }

}
