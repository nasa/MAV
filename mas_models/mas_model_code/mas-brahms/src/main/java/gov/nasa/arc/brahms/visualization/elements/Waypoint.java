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

package gov.nasa.arc.brahms.visualization.elements;

import java.awt.geom.Point2D;

public class Waypoint {

	private String name;
	private String latitude;
	private String longitude;
	private String altitudeRestriction;
	
	private Point2D.Double center;
	private Point2D.Double cornerOne;		
	private Point2D.Double cornerTwo;
	private Point2D.Double cornerThree;
	
	public Waypoint(String name, String latitude, String longitude, String altRest) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitudeRestriction = altRest;
		this.center = new Point2D.Double(0, 0);
	}
	
	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getAltRestrict() {
		return altitudeRestriction;
	}

	public int[] getXVerts(double scale) {
		if(scale < 4.0)
			scale = 3.0;
		else if(scale >= 4.0)
			scale = 4.0;
		return new int[] {(int)cornerOne.getX(), (int)(cornerTwo.getX() - scale), (int)(cornerThree.getX() + scale)};
	}
	
	public int[] getYVerts(double scale) {
		if(scale < 4.0)
			scale = 3.0;
		else if(scale == 4.0)
			scale = 4.0;
		else
			scale = 4.5;
		return new int[] {(int)(cornerOne.getY() + scale), (int)(cornerTwo.getY() - scale), (int)(cornerThree.getY() - scale)};
	}
	
	public Point2D.Double getCenter() {
		return center;
	}
	
	public String getName() {
		return name;
	}
	
	public void setCenter(Point2D.Double center) {
		this.center = center;
		cornerOne = new Point2D.Double(center.getX(), center.getY()-5);
		cornerTwo = new Point2D.Double(center.getX()+5, center.getY()+5);
		cornerThree = new Point2D.Double(center.getX()-5, center.getY()+5);
	}
	
	public boolean contains(double x, double y) {
		Point2D.Double obj = new Point2D.Double(x, y);

		Point2D.Double c1 = cornerOne;
		Point2D.Double c2 = cornerTwo;
		Point2D.Double c3 = cornerThree;
		
		// do 3 tests here, if all same sign, then its inside
		Point2D.Double p11 = new Point2D.Double(obj.getX() - c1.getX(), obj.getY() - c1.getY());
		Point2D.Double p12 = new Point2D.Double(-(c2.getY() - c1.getY()), c2.getX() - c1.getX());
		double test1 = (p11.getX() * p12.getX()) + (p11.getY() * p12.getY());
				
		Point2D.Double p21 = new Point2D.Double(obj.getX() - c2.getX(), obj.getY() - c2.getY());
		Point2D.Double p22 = new Point2D.Double(-(c3.getY() - c2.getY()), c3.getX() - c2.getX());
		double test2 = (p21.getX() * p22.getX()) + (p21.getY() * p22.getY());
		
		Point2D.Double p31 = new Point2D.Double(obj.getX() - c3.getX(), obj.getY() - c3.getY());
		Point2D.Double p32 = new Point2D.Double(-(c1.getY() - c3.getY()), c1.getX() - c3.getX());
		double test3 = (p31.getX() * p32.getX()) + (p31.getY() * p32.getY());
		
		if((test1 >= 0 && test2 >= 0 && test3 >= 0) || (test1 < 0 && test2 < 0 && test3 < 0))
			return true;
		
		return false;
	}
}
