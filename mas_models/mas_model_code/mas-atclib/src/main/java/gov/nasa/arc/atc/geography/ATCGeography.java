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
* *******************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.geography;

import gov.nasa.arc.atc.factories.RouteFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author hamon
 *
 */
public class ATCGeography implements Geography {

	private final String geographyName;
	private final Map<String,ATCNode> waypoints;
	private final List<ATCNode> arrivalNodes;
	private final List<ATCNode> departureNodes;
	private final List<Segment> segments;
	private final List<Airport> airports;
	private final List<Sector> sectors;
	//
	private final Map<String, List<ATCNode>> previousNodes;
	private final Map<String, List<ATCNode>> nextNodes;
	//
	private List<Route> arrivalRoutes;
	//
	private double minLatitude;
	private double minLongitude;
	private double maxLatitude;
	private double maxLongitude;
	private double deltaLatitude;
	private double deltaLongitude;
	private double minTanLat;
	private double maxTanLat;
	//
	private double latOffset;
	private double longOffset;

	public ATCGeography(String atcGeographyName) {
		geographyName = atcGeographyName;
		waypoints = new HashMap<>();
		arrivalNodes = new LinkedList<>();
		departureNodes = new LinkedList<>();
		segments = new LinkedList<>();
		previousNodes = new HashMap<>();
		nextNodes = new HashMap<>();
		airports = new LinkedList<>();
		sectors = new LinkedList<>();
		arrivalRoutes = new ArrayList<>();
		//
		minLatitude = 90;
		minLongitude = 90;
		maxLatitude = -90;
		maxLongitude = -90;
		deltaLatitude = 0;
		deltaLongitude = 0;
		maxTanLat = 0;
		//
		minLatitude = 90;
		minLongitude = 90;
		maxLatitude = -90;
		maxLongitude = -90;
		deltaLatitude = 0;
		deltaLongitude = 0;
		maxTanLat = -90;
		minTanLat = 90;
	}

	public void addAirport(Airport airport) {
		airports.add(airport);
		airport.getRunways().forEach(runway -> {
			addArrivalNode(runway);
			addDepartureNode(runway);
		});
		updateBoundsCoordinates(airport);
	}

	public void addSector(Sector sector) {
		// TODO update mon max lat long
		sectors.add(sector);
	}

	public void addWaypoint(Waypoint wp) {
		if (!waypoints.containsKey(wp.getName())) {
			waypoints.put(wp.getName(),wp);
		}
		updateBoundsCoordinates(wp);
	}

	public void addArrivalNode(ATCNode node) {
		if (!waypoints.containsKey(node.getName())) {
			waypoints.put(node.getName(),node);
			updateBoundsCoordinates(node);
		}
		if (!arrivalNodes.contains(node)) {
			arrivalNodes.add(node);
		}
		if (node instanceof Runway) {
			updateAirports((Runway) node);
		}
	}

	public void addDepartureNode(ATCNode node) {
		if (!waypoints.containsKey(node.getName())) {
			waypoints.put(node.getName(),node);
			updateBoundsCoordinates(node);
		}
		if (!departureNodes.contains(node)) {
			departureNodes.add(node);
		}
		if (node instanceof Runway) {
			updateAirports((Runway) node);
		}
	}

	private void updateAirports(Runway runway) {
		if (runway.getAirport() == null) {
			throw new IllegalStateException("Airport cannot be null for runway: " + runway);
		}
		if (!airports.contains(runway.getAirport())) {
			airports.add(runway.getAirport());
		} else {
			airports.stream().filter(a -> a.getName() == null ? runway.getAirport().getName() == null : a.getName().equals(runway.getAirport().getName())).filter(a -> !a.getRunways().contains(runway)).forEach(a -> a.addRunway(runway));
		}
	}

	public void addSegment(Segment seg) {
		// Check that the segment edges are already added in the geography
		// This is a restrictive, but wanted design
		if (!waypoints.containsKey(seg.getFromWaypoint().getName())) {
			throw new IllegalStateException("FROM waypoint of " + seg + " does not exist in " + toString());
		}
		if (!waypoints.containsKey(seg.getToWaypoint().getName())) {
			throw new IllegalStateException("TO waypoint of " + seg + " does not exist in " + toString());
		}
		boolean hasDuplicates = false;
		for (Segment segment : segments) {
			if (segment.hasSameEdges(seg)) {
				hasDuplicates = true;
				break;
			}
		}
		if (!hasDuplicates) {
			segments.add(new Segment(seg.getFromWaypoint(), seg.getToWaypoint()));
			updateGraph(seg);
		}
	}

	public List<Airport> getAirports() {
		return Collections.unmodifiableList(airports);
	}

	public List<ATCNode> getWaypoints() {
		return Collections.unmodifiableList(waypoints.values().stream().collect(Collectors.toList()));
	}

	public ATCNode getNodeByName(String name) {
		return waypoints.get(name);
	}

	public List<Segment> getSegments() {
		return Collections.unmodifiableList(segments);
	}

	public List<Sector> getSectors() {
		return Collections.unmodifiableList(sectors);
	}

	public List<ATCNode> getPreviousNodes(ATCNode node) {
		if (previousNodes.containsKey(node.getName())) {
			return Collections.unmodifiableList(previousNodes.get(node.getName()));
		}
		return Collections.emptyList();
	}

