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
package gov.nasa.arc.atc.viewer;

import gov.nasa.arc.atc.SimulationEngine;
import gov.nasa.arc.atc.SimulationManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

/**
 * FXML Controller class
 * <p>
 * Images from: https://design.google.com/icons/
 *
 * @author hamon
 */
public class PlayerController implements Initializable, PropertyChangeListener {

    private static final int DEFAULT_BUTTON_SIZE = 30;
    private static final Logger LOG = Logger.getGlobal();
    private static final String TIME_LABEL_PREFIX = "Time : ";

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(PlayerController.this);

    private enum SpeedValue {
        X025("x 1/4", 0.25), X05("x 1/2", 0.5), X1("x 1", 1), X2("x 2", 2), X4("x 4", 4), X8("x 8", 8), X16("x 16", 16), X32("x 32", 32), X64("x 64", 64);

        private final String name;
        private final double speed;

        SpeedValue(String displayName, double value) {
            name = displayName;
            speed = value;
        }

        @Override
        public String toString() {
            return name;
        }

        protected double getSpeedValue() {
            return speed;
        }
    }


    @FXML
    private Button pausePlayButton;
    @FXML
    private Button nextStepButton;
    @FXML
    private Button previousStepButton;
    @FXML
    private ChoiceBox<SpeedValue> speedChoiceB;

    @FXML
    private Slider progressSlider;
    @FXML
    private Label timeLabel;

    private ImageView playImage;
    private ImageView pauseImage;

    private enum PlayerState {
        PLAY, PAUSE, NO_SCENARIO
    }

    private PlayerState playerState;
    private int currentStep = 0;
    private int lastStep = 0;
    private StringBuilder timeSBuilder;
    //
    private boolean playedBeforeSlidedPressed = false;

    private Timer dragTimer;

    /**
     * Initializes the controller class.
     *
     * @param url the url
     * @param rb  the resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image playIcon = new Image(getClass().getResourceAsStream("ic_play_circle_outline_black_24dp_2x.png"));
        Image pauseIcon = new Image(getClass().getResourceAsStream("ic_pause_circle_outline_black_24dp_2x.png"));
        Image nextIcon = new Image(getClass().getResourceAsStream("ic_skip_next_black_24dp_2x.png"));
        Image previousIcon = new Image(getClass().getResourceAsStream("ic_skip_previous_black_24dp_2x.png"));
        //
        playImage = new ImageView(playIcon);
        playImage.setFitWidth(DEFAULT_BUTTON_SIZE);
        playImage.setFitHeight(DEFAULT_BUTTON_SIZE);
        //
        pauseImage = new ImageView(pauseIcon);
        pauseImage.setFitWidth(DEFAULT_BUTTON_SIZE);
        pauseImage.setFitHeight(DEFAULT_BUTTON_SIZE);
        //
        ImageView nextImage = new ImageView(nextIcon);
        nextImage.setFitWidth(DEFAULT_BUTTON_SIZE);
        nextImage.setFitHeight(DEFAULT_BUTTON_SIZE);
        //
        ImageView previousImage = new ImageView(previousIcon);
        previousImage.setFitWidth(DEFAULT_BUTTON_SIZE);
        previousImage.setFitHeight(DEFAULT_BUTTON_SIZE);
        //
        pausePlayButton.setText("");
        speedChoiceB.setItems(FXCollections.observableArrayList(SpeedValue.values()));
        speedChoiceB.getSelectionModel().select(SpeedValue.X1);
        progressSlider.setDisable(true);
        timeLabel.setDisable(true);
        nextStepButton.setGraphic(nextImage);
        previousStepButton.setGraphic(previousImage);
        setPlayerState(PlayerState.NO_SCENARIO);
        //
        speedChoiceB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            LOG.log(Level.FINE, "speedChoice changed : {0} {1} {2}", new Object[]{observable, oldValue, newValue});
            propertyChangeSupport.firePropertyChange(SimulationEngine.SPEED_CHANGE, null, newValue.getSpeedValue());
        });
        //
        SimulationManager.addPropertyChangeListener(PlayerController.this);
        SimulationEngine.addPropertyChangeListener(PlayerController.this);
        propertyChangeSupport.addPropertyChangeListener(SimulationEngine.getInstance());
        //
        dragTimer = new Timer(50, e -> {
            LOG.log(Level.FINE, "drag timer {0}", e);
            propertyChangeSupport.firePropertyChange(SimulationEngine.SIMULATION_JUMP_TIME, null, (int) progressSlider.getValue());
        });
        dragTimer.setRepeats(false);
    }

    /**
     * Pauses the simulation replay. Fires a {@link PropertyChangeEvent}
     *
     * @param event {@link ActionEvent}
     */
    @FXML
    public void onPausePlayButton(ActionEvent event) {
        LOG.log(Level.FINE, "onPausePlayButton: {0}", event);
        switch (playerState) {
            case PAUSE:
                propertyChangeSupport.firePropertyChange(SimulationEngine.PLAY, null, null);
                break;
            case PLAY:
                propertyChangeSupport.firePropertyChange(SimulationEngine.PAUSE, null, null);
                break;
            default:
                throw new IllegalStateException(playerState.name());
        }
    }

