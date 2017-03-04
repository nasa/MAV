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

package gov.nasa.arc.brahms.model;

import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Area;
import gov.nasa.arc.brahms.model.concept.AreaDef;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.concept.Class_b;
import gov.nasa.arc.brahms.model.concept.Conceptual_Class;
import gov.nasa.arc.brahms.model.concept.Conceptual_Object;
import gov.nasa.arc.brahms.model.concept.Group;
import gov.nasa.arc.brahms.model.concept.Object_b;
import gov.nasa.arc.brahms.model.concept.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiAgentSystem {

	public static Set<Group> allGroups = new HashSet<Group>();
	public static Set<Agent> allAgents = new HashSet<Agent>();
	
	public static Map<String, Agent> agNameToAgentMap = 
									new HashMap<String, Agent>();
	
	
	public static Set<Object_b> allObjects = new HashSet<Object_b>();
	
	public static Map<String, Object_b> objNameToObjectMap = 
									new HashMap<String, Object_b>();
	
	public static Map<String, Class_b> allClasses = 
								new HashMap<String, Class_b>();
								
	public static Set<Conceptual_Object> allConceptObjects = 
			new HashSet<Conceptual_Object>();
								
	public static Map<String, Conceptual_Class> allConceptClasses = 
			new HashMap<String, Conceptual_Class>();
			
	public static Map<String, List<Basic>> typeNameToInstancesMap
				= new HashMap<String, List<Basic>>();
								
								
	public static Set<Area> allAreas = new HashSet<Area>();
	public static Set<AreaDef> allAreaDefs = new HashSet<AreaDef>();
	public static Set<Path> allPaths = new HashSet<Path>();

	protected int globalClock;

	public static int duration;

	public Set<Agent> getAgents() {
		return allAgents;
	}
	
	public void addAgents(Agent a) {
		allAgents.add(a);
		MultiAgentSystem.agNameToAgentMap.put(a.getName(), a);
		
	}

	public Set<Object_b> getObjects() {
		return allObjects;
	}
	
	protected void updateTypeNameConceptMap(String typeName, Basic b) {
		List<Basic> concepts; 
		if (typeNameToInstancesMap.containsKey(typeName)) {
			concepts = typeNameToInstancesMap.get(typeName);
		} else {
			concepts = new ArrayList<Basic>();
		}
		concepts.add(b);
		typeNameToInstancesMap.put(typeName, concepts);
	}
	
	public void addObjects(Object_b b) {
		allObjects.add(b);
		objNameToObjectMap.put(b.getName(), b);
	}
	
	public void updateTheTypeMap() {
		for(Object_b b : allObjects) {
			updateTypeNameConceptMap(b.getClassInstance(), b);
			for(String classType : b.getClassExtends())
				updateTypeNameConceptMap(classType, b);
		}
		for(Agent a : allAgents) {
			for(String classType : a.getMemberOfs()) {
				updateTypeNameConceptMap(classType, a);
			}
		}
	}
	
	public void addConceptualObjects(Conceptual_Object b) {
		allConceptObjects.add(b);
	}
	
	public void addClasses(String str, Class_b c) {
		allClasses.put(str, c);
	}
	
	public void addGroups(Group g) {
		allGroups.add(g);
	}
	
	public Set<Conceptual_Object> getConceptObjs() {
		return allConceptObjects;
	}
	
	public Set<Group> getGroups() {
		return allGroups;
	}
	
	public void removeAllAgObj() {
		allAgents.clear();
		allObjects.clear();
		allClasses.clear();
		allGroups.clear();
	}
	
	public void removeAgent(Agent ag){
		allAgents.remove(ag);
	}
	
	public Map<String, Class_b> getClasses() {
		return allClasses;
	}

	public Map<String, Conceptual_Class> getConceptClasses() {
		return allConceptClasses;
	}
	
	public int getGlobalClock() {
		return globalClock;
	}

	public void setGlobalClock(int val) {
		globalClock = val;
	}
	
	public Set<Area> getAllAreas() {
		return allAreas;
	}
	
	public int getNumAreas() {
		return allAreas.size();
	}
	
	public Set<AreaDef> getAllAreaDefs() {
		return allAreaDefs;
	}
	
	public Set<Path> getAllPaths() {
		return allPaths;
	}
	
	public void setDuration(int dur){
		duration = dur;
	}
	public int getDuration(){
		return duration;
	}

}
