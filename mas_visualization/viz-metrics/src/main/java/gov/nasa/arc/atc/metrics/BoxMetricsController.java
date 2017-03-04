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

package gov.nasa.arc.atc.metrics;

import java.io.IOException;
import org.controlsfx.control.PopOver;

import gov.nasa.arc.atc.charts.BoxInfo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
/**
 * Display simple metrics [box] and configure Argmax Pop Up
 * @author Kelsey
 *
 */

public class BoxMetricsController {
	private PopOver pop;	
	private ArgmaxInfo argmaxMap = new ArgmaxInfo();
	private String categoryName;
    @FXML
    private TitledPane pane;
    @FXML
    private Label average;
    @FXML
    private Label max;
    @FXML
    private Label total;
    @FXML
    private Button viewSimulationButton;

	/**--------------------------------*/
    public BoxMetricsController(){
    	pop = new PopOver();
    	Platform.runLater( () -> addInteractivity() );
    }

    @FXML
    void handleViewSimAction(ActionEvent event) {
    	if(argmaxMap == null){
    		System.out.println("ERROR - argmaxMap needs to load argmax simulation values");
    	}else{
        	String time =  argmaxMap.getTime();
        	if(argmaxMap.getTime() != null){
        		throw new UnsupportedOperationException("TODO");
            	//DisplayViewConfigurations.openSimulation( Integer.parseInt( time), argmaxMap);
        	}
    	}
    }
    
    @FXML
    void handleArgmaxClose(MouseEvent event) {
    	pop.hide();
    }

    @FXML
    void handleArgmaxPopUp(MouseEvent event) {
    	pop.show(viewSimulationButton);
    }

    public void setInfo(BoxInfo box){
    	this.categoryName = box.getName();
    	pane.setText(categoryName); // titled pane
    	total.setText(box.getTotalString());
    	average.setText(box.getAverageString());
    	max.setText(box.getMaxString());
        argmaxMap = box.getArgmax();
    }

    private void addInteractivity(){
		FXMLLoader fxml = new FXMLLoader(getClass().getResource(MetricsUtil.ARGMAX_POPUP));
    	try{
    		fxml.load();
    	}catch(IOException e){
    		System.out.println("issue with addInteractivity in SimpleMetricsController :"+e);
    	}
		Parent node = fxml.getRoot();
		ArgmaxController popUpController = fxml.getController();
		popUpController.setArgmaxInfo(this.categoryName, argmaxMap );
		pop.setContentNode(node);
    }
	
}