    /**
     * Handles the simulation time change on the timeline's slider
     *
     * @param event {@link MouseEvent}
     */
    @FXML
    public void onSliderMousePressed(MouseEvent event) {
        LOG.log(Level.FINE, "onSliderMousePressed: {0}", event);
        if (playerState.equals(PlayerState.PLAY)) {
            playedBeforeSlidedPressed = true;
            propertyChangeSupport.firePropertyChange(SimulationEngine.PAUSE, null, null);
        } else {
            playedBeforeSlidedPressed = false;
        }
    }

    /**
     * Does nothing at the moment due to perfo issues with the former SimulationDataModel
     *
     * @param event {@link MouseEvent}
     */
    @FXML
    public void onSliderMouseDragged(MouseEvent event) {
        LOG.log(Level.FINE, "onSliderMouseDragged: on value {0} on event {1}", new Object[]{progressSlider.getValue(), event});
        if (!dragTimer.isRunning()) {
            dragTimer.start();
        }
    }

    /**
     * request move one time specified by the slider position
     *
     * @param event {@link MouseEvent} triggering the action
     */
    @FXML
    public void onSliderMouseReleased(MouseEvent event) {
        LOG.log(Level.FINE, "onSliderMouseReleased: {0}", event);
        propertyChangeSupport.firePropertyChange(SimulationEngine.SIMULATION_JUMP_TIME, null, (int) progressSlider.getValue());
        if (playedBeforeSlidedPressed) {
            propertyChangeSupport.firePropertyChange(SimulationEngine.PLAY, null, null);
        }
        if (dragTimer.isRunning()) {
            dragTimer.stop();
        }
    }

    /**
     * request move one time step backward in the {@link SimulationEngine}
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onPreviousAction(ActionEvent event) {
        LOG.log(Level.FINE, "onPreviousAction: {0}", event);
        propertyChangeSupport.firePropertyChange(SimulationEngine.PREVIOUS_STEP, null, null);
    }

    /**
     * request move one time step forward in the {@link SimulationEngine}
     *
     * @param event {@link ActionEvent} triggering the action
     */
    @FXML
    public void onNextAction(ActionEvent event) {
        LOG.log(Level.FINE, "onNextAction: {0}", event);
        propertyChangeSupport.firePropertyChange(SimulationEngine.NEXT_STEP, null, null);

    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case SimulationEngine.PLAY:
                setPlayerState(PlayerState.PLAY);
                break;
            case SimulationEngine.PAUSE:
                setPlayerState(PlayerState.PAUSE);
                break;
            case SimulationEngine.SIMULATION_DURATION:
                lastStep = SimulationManager.getSimulationDataModel().getMaxSimTime();
                runLater(() -> progressSlider.setMax(lastStep));
                // hum
                currentStep = SimulationManager.getSimulationDataModel().getMinSimTime();
                runLater(() -> progressSlider.setMin(currentStep));
                //
                updateProgress();
                break;
            case SimulationEngine.SIMULATION_TIME_CHANGED:
                currentStep = (int) event.getNewValue();
                updateProgress();
                break;
            case SimulationManager.DATA_MODEL_CHANGED:
                setPlayerState(PlayerState.PAUSE);
                currentStep = SimulationManager.getSimulationDataModel().getMinSimTime();
                runLater(() -> progressSlider.setMin(currentStep));
                progressSlider.setDisable(false);
                timeLabel.setDisable(false);
                break;
            default:
                // nothing to do
                break;
        }
    }

    private void setPlayerState(PlayerState state) {
        playerState = state;
        switch (playerState) {
            case PAUSE:
                displayAsPause();
                break;
            case PLAY:
                displayAsPlay();
                break;
            case NO_SCENARIO:
                displayAsNoScenario();
                break;
            default:
                // nothing
        }
    }

    private void displayAsPause() {
        pausePlayButton.setGraphic(playImage);
        previousStepButton.setDisable(false);
        nextStepButton.setDisable(false);
        speedChoiceB.setDisable(false);
        pausePlayButton.setDisable(false);
    }

    private void displayAsPlay() {
        pausePlayButton.setGraphic(pauseImage);
        previousStepButton.setDisable(true);
        nextStepButton.setDisable(true);
        speedChoiceB.setDisable(false);
        pausePlayButton.setDisable(false);
    }

    private void displayAsNoScenario() {
        pausePlayButton.setGraphic(playImage);
        previousStepButton.setDisable(true);
        nextStepButton.setDisable(true);
        pausePlayButton.setDisable(true);
        speedChoiceB.setDisable(true);
    }

    private void updateProgress() {
        runLater(() -> {
            progressSlider.setValue(currentStep);
            timeSBuilder = new StringBuilder();
            timeSBuilder.append(TIME_LABEL_PREFIX);
            timeSBuilder.append(currentStep);
            timeSBuilder.append("/");
            timeSBuilder.append(lastStep);
            timeLabel.setText(timeSBuilder.toString());
        });
    }

}
