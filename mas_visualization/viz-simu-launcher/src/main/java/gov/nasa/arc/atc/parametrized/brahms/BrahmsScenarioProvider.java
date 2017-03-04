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

package gov.nasa.arc.atc.parametrized.brahms;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.simulation.SimulationContext;
import org.openide.util.lookup.ServiceProvider;

import gov.nasa.arc.atc.algos.ScenarioProvider;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * @author ahamon
 */
@ServiceProvider(service = ScenarioProvider.class)
public class BrahmsScenarioProvider implements ScenarioProvider {

    private static final String DISPLAY_NAME = "Brahms scenario";
    private static final String FXML_NAME = "BrahmsInputConfigurator.fxml";
    private static final Logger LOG = Logger.getGlobal();

    private Node rootNode;
    private BrahmsInputConfiguratorFXController controller;

    public BrahmsScenarioProvider() {
        // fxml is only parsed when getNode is invoked
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public Node getNode() {
        if (rootNode == null) {
            loadFXML();
        }
        return rootNode;
    }

    @Override
    public SimulationContext createScenario() {
        return controller.getScenario();
    }

    @Override
    public boolean isConfigurationOK() {
        return controller != null && controller.checkConfiguration();
    }

    private void loadFXML() {
        FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FXML_NAME));
        try {
            fXMLLoader.load();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Exception {0}", e);
            e.printStackTrace();
        }
        rootNode = fXMLLoader.getRoot();
        controller = fXMLLoader.getController();
    }

}
