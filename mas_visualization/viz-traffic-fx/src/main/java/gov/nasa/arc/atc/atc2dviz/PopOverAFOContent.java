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

import gov.nasa.arc.atc.core.NewPlane;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import org.controlsfx.control.PopOver;

/**
 *
 * @author ahamon
 *
 */
public class PopOverAFOContent {

	private static final Logger LOG = Logger.getGlobal();

    private final NewPlane afo;
    private final Parent root;
    private final InfoPopUpController popUpController;

	/**
	 *
	 * @param popOver the pop up to add the content to
	 * @param theAFO the aircraft to represent
	 */
	public PopOverAFOContent(PopOver popOver, NewPlane theAFO) {
		afo = theAFO;
		FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("InfoPopUpContent.fxml"));
		try {
			fXMLLoader.load();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Exception while loading fxml pop up content :: {0}", e);
		}
		root = fXMLLoader.getRoot();
		popUpController = fXMLLoader.getController();
		popUpController.setAFO(afo);
		popUpController.setPopUpOver(popOver);
		popUpController.updateInfos();
	}

	public Node getNode() {
		return root;
	}

	void setFillColor(Color color) {
		popUpController.setFillColor(color);
	}

	/**
	 * update the pop up information
	 */
	void updateInfos() {
		popUpController.updateInfos();
	}

}
