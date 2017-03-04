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

package gov.nasa.arc.atc.algos.dsas;

import gov.nasa.arc.atc.simulation.SimulatedTrajectory;
import gov.nasa.arc.atc.utils.Constants;
import gov.nasa.arc.atc.utils.GapUtils;

/**
 * @author ahamon
 *
 */
public class ArrivalGap implements Gap {

	private final SimulatedTrajectory arrival1;
	private final SimulatedTrajectory arrival2;
	private int startTime;
	private int endTime;
	private int gapDuration;
	private int nbDepartures;
//	private ArrivalGapType gapType;

	public ArrivalGap(final SimulatedTrajectory a1, final SimulatedTrajectory a2) {
		arrival1 = a1;
		arrival2 = a2;
		if (arrival2.getArrivalTime() < arrival1.getArrivalTime()) {
			throw new IllegalArgumentException("End time (" + arrival2.getArrivalTime() + ") cannot be strictly inferior to start time(" + arrival1.getArrivalTime() + ")");
		}
		updateGapAttributes();
	}

	@Override
	public int getStartTime() {
		return startTime;
	}

	@Override
	public int getEndTime() {
		return endTime;
	}

	@Override
	public int getGapDuration() {
		return gapDuration;
	}

	public int getNbDeparturesPossible() {
		return nbDepartures;
	}

//	public void setStartTime(int newStartTime) {
//		// if (endTime < newStartTime) {
//		// throw new IllegalArgumentException("Start time (" + newStartTime + ") cannot be strictly superior to end time(" + endTime + ")");
//		// }
//		// if (newStartTime < arrival1.getOriginalStartTime()) {
//		// throw new IllegalArgumentException("Cannot take off second afo at (" + newStartTime + ") before its original departure time (" + arrival1.getOriginalStartTime() + ")");
//		// }
//		// arrival1.getSlotMarker().setStartTime(newStartTime);
//		// updateGapAttributes();
//	}
//
//	public void setEndTime(int newEndTime) {
//		// if (newEndTime < startTime) {
//		// throw new IllegalArgumentException("End time (" + newEndTime + ") cannot be strictly inferior to start time(" + startTime + ")");
//		// }
//		// if (newEndTime < arrival2.getOriginalStartTime()) {
//		// throw new IllegalArgumentException("Cannot take off second afo at (" + newEndTime + ") before its original departure time (" + arrival2.getOriginalStartTime() + ")");
//		// }
//		// arrival2.getSlotMarker().setStartTime(newEndTime);
//		// updateGapAttributes();
//	}

	public int getEarliestTimeAtNumber(int arrivalIndex) {
		if (arrivalIndex < 0) {
			throw new IllegalArgumentException("Index cannot be negative: " + arrivalIndex);
		}
		if (arrivalIndex > nbDepartures) {
			throw new IllegalArgumentException("Index (" + arrivalIndex + ") is greater and nbArrivalsPossible (" + nbDepartures + ")");
		}
		if (arrivalIndex == 1) {
			return startTime + Constants.DEP_ARR_MIN;
		} else {
			return startTime + Constants.DEP_ARR_MIN + (arrivalIndex - 1) * Constants.ARR_ARR_MIN;
		}
	}

	// should be private and handled using property changes
	public final void updateGapAttributes() {
		startTime = arrival1.getArrivalTime();
		endTime = arrival2.getArrivalTime();
		gapDuration = endTime - startTime;
		nbDepartures = GapUtils.calculateNbDepartures(ArrivalGap.this);
//		gapType = GapUtils.calculateArrivalType(gapDuration);
	}

	public SimulatedTrajectory getFirstArrival() {
		return arrival1;
	}

	public SimulatedTrajectory getSecondArrival() {
		return arrival2;
	}

//	public ArrivalGapType getGapType() {
//		return gapType;
//	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" nbArrivals=");
		sb.append(nbDepartures);
		sb.append(" starting at t=");
		sb.append(startTime);
		sb.append(" duration=");
		sb.append(gapDuration);
//		sb.append(" type=");
//		sb.append(gapType);
		return sb.toString();
	}

}
