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

/**
 * 
 * @author hamon
 *
 */
public class ATCNode extends GeographyElement {

	private final String name;
	private final boolean isMeterFix;

	/**
	 * 
	 * @param nName the {@link ATCNode} name
	 * @param nLatitude the {@link ATCNode} latitude in degrees (decimal version)
	 * @param nLongitude the {@link ATCNode} longitude in degrees (decimal version)
	 * @param nAltitude the {@link ATCNode} altitude in feet
	 * @param isMFix if the {@link ATCNode} is a meter fix point
	 */
	public ATCNode(String nName, double nLatitude, double nLongitude, double nAltitude, boolean isMFix) {
		super(nLatitude, nLongitude, nAltitude);
		name = nName;
		isMeterFix = isMFix;
	}

	/**
	 * Creates a non meter fix {@link ATCNode}
	 * 
	 * @param nName the {@link ATCNode} name
	 * @param nLatitude the {@link ATCNode} latitude in degrees (decimal version)
	 * @param nLongitude the {@link ATCNode} longitude in degrees (decimal version)
	 * @param nAltitude the {@link ATCNode} altitude in feet
	 */
	public ATCNode(String nName, double nLatitude, double nLongitude, double nAltitude) {
		this(nName, nLatitude, nLongitude, nAltitude, false);
	}

	/**
	 * 
	 * @return nName the {@link ATCNode} name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return if the {@link ATCNode} is a meter fix point
	 */
	public boolean isMeterFix() {
		return isMeterFix;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isMeterFix ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ATCNode other = (ATCNode) obj;
		if (isMeterFix != other.isMeterFix)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
