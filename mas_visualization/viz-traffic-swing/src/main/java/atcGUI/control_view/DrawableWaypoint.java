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

import gov.nasa.arc.brahms.visualization.elements.Waypoint;

public class DrawableWaypoint {

	public DrawableWaypoint() {
	}
	
	public void drawWaypoint(Graphics2D g2d, Waypoint way, double scale, Point2D.Double wUpLeft, boolean drawName) {
		AffineTransform worldToViewTrans = new AffineTransform(1, 0, 0, 1, -wUpLeft.getX(), -wUpLeft.getY());
		AffineTransform worldToViewScale = new AffineTransform(scale, 0, 0, scale, 0, 0);
		worldToViewScale.concatenate(worldToViewTrans);
		g2d.setTransform(worldToViewScale);
		g2d.fillPolygon(way.getXVerts(scale), way.getYVerts(scale), 3);
		if (scale > 4.0)
			scale = 4.0;
		if (drawName)
			g2d.drawString(way.getName(), (int)(way.getCenter().getX()+(6 - scale)), (int)(way.getCenter().getY()+(5 - scale)));
	}	
}
