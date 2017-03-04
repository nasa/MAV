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

package gov.nasa.arc.atc.viewer.flightplan;

import gov.nasa.arc.atc.FlightPlanUpdate;
import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.geography.FlightSegment;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class FlightPlanViewerContainerController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    private DataModel dataModel;

    @FXML
    private ListView<FlightPlanUpdate> flightPlanList;
    @FXML
    private AnchorPane viewerAnchorP;


    private FlightPlanViewer viewer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewer = new FlightPlanViewer();
        viewerAnchorP.getChildren().add(viewer);
        AnchorPane.setBottomAnchor(viewer, 8.0);
        AnchorPane.setTopAnchor(viewer, 8.0);
        AnchorPane.setRightAnchor(viewer, 8.0);
        AnchorPane.setLeftAnchor(viewer, 8.0);
        flightPlanList.getSelectionModel().selectedItemProperty().addListener((obs, old, newV) -> {
            LOG.log(Level.FINE, "FlightPlanViewerContainerController flightPlanList selection changed: {0} {1} {2}", new Object[]{obs, old, newV});
            if (newV != null) {
                viewer.displayFlightPlanUpdate(newV);
            }
        });
    }

    public void setDataModel(DataModel data) {
        dataModel = data;
        double minS = Double.MAX_VALUE;
        double maxS = Double.MIN_VALUE;
        for(FlightPlanUpdate update : data.getFlighPlanUpdates()){
            for(FlightSegment s : update.getFlightSegments()){
                minS= Math.min(minS,s.getEndSpeed());
                maxS= Math.max(maxS,s.getEndSpeed());
            }
        }
        System.err.println(" !!! Speeds ["+minS+" - "+maxS+"]");
        rebuildList();
    }


    private void rebuildList() {
        flightPlanList.getItems().clear();
        flightPlanList.getItems().addAll(dataModel.getFlighPlanUpdates());
    }


}
