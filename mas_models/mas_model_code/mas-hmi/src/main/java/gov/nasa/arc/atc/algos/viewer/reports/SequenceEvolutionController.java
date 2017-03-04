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

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.reports.ArrivalInfo;
import gov.nasa.arc.atc.reports.DepartureInfo;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class SequenceEvolutionController implements Initializable {

	public static final double TIME_PADDING_PERCENTAGE = 0.04;

	public static final double CENTER_WIDTH_PERCENTAGE = 0.15;
	public static final double CENTER_WIDTH_MIN = 25;

	private static final Logger LOG = Logger.getGlobal();

	private static final Comparator<SingleSequenceInOutView> inOutViewComparator = (SingleSequenceInOutView view1, SingleSequenceInOutView view2) -> Integer.compare(view1.getSimulationTime(), view2.getSimulationTime());

	@FXML
	private Pane sequencePane;
	@FXML
	private Slider progressSlider;
	@FXML
	private Button sequenceNextB;
	@FXML
	private Button sequencePreviousB;
	@FXML
	private ProgressBar sequenceProgressBar;
	@FXML
	private Label timeSequenceLabel;
	@FXML
	private Label simTimeLabel;

	private int nbInvocations = 0;
	private int sequenceIndex = 0;
	private double sequenceProgess = 0.0;

	private Group sequenceGroup;
	private TimeScale timeScale;
	private Rectangle past;

	private final List<SingleSequenceInOutView> sequences = new ArrayList<>();
	private SingleSequenceInOutView currentSequenceDisplayed = null;

	private Line leftVSeparation;
	private Line rightVSeparation;
	private Rectangle clip;

	private double sequenceWidth = 0.0;
	private double sequenceHeight = 0.0;
	private double seqCenterWidth = 0.0;

	private int duration = 0;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOG.log(Level.INFO, "init SequenceEvolutionController with URL={0} ResourceBundle={1}", new Object[] { location, resources });

		progressSlider.setValue(1);
		initSequencePane();
		initSizeListeners();
		updateSequenceControls();
		progressSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			LOG.log(Level.INFO, "progressSlider value change : {0} {1} {2}", new Object[] { observable, oldValue, newValue });
			sequenceIndex = (int) progressSlider.getValue();
			updateSequenceControls();
		});
	}

	@FXML
	public void onPreviousSequenceAction(ActionEvent event) {
		LOG.log(Level.FINE, "onPreviousSequenceAction : event = {0}", event);
		sequenceIndex--;
		updateSequenceControls();
	}

	@FXML
	public void onNextSequenceAction(ActionEvent event) {
		LOG.log(Level.FINE, "onNextSequenceAction : event = {0}", event);
		sequenceIndex++;
		updateSequenceControls();
	}

	public void displaySequences(final int reportDuration, Map<Integer, List<DepartureInfo>> depIn, Map<Integer, List<DepartureInfo>> depOut, Map<Integer, List<ArrivalInfo>> arrIn, Map<Integer, List<ArrivalInfo>> arrOut) {
		// clear data
		if (currentSequenceDisplayed != null) {
			sequenceGroup.getChildren().remove(currentSequenceDisplayed.getNode());
		}
		sequences.clear();
		duration = (int) (reportDuration * (1.0 + SequenceEvolutionController.TIME_PADDING_PERCENTAGE));
		timeScale.setMaxTime(duration);
		//
		Object[] timeValues = arrIn.keySet().toArray();
		//
		for (int i = 0; i < timeValues.length; i++) {
			final int simTime = (int) timeValues[i];
			final SingleSequenceInOutView sequenceView = new SingleSequenceInOutView(duration, simTime, arrIn.get(simTime), arrOut.get(simTime), depIn.get(simTime), depOut.get(simTime), null, null);
			sequences.add(sequenceView);
			sequenceView.updateSize(sequenceWidth, sequenceHeight, seqCenterWidth);
		}
		sequences.sort(inOutViewComparator);

		// hum ..
		sequences.forEach(view -> {
			if (!sequenceGroup.getChildren().contains(view.getNode())) {
				sequenceGroup.getChildren().add(view.getNode());
			}
		});
		sequences.forEach(view -> view.updateSize(sequenceWidth, sequenceHeight, seqCenterWidth));
		sequences.forEach(view -> {
			if (sequenceGroup.getChildren().contains(view.getNode())) {
				sequenceGroup.getChildren().remove(view.getNode());
			}
		});
		// end hum ..

		nbInvocations = sequences.size();
		if (!sequences.isEmpty()) {
			displayInvocation(0);
			updateSequenceControls();
		} else {
			currentSequenceDisplayed = null;
		}
	}

	private void initSequencePane() {
		leftVSeparation = new Line();
		rightVSeparation = new Line();
		clip = new Rectangle();
		timeScale = new TimeScale(sequencePane);
		sequenceGroup = new Group();
		past = new Rectangle();
		//
		leftVSeparation.setStroke(Color.WHITESMOKE);
		leftVSeparation.setStartY(0.0);
		rightVSeparation.setStroke(Color.WHITESMOKE);
		rightVSeparation.setStartY(0.0);
		//
		past.setFill(Color.LIGHTGRAY);
		//
		sequencePane.setClip(clip);
		//
		sequencePane.getChildren().add(past);
		sequencePane.getChildren().add(leftVSeparation);
		sequencePane.getChildren().add(rightVSeparation);
		sequencePane.getChildren().add(sequenceGroup);
		sequencePane.getChildren().add(timeScale.getNode());
		//
		sequenceWidth = sequencePane.getWidth();
		sequenceHeight = sequencePane.getHeight();
		//
		updateSequenceSize();
	}

	private void initSizeListeners() {
		sequencePane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			LOG.log(Level.INFO, "new scene width change : {0} {1} {2}", new Object[] { observable, oldValue, newValue });
			sequenceWidth = (double) newValue;
			updateSequenceSize();
		});
		sequencePane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			LOG.log(Level.INFO, "new scene height change : {0} {1} {2}", new Object[] { observable, oldValue, newValue });
			sequenceHeight = (double) newValue;
			updateSequenceSize();
		});
	}

	private void updateSequenceSize() {
		clip.setWidth(sequenceWidth);
		clip.setHeight(sequenceHeight);
		//
		past.setWidth(sequenceWidth);
		if (currentSequenceDisplayed != null) {
			int simTime = currentSequenceDisplayed.getSimulationTime();
			double yUp = (duration - simTime) * sequenceHeight / duration;
			past.setY(yUp);
			past.setHeight(sequenceHeight - yUp);
		} else {
			past.setHeight(0.0);
		}
		// separation line
		seqCenterWidth = Math.max(CENTER_WIDTH_MIN, sequenceWidth * CENTER_WIDTH_PERCENTAGE);
		leftVSeparation.setStartX((sequenceWidth - seqCenterWidth) / 2.0);
		leftVSeparation.setEndX((sequenceWidth - seqCenterWidth) / 2.0);
		leftVSeparation.setEndY(sequenceHeight);
		rightVSeparation.setStartX((sequenceWidth + seqCenterWidth) / 2.0);
		rightVSeparation.setEndX((sequenceWidth + seqCenterWidth) / 2.0);
		rightVSeparation.setEndY(sequenceHeight);
		sequences.forEach(sequence -> sequence.updateSize(sequenceWidth, sequenceHeight, seqCenterWidth));
		//
		timeScale.setWidth(seqCenterWidth);
		timeScale.setHeight(sequenceHeight);
		// can be optimized
		timeScale.setX(leftVSeparation.getStartX());
	}

	private void updateSequenceControls() {
		if (nbInvocations > 0) {
			progressSlider.setMax(nbInvocations - 1.0);
			sequenceProgess = (double) (sequenceIndex + 1) / (double) nbInvocations;
			sequenceNextB.setDisable(sequenceIndex >= (nbInvocations - 1));
			sequencePreviousB.setDisable(sequenceIndex == 0);
			timeSequenceLabel.setText(Integer.toString(sequenceIndex));
			sequenceProgressBar.setProgress(sequenceProgess);
			simTimeLabel.setText(getCurrentSimTimeString());
			progressSlider.setValue(sequenceIndex);
		} else {
			sequenceNextB.setDisable(true);
			sequencePreviousB.setDisable(true);
			timeSequenceLabel.setText(" --- ");
			sequenceProgressBar.setProgress(0);
			simTimeLabel.setText(" --- ");
			progressSlider.setMax(0);
		}
		displayInvocation(sequenceIndex);
	}

	private void displayInvocation(int invocationIndex) {
		if (currentSequenceDisplayed != null) {
			sequenceGroup.getChildren().remove(currentSequenceDisplayed.getNode());
		}
		if (!sequences.isEmpty()) {
			currentSequenceDisplayed = sequences.get(invocationIndex);
			sequenceGroup.getChildren().add(currentSequenceDisplayed.getNode());
			Platform.runLater(() -> currentSequenceDisplayed.updateSize(sequenceWidth, sequenceHeight, seqCenterWidth));
			int simTime = currentSequenceDisplayed.getSimulationTime();
			double yUp = (duration - simTime) * sequenceHeight / duration;
			past.setY(yUp);
			past.setHeight(sequenceHeight - yUp);
		} else {
			currentSequenceDisplayed = null;
		}
	}

	private String getCurrentSimTimeString() {
		if (currentSequenceDisplayed == null) {
			return "--";
		}
		return "Time : " + Integer.toString(currentSequenceDisplayed.getSimulationTime());
	}

}
