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

package gov.nasa.arc.atc.viewer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author ahamon
 *
 */
public class PosterData {
	
	public static final int DURATION = 450;

	private static final List<SimpleAircraft> AIRCRAFTS = createSimpleAircrafts(8, DURATION);

	private PosterData() {
		//
	}

	private static List<SimpleAircraft> createSimpleAircrafts(int number, int duration) {
		List<SimpleAircraft> result = new LinkedList<>();
		for (int i = 0; i < number; i++) {
			result.add(new SimpleAircraft(generateRandomCallSign(), duration));
		}
		return Collections.unmodifiableList(result);
	}

	public static List<SimpleAircraft> getSimpleAircrafts() {
		return Collections.unmodifiableList(AIRCRAFTS);
	}

	public static String generateRandomCallSign() {
		Random r = new Random();
		StringBuilder stringBuilder = new StringBuilder(7);
		String letter = "QWERTYUIOPLKJHGFDSAZXCVBNM";
		String numerical = "1234567890";
		for (int i = 0; i < 3; i++) {
			stringBuilder.append(letter.charAt(r.nextInt(letter.length())));
		}
		stringBuilder.append('_');
		for (int i = 0; i < 3; i++) {
			stringBuilder.append(numerical.charAt(r.nextInt(numerical.length())));
		}
		return stringBuilder.toString();
	}

	public static void main(String[] args) {
		createSimpleAircrafts(12, 450);
	}

	public static class SimpleAircraft {
		private static int UNIQUE_ID = 0;

		private final String name;
		private final int duration;
		private final int[] delaysAt;
		private final int iD;

		public SimpleAircraft(String aName, int simDuration) {
			name = aName;
			duration = simDuration;
			delaysAt = new int[duration];
			iD = getUniqueID();
			generateDelay();
		}

		public String getName() {
			return name;
		}

		public int getOverallDelay() {
			return delaysAt[duration - 1];
		}

		public int getDelayAt(int time) {
			return delaysAt[time];
		}

		public int getDuration() {
			return duration;
		}

		private void generateDelay() {
			delaysAt[0] = 0;
			int modulo = 4;
			for (int i = 1; i < delaysAt.length; i++) {
				if (i % (modulo * (15 + iD)) == 0) {
					delaysAt[i] = delaysAt[i - 1] +  iD;
					modulo++;
				} else {
					delaysAt[i] = delaysAt[i - 1];
				}
			}
		}

		private static int getUniqueID() {
			UNIQUE_ID++;
			return UNIQUE_ID;
		}
	}

}