	public List<ATCNode> getNextNodes(ATCNode node) {
		if (nextNodes.containsKey(node.getName())) {
			return Collections.unmodifiableList(nextNodes.get(node.getName()));
		}
		return Collections.emptyList();
	}

	public List<ATCNode> getPreviousNodes(String wpName) {
		for (ATCNode node : waypoints.values()) {
			if (node.getName().equals(wpName)) {
				return getPreviousNodes(node);
			}
		}
		return Collections.emptyList();
	}

	public List<ATCNode> getNextNodes(String wpName) {
		for (ATCNode node : waypoints.values()) {
			if (node.getName().equals(wpName)) {
				return getNextNodes(node);
			}
		}
		return Collections.emptyList();
	}

	public List<ATCNode> getArrivalNodes() {
		return Collections.unmodifiableList(arrivalNodes);
	}

	public List<ATCNode> getDepartureNodes() {
		return Collections.unmodifiableList(departureNodes);
	}

	public List<Route> getArrivalRoutes() {
		return Collections.unmodifiableList(arrivalRoutes);
	}

	public boolean isADepartureNode(String nodeName) {
		return departureNodes.stream().anyMatch(node -> node.getName().equals(nodeName));
	}

	public boolean isAnArrivalNode(String nodeName) {
		return arrivalNodes.stream().anyMatch(node -> node.getName().equals(nodeName));
	}

	public List<Route> getPathsToArrival(String arrivalName) {
		ATCNode arrival = null;
		for (ATCNode node : arrivalNodes) {
			if (node.getName().equals(arrivalName)) {
				arrival = node;
			}
		}
		if (arrival == null) {
			return Collections.emptyList();
		}
		Route route = new Route();
		return getPathsToNode(arrival, route);
	}

	private List<Route> getPathsToNode(ATCNode wp, Route routeToWP) {
		routeToWP.addAtStart(wp);
		List<Route> result = new LinkedList<>();
		List<ATCNode> previousWPs = getPreviousNodes(wp);
		switch (previousWPs.size()) {
		case 0:
			// nothing to do since no previous node
			result.add(routeToWP);
			break;
		case 1:
			result.addAll(getPathsToNode(previousWPs.get(0), routeToWP));
			break;
		default:
			previousWPs.forEach(previous -> result.addAll(getPathsToNode(previous, new Route(routeToWP))));
			break;
		}
		return Collections.unmodifiableList(result);
	}

	private void updateGraph(Segment seg) {
		// updating the map of next waypoints
		if (!nextNodes.containsKey(seg.getFromWaypoint().getName())) {
			nextNodes.put(seg.getFromWaypoint().getName(), new LinkedList<>());
		}
		nextNodes.get(seg.getFromWaypoint().getName()).add(seg.getToWaypoint());
		// updating the map of previous waypoints
		if (!previousNodes.containsKey(seg.getToWaypoint().getName())) {
			previousNodes.put(seg.getToWaypoint().getName(), new LinkedList<>());
		}
		previousNodes.get(seg.getToWaypoint().getName()).add(seg.getFromWaypoint());
		rebuildRoutes();
	}

	private void rebuildRoutes() {
		arrivalRoutes = RouteFactory.createArrivalRoutes(this);
		// TODO: departure routes
	}

	private void updateBoundsCoordinates(ATCNode node) {
		// Updating min and max coordinates
		minLatitude = Math.min(minLatitude, node.getLatitude());
		maxLatitude = Math.max(maxLatitude, node.getLatitude());
		minLongitude = Math.min(minLongitude, node.getLongitude());
		maxLongitude = Math.max(maxLongitude, node.getLongitude());
		deltaLatitude = maxLatitude - minLatitude;
		deltaLongitude = maxLongitude - minLongitude;

        double tanLat = node.getTanLatitude();
		if (minTanLat > tanLat) {
			minTanLat = tanLat;
		} else if (maxTanLat < tanLat) {
			maxTanLat = tanLat;
		}

		latOffset = maxTanLat - minTanLat;
		longOffset = maxLongitude - minLongitude;
	}

	@Override
	public String toString() {
		return geographyName;
	}

	@Override
	public double getMaxLatitude() {
		return maxLatitude;
	}

	@Override
	public double getMaxLongitude() {
		return maxLongitude;
	}

	@Override
	public double getMinLatitude() {
		return minLatitude;
	}

	@Override
	public double getMinLongitude() {
		return minLongitude;
	}

	@Override
	public double getDeltaLatitude() {
		return deltaLatitude;
	}

	@Override
	public double getDeltaLongitude() {
		return deltaLongitude;
	}

	@Override
	public List<GeographyElement> getElements() {
		return waypoints.values().stream().collect(Collectors.toList());
	}

	@Override
	public double getMaxTanLat() {
		return maxTanLat;
	}

	public double getLatitudeOffset() {
		return latOffset;
	}

	public double getLongitudeOffset() {
		return longOffset;
	}

	public boolean isRunwayOrAirport(String nodeName) {
		for (Airport a : airports) {
			if (a.getName().equals(nodeName)) {
				return true;
			}

			for (Runway r : a.getRunways()) {
				if (r.getName().equals(nodeName)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isRunway(String nodeName) {
		for (Airport a : airports) {
			for (Runway r : a.getRunways()) {
				if (r.getName().equals(nodeName)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isAirport(String nodeName) {
		for (Airport a : airports) {
			if (a.getName().equals(nodeName)) {
				return true;
			}
		}
		return false;
	}
}
