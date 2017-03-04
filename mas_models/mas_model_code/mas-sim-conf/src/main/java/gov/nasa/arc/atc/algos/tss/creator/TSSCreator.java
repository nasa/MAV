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

package gov.nasa.arc.atc.algos.tss.creator;

import gov.nasa.arc.atc.algos.Algorithm;
import gov.nasa.arc.atc.algos.AlgorithmCreator;
import gov.nasa.arc.atc.simulation.SimulationContext;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * 
 * @author ahamon
 *
 */
public class TSSCreator implements AlgorithmCreator {

	private static final String FXML_NAME = "TSSCreator.fxml";
	private static final Logger LOG = Logger.getGlobal();

	private static final String HELP_MESSAGE = "A TSS algorithm needs: \n" + 
			" - a function to determine eligible slots \n" + 
			" - a function to separate to consecutive slots \n" + 
			" - a arrival waypoint";

    private final BooleanProperty configurationOK = new SimpleBooleanProperty(false);

	private Node rootNode;
	private TSSCreatorFxControler controller;


	public TSSCreator() {
		// fxml is only parsed when getNode is invoked
	}

	@Override
	public Node getNode() {
		if (rootNode == null) {
			loadFXML();
		}
		return rootNode;
	}

	@Override
	public boolean isConfigurationOK() {
		return controller != null ? controller.isConfigurationOK() : false;
	}

	@Override
	public void setScenario(SimulationContext context) {
		if (controller == null) {
			loadFXML();
		}
		controller.setSimulationContext(context);
	}

	@Override
	public Algorithm createAlgorithm() {
		return controller.createTSS();
	}

	@Override
	public String getHelpMessage() {
		return HELP_MESSAGE;
	}

	@Override
	public void addConfigurationPropertyListener(ChangeListener<Boolean> listener) {
		configurationOK.addListener(listener);
	}

	@Override
	public void removeConfigurationPropertyListener(ChangeListener<Boolean> listener) {
		configurationOK.removeListener(listener);
	}

	private void loadFXML() {
		FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FXML_NAME));
		try {
			fXMLLoader.load();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exception {0}", e);
		}
		rootNode = fXMLLoader.getRoot();
		controller = fXMLLoader.getController();
		controller.addConfigurationPropertyListener(this::configOkListener);
	}

	private void configOkListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		LOG.log(Level.FINE, "configOkListener {0} {1} {2}", new Object[] { observable, oldValue, newValue });
        configurationOK.set(newValue);
	}

}
