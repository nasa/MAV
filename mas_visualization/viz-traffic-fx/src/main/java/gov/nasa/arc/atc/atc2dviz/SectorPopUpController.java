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

package gov.nasa.arc.atc.atc2dviz;

import gov.nasa.arc.atc.SimulationEngine;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.geography.Sector;
import gov.nasa.arc.atc.metrics.Metrics;
import gov.nasa.arc.atc.metrics.SectorCalculation;
import gov.nasa.arc.atc.metrics.SimulationCalculations;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.application.Platform.runLater;

/**
 * @author Kelsey
 * @author ahamon
 */
public class SectorPopUpController implements Initializable, PropertyChangeListener {
    @FXML
    private GridPane gridPane;
    @FXML
    private Label sectorName;
    @FXML
    private Label nbAircrafts;
    @FXML
    private Label controllerName;
    @FXML
    private Label workload;
    @FXML
    private Label perceptualWorkloadId;
    @FXML
    private Label temporalWorkloadId;
    @FXML
    private Label decisionWorkloadId;
    @FXML
    private Label perceptualWorkload;
    @FXML
    private Label temporalWorkload;
    @FXML
    private Label decisionWorkload;
    @FXML
    private Rectangle rectangle;
    @FXML
    private Label percentageCapacityId;
    @FXML
    private Label percentageCapacity;
    @FXML
    private Label maxCapacityId;
    @FXML
    private Label maxCapacity;
    @FXML
    private ToggleButton workloadButtonId;
    @FXML
    private ToggleButton hideWorkloadButtonId;
    @FXML
    private Label infoStatus;
    //	@FXML
//	private PieChart chartId;
    @FXML
    private Button close;
    private final PropertyChangeSupport propertyChangeSupport;
    private BlinkingAnimation blinkAnim;
    private Sector mySector;
    private boolean isBlinking;
    private boolean allowBlink;
    private boolean view;

