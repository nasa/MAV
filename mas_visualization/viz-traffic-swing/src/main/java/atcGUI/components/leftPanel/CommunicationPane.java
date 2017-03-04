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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import atcGUI.components.ATCVisFrame;

public class CommunicationPane extends JPanel {

	private JTextPane communicationOutput;

	public CommunicationPane() {
		instantiateDimensions();
		instantiateTextArea();
		instantiateStyle();
	}
	
	private void instantiateDimensions() {
		this.setMaximumSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 350));
		this.setMinimumSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 70));
	}
	
	private void instantiateTextArea() {
		communicationOutput = new JTextPane();
		communicationOutput.setAlignmentX(CENTER_ALIGNMENT);
		communicationOutput.setAlignmentY(CENTER_ALIGNMENT);
		communicationOutput.setEditable(false);
		((DefaultCaret) communicationOutput.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		communicationOutput.setOpaque(true);
		communicationOutput.setBackground(Color.white);
		this.add(communicationOutput);
	}
	
	private void instantiateStyle() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Communication Window"));
		this.setBackground(Color.lightGray);
	}
	
	public void clearCommunicationOutput() {
		JTextPane temp = new JTextPane();
		temp.setAlignmentX(CENTER_ALIGNMENT);
		temp.setAlignmentY(CENTER_ALIGNMENT);
		temp.setEditable(false);
		((DefaultCaret) temp.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		temp.setOpaque(true);
		temp.setBackground(Color.white);
		communicationOutput.setDocument(temp.getDocument());
	}
	
	public void updateCommunicationOutput(String comms, Color color) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
		StyledDocument doc = communicationOutput.getStyledDocument();
		try {
			String[] output = comms.split("\n");
			int length = communicationOutput.getDocument().getLength();
			doc.insertString(length, output[0] + "\n", aset);

			length = communicationOutput.getDocument().getLength();
			aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.black);
			doc.insertString(length, output[1] + "\n\n", aset);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void setPreferredCommPaneSize(Dimension dimension) {
		this.setPreferredSize(dimension);
	}
}
