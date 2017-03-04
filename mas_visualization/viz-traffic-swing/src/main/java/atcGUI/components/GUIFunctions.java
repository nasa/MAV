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

package atcGUI.components;

import java.awt.Color;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.event.ChangeEvent;

import atcGUI.control_view.ATCVisController;
import atcGUI.control_view.ATCVisViewer;
import atcGUI.control_view.ArrivalRedrawRoutine;
import atcGUI.control_view.ArrivalVisViewer;
import atcGUI.control_view.RedrawRoutine;
import atcGUI.model.SimulationDataModel;

public class GUIFunctions {
    /**
     * Refreshes the view by repainting the canvas. Call this any time the picture in the
     * main view should change.
     */
    public static void refresh() {
        RedrawRoutine.inst().refreshView();
    }
    
    /**
     * Refreshes the view by repainting the canvas. Call this any time the picture in the
     * etaTimeline window view should change.
     */
    public static void refreshArrivalWindow() {
        ArrivalRedrawRoutine.inst().refreshView();
    }
    
    /**
     * Changes the amount the vertical scroll bar returns when it is all the way at the top.
     * @param newMin the new value to return when the vertical scrollbar is all the way at the top.
     */
    public static void setVScrollBarMin(int newMin) {
        ATCVisFrame.inst().setScrollAttribute(ATCVisScrollbarAttrConsts.V_SCROLL_BAR, ATCVisScrollbarAttrConsts.MIN, newMin);
    }
    
    /**
     * Changes the amount the horizontal scroll bar returns when it is all the way at the right.
     * @param newMin the new value to return when the horizontal scrollbar is all the way at the right.
     */
    public static void setHScrollBarMin(int newMin) {
        ATCVisFrame.inst().setScrollAttribute(ATCVisScrollbarAttrConsts.H_SCROLL_BAR, ATCVisScrollbarAttrConsts.MIN, newMin);
    }
    
    /**
     * Changes the amount the vertical scroll bar returns when it is all the way at the bottom.
     * Important: See setVScrollBarKnob for further clarification!
     * @param newMax the new value to return when the horizontal scrollbar is all the way at the right.
     */
    public static void setVScrollBarMax(int newMax) {
        ATCVisFrame.inst().setScrollAttribute(ATCVisScrollbarAttrConsts.V_SCROLL_BAR, ATCVisScrollbarAttrConsts.MAX, newMax);
    }
    
    /**
     * Changes the amount the horizontal scroll bar returns when it is all the way at the left.
     * Important: See setHScrollBarKnob for further clarification!
     * @param newMax the new value to return when the horizontal scrollbar is all the way at the left.
     */
    public static void setHScrollBarMax(int newMax) {
        ATCVisFrame.inst().setScrollAttribute(ATCVisScrollbarAttrConsts.H_SCROLL_BAR, ATCVisScrollbarAttrConsts.MAX, newMax);
    }
    
    /**
     * Sets the width of the scroll bar's knob. This is not just purely visual! If you have, for example, a knob
     * width of 2 and a maximum value of 4, then the knob will "fill" the last half of the scroll bar and return
     * a value of 2 if pushed all the way to the left!
     * @param newKnob the new width of the knob.
     */
    public static void setVScrollBarKnob(int newKnob) {
        ATCVisFrame.inst().setScrollAttribute(ATCVisScrollbarAttrConsts.V_SCROLL_BAR, ATCVisScrollbarAttrConsts.KNOB, newKnob);
    }
    
    /**
     * Sets the width of the scroll bar's knob. This is not just purely visual! If you have, for example, a knob
     * width of 2 and a maximum value of 4, then the knob will "fill" the last half of the scroll bar and return
     * a value of 2 if pushed all the way to the left!
     * @param newKnob the new width of the knob.
     */
    public static void setHScrollBarKnob(int newKnob) {
        ATCVisFrame.inst().setScrollAttribute(ATCVisScrollbarAttrConsts.H_SCROLL_BAR, ATCVisScrollbarAttrConsts.KNOB, newKnob);
    }
    
    /**
     * Sets the position of the scroll bar's knob. I do not believe this will trigger a 
     * scrollbar changed event in your controller, but I could be wrong.
     * @param newPosit the new position of the knob.
     */
    public static void setHScrollBarPosit(int newPosit) {
        ATCVisFrame.inst().setScrollAttribute(ATCVisScrollbarAttrConsts.H_SCROLL_BAR, ATCVisScrollbarAttrConsts.POSIT, newPosit);
    }
    
    /**
     * Sets the position of the scroll bar's knob. I do not believe this will trigger a 
     * scrollbar changed event in your controller, but I could be wrong.
     * @param newPosit the new position of the knob.
     */
    public static void setVScrollBarPosit(int newPosit) {
        ATCVisFrame.inst().setScrollAttribute(ATCVisScrollbarAttrConsts.V_SCROLL_BAR, ATCVisScrollbarAttrConsts.POSIT, newPosit);
    }

    /**
     * Creates a new ATCVisFrame. This is probably about the first thing you should call.
     * @param inst the ATCVisController you wish to receive button events.
     * @param viewRefresher The view refresher used to draw the canvas.
     * @param mouseListener The mouseListener that will be sent mouse events from the canvas.
     * @param mouseMotionListener The Mouse Motion Listener that will be sent events from the canvas.
     */
    public static void createATCVisFrame(ATCVisController inst, ATCVisViewer viewRefresher, ArrivalVisViewer arriveRefresher, MouseListener mouseListener, MouseMotionListener mouseMotionListener, SimulationDataModel data, String[] args) {
        ATCVisFrame.createATCVisFrame(inst, viewRefresher, arriveRefresher, mouseListener, mouseMotionListener, data, args);
    }
    
