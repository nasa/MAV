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

package atcGUI.components.rightPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import atcGUI.components.ATCVisFrame;

public class RightPanel extends JPanel {
	
	private DeparturePane departurePanel;
	private SeparationViolatorPane violatorPanel;
	private ErrorPane errorPanel;
	
	public RightPanel() {
		departurePanel = new DeparturePane();
		violatorPanel = new SeparationViolatorPane();
		errorPanel = new ErrorPane();
		buildRightPanel();
	}
	
	private void buildRightPanel() {
		int height = departurePanel.getHeight() + violatorPanel.getHeight() + errorPanel.getHeight();
		int width = ATCVisFrame.SIDEGUIWIDTH;
		this.setPreferredSize(new Dimension(width, height));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(departurePanel, this);
		this.add(violatorPanel, this);
		this.add(errorPanel, this);
		this.add(Box.createRigidArea(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 300)));
	}
	
	public void clearDepartureQueue() {
		departurePanel.clearDepartureQueue();
	}
	
	public void updateDepartureQueue(String departures) {
		departurePanel.updateDepartureQueue(departures);
	}
	
	public void clearViolatorPane() {
		violatorPanel.clearViolatorOutput();
	}
	
	public void updateViolatorPane(String violators) {
		violatorPanel.updateSeparationViolation(violators);
	}
	
	public void clearErrorOutput() {
		errorPanel.clearErrorOutput();
	}
	
	public void updateErrorOutput(String errors) {
		errorPanel.updateErrorOutput(errors);
	}
}
