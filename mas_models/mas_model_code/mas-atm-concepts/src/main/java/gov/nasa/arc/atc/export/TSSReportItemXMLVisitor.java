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

package gov.nasa.arc.atc.export;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.arc.atc.reports.TSSReportItem;
import gov.nasa.arc.atc.reports.tss.ScheduleConflictResolution;
import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.simulation.TimedFlightParameters;
import gov.nasa.arc.atc.FlightParameters;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.geography.Runway;

public class TSSReportItemXMLVisitor {

	public static final String ALGO_ATTRIBUTE = "algorithm";
	public static final String SCENARIO_ATTRIBUTE = "scenario";
	public static final String NB_STEPS_ATTRIBUTE = "nbSteps";
	public static final String STEP_DURATION_ATTRIBUTE = "stepDuration";
	public static final String TIME_ATTRIBUTE = "time";
	public static final String DELAY_ATTRIBUTE = "delay";
	public static final String LEAD_ATTRIBUTE = "lead";
	public static final String TRAIL_ATTRIBUTE = "trail";
	public static final String NODE_ATTRIBUTE = "node";
	public static final String REASON_ATTRIBUTE = "reason";
	public static final String MESSAGE_ATTRIBUTE = "message";
	public static final String ITEM_TYPE_ATTRIBUTE = "type";
	//
	public static final String FROM_WPT_ATTRIBUTE = "fromWPT";
	public static final String TO_WPT_ATTRIBUTE = "toWPT";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String LATITUDE_ATTRIBUTE = "latitude";
	public static final String LONGITUDE_ATTRIBUTE = "longitude";
	public static final String ALTITUDE_ATTRIBUTE = "altitude";
	public static final String HEADING_ATTRIBUTE = "heading";
	public static final String STATUS_ATTRIBUTE = "status";
	public static final String AIRSPEED_ATTRIBUTE = "airSpeed";
	public static final String VSPEED_ATTRIBUTE = "vSpeed";
	public static final String ID_ATTRIBUTE = "id";

	public static final String SIMPLE_ITEM_TYPE = "simpleItem";

	private static final Logger LOG = Logger.getGlobal();

	private TSSReportItemXMLVisitor() {
		// private utility constructor
	}

	public static Element exportItem(Document doc, TSSReportItem item) {
		if (item instanceof ScheduleConflictResolution) {
			return exportScheduleConflictResolution(doc, (ScheduleConflictResolution) item);
		}
		return exportSimpleItem(doc, item);
	}

	public static Element exportScheduleConflictResolution(Document doc, ScheduleConflictResolution item) {
		Element itemRoot = doc.createElement(ReportXMLExport.ITEM_ELEMENT);
		itemRoot.setAttribute(ITEM_TYPE_ATTRIBUTE, ScheduleConflictResolution.class.getSimpleName());
		itemRoot.setAttribute(ID_ATTRIBUTE, Integer.toString(item.getID()));
		item.getValues().forEach((name, value) -> exportItemValue(doc, itemRoot, name, value));
		return itemRoot;
	}

	public static Element exportSimpleItem(Document doc, TSSReportItem item) {
		LOG.log(Level.WARNING, "exporting simple item ?? {0}", item);
		Element itemElement = doc.createElement(ReportXMLExport.ITEM_ELEMENT);
		itemElement.setAttribute(ITEM_TYPE_ATTRIBUTE, item.getClass().getSimpleName());
		itemElement.setAttribute(DELAY_ATTRIBUTE, Integer.toString(item.getDelay()));
		itemElement.setAttribute(LEAD_ATTRIBUTE, item.getLead());
		itemElement.setAttribute(TRAIL_ATTRIBUTE, item.getTrail());
		itemElement.setAttribute(NODE_ATTRIBUTE, item.getNodeName());
		itemElement.setAttribute(REASON_ATTRIBUTE, item.getReason());
		itemElement.setAttribute(TIME_ATTRIBUTE, Integer.toString(item.getSimulationTime()));
		itemElement.setAttribute(ID_ATTRIBUTE, Integer.toString(item.getID()));
		return itemElement;
	}

	private static void exportItemValue(Document doc, Element parentElement, String name, Object value) {
		final Element valueElement = doc.createElement(name);
		if (value != null) {
			if (value instanceof FlightSegment) {
				exportFlightSegment(valueElement, (FlightSegment) value);
			} else if (value instanceof Runway) {
				exportRunway(valueElement, (Runway) value);
			} else if (value instanceof SimulatedTrajectory) {
				exportSimulatedTrajectory(valueElement, (SimulatedTrajectory) value);
			} else if (value instanceof TimedFlightParameters) {
				exportTimedParameter(valueElement, (TimedFlightParameters) value);
			} else if (value instanceof FlightParameters) {
				exportFlightParameter(valueElement, (FlightParameters) value);
			} else {
				valueElement.setTextContent(value.toString());
			}
		} else {
			LOG.log(Level.SEVERE, "value {0} is null on document {1}", new Object[] { name, doc });
		}
		parentElement.appendChild(valueElement);
	}

	private static void exportFlightSegment(Element element, FlightSegment segment) {
		element.setAttribute(FROM_WPT_ATTRIBUTE, segment.getFromWaypoint().getName());
		element.setAttribute(TO_WPT_ATTRIBUTE, segment.getToWaypoint().getName());
	}

	private static void exportRunway(Element element, Runway runway) {
		element.setAttribute(NAME_ATTRIBUTE, runway.getName());
	}

	private static void exportSimulatedTrajectory(Element element, SimulatedTrajectory trajectory) {
		element.setAttribute(NAME_ATTRIBUTE, trajectory.getSlotMarker().getName());
	}

	private static void exportTimedParameter(Element element, TimedFlightParameters timedFlightParameters) {
		element.setAttribute(TIME_ATTRIBUTE, Integer.toString(timedFlightParameters.getTimeStamp()));
		exportFlightParameter(element, timedFlightParameters);
	}

	private static void exportFlightParameter(Element element, FlightParameters flightParameters) {
		element.setAttribute(LATITUDE_ATTRIBUTE, Double.toString(flightParameters.getLatitude()));
		element.setAttribute(LONGITUDE_ATTRIBUTE, Double.toString(flightParameters.getLongitude()));
		element.setAttribute(ALTITUDE_ATTRIBUTE, Double.toString(flightParameters.getAltitude()));
		element.setAttribute(HEADING_ATTRIBUTE, Double.toString(flightParameters.getHeading()));
		element.setAttribute(STATUS_ATTRIBUTE, Integer.toString(flightParameters.getStatus()));
		element.setAttribute(AIRSPEED_ATTRIBUTE, Double.toString(flightParameters.getAirSpeed()));
		element.setAttribute(VSPEED_ATTRIBUTE, Double.toString(flightParameters.getVerticalSpeed()));
	}

}
