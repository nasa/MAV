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

package gov.nasa.arc.atc.geography;

import gov.nasa.arc.atc.core.Coordinates;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author ahamon
 *
 */
public class Region {

	private final int minAltitude;
	private final int maxAltitude;
	private final List<Coordinates> vertices;
	private final String name;
	//
	// scaled by 1000 [to format as integers]
	private final Polygon polygon;

	public Region(int minAlt, int maxAlt, String regionName) {
		minAltitude = minAlt;
		maxAltitude = maxAlt;
		vertices = new ArrayList<>();
		polygon = new Polygon();
		name = regionName;
	}

	public Region(int minAlt, int maxAlt) {
		this(minAlt, maxAlt, "N/A");
	}

	public void addVertex(double latitude, double longitude) {
		addVertex(new Coordinates(latitude, longitude));
	}

	public void addVertex(Coordinates coordiantes) {
		vertices.add(coordiantes);
	}

	public int getMinAltitude() {
		return minAltitude;
	}

	public int getMaxAltitude() {
		return maxAltitude;
	}

	public List<Coordinates> getVertices() {
		return Collections.unmodifiableList(vertices);
	}

	public String getName() {
		return name;
	}

	public boolean containsPoint(double latitude, double longitude) {
		if (polygon.npoints == 0) {
			buildPolygon();
		}
		int x = (int) (latitude * 1000);
		int y = (int) (longitude * 1000);
		return polygon.contains(x, y);
	}

    private void buildPolygon() {
        //TODO: test
        //Coordinates point : getVertices()
		for (Coordinates point : getVertices()) {
			// !!! all polygon vertices are scaled by 1000 so they can be represented as integers
			polygon.addPoint((int) (point.getLatitude() * 1000), (int) (point.getLongitude() * 1000));
		}
	}

}
