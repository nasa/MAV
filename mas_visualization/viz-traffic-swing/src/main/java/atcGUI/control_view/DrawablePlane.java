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
import gov.nasa.arc.brahms.visualization.elements.Plane;

public class DrawablePlane {
	
	public static void drawPlane(Graphics2D g2d, Plane plane, double scale, Point2D.Double wUpLeft, String displayMode) {
		AffineTransform worldToViewTrans = new AffineTransform(1, 0, 0, 1, -wUpLeft.getX(), -wUpLeft.getY()); 
		AffineTransform worldToViewScale = new AffineTransform(scale, 0, 0, scale, 0, 0);
		worldToViewScale.concatenate(worldToViewTrans);
		g2d.setTransform(worldToViewScale);
		
		int diff = (int)(scale);
		String planeName = plane.getName();
		if (diff > 4)
			diff = 4;
		int diameter = 6 - diff;
		double planeX = plane.getScreenCoord().x;
		double planeY = plane.getScreenCoord().y;
		double newX = planeX - diameter / 2.0;
	    double newY = planeY - diameter / 2.0;
	    double nameX = planeX + 6.0 - diff;
	    double nameY = planeY - 6.0 + diff;
		g2d.fillOval((int)newX, (int)newY, diameter, diameter);
				
		switch (displayMode) {
			case ATCVisFrame.NAMEONLY:
				g2d.drawString(planeName, (int)nameX, (int)nameY);
				break;
			case ATCVisFrame.CONTROLLER:
				handleControllerDisplay(g2d, planeX, planeY, diff, scale, plane, planeName);
				break;
			case ATCVisFrame.SUPERVISOR:
				g2d.drawString(plane.getAltitude(), (int)nameX, (int)nameY);
				break;
			default:
				g2d.drawString(planeName, (int)planeX+6, (int)planeY+5);
		}
	}	

	private static void handleControllerDisplay(Graphics2D g2d, double planeX, double planeY, int diff, double scale, Plane plane, String planeName) {	
		g2d.drawLine((int)planeX+3-diff, (int)planeY-11+(diff*2), (int)planeX+3-diff, (int)planeY-42+(diff*5));				// draws vertical line above plane dot
		g2d.drawString(plane.getSpeed(), (int)planeX+8-diff, (int)planeY-12+(diff*2));										// draws speed of plane
		g2d.drawString(plane.getAltitude() + "	" + plane.getDestination(), (int)planeX+8-diff, (int)planeY-23+(diff*3));	// draws altitude of plane
		g2d.drawString(planeName, (int)planeX+8-diff, (int)planeY-34+(diff*4));												// draws name of plane
	}
	
}
