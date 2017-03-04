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

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
/**
 * Controller for ArgmaxPopUp in Simulation Metrics
 * @author Kelsey
 *
 */
public class ArgmaxController {

    @FXML
    private Label sectorValue;

    @FXML
    private Label controllerValue;

    @FXML
    private Label airplaneValue;

    @FXML
    private Label timeValue;

    @FXML
    private Label waypointValue;
    
    @FXML
    private Label categoryLabel;
    
	// --------------------------------------------------------------------
    public ArgmaxController(){
    }

    public void setArgmaxInfo(String boxName, ArgmaxInfo argmax){
    	String[] label = boxName.split(" ");
    	categoryLabel.setText(label[0]);
    	sectorValue.setText( formatTitle(argmax.getSector()) );
    	controllerValue.setText( formatTitle(argmax.getController() ) );
    	airplaneValue.setText( formatTitle( argmax.getAirplane() ) );
    	timeValue.setText( formatTitle(argmax.getTime() ) );
    	waypointValue.setText( formatTitle(argmax.getWaypoint() ) );
    }
    
    public String formatTitle(List<String> list){
    	if(list == null){
    		return "";
    	}else{
    		return list.toString();
    	}
    }
    public String formatTitle(Object value){
    	if(value == null){
    		return "";
    	}else{
        	return value.toString();
    	}
    }

}
