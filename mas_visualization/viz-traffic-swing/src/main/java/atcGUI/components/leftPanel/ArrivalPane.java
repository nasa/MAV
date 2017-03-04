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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import atcGUI.components.ATCVisFrame;

public class ArrivalPane extends JPanel {
	
	private Canvas arrivalCanvas;
	
	public ArrivalPane() {
		instantiateCanvas();
		instantiateDimensions();
		this.add(arrivalCanvas);
		instantiateStyle();
	}
	
	private void instantiateCanvas() {
		arrivalCanvas = new Canvas();
		arrivalCanvas = new ArrivalCanvas();
		arrivalCanvas.setMaximumSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 600));
		arrivalCanvas.setMinimumSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 473));
	}
	
	private void instantiateDimensions() {
		this.setMaximumSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 600));
		this.setMinimumSize(new Dimension(ATCVisFrame.SIDEGUIWIDTH, 473));
	}
	
	private void instantiateStyle() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Arrival Sequence"));
		this.setBackground(Color.lightGray);
	}
	
	public void setPreferredCanvasSize(Dimension dimension) {
		arrivalCanvas.setPreferredSize(dimension);
	}
	
	public Canvas getArrivalCanvas() {
		return arrivalCanvas;
	}
}
