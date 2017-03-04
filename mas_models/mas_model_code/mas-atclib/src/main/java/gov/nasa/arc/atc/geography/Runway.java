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

/**
 * 
 * @author ahamon
 *
 */
public class Runway extends Waypoint{
	
	private final int qfu;
	private final Airport myAirport;
	
	private boolean active;

	public Runway(String nName, Airport airport,int rQFU) {
		super(nName, airport.getLatitude(), airport.getLongitude(), airport.getAltitude());
		qfu=rQFU;
		myAirport=airport;
		active=false;
	}
	
	public void activate(){
		active =true;
	}
	
	public void deactivate(){
		active = false;
	}
	
	public boolean isActive(){
		return active;
	}
	
	public int getQFU(){
		return qfu;
	}
	
	public Airport getAirport() {
		return myAirport;
	}
	
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder();
		sb.append("Runway ");
		sb.append(getName());
		return sb.toString();
	}
//	
//	@Override
//	public  boolean equals(Object obj) {
//		if (obj instanceof Runway) {
//			Runway node = (Runway) obj;
//			return node.getName().equals(getName());
//		}
//		return false;
//	}

}
