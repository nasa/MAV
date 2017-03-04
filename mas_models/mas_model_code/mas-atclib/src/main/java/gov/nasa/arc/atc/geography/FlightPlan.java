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

/********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.geography;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author hamon
 *
 */
public class FlightPlan {

	private static final Logger LOG = Logger.getGlobal();

	private static final double FPL_PRECISION = 0.000001;

	private final String afoName;
	private final List<FlightSegment> segments;
	private final List<FlightSegment> path;
	private final List<ATCNode> waypoints;
	//
	private Airport departureAirport;
	private Airport arrivalAirport;
	private Runway departureRunway;
	private Runway arrivalRunway;
	//
	private boolean isValid;
	private boolean isCompleted;
	//
	private FlightSegment initialSegment = null;
	private FlightSegment currentSegment = null;

	/**
	 * Creates an empty flightplan
	 * 
	 * @param name the name of the slot this flightplan corresponds to
	 */
	public FlightPlan(String name) {
		afoName = name;
		segments = new LinkedList<>();
		path = new LinkedList<>();
		waypoints = new LinkedList<>();
		isValid = true;
		isCompleted = false;
	}

	public String getAfoName() {
		return afoName;
	}

	/**
	 * 
	 * @param segment the segment to add
	 */
	public void addSegment(FlightSegment segment) {
		segments.add(segment);
		updatePath();
		updateWaypoints();
	}

	public void setDepartureRunway(Runway depRunway) {
		departureRunway = depRunway;
		departureAirport = departureRunway.getAirport();
		updatePath();
	}

	public void setArrivalRunway(Runway arrRunway) {
		arrivalRunway = arrRunway;
		arrivalAirport = arrivalRunway.getAirport();
		updatePath();
	}

	public Airport getArrivalAirport() {
		return arrivalAirport;
	}

	public Runway getArrivalRunway() {
		return arrivalRunway;
	}

	public Airport getDepartureAirport() {
		return departureAirport;
	}

	public Runway getDepartureRunway() {
		return departureRunway;
	}

	public List<FlightSegment> getSegments() {
		return Collections.unmodifiableList(segments);
	}

	public List<FlightSegment> getPath() {
		return Collections.unmodifiableList(path);
	}

	public boolean isValid() {
		return isValid;
	}

	public int getLenght() {
		return path.size();
	}

	private void updatePath() {
		// TODO take into account arrival/departure???
		path.clear();
		// should not have to test if empty since method is call on addSegment
		// initialization
		path.add(segments.get(0));
		List<FlightSegment> tmpSegmentsList = new LinkedList<>();
		tmpSegmentsList.addAll(segments);
		tmpSegmentsList.remove(0);
		//
		boolean add;
		int index = 0;

		//
		boolean progress = true;
		while (progress) {
			//
			int previousPathSize = path.size();
			// for each remain segment outside the path
			for (FlightSegment s : tmpSegmentsList) {
				add = false;
				// for each segment in path
				for (int i = 0; i < path.size(); i++) {
					if (s.getToWaypoint().equals(path.get(i).getFromWaypoint())) {
						add = true;
						index = i;
						break;
					}
					if (s.getFromWaypoint().equals(path.get(i).getToWaypoint())) {
						add = true;
						index = i + 1;
						break;
					}
				}
				// if segment is to be added
				if (add) {
					path.add(index, s);
				}

			}
			// removing all segments in path from tmpSegmentsList
			path.stream().filter(s -> tmpSegmentsList.contains(s)).forEach(tmpSegmentsList::remove);
			//
			int newPathSize = path.size();
			progress = newPathSize != previousPathSize;
		}
		// Check path is well sequenced
		if (path.size() != segments.size()) {
			isValid = false;
			return;
		}
		for (int i = 1; i < path.size() - 1; i++) {
			if (!path.get(i).getFromWaypoint().equals(path.get(i - 1).getToWaypoint())) {
				isValid = false;
				return;
			}
			if (!path.get(i).getToWaypoint().equals(path.get(i + 1).getFromWaypoint())) {
				isValid = false;
				return;
			}
		}
		isValid = true;
	}

	private void updateWaypoints() {
		waypoints.clear();
		segments.stream().map(segment -> {
			if (!waypoints.contains(segment.getFromWaypoint())) {
				waypoints.add(segment.getFromWaypoint());
			}
			return segment;
		}).filter(segment -> (!waypoints.contains(segment.getToWaypoint()))).forEach(segment -> waypoints.add(segment.getToWaypoint()));
	}

	public ATCNode getFirstWaypoint() {
		if (path.isEmpty()) {
			return null;
		}
		return path.get(0).getFromWaypoint();
	}

	public ATCNode getLastWaypoint() {
		if (path.isEmpty()) {
			return null;
		}
		return path.get(path.size() - 1).getToWaypoint();
	}

	public List<ATCNode> getWayPoints() {
		return Collections.unmodifiableList(waypoints);
	}

	public FlightSegment getInitialSegment() {
		return initialSegment;
	}

	/**
	 * This method is used to define the first segment used for the simulation
	 * 
	 * @param fromWP the name of the waypoint starting the segment
	 * @param toWP the name of the waypoint ending the segment
	 */
	public final void setInitialSegment(String fromWP, String toWP) {
		setCurrentSegment(fromWP, toWP);
		initialSegment = currentSegment;
	}

	/**
	 * This method is used to define the first segment used for the simulation
	 * 
	 * @param segment the starting segment
	 */
	public final void setInitialSegment(FlightSegment segment) {
		setCurrentSegment(segment);
		initialSegment = currentSegment;
	}

