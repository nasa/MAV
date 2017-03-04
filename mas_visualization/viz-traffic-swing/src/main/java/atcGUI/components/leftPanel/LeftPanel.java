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

package atcGUI.components.leftPanel;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import atcGUI.components.ATCVisFrame;

public class LeftPanel extends JPanel {

	private ArrivalPane arrivalPanel;
	private CommunicationPane communicationPanel;
	private ControlsPane controlsPanel;
	
	public LeftPanel(String imageDirPath) {
		arrivalPanel = new ArrivalPane();
		communicationPanel = new CommunicationPane();
		controlsPanel = new ControlsPane(imageDirPath);
		setPreferredPaneSizes();
		buildLeftPanel();
	}
	
	private void setPreferredPaneSizes() {
		int screenHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();

		if (screenHeight <= 768) {
			// arrivalPanel value + communicationPanel value <= 543
			arrivalPanel.setPreferredCanvasSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 473));
			communicationPanel.setPreferredCommPaneSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 70));
		} else if (screenHeight <= 900) {
			// arrivalPanel value + communicationPanel value <= 675
			arrivalPanel.setPreferredCanvasSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 525));
			communicationPanel.setPreferredCommPaneSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 150));
		} else if (screenHeight <= 1080) { 
			// arrivalPanel value + communicationPanel value <= 855
			arrivalPanel.setPreferredCanvasSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 600));
			communicationPanel.setPreferredCommPaneSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 255));
		}else {
			arrivalPanel.setPreferredCanvasSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 600));
			communicationPanel.setPreferredCommPaneSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 300));
		}
	}
	
	private void buildLeftPanel() {
		this.setLayout(new BorderLayout());
		this.add(arrivalPanel, BorderLayout.NORTH);
		this.add(communicationPanel,BorderLayout.CENTER);
		this.add(controlsPanel, BorderLayout.SOUTH);
	}
	
	public Canvas getArrivalCanvas() {
		return arrivalPanel.getArrivalCanvas();
	}
	
	public void clearCommOutput() {
		communicationPanel.clearCommunicationOutput();
	}
	
	public void updateCommOutput(String comms, Color color) {
		communicationPanel.updateCommunicationOutput(comms, color);
	}
	
	public void updateSimTimeView(String time) {
		controlsPanel.updateSimTimePanel(time);
	}
	
	public void updateExecuteText(String action) {
		controlsPanel.updateExecuteText(action);
	}

	public void enableExecuteButton(boolean enabled) {
		controlsPanel.enableExecute(enabled);
	}
}
