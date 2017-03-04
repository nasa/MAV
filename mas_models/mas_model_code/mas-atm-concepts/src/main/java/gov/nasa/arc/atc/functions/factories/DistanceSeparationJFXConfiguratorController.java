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

package gov.nasa.arc.atc.functions.factories;

import gov.nasa.arc.atc.simulation.SimulationContext;
import gov.nasa.arc.atc.geography.Airport;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

/**
 * 
 * @author ahamon
 *
 */
public class DistanceSeparationJFXConfiguratorController implements Initializable {

    private final BooleanProperty airportOK = new SimpleBooleanProperty(false);

	@FXML
	private ComboBox<Airport> airportBox;

    private SimulationContext context;
    private Airport selectedAirport;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// noting to do, init on setScenario
	}

	public Airport getAirport() {
		return selectedAirport;
	}

	public void setScenario(SimulationContext simulationContext) {
		context = simulationContext;
		airportBox.getItems().setAll(context.getGeography().getAirports());
		airportBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
			selectedAirport = newV;
			airportOK.setValue(selectedAirport != null);
		});
	}

	protected void addConfigOKListener(ChangeListener<Boolean> listener) {
		airportOK.addListener(listener);
	}

	protected void removeConfigOKListener(ChangeListener<Boolean> listener) {
		airportOK.removeListener(listener);
	}

}
