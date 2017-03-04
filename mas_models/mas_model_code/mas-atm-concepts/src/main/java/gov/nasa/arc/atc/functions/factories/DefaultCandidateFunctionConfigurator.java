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

package gov.nasa.arc.atc.functions.factories;

import gov.nasa.arc.atc.simulation.SimulationContext;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 * 
 * @author ahamon
 *
 */
public final class DefaultCandidateFunctionConfigurator implements FunctionConfigurator {
	@Override
	public void setScenario(SimulationContext simulationContext) {
		// nothing to do, function is scenario independent
	}

	@Override
	public boolean isConfigurationOK() {
		return true;
	}

	@Override
	public Object[] getParameters() {
		return new Object[0];
	}

	@Override
	public Node getNode() {
		return new Group();
	}

	@Override
	public void addConfigOKListener(ChangeListener<Boolean> listener) {
		// nothing to do property never changes
	}

	@Override
	public void removeConfigOKListener(ChangeListener<Boolean> listener) {
		// nothing to do property never changes
	}
}
