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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.arc.atc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.Region;
import gov.nasa.arc.atc.parsing.xml.queue.USAQueueParser;
import gov.nasa.arc.atc.parsing.xml.queue.XMLMaster;
import gov.nasa.arc.atc.utils.MapDrawingUtils;
import gov.nasa.arc.atc.utils.MathUtils;
import javafx.application.Platform;

/**
 *
 * @author hamon
 */
public class WorldGeography {

	public static final String ATC_GEOGRAPHY_ADDED = "newATCGeographyAdded";
	public static final String ATC_GEOGRAPHY_CHANGED = "atcGeographyChanged";

	private static final double DEFAULT_COORDINATES = 100.0;
	private static final File DEFAULT_US_FILE = new File(MainResources.class.getResource("states.xml").getPath());

	private final PropertyChangeSupport propertyChangeSupport;
	private final List<ATCGeography> atcGeographies;
	private final List<Region> backgroundRegions;

	private double minLatitude;
	private double minLongitude;
	private double maxLatitude;
	private double maxLongitude;
	private double deltaLatitude;
	private double deltaLongitude;
	//
	private double latOffset;
	private double longOffset;
	private double minLong;
	private double maxTanLat = 0;
	//
	private double minTanLat = DEFAULT_COORDINATES;
	private double tanLat;
	private double lon;
	private ATCGeography currentATCGeography;

	/**
	 * Creates a new empty geography
	 */
	@SuppressWarnings("unchecked")
	public WorldGeography() {
		propertyChangeSupport = new PropertyChangeSupport(WorldGeography.this);
		atcGeographies = new LinkedList<>();
		// TEMP
		backgroundRegions = (List<Region>) XMLMaster.requestParsing(DEFAULT_US_FILE, new USAQueueParser(), WorldGeography.this);
		minLatitude = DEFAULT_COORDINATES;
		minLongitude = DEFAULT_COORDINATES;
		maxLatitude = -DEFAULT_COORDINATES;
		maxLongitude = -DEFAULT_COORDINATES;
		deltaLatitude = 0;
		deltaLongitude = 0;
		maxTanLat = -DEFAULT_COORDINATES;
		minLong = DEFAULT_COORDINATES;
	}

	public void setATCGeography(ATCGeography geography) {
		if (!atcGeographies.contains(geography)) {
			addATCGeography(geography);
		}
		currentATCGeography = geography;
		Platform.runLater(() -> propertyChangeSupport.firePropertyChange(ATC_GEOGRAPHY_CHANGED, null, geography));
	}

	private void addATCGeography(ATCGeography geography) {
		atcGeographies.add(geography);
		// Updating min and max coordinates
		minLatitude = Math.min(minLatitude, geography.getMinLatitude());
		maxLatitude = Math.max(maxLatitude, geography.getMaxLatitude());
		minLongitude = Math.min(minLongitude, geography.getMinLongitude());
		maxLongitude = Math.max(maxLongitude, geography.getMaxLongitude());
		deltaLatitude = maxLatitude - minLatitude;
		deltaLongitude = maxLongitude - minLongitude;
		// TODO: fire some ppty change

		// TODO CHECK and optimize
		geography.getElements().forEach(element -> {
			tanLat = element.getTanLatitude();
			lon = element.getLongitude();
			if (minTanLat > tanLat) {
				minTanLat = tanLat;
			} else if (maxTanLat < tanLat) {
				maxTanLat = tanLat;
			}
			if (minLong > lon) {
				minLong = lon;
			} else if (maxLongitude < lon) {
				maxLongitude = lon;
			}
		});
		latOffset = maxTanLat - minTanLat;
		longOffset = maxLongitude - minLong;
		MapDrawingUtils.updateOnGeographyChange();
		Platform.runLater(() -> propertyChangeSupport.firePropertyChange(ATC_GEOGRAPHY_ADDED, null, geography));
	}

	public ATCGeography getCurrentATCGeography() {
		return currentATCGeography;
	}

	public List<Region> getBackgroundRegions() {
		return Collections.unmodifiableList(backgroundRegions);
	}

	public double getMaxLatitude() {
		return maxLatitude;
	}

	public double getMaxLongitude() {
		return maxLongitude;
	}

	public double getMinLatitude() {
		return minLatitude;
	}

	public double getMinLongitude() {
		return minLongitude;
	}

	public double getDeltaLatitude() {
		return deltaLatitude;
	}

	public double getDeltaLongitude() {
		return deltaLongitude;
	}

	public double getSectorLatOffset() {
		return latOffset;
	}

	public double getSectorLongOffset() {
		return longOffset;
	}

	public double getMaxTanLatitude() {
		return MathUtils.tanLatitude(maxLatitude);
	}

	/**
	 * 
	 * @param listener the PropertyChangeListener to be added
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * 
	 * @param listener the PropertyChangeListener to be added
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

}
