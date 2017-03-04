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

import static org.junit.Assert.*;

import gov.nasa.arc.atc.simulation.SlotTrajectory;
import org.junit.Test;

import gov.nasa.arc.atc.simulation.SimulatedTrajectory;

/**
 * 
 * @author ahamon
 *
 */
public class DepartureGapTest {

	private static final int DEFAULT_SIM_DURATION = 1200;

	@Test
	public void testCreation() {
		SimulatedTrajectory dep1 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 10), DEFAULT_SIM_DURATION);
		SimulatedTrajectory dep2 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 15), DEFAULT_SIM_DURATION);
		DepartureGap gap = new DepartureGap(dep1, dep2);
		assertEquals(5, gap.getGapDuration());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreationFail() {
		SimulatedTrajectory dep1 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 10), DEFAULT_SIM_DURATION);
		SimulatedTrajectory dep2 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 5), DEFAULT_SIM_DURATION);
		DepartureGap gap = new DepartureGap(dep1, dep2);
		fail("" + gap);
	}

	@Test
	public void testSetStart() {
		SimulatedTrajectory dep1 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 10), DEFAULT_SIM_DURATION);
		SimulatedTrajectory dep2 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 15), DEFAULT_SIM_DURATION);
		DepartureGap gap = new DepartureGap(dep1, dep2);
		gap.setStartTime(15);
		assertEquals(0, gap.getGapDuration());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetStartFail1() {
		SimulatedTrajectory dep1 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 10), DEFAULT_SIM_DURATION);
		SimulatedTrajectory dep2 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 15), DEFAULT_SIM_DURATION);
		DepartureGap gap = new DepartureGap(dep1, dep2);
		gap.setStartTime(17);
		fail("" + gap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetStartFail2() {
		SimulatedTrajectory dep1 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 10), DEFAULT_SIM_DURATION);
		SimulatedTrajectory dep2 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 15), DEFAULT_SIM_DURATION);
		DepartureGap gap = new DepartureGap(dep1, dep2);
		gap.setStartTime(5);
		fail("" + gap);
	}

	@Test
	public void testSetEnd() {
		SimulatedTrajectory dep1 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 10), DEFAULT_SIM_DURATION);
		SimulatedTrajectory dep2 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 5), DEFAULT_SIM_DURATION);
		dep2.getSlotMarker().setDepartureTime(15);
		DepartureGap gap = new DepartureGap(dep1, dep2);
		gap.setEndTime(20);
		assertEquals(10, gap.getGapDuration());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetStartEnd1() {
		SimulatedTrajectory dep1 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 10), DEFAULT_SIM_DURATION);
		SimulatedTrajectory dep2 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 5), DEFAULT_SIM_DURATION);
		dep2.getSlotMarker().setDepartureTime(15);
		DepartureGap gap = new DepartureGap(dep1, dep2);
		gap.setEndTime(9);
		fail("" + gap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetStartEnd2() {
		SimulatedTrajectory dep1 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 10), DEFAULT_SIM_DURATION);
		SimulatedTrajectory dep2 = new SlotTrajectory(DepartureFixedTest.getAtcGeography(),DepartureFixedTest.createDeparture(1, 15), DEFAULT_SIM_DURATION);
		DepartureGap gap = new DepartureGap(dep1, dep2);
		gap.setEndTime(10);
		fail("" + gap);
	}

}
