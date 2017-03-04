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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.arc.atc.core.DataModel;
import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.viewer.CustomStageContent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * 
 * @author ahamon
 *
 */
public class AirportSequencesViewer implements CustomStageContent {

	private static final Logger LOG = Logger.getGlobal();
	private static final String FXML_NAME = "AirportSequencesViewer.fxml";

	private AirportSequencesViewerController controller;
	private Node rootNode;

	@Override
	public Node getNode() {
		if (rootNode == null) {
			loadFXML();
		}
		return rootNode;
	}

	@Override
	public String getTitle() {
		if (controller == null) {
			loadFXML();
		}
		return controller.getAirport() != null ? controller.getAirport().getName() : "No Airport";
	}

	@Override
	public void setSize(double newWidth, double newHeight) {
		if (controller == null) {
			loadFXML();
		}
		controller.setSize(newWidth, newHeight);
	}

	public void setAirport(Airport airport) {
		if (controller == null) {
			loadFXML();
		}
		controller.setAirport(airport);
	}

	public void setDataModel(DataModel dataModel) {
		controller.setDataModel(dataModel);
	}

	public void updateTime(int simTime) {
		controller.updateTime(simTime);
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
	}

}
