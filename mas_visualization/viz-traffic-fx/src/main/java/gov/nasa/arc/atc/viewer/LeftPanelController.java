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
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.core.NewPlane;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.Runway;
import gov.nasa.arc.atc.viewer.proto.SimpleSeqViewer;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;

import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

/**
 * FXML Controller class
 *
 * @author hamon
 */
public class LeftPanelController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    private SimpleSeqViewer arrivalSequenceViewer;

    @FXML
    private ChoiceBox<ATCNode> runwayChoiceB;
    @FXML
    private Pane arrivalSequencePane;
    @FXML
    private CheckBox arrivalGapsCheckB;

//    private final List<AirportSequencesViewer> airportSequences = new LinkedList<>();
//    private CustomStage customStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //
//        customStage = new CustomStage();
        //


        arrivalSequenceViewer = new SimpleSeqViewer();
        arrivalSequencePane.getChildren().add(arrivalSequenceViewer.getNode());
        arrivalSequencePane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            LOG.log(Level.FINE, "new arrivalSequencePane width change : {0} {1} {2}", new Object[]{observable, oldValue, newValue});
            updateOnSizeChange();
        });
        arrivalSequencePane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            LOG.log(Level.FINE, "new arrivalSequencePane height change : {0} {1} {2}", new Object[]{observable, oldValue, newValue});
            updateOnSizeChange();
        });
        arrivalGapsCheckB.setSelected(true);
        arrivalGapsCheckB.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            LOG.log(Level.FINE, "new arrival gap visibility change : {0} {1} {2}", new Object[]{observable, oldValue, newValue});
            arrivalSequenceViewer.showGaps(arrivalGapsCheckB.isSelected());
        });
        //
        SimulationEngine.addPropertyChangeListener(this::handleSimulationChange);
        SimulationManager.addPropertyChangeListener(this::handleSimulationChange);
        runLater(this::updateOnSizeChange);
    }

    private void updateOnSizeChange() {
        arrivalSequenceViewer.setSize(arrivalSequencePane.getWidth(), arrivalSequencePane.getHeight());
    }

    private void handleSimulationChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case SimulationManager.DATA_MODEL_CHANGED:
                DataModel dataModel = (DataModel) evt.getNewValue();
                //its a trap...
                ATCNode runway = findBusiestRunway(dataModel);
                arrivalSequenceViewer.setRunway(runway);
                arrivalSequenceViewer.setDataModel(dataModel);
//                airportSequences.forEach(viewer -> viewer.setDataModel(dataModel));
                break;
            case SimulationEngine.SIMULATION_TIME_CHANGED:
                final int simTime = (int) evt.getNewValue();
                arrivalSequenceViewer.updateTimes(simTime);
//                airportSequences.forEach(viewer -> viewer.updateTime(simTime));
                break;
            case SimulationManager.ATC_GEOGRAPHY_CHANGED:
                ATCGeography geography = (ATCGeography) evt.getNewValue();
                runwayChoiceB.getItems().clear();
                geography.getAirports().forEach(airport -> runwayChoiceB.getItems().addAll(airport.getRunways()));
//                geography.getAirports().forEach(airport -> {
//                    AirportSequencesViewer viewer = new AirportSequencesViewer();
//                    viewer.setAirport(airport);
//                    airportSequences.add(viewer);
//                    customStage.addContent(viewer);
//                });
                break;
            default:
                break;
        }
    }

    private ATCNode findBusiestRunway(DataModel dataModel) {
        Map<String, Integer> arrivals = new HashMap<>();
        for(Airport airport : SimulationManager.getATCGeography().getAirports()){
            for(Runway r : airport.getRunways()){
                arrivals.put(r.getName(),0);
            }
        }
        for (NewPlane plane : dataModel.getArrivingPlanes()) {
            if (arrivals.containsKey(plane.getDestination())) {
                int oldV = arrivals.get(plane.getDestination());
                arrivals.put(plane.getDestination(), oldV + 1);
            }
        }
        Optional<Map.Entry<String, Integer>> runway = arrivals.entrySet().stream().max((e1, e2) -> Integer.compare(e1.getValue(), e2.getValue()));
        if (runway.isPresent()) {
            return  SimulationManager.getATCGeography().getNodeByName(runway.get().getKey());
        }
        return null;
    }

}
