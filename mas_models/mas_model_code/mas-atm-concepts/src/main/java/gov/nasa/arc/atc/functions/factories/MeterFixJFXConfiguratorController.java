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

import gov.nasa.arc.atc.functions.MeterFixCandidateFunction;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * 
 * @author ahamon
 *
 */
public class MeterFixJFXConfiguratorController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    private final BooleanProperty timeOK = new SimpleBooleanProperty(true);

	@FXML
	private TextField meterFixTimeField;

    private int time = MeterFixCandidateFunction.DEFAULT_INBOUND_METERFIX_TIME;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		meterFixTimeField.setText(Integer.toString(time));
		meterFixTimeField.textProperty().addListener((a1, a2, a3) -> {
			LOG.log(Level.FINE, "meterFixTimeField text changed {0}, {1} {2}", new Object[] { a1, a2, a3 });
			checkMeterFixTime();
		});
	}

	protected int getTime() {
		return time;
	}

	protected void addConfigOKListener(ChangeListener<Boolean> listener) {
		timeOK.addListener(listener);
	}

	protected void removeConfigOKListener(ChangeListener<Boolean> listener) {
		timeOK.removeListener(listener);
	}

	private void checkMeterFixTime() {
		try {
			time = Integer.parseInt(meterFixTimeField.getText());
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Not a valid number for the meterFix time :: {0} -> {1}", new Object[] { meterFixTimeField.getText(), e });
			time = -1;
		}
		timeOK.set(time > 0);
	}

}
