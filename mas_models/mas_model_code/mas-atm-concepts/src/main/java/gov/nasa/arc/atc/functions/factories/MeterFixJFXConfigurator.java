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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * 
 * @author ahamon
 *
 */
public class MeterFixJFXConfigurator implements FunctionConfigurator {

	private static final String FXML_NAME = "MeterFixFunctionConfigurator.fxml";

	private static final Logger LOG = Logger.getGlobal();

    private final Node rootNode;
    private final MeterFixJFXConfiguratorController controller;

	private SimulationContext context;

	/**
	 * Creates the MeterFixJFXConfigurator using the file named in the FXML_NAME constant
	 */
	public MeterFixJFXConfigurator() {
		FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FXML_NAME));
		try {
			fXMLLoader.load();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exception {0}", e);
		}
		rootNode = fXMLLoader.getRoot();
		controller = fXMLLoader.getController();
	}

	/*
	 * CandidateFunctionConfigurator methods
	 */

	@Override
	public Node getNode() {
		return rootNode;
	}

	@Override
	public boolean isConfigurationOK() {
		return controller != null && controller.getTime() > 0 && context != null ;
	}

	@Override
	public Object[] getParameters() {
		if (context == null) {
			throw new IllegalStateException("Scenario shall not be null when getParameter is invoked");
		}
		return new Object[] { context.getGeography(), controller.getTime() };
	}

	@Override
	public void setScenario(SimulationContext simulationContext) {
		context = simulationContext;
	}

	@Override
	public void addConfigOKListener(ChangeListener<Boolean> listener) {
		controller.addConfigOKListener(listener);
	}

	@Override
	public void removeConfigOKListener(ChangeListener<Boolean> listener) {
		controller.removeConfigOKListener(listener);
	}

}
