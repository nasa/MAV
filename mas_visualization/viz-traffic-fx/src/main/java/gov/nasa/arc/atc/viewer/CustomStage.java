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

package gov.nasa.arc.atc.viewer;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

/**
 * @author ahamon
 */
public class CustomStage {

    private final Stage stage;
    private final Scene scene;
    private final TabPane root;

    private final List<CustomStageContent> myContent;

    // hum... is there a better way to do it ?
    private static final int BLANK_SPACE = 14 * 2;
    private static final int PADDING = 8;

    public CustomStage() {
        root = new TabPane();
        scene = new Scene(root, 1300, 950);
        stage = new Stage();
        stage.setScene(scene);
        myContent = new LinkedList<>();
        stage.show();
        scene.widthProperty().addListener((obs, old, newValue) -> updateSizes());
        scene.heightProperty().addListener((obs, old, newValue) -> updateSizes());

    }


    public void addContent(CustomStageContent content) {
        myContent.add(content);
        Tab tab = new Tab(content.getTitle(), content.getNode());
        tab.setClosable(false);
        root.getTabs().add(tab);
        stage.show();
    }

    public void clearContent() {
        myContent.clear();
        root.getTabs().clear();
        stage.hide();
    }

    private void updateSizes() {
        double newWidth = (scene.getWidth() - BLANK_SPACE - PADDING * (myContent.size() - 1)) / myContent.size();
        double newHeight = scene.getHeight() - 98; // !!!!!!
        myContent.forEach(content -> content.setSize(newWidth, newHeight));
    }

}
