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

import gov.nasa.arc.atc.core.NewSlot;
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
public class PopOverSlotContent {

    private static final Logger LOG = Logger.getGlobal();

    private final NewSlot slot;
    private final Parent root;
    private final SlotInfoPopUpController popUpController;

    /**
     *
     * @param popOver the pop up to add the content to
     * @param theSlot the slot to represent
     */
    public PopOverSlotContent(PopOver popOver, NewSlot theSlot) {
        slot = theSlot;
        FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("SlotInfoPopUpContent.fxml"));
        try {
            fXMLLoader.load();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Exception while loading fxml pop up content :: {0}", e);
        }
        root = fXMLLoader.getRoot();
        popUpController = fXMLLoader.getController();
        popUpController.setSlot(slot);
        popUpController.setPopUpOver(popOver);
        popUpController.updateInfos();
    }

    public Node getNode() {
        return root;
    }

    public void setFillColor(Color color) {
        popUpController.setFillColor(color);
    }

    /**
     *
     * updates the pop up content
     */
    public void updateInfos() {
        popUpController.updateInfos();
    }

}
