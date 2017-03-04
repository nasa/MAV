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

package gov.nasa.arc.atc.scenariogen.hmi.flightplans;

import gov.nasa.arc.atc.scenariogen.core.Flow;
import gov.nasa.arc.atc.scenariogen.core.ScheduledFlightPlan;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class ScheduledFlightPlanContainerController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    @FXML
    private ListView<ScheduledFlightPlan> flightPlanList;
    @FXML
    private AnchorPane viewerAnchorP;

    private final List<Flow> flows = new LinkedList<>();

    private ScheduledFlightPlanViewer viewer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewer = new ScheduledFlightPlanViewer();
        viewerAnchorP.getChildren().add(viewer);
        AnchorPane.setBottomAnchor(viewer, 8.0);
        AnchorPane.setTopAnchor(viewer, 8.0);
        AnchorPane.setRightAnchor(viewer, 8.0);
        AnchorPane.setLeftAnchor(viewer, 8.0);
        flightPlanList.getSelectionModel().selectedItemProperty().addListener((obs, old, newV) -> {
            LOG.log(Level.FINE,"ScheduledFlightPlanContainerController flightPlanList selection changed: {0} {1} {2}",new Object[]{obs,old,newV});
            if (newV != null) {
                viewer.displayFlightPlan(newV);
            }
        });
    }

    public void addFlow(Flow flow) {
        flows.add(flow);
        flow.addPropertyChangeListener(this::handleFlowChange);
    }

    private void handleFlowChange(PropertyChangeEvent event) {
        LOG.log(Level.FINE,"handleFlowChange: {0}",event);
        rebuildList();
    }

    private void rebuildList() {
        flightPlanList.getItems().clear();
        flows.forEach(flow -> flow.getSubFlows().forEach(subFlow -> flightPlanList.getItems().addAll(subFlow.getGeneratedFlightPlans())));
    }


}