    public static void adjustHorizontalScrollbar(AdjustmentEvent evt) {
    	ATCVisFrame.inst().jScrollBarHorizontalAdjustmentValueChanged(evt);
    }
    
    public static void adjustVerticalScrollbar(AdjustmentEvent evt) {
    	ATCVisFrame.inst().jScrollBarVerticalAdjustmentValueChanged(evt);
    }

    /**
     * This clears any text in the communication window; it is called after the simulation has finished.
     */
    public static void clearCommWindowOutput() {
    	ATCVisFrame.inst().clearCommOutput();
    }
    
    /**
     * This updates the departure queue by erasing everything from it. Called when simulation is over.
     */
    public static void clearDepartureQueue() {
    	ATCVisFrame.inst().clearDepartureQueue();
    }
    
    /**
     * This erases any errors from error window; it is called after the simulation has finished.
     */
    public static void clearErrorList() {
    	ATCVisFrame.inst().clearErrorList();
    }
    
    /**
     * This erases the separation violator list; it is called after the simulation has finished.
     */
    public static void clearSepViolatorList() {
    	ATCVisFrame.inst().clearViolatorList();
    }
    
    public static void execute() {
    	ATCVisFrame.inst().executeActionPerformed();
    }
    
    /**
     * This returns the size of the canvas on which the planes and waypoints get drawn.
     * @return the length of the sides of the square canvas
     */
    public static int getCanvasSize() {
    	return ATCVisFrame.inst().getCANVASSIZE();
    }
    
    /**
     * This allows the view to know what option is selected by user.
     * Possible options: supervisor, controller, name only
     * @return the currently selected value 
     */
    public static String getDisplayMode() {
    	return ATCVisFrame.inst().getDisplayMode();
    }
    
    /**
     * This gets the pause time for drawing the simulator
     * min time: 0 milliseconds, max time: 100 milliseconds
     * @return time in milliseconds to pause between each update to the GUI
     */
    public static int getExecutionSpeed() {
    	return ATCVisFrame.inst().getSleepTime();
    }
    
    public static int getMaxSimulationTime() {
    	return ATCVisFrame.inst().getMaxSimTime();
    }
    
    /**
     * This returns whether or not to sector boundary lines on the screen.
     * @return true if you should draw sector boundary lines on screen
     */
    public static boolean isSectorBoundariesVisible() {
    	return ATCVisFrame.inst().sectorBoundariesVisible();
    }
    
    /**
     * This checks to see if the simulation is currently running in GUI
     * @return true if the simulation is running
     */
    public static boolean isSimRunning() {
    	return ATCVisFrame.inst().isSimRunning();
    }
    
    /**
     * This returns whether or not to draw the slot markers to the screen.
     * @return true if you should draw slot markers on screen
     */
    public static boolean isSlotsVisible() {
    	return ATCVisFrame.inst().isSlotsVisible();
    }
    
    /**
     * This checks to see if the simulation should be paused or not.
     * @return true if the simulation is paused
     */
    public static boolean isStepEnabled() {
    	return ATCVisFrame.inst().isStepEnabled();
    }
        
    /**
     * This checks to see if the simulation is paused or running.
     * @return true if the simulation is running
     */
    public static boolean runNextStep() {
    	return ATCVisFrame.inst().runNextStep();
    }
    
    /**
     * This allows you to reset the sim time in the GUI.
     */
    public static void resetSimTime() {
    	ATCVisFrame.inst().resetSimTime();
    }
    
    /**
     * This performs final preparation and then makes GUI visible
     */
    public static void showGUI() {
    	ATCVisFrame.inst().showGUI();
    }
        
    /**
     * This sends text to the communication window to update it with.
     * @param text the new text to add to the communication window
     * @param color the color to make the name of the controller
     */
    public static void updateCommWindowOutput(String text, Color color) {
    	ATCVisFrame.inst().addCommOutput(text, color);
    }
    
    /**
     * This updates the departure queue in the GUI.
     * @param depart the string of text to set the departure queue with
     */
    public static void updateDepartureQueue(String depart) {
    	ATCVisFrame.inst().updateDepartureQueue(depart);
    }
    
    /**
     * This allows you to print errors to the error window
     * @param errors All the errors to be printed to the window
     */
    public static void updateErrorList(String errors) {
    	ATCVisFrame.inst().updateErrorList(errors);
    }
    
    /**
     * Alerts the ETA time line on the GUI to update.
     */
    public static void updateEtaArrivalWindowOutput() {
    	ATCVisFrame.inst().updateEtaArrivalOutput();
    }
    
    /**
     * Updates the list of planes in the separation violator window
     * @param violators the list of all violating planes
     */
    public static void updateSeparationViolation(String violators) {
    	ATCVisFrame.inst().updateSeparationViolation(violators);
    }
    
    /**
     * This alerts the GUI to update the sim time.
     * @return the new time of the simulation
     */
    public static int updateSimTime() {
    	return ATCVisFrame.inst().updateSimTime();
    }
    
    public static void updateSimTimeSlider(ChangeEvent e) {
    	ATCVisFrame.inst().updateSimTimeSlider(e);
    }
    
    public static void updateSleepTime(int time) {
    	ATCVisFrame.inst().updateSleepTime(time);
    }
    
    /**
     * This checks whether or not to print the names of waypoints.
     * @return true if you should print the names
     */
    public static boolean waypointNamesVisible() {
    	return ATCVisFrame.inst().waypointNamesVisible();
    }
    
    /**
     * This causes canvas to be zoomed in
     */
    public static void zoomIn() {
    	ATCVisFrame.inst().zoomInActionPerformed();
    }
    
    /**
     * This causes canvas to be zoomed out
     */
    public static void zoomOut() {
    	ATCVisFrame.inst().zoomOutActionPerformed();
    }
}
