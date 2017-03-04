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

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CenterPanel extends JPanel {

	private JScrollBar horizontalScroller;
	private JScrollBar verticalScroller;
	private Canvas canvas;
	private JSlider simTimeSlider;

	public CenterPanel(int maxSimTime) {
		instantiateScrollBars();
		instantiateCanvas();
		instantiateTimeSlider(maxSimTime);
		buildCenterPanel();
	}
	
	private void instantiateScrollBars() {
		horizontalScroller = new JScrollBar();
		horizontalScroller.setMaximum(ATCVisFrame.SCROLLBARSIZE);
		horizontalScroller.setOrientation(JScrollBar.HORIZONTAL);
		horizontalScroller.setVisibleAmount(ATCVisFrame.SCROLLBARSIZE);
		horizontalScroller.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent evt) {
				GUIFunctions.adjustHorizontalScrollbar(evt);
			}
		});
		
		verticalScroller = new JScrollBar();
		verticalScroller.setMaximum(ATCVisFrame.SCROLLBARSIZE);
		verticalScroller.setVisibleAmount(ATCVisFrame.SCROLLBARSIZE);
		verticalScroller.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent evt) {
				GUIFunctions.adjustVerticalScrollbar(evt);
			}
		});
	}
	
	private void instantiateCanvas() {
		canvas = new Canvas();
		canvas = new ATCVisCanvas();
	}
	
	private void instantiateTimeSlider(int maxSimTime) {
		int minSimTime = 0;
		simTimeSlider = new JSlider(JSlider.HORIZONTAL, minSimTime, maxSimTime, minSimTime);
		simTimeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				GUIFunctions.updateSimTimeSlider(e);
			}
		});
	}

	private void buildCenterPanel() {
		JPanel hGap = new JPanel();
		hGap.setSize(new Dimension(5, this.getHeight()));
		JPanel vGap = new JPanel();
		vGap.setSize(new Dimension(this.getWidth(), 5));

		JPanel midPanel = new JPanel();
		midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.X_AXIS));
		midPanel.add(verticalScroller, this);
		midPanel.add(hGap, this);
		midPanel.add(canvas, this);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(horizontalScroller, this);
		this.add(vGap, this);
		this.add(midPanel, this);
		this.add(simTimeSlider, this);
	}

	public JScrollBar getHorizontalScrollbar() {
		return horizontalScroller;
	}

	public JScrollBar getVerticalScrollbar() {
		return verticalScroller;
	}

	public Canvas getSimulationCanvas() {
		return canvas;
	}

	public void setCanvasSize(int size) {
		canvas.setMaximumSize(new Dimension(size, size));
		canvas.setMinimumSize(new Dimension(size, size));
		canvas.setPreferredSize(new Dimension(size, size));
	}

	public void setSimTimeSliderValue(int time) {
		simTimeSlider.setValue(time);
	}
}
