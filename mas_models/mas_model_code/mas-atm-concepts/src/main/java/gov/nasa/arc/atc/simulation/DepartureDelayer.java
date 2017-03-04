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

package gov.nasa.arc.atc.simulation;

import java.util.Random;

/**
 * 
 * @author ahamon
 * 
 * this class is a temporary fix to overcome the brahms files start time bug
 *
 */
public class DepartureDelayer {
	
	//private static final int[] DELAYS = new int[]{ 4, 74, 117, 195, 256, 316, 400, 445, 515, 572, 664, 700, 767, 843, 920, 961, 1048, 1104, 1160, 1221, 1298, 1377, 1416, 1505, 1570, 1624, 1683, 1753, 1823, 1876, 1944, 2025, 2071, 2138, 2215, 2283, 2333, 2406, 2459, 2522, 2603, 2671, 2717, 2809, 2848, 2934, 2986, 3045, 3113, 3199, 3263, 3300, 3373, 3440, 3502, 3561, 3631, 3703, 3775, 3828};
	private static final int[] DELAYS = new int[]{  9, 14, 18, 17, 28, 35, 36, 40, 43, 54, 51, 62, 65, 68, 73, 81, 85, 95, 95, 105, 102, 115, 118, 123, 126, 134, 139, 145, 141, 151, 157, 164, 170, 166, 176, 178, 183, 187, 200, 199, 206, 207, 217, 218, 224, 231, 238, 240, 246, 255, 258, 264, 265, 269, 278, 281, 284, 287, 295};
	
	
	private DepartureDelayer(){
		// private utility constructor
	}
	
	public static int getDelayAtIndex(int index){
		return DELAYS[index%DELAYS.length];
	}
	
	
	public static void main(String[] args){
		int delta = 5;
		int value;
		Random rand = new Random();
		
		for(int i = 1;i<60;i++){
			value = i*delta+(rand.nextInt(10)-4);
			System.out.print(" "+value+",");
		}
		
		for(int i = 0;i<130;i++){
			getDelayAtIndex(i);
		}
		
	}

}
