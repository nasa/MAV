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

package gov.nasa.arc.atc.algos.viewer.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.atc.reports.ArrivalInfo;
import gov.nasa.arc.atc.reports.DepartureInfo;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 * 
 * @author ahamon
 *
 */
public class SingleSequenceInOutView {

	private final Group mainNode;
	private final int simTime;
	// TODO : remove refs ?
	private final List<ArrivalInfo> arrivalsIN;
	private final List<ArrivalInfo> arrivalsOUT;
	private final List<DepartureInfo> departuresIN;
	private final List<DepartureInfo> departuresOUT;

	private final int duration;

	private final Map<Integer, Integer> lastLandings;
	private final Map<Integer, Integer> lastTakeOffs;

	private final List<FullSequenceTag> tags;

	private double width = 1.0;
	private double height = 1.0;
	private double centerWidth = 0.0;
	private double secondsPixelRatio = 1;

	/**
	 * 
	 * @param reportDuration
	 * @param simulationTine
	 * @param arrivalsIN
	 * @param arrivalsOUT
	 * @param departuresIN
	 * @param departuresOUT
	 * @param lastLandings
	 * @param lastTakeOffs
	 */
	public SingleSequenceInOutView(final int reportDuration, final int simulationTine, List<ArrivalInfo> arrivalsIN, List<ArrivalInfo> arrivalsOUT, List<DepartureInfo> departuresIN, List<DepartureInfo> departuresOUT, Map<Integer, Integer> lastLandings, Map<Integer, Integer> lastTakeOffs) {
		mainNode = new Group();
		simTime = simulationTine;
		// duration = (int) (reportDuration * (1.0 + SequenceEvolutionController.TIME_PADDING_PERCENTAGE));
		duration = reportDuration;
		this.arrivalsIN = arrivalsIN != null ? arrivalsIN : Collections.emptyList();
		this.arrivalsOUT = arrivalsOUT != null ? arrivalsOUT : Collections.emptyList();
		this.departuresIN = departuresIN != null ? departuresIN : Collections.emptyList();
		this.departuresOUT = departuresOUT != null ? departuresOUT : Collections.emptyList();
		this.lastLandings = lastLandings;
		this.lastTakeOffs = lastTakeOffs;
		//
		tags = new ArrayList<>();
		//
		createView();
	}

	protected void updateSize(double newWidth, double newHeight, double seqCenterWidth) {
		width = newWidth;
		height = newHeight;
		centerWidth = seqCenterWidth;
		secondsPixelRatio = height / duration;
		updateView();
	}

	public int getSimulationTime() {
		return simTime;
	}

	public Node getNode() {
		return mainNode;
	}

	private void createView() {
		for (int i = 0; i < arrivalsOUT.size(); i++) {
			ArrivalInfo in = arrivalsIN.get(i);
			ArrivalInfo out = arrivalsOUT.get(i);
			FullSequenceTag tag = new FullSequenceTag(duration, in, out);
			tags.add(tag);
			mainNode.getChildren().add(tag.getNode());
		}

		for (int i = 0; i < departuresOUT.size(); i++) {
			DepartureInfo in = departuresIN.get(i);
			DepartureInfo out = departuresOUT.get(i);
			FullSequenceTag tag = new FullSequenceTag(duration, in, out);
			tags.add(tag);
			mainNode.getChildren().add(tag.getNode());
		}
		// TODO : landed planes

	}

	private void updateView() {
		tags.forEach(tag -> {
			tag.setWidth(width, centerWidth);
			tag.setY(secondsPixelRatio);
		});
	}

}
