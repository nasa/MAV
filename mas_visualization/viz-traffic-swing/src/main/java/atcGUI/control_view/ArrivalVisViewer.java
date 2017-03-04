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

package atcGUI.control_view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import atcGUI.components.GUIFunctions;
import gov.nasa.arc.atc.utils.Constants;
import gov.nasa.arc.brahms.visualization.elements.Plane;
import atcGUI.model.SimulationDataModel;
import gov.nasa.arc.brahms.visualization.elements.Slot;

public class ArrivalVisViewer implements Observer {
	
	private static final double SECONDS_PER_MINUTE = 60.0;
	private SimulationDataModel data;
	
	private Color[] agentColors = {new Color(255, 138, 128), new Color(68, 138, 255), new Color(224, 64, 251), new Color(0, 191, 165), new Color(255, 64, 129), new Color(255, 171, 64), new Color(24, 255, 255), new Color(215, 204, 200)};
	
	public ArrivalVisViewer() {
		data = null;
	}
	
	/** 
	 * @param g2d the canvas that the time line will be drawn on
	 */
	public void refreshView(Graphics2D g2d, Canvas c) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setPaint(Color.black);
        g2d.setColor(Color.white);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		
		if (GUIFunctions.isSimRunning()) {
			List<String> controllers = data.getControllerNames();
			List<Plane> planes = data.getArrivingPlanes();
			if (!planes.isEmpty()) {
//				for (Plane p : planes) {
				for (int i = 0; i < planes.size(); i++) {
					Plane p = planes.get(i);
					// draw plane if it is flying to LGA and within ArrivalRedrawRoutine.TIMELINE_LENGTH of landing
					if (!p.isLanded() && p.isFlying() && !p.isDeparture() && (p.getEta() / SECONDS_PER_MINUTE) <= ArrivalRedrawRoutine.TIMELINE_LENGTH) {
						String cont = p.getController();
						int j;
						for (j = 0; j < controllers.size(); j++) {
							if (cont.equals(controllers.get(j)))
								break;
						}
						String name = "slot_" + p.getName();
						
						String gapSize = "";
						if (i < planes.size() - 1) {
							Plane nextPlane = planes.get(i + 1);
							double gap = nextPlane.getEta() - p.getEta();
							if (gap >= Constants.TRIPLE)
								gapSize = "tr";
							else if (gap >= Constants.DOUBLE)
								gapSize = "db";
						}
						
						drawPlane(g2d, p, c, agentColors[j]);
						drawSlot(g2d, data.getSlot(name), c, p.isMetering(), gapSize);
					}
				}
			} else {
				for (Slot s : data.getSlots()) {
					if (s.getAppearTime() <= data.getSimTime() && s.getETA() / SECONDS_PER_MINUTE <= ArrivalRedrawRoutine.TIMELINE_LENGTH)
						drawSlot(g2d, s, c, "0", "");	
				}
			}
		} 
	}
	
	public void updateTimeline() {
		GUIFunctions.refreshArrivalWindow();
	}
	
	public void drawPlane(Graphics2D g2d, Plane p, Canvas c, Color color) {
    	double yPercentage = 1.0 - ((p.getEta() / SECONDS_PER_MINUTE) / ArrivalRedrawRoutine.TIMELINE_LENGTH);
    	int height = c.getHeight() - 41;
    	int yLoc = (int) (height * yPercentage) + 10;
    	int centerX = c.getWidth() / 2;

    	g2d.drawLine(centerX - 30, yLoc, centerX - 11, yLoc);
    	g2d.setColor(color);

    	if (p.getMeterCount() > 0) {
    		String name = p.getName() + " (" + p.getMeterCount() + ")";
    		g2d.drawString(name, centerX - 93, yLoc);
    	} else
    		g2d.drawString(p.getName(), centerX - 85, yLoc);

    	g2d.setColor(Color.white);
	}
	
	public void drawSlot(Graphics2D g2d, Slot slot, Canvas c, String metering, String gapSize) {
    	if (slot != null && slot.getETA() != 0.0) {
    		int height = c.getHeight() - 41;
    		int centerX = c.getWidth() / 2;
		 	double yPercentage = 1.0 - ((slot.getETA() / SECONDS_PER_MINUTE) / ArrivalRedrawRoutine.TIMELINE_LENGTH);
		 	int yLoc = (int) (height * yPercentage) + 10;
		 	g2d.drawLine(centerX + 11, yLoc, centerX + 30, yLoc);
		 	if (metering.equals("1"))
		 		g2d.setColor(Color.ORANGE);
		 	String name = gapSize + " " + slot.getName().substring(5);
			g2d.drawString(name, centerX + 45, yLoc);
			g2d.setColor(Color.WHITE);
    	}
	}
	
	/** 
	 * @param o the SimulationDataModel object
	 * @param arg the list of shapes stored in the model
	 */
	@Override
	public void update(Observable o, Object arg) {
		data = (SimulationDataModel)o;
		GUIFunctions.refresh();
	}

}
