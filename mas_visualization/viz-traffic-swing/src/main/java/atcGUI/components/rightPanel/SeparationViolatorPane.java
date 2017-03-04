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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import atcGUI.components.ATCVisFrame;

public class SeparationViolatorPane extends JPanel {
	
	private JTextArea violatorOutput;

	public SeparationViolatorPane() {
		instantiateDimensions();
		instantiateTextArea();
		instantiateStyle();
	}
	
	private void instantiateDimensions() {
		this.setMaximumSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 250));
		this.setMinimumSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 300));
	}
	
	private void instantiateTextArea() {
		violatorOutput = new JTextArea();
		violatorOutput.setAlignmentX(CENTER_ALIGNMENT);
		violatorOutput.setAlignmentY(CENTER_ALIGNMENT);
		violatorOutput.setEditable(false);
		violatorOutput.setOpaque(true);
		violatorOutput.setBackground(Color.white);
		violatorOutput.setLineWrap(true);
		this.add(violatorOutput);
	}
	
	private void instantiateStyle() {
		this.add(new JScrollPane(violatorOutput));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Separation Violators"));
		this.setBackground(Color.lightGray);
	}
	
	public void clearViolatorOutput() {
		if (violatorOutput != null)
			violatorOutput.setText("");
	}
	
	public void updateSeparationViolation(String violators) {
		violatorOutput.setText(violators);
	}
}
