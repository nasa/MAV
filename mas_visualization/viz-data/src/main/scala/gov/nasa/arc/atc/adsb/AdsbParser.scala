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

package gov.nasa.arc.atc.adsb
//
//import gov.nasa.arc.atc.geography.Position
//import gov.nasa.arc.atc.{AfoUpdate, AfoUpdateImpl}
//import gov.nasa.race.data.FlightPos
//import gov.nasa.race.data.translators.SBS2FlightPos
//
//import scala.tools.nsc.interpreter._
//
///**
//  * @author ahamon
//  */
object AdsbParser {
//
//  def generateAFOUpdates(stream: InputStream): Map[String,List[AfoUpdate]] ={
//
//    // get all the lines from the file
//    val lines = scala.io.Source.fromInputStream(stream).getLines
//
//    // instanciates the translator from RACE API
//    val translator = new SBS2FlightPos()
//
//    // get all positions as Any (result from translator is type Any
//    val rawValues: List[Any] = lines map translator.translate filter (e => e.isDefined) map (nonNulE => nonNulE.get) toList
//
//    // cast to FlightPos
//    val flightPositions: List[FlightPos] = rawValues map (fPos => fPos match { case fPos: FlightPos => fPos })
//
//    // get all call signs
//    val callSign = (flightPositions map (pos => pos.cs)).toList.distinct
//
//    // map containing each call sign and its flightPosition (sorted)
//    val callSignsPositions: Map[String,List[FlightPos]] = callSign map (cs => (cs, flightPositions.filter(fPos => fPos.cs == cs).toList.sortWith((p1: FlightPos, p2: FlightPos) => p1.date.compareTo(p2.date) < 0))) toMap
//
//    // returning the map containing each
//    callSignsPositions map(x => (x._1, convertToAFOUpdate(x._2)))
//  }
//
//
//  def convertToAFOUpdate(positions: List[FlightPos]): List[AfoUpdate] = {
//    //TODO: test traffic type against ...?
//    val startTime = if(positions.nonEmpty) positions.head.date.getSecondOfDay else 0
//    val endTime = if(positions.nonEmpty) positions.last.date.getSecondOfDay - startTime else 0
//    // vertical speed not calculate yet
//    val segmentID = 0
//    positions map(fPos => new AfoUpdateImpl(fPos.cs, fPos.date.getSecondOfDay, new Position(fPos.position.φ.toDegrees, fPos.position.λ.toDegrees, fPos.altitude.toFeet), fPos.speed.toInternationalMilesPerHour, -1, fPos.heading.toDegrees, segmentID, "ARRIVAL", 1, startTime, 0, false, endTime-startTime, "ME", 0))
//  }


}
