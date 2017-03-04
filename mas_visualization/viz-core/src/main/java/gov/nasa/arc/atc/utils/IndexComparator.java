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

package gov.nasa.arc.atc.utils;

import java.util.Comparator;

/**
 * 
 * @author ahamon
 *
 */
public class IndexComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		int i1;
		int i2;
		if ("".equals(o1)) {
			i1 = 0;
		} else {
			i1 = Integer.parseInt(o1);
		}
		if ("".equals(o2)) {
			i2 = 0;
		} else {
			i2 = Integer.parseInt(o2);
		}
		return Integer.compare(i1, i2);
	}
}
