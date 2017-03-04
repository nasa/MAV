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

//import gov.nasa.arc.atc.SimulationEngine;
import gov.nasa.arc.atc.SimulationManager;
//import gov.nasa.arc.atc.atc2dviz.HighlightManager;
//import gov.nasa.arc.atc.atc2dviz.Waypoint2D;
import gov.nasa.arc.atc.geography.Sector;
import gov.nasa.arc.atc.utils.MapDrawingUtils;
import gov.nasa.arc.atc.utils.SectorUtils;
import gov.nasa.arc.atc.utils.SectorUtilsTEMP;
//import gov.nasa.arc.atc.viewer.ViewerComponents;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
/**
 * 
 * @author Kelsey
 *
 */
public class SimulationLinkController {
	public static final String AIRPLANE = "Airplane";
	public static final String TIME = "Time";
	public static final String CONTROLLER = "Controller";
	public static final String SECTOR = "Sector";
	public static final String WAYPOINT = "Waypoint";
    @FXML
    private Label simulationTime;
    @FXML
    private Button cancelButton;
    @FXML
    private Button viewButton;
    private int jumpTime;
    //argmax values
    private String jumpToSector;
    private String jumpToAircraft;
    private String jumpToSlot;
    private String jumpToWaypoint;

    @FXML
    void handleCancel(ActionEvent event) {
//    	DisplayViewConfigurations.getSimulationLinkApp().closeApp();
		throw new UnsupportedOperationException("TODO");
    }

    @FXML
    void handleView(ActionEvent event) {
//    	DisplayViewConfigurations.getSimulationLinkApp().closeApp();
//    	DisplayViewConfigurations.getSM().closeApp();
		throw new UnsupportedOperationException("TODO");
//		SimulationEngine.jumpToTime(this.jumpTime);
//
//		if(jumpToSector != null){
//			zoomToSector();
//		}
//
//		// register waypoints
//		// move to place where aircrafts and slots are registered?
//		for(Waypoint2D waypoint : ViewerComponents.getWorld2D().getWaypoint2Ds() ){
//			HighlightManager.registerWaypoint2D(waypoint);
//		}
//		HighlightManager.highlightAircraft(jumpToAircraft, true);
//		HighlightManager.highlightSlot(jumpToSlot, true);
//		HighlightManager.highlightWaypoint(jumpToWaypoint, true);

    }
    
    private void zoomToSector(){
    	for(Sector sector : SimulationManager.getATCGeography().getSectors()){
    		if(sector.getName().equals(jumpToSector)){
    			MapDrawingUtils.updateMapsViewPoint(SectorUtils.getSectorsBounds(sector));
    		}
    	}
    }
    
    public void setSimulationTime(int t){
    	this.jumpTime = t;
    	simulationTime.setText(Integer.toString(t));
    }

	public void setOtherSimulationInfo(ArgmaxInfo argmax) {
	    try{
	    	this.jumpToSector = argmax.getSector();
	    }catch(Exception e){
	    	// dont zoom to sector
	    }
	    this.jumpToAircraft = argmax.getAirplane();
		this.jumpToSlot = jumpToAircraft.replace("plane_","slot_");
	    this.jumpToWaypoint = argmax.getWaypoint();
	}

}
