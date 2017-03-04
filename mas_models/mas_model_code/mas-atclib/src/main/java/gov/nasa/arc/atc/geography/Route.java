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

/********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.geography;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author hamon
 *
 */
public class Route {

	private final List<ATCNode> nodes;

	public Route(Route routeToCopy) {
		nodes = new LinkedList<>();
		nodes.addAll(routeToCopy.getRoute());
	}

	public Route() {
		nodes = new LinkedList<>();
	}

	public void addAtStart(ATCNode wp) {
		nodes.add(0, wp);
	}

	public void addAtEnd(ATCNode wp) {
		nodes.add(wp);
	}
	
	public ATCNode getStart(){
		if (nodes.isEmpty()) {
			return null;
		}
		return nodes.get(0);
	}
	
	public ATCNode getNodeAt(int index){
		return nodes.get(index);
	}

	public List<ATCNode> getRoute() {
		return Collections.unmodifiableList(nodes);
	}

	public String fullToString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Route : ");
		for (int i = 0; i < nodes.size() - 1; i++) {
			sb.append(nodes.get(i).getName()).append(" -> ");
		}
		if (!nodes.isEmpty()) {
			sb.append(nodes.get(nodes.size() - 1).getName());
		}
		return sb.toString();
	}
	
	public boolean passesBy(ATCNode node){
        return nodes.stream().anyMatch(step -> step.equals(node));
	}
	
	public boolean isContained(Route route){
		if(nodes.isEmpty()){
			return true;
		}
		List<ATCNode> otherRoute = route.getRoute();
		boolean allNodesContained = otherRoute.containsAll(nodes);
		if(!allNodesContained){
			return false;
		}		
		int otherStartIndex = otherRoute.indexOf(nodes.get(0));
		if(otherRoute.size()<nodes.size()+otherStartIndex){
			return false;
		}
		for(int i = 1; i<nodes.size();i++){
			if(!nodes.get(i).equals(otherRoute.get(otherStartIndex+i))){
				return false;
			}
		}
		return true;
	}
	
	//TODO: the contains route method

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Route : ");
		if (nodes.isEmpty()) {
			sb.append("empty");
		} else if (nodes.size() == 1) {
			sb.append(nodes.get(0).getName()).append(".");
		} else {
			sb.append(nodes.get(0).getName());
			sb.append(" --(");
			sb.append(nodes.size());
			sb.append(")--> ");
			sb.append(nodes.get(nodes.size() - 1).getName());
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;
		return true;
	}
	
	

}
