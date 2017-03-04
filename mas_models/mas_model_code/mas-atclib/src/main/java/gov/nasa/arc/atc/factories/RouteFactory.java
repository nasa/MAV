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

package gov.nasa.arc.atc.factories;

import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Route;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author ahamon
 *
 */
public class RouteFactory {

	private RouteFactory() {
		// private utility constructor
	}

	/**
	 * Creates all the possible routes leading to the arrival nodes in the geography
	 * 
	 * @param geography the {@link ATCGeography} to analyze
	 * @return all possible routes leading to arrival nodes
	 */
	public static List<Route> createArrivalRoutes(ATCGeography geography) {
		List<Route> arrivalRoutes = new ArrayList<>();
        // for each arrival
        geography.getArrivalNodes().forEach(node -> analyzeNode(geography, node, new Route(), arrivalRoutes));
		return Collections.unmodifiableList(arrivalRoutes);
	}

	private static void analyzeNode(ATCGeography geography, ATCNode node, Route route, List<Route> arrivalRoutes) {
		Route newRoute = new Route(route);
		if(route.passesBy(node)){
			newRoute.addAtStart(node);
			arrivalRoutes.add(newRoute);
			return;
		}
		newRoute.addAtStart(node);
		final List<ATCNode> previousNodes = geography.getPreviousNodes(node);
		if (!previousNodes.isEmpty()) {
            previousNodes.forEach(previous -> analyzeNode(geography, previous, newRoute, arrivalRoutes));
		} else {
			arrivalRoutes.add(newRoute);
		}
	}

}