	/**
	 * This method is used to define the first segment used for the simulation
	 * 
	 * @param segmentID the segment's id, in the path order
	 */
	public final void setInitialSegment(int segmentID) {
		LOG.log(Level.FINE, "Setting initial segment (id={0}) to {1}", new Object[] { segmentID, path.get(segmentID) });
		setCurrentSegment(path.get(segmentID));
		initialSegment = currentSegment;
	}

	public final void setCurrentSegment(String fromWP, String toWP) {
		for (FlightSegment segment : path) {
			if (segment.getFromWaypoint().getName().equals(fromWP)) {
				if (!segment.getFromWaypoint().getName().equals(fromWP)) {
					throw new IllegalArgumentException("Current path has segment " + segment + " but requested waypoints are " + fromWP + " and " + toWP);
				}
				currentSegment = segment;
				break;
			}
		}
	}

	public final void setCurrentSegment(String toWP) {
		for (FlightSegment segment : path) {
			if (segment.getToWaypoint().getName().equals(toWP)) {
				currentSegment = segment;
				break;
			}
		}
	}

	public final void setCurrentSegment(FlightSegment segment) {
		// !! do not use contains as instances may be differents due to Brahms non persistence
		if (!pathContains(segment)) {
			throw new IllegalArgumentException("The current flight plan path does not contain ::" + segment);
		}
		currentSegment = segment;
	}

	public FlightSegment getCurrentSegment() {
		return currentSegment;
	}

	public int getCurrentSegmentIndex() {
		return path.indexOf(currentSegment);
	}

	public FlightSegment getSegment(int index) {
		return path.get(index);
	}

	public FlightSegment getSegmentEndingAt(String waypointName) {
		for (FlightSegment seg : path) {
			if (seg.getToWaypoint().getName().equals(waypointName)) {
				return seg;
			}
		}
		return null;
	}

	public int getSegmentIndex(FlightSegment segment) {
		if (segment == null) {
			return -1;
		}
		return path.indexOf(segment);
	}

	public int getSegmentIndexEndingAt(String waypointName) {
		return getSegmentIndex(getSegmentEndingAt(waypointName));
	}

	public boolean isCurrentSegment(FlightSegment segment) {
		return currentSegment.getToWaypoint().getName().equals(segment.getToWaypoint().getName()) && currentSegment.getFromWaypoint().getName().equals(segment.getFromWaypoint().getName());
	}

	public FlightSegment getPreviousSegment(FlightSegment segment) {
		for (FlightSegment pathSegment : path) {
			if (pathSegment.getToWaypoint().getName().equals(segment.getFromWaypoint().getName())) {
				return pathSegment;
			}
		}
		return null;
	}

	public void start() {
		if (!isValid) {
			throw new IllegalStateException("cannot start a non valid flightplan");
		}
		if (initialSegment == null) {
			throw new IllegalStateException("cannot start flight plan has not been initialized, use method :: setInitialSegment");
		}
		if (!path.isEmpty()) {
			currentSegment = initialSegment;
			isCompleted = false;
		} else {
			isCompleted = true;
		}
	}

	public void reset() {
		start();
	}

	public boolean advanceToNextSegment() {
		if (getNextSegment() != null) {
			currentSegment = getNextSegment();
			// not useful, just to be sure, remove and test eventually
			isCompleted = false;
			return true;
		}
		isCompleted = true;
		return false;
	}

	public boolean rewindToPreviousSegment() {
		if (getPreviousSegment() != null) {
			currentSegment = getPreviousSegment();
			isCompleted = false;
			return true;
		}
		return false;
	}

	public FlightSegment getPreviousSegment() {
		int index = path.indexOf(currentSegment);
		if (index > 0) {
			return path.get(index - 1);
		}
		return null;
	}

	public FlightSegment getNextSegment() {
		int index = path.indexOf(currentSegment);
		if (index < path.size() - 1) {
			return path.get(index + 1);
		}
		return null;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FlightPlan for::");
		sb.append(afoName);
		sb.append(" [");
		for (int i = 0; i < path.size(); i++) {
			sb.append(path.get(i).getFromWaypoint().getName());
			sb.append("-");
			sb.append(path.get(i).getToWaypoint().getName());
			sb.append(" ");
		}
		sb.append("] is valid->");
		sb.append(isValid);
		return sb.toString();
	}

	public boolean goesThrought(ATCNode node) {
		return path.stream().anyMatch(seg -> seg.getFromWaypoint().getName().equals(node.getName()) || seg.getToWaypoint().getName().equals(node.getName()));
	}

	public boolean goesThrought(String nodeName) {
		return path.stream().anyMatch(seg -> seg.getFromWaypoint().getName().equals(nodeName) || seg.getToWaypoint().getName().equals(nodeName));
	}

	private boolean pathContains(FlightSegment segment) {
		for (FlightSegment segPath : path) {
			if (segPath.getFromWaypoint().equals(segment.getFromWaypoint()) && segPath.getToWaypoint().equals(segment.getToWaypoint())) {
				boolean afoMatch = segPath.getAfoName().equals(segment.getAfoName());
				boolean altitudeMatch = Math.abs(segPath.getdEndAltitude() - segment.getdEndAltitude()) < FPL_PRECISION;
				boolean speedMatch = Math.abs(segPath.getEndSpeed() - segment.getEndSpeed()) < FPL_PRECISION;
				//
//				assert afoMatch;
//				assert altitudeMatch;
//				assert speedMatch;
				return afoMatch && altitudeMatch && speedMatch;
			}
		}

		return false;
	}
}
