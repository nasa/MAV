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

package gov.nasa.arc.atc.viewer.sequence;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.geography.Runway;
import gov.nasa.arc.atc.viewer.proto.SimpleSeqViewer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * 
 * @author ahamon
 *
 */
public class AirportSequencesViewerController implements Initializable {

	@FXML
	private Label airportLabel;
	@FXML
	private HBox sequencesBox;
	@FXML
	private CheckBox arrivalGapsCheckB;
	
	private final Map<Runway, SimpleSeqViewer> seqViewers = new HashMap<>();

	private Airport airport;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		arrivalGapsCheckB.selectedProperty().addListener((obs, old, newValue) -> seqViewers.values().forEach(seqV -> seqV.showGaps(newValue)));
	}

	public void setAirport(Airport anAirport) {
		airport=anAirport;
		airportLabel.setText(airport.getName());
		airport.getRunways().forEach(runway -> {
			SimpleSeqViewer seqViewer = new SimpleSeqViewer(runway);
			seqViewers.put(runway, seqViewer);
			sequencesBox.getChildren().add(seqViewer.getNode());
		});
	}

	public Airport getAirport() {
		return airport;
	}
	
	public void setSize(double newWidth, double newHeight) {
		// TODO Auto-generated method stub

	}

	public void setDataModel(DataModel dataModel) {
		seqViewers.values().forEach(seqViewer -> seqViewer.setDataModel(dataModel));

	}

	public void updateTime(int simTime) {
		seqViewers.values().forEach(seqViewer -> seqViewer.updateTimes(simTime));
	}

}
