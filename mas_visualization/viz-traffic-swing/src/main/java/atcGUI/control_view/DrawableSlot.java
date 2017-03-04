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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import atcGUI.components.ATCVisFrame;
import gov.nasa.arc.brahms.visualization.elements.Slot;

public class DrawableSlot {

	public static void drawSlot(Graphics2D g2d, Slot slot, double scale, Point2D.Double wUpLeft, String displayMode) {
		AffineTransform worldToViewTrans = new AffineTransform(1, 0, 0, 1, -wUpLeft.getX(), -wUpLeft.getY()); 
		AffineTransform worldToViewScale = new AffineTransform(scale, 0, 0, scale, 0, 0);
		worldToViewScale.concatenate(worldToViewTrans);
		g2d.setTransform(worldToViewScale);

		long radius = slot.getRadius();
		int diameter = (int) (radius * 2);
		double slotX = slot.getScreenCoord().x;
		double slotY = slot.getScreenCoord().y;
		double cornerX = slotX - radius;
	    double cornerY = slotY - radius;
		double nameX = slotX + radius - scale;
		double nameY = slotY + radius - scale;
		g2d.drawOval((int)cornerX, (int)cornerY, diameter, diameter);

		switch (displayMode) {
			case ATCVisFrame.NAMEONLY:
				g2d.drawString(slot.getAgent().getName().substring(5), (int)nameX, (int)nameY);
				break;
			case ATCVisFrame.CONTROLLER:
				g2d.drawString(slot.getSpeed(), (int)nameX, (int)nameY);
				break;
			case ATCVisFrame.SUPERVISOR:	// slots aren't shown on supervisor's screen
				break;
		}
	}
}
