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

package gov.nasa.arc.atc.scenariogen

import java.io.File

import gov.nasa.arc.atc.algos.viewer.SimulationConfigurationLoader
import gov.nasa.arc.atc.scenarios.week2.run6.Week2Run6


/**
  * @author ahamon
  */
object ScenarioParser {


  def main(args: Array[String]): Unit = {


    // loading week 2 run 6
    val week2Run6 = new Week2Run6
    val path = week2Run6.getClass.getResource("scenario.properties").getPath
    val file = new File(path)
    SimulationConfigurationLoader.loadConfiguration(file)

  }

}