    /**
     * --------------------------------
     */
    public SectorPopUpController() {
        // SimulationEngine.addPropertyChangeListener(this::propertyChange);
        propertyChangeSupport = new PropertyChangeSupport(SectorPopUpController.this.view);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setSector(Sector s) {
        mySector = s;
        mySector.addPropertyListener(this::propertyChange);
        SimulationEngine.addPropertyChangeListener(this::propertyChange);
        updateData();
    }

    @FXML
    private void handleClose(ActionEvent event) {
        view = false;
        propertyChangeSupport.firePropertyChange("handle_close", null, false);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        runLater(this::updateData);
    }

    // get info from mySector
    private void updateData() {
        if (isBlinking) {
            stopBlinking();
        }
        rectangle.setVisible(true); // rectangle
        workload.setVisible(true);
        String name = mySector.getName();
        sectorName.setText(name);

        //TODO is it the right place
        if (SimulationManager.getSimulationDataModel() != null) {

            try { // sector info
                //TODO: optimize

                //
//                System.err.println(" --- sector pop up:: " + Metrics.getSimulationCalculation(SimulationManager.getSimulationDataModel()).getSectorCalculations());
//                System.err.println(" > sector name:: " + name);
                SimulationCalculations simCal =Metrics.getSimulationCalculation(SimulationManager.getSimulationDataModel());
                SectorCalculation sectorCalc = simCal.getSectorCalculations().get(name);
//                System.err.println(" !! sectorCalc -> " + sectorCalc);


//                System.err.println(" !! ? FXML :: infoStatus= "+infoStatus);
//                System.err.println(" !! ? FXML :: nbAircrafts= "+nbAircrafts);
//                System.err.println(" !! ? FXML :: maxCapacity= "+maxCapacity);
//                System.err.println(" !! ? FXML :: percentageCapacity= "+percentageCapacity);
//                System.err.println(" !! ? FXML :: workload= "+workload);


                controllerName.setText(simCal.findControllerFromSector(name));

                //TODO remove
                if(sectorCalc==null){
                    return;
                }

                try { // get time
                    int simTime = SimulationEngine.getSimTime();
                    infoStatus.setText("");
                    nbAircrafts.setText(Integer.toString(sectorCalc.getnbAircrafts(simTime)));
                    maxCapacity.setText(Integer.toString(sectorCalc.getMaxCapacity()));
                    percentageCapacity.setText(roundDouble(sectorCalc.getCurrentCapacity(simTime) * 100) + "%");
                    workload.setText("Workloads");
//                    perceptualWorkload.setText(roundDouble(sectorCalc.getPerceptualWorkload()) + "%");
//                    temporalWorkload.setText(roundDouble(sectorCalc.getTemporalWorkload()) + "%");
//                    decisionWorkload.setText(roundDouble(sectorCalc.getDecisionWorkload()) + "%");
//				ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data("Perceptual", sectorCalc.getPerceptualWorkload()), new PieChart.Data("Temporal", sectorCalc.getTemporalWorkload()), new PieChart.Data("Decision", sectorCalc.getDecisionWorkload()));
//				chartId.setData(pieChartData);
                    this.updateRectangle(sectorCalc.getCurrentCapacity(simTime), this.getRectangleWidth());
                } catch (Exception e) {
                    infoStatus.setText("no sector info in init block?");
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                infoStatus.setText("no sector info in init block");
                ex.printStackTrace();
            }


        }
    }

    private double getRectangleWidth() {
        // get column 2 width
        ObservableList<ColumnConstraints> columnConstraints = gridPane.getColumnConstraints();
        ColumnConstraints constraints = columnConstraints.get(2);
        // get dynamic width as it changes??
        return constraints.getPrefWidth();
    }

    // set rectangle color and width
    private void updateRectangle(double capacity, double w) {
        double width = w * capacity;
        rectangle.setWidth(width);
        if (0 <= capacity && capacity < .50) {
            rectangle.setFill(Color.CORNFLOWERBLUE);
        } else if (capacity < .85) {
            rectangle.setFill(Color.GREEN);
        } else if (capacity < 1.00) {
            rectangle.setFill(Color.ORANGE);
        } else if (capacity < 1.15) {
            rectangle.setFill(Color.RED);
        } else {
            if (allowBlink) {
                blinkAnim = new BlinkingAnimation(rectangle);
                rectangle = blinkAnim.getMainComp();
                startBlinking();
            }
        }
    }

    public boolean isAllowingBlick() {
        return allowBlink;
    }

    public void allowBlinking() {
        allowBlink = true;
    }

    public void dontAllowBlinking() {
        allowBlink = false;
    }

    public void stopBlinking() {
        if (isBlinking) {
            isBlinking = false;
            blinkAnim.blinkOff();
        }
    }

    public void startBlinking() {
        isBlinking = true;
        blinkAnim.blinkOn();
    }

    // sets visibilities
    @FXML
    private void displayWorkloadBreakdown(ActionEvent event) {
        allowBlink = true;
        rectangle.setVisible(true); // rectangle
        workload.setVisible(true);
//		chartId.setVisible(true); // pie chart
        perceptualWorkload.setVisible(true);
        temporalWorkload.setVisible(true);
        decisionWorkload.setVisible(true);
        perceptualWorkloadId.setVisible(true);
        temporalWorkloadId.setVisible(true);
        decisionWorkloadId.setVisible(true);
    }

    @FXML
    private void hideWorkloadBreakdown(ActionEvent event) {
        // allowBlink=false;
        // rectangle.setVisible(false); //rectangle
        workload.setVisible(false);
//		chartId.setVisible(false); // pie chart
        perceptualWorkload.setVisible(false);
        temporalWorkload.setVisible(false);
        decisionWorkload.setVisible(false);
        perceptualWorkloadId.setVisible(false);
        temporalWorkloadId.setVisible(false);
        decisionWorkloadId.setVisible(false);
    }

    // format percentages
    private String roundDouble(Double num) {
        Double num1 = num * 100;
        long num2 = Math.round(num1);
        Double num3 = (double) num2;
        Double number = num3 / 100;
        return Double.toString(number);
    }

    public void addPropertyListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);

    }
}
