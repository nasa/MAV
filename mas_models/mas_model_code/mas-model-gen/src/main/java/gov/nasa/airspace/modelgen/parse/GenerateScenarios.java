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

package gov.nasa.airspace.modelgen.parse;

import gov.nasa.airspace.modelgen.routes.Aircraft;
import gov.nasa.airspace.modelgen.routes.FileManagerUtils;
import gov.nasa.airspace.modelgen.routes.Waypoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;


public class GenerateScenarios {
	
	Map<String, Aircraft> aircrafts;
	Waypoint wp; 
	Config config;
	
	static final String AIRCRAFT_ID = "acid";
	static final String NUMBER_OF_POSITIONS = "number_of_points";
	static final String WAYPOINT_START = "WPT(#)";
	static final String AIRCRAFT_ROUTE_START = "INCOMING |-------"
			+ " MPI adrs_mpi_trajectory_st -------";
	
	static final String FLIGHT_STATE = "INCOMING |FLIGHT_STATE";
	static final String TYPE_ID = "0";
	
	public static final int SECONDS_IN_ONE_HOUR = 3600;
	public static final int MINS_IN_ONE_HOUR = 60;
	
	
	//Trajectory indicies in the ADRS data
	static final int START_TOK = 0;
	static final int AC_ID = 1;
	static final int WY_ID = 1;
	static final int LAT_ID = 2;
	static final int LON_ID = 3;
	static final int CAS_ID = 6;
	static final int ALT_ID = 7;
	
	static final int CALL_SIGN = 0;
	static final int ENTRY_TIME_IN_SECS = 1;
	static final int ALT = 19;
	static final int TARGET_WY = 23;
	static final int TARGET_ALT = 24;
	static final int IS_DEP = 32;
	
	//Flight State indices in the ADRS Data
	static final int FLST_START_TIME = 3;
	static final int FLST_LAT_ID = 5;
	static final int FLST_LON_ID = 6;
	static final int FLST_ALT_ID = 7;
	static final int FLST_CAS_ID = 9;
	
	//Timeline_meter indices
	static final int TLM_REAL_TIME = 1;
	static final int TLM_TIME = 2;
	static final int TLM_AC_ID = 4;
	static final int TLM_STA_FR = 13;
	static final int TLM_STA_ETA = 18;
	
	//manual way points indices
	
	static final int MAN_WY_ID = 1;
	
	private String globalStart = "";
	private int startTime = 0;
	
	
	String inputPath;

	Map<String, String> ATC =
				new HashMap<String, String>();
	
	Map<String, String> wayPointTohandOffControllerMap =
			new HashMap<String, String>();
	
	Set<String> manualWaypoints = 
				new HashSet<String>();

	boolean noInputFile = true;
	
	public void parseControllersFile() throws IOException {
		if(!config.containsKey("initial.controller")) return;
		
		try(BufferedReader br = new
				BufferedReader(new FileReader(inputPath +
						config.getPropertyString("initial.controller")))) {
			String line;
			String[] nextLine;
			while ((line = br.readLine()) != null) {
				nextLine = line.split(":");
				String controller = cleanStr(nextLine[0]);
				StringTokenizer tok = new StringTokenizer(nextLine[1], ",");
				while(tok.hasMoreElements()) {
					String wp = tok.nextElement().toString();
					ATC.put(wp, controller);
				}
				//there is information about the handoff point as well
				//as the handoff controller
				if(nextLine.length > 2) {
					String[] vals = nextLine[2].split(",");
					String handOffWaypoint = vals[0];
					String handOffController = vals[1];
					wayPointTohandOffControllerMap.put(handOffWaypoint, handOffController);
				}
			}
		}
		System.out.println(ATC.toString());
		System.out.println(wayPointTohandOffControllerMap.toString());
	}
	
	public void parseInputFile() throws IOException {
		if(!config.containsKey("macsinput.file")) return;
		noInputFile = false;
		try(BufferedReader br = new
				BufferedReader(new FileReader(inputPath +
						config.getPropertyString("macsinput.file")))) {
	        String line = br.readLine(); // skip the header
	        String[] nextLine = null;
	        while ((line = br.readLine()) != null) {
	            nextLine = line.split("\t");
	            String acid = nextLine[0];
	            String isDeparture = nextLine[32];
	            if(!aircrafts.containsKey(acid)) {
	            	//System.out.println("acid :" + acid);
	            	continue;
	            }
	            
	            Aircraft ac = aircrafts.get(acid);
	            ac.setIsDeparture(Boolean.valueOf(isDeparture));
	            
	        }
		}
	}
	
	public void parseTimeMeterList() throws IOException {
		try(BufferedReader br = new
				BufferedReader(new FileReader(inputPath +
						config.getPropertyString("timelinemeter.data")))) {
			String line = br.readLine();
			String[] nextLine = line.split("\t");
			while((line = br.readLine()) != null) {
				nextLine = line.split("\t");
				String acid = cleanStr(nextLine[TLM_AC_ID]);
				if(!aircrafts.containsKey(acid)) continue;
				
				Aircraft ac = aircrafts.get(acid);
				if(ac.isSTAFrozen()) continue;
				
				if(cleanStr(nextLine[TLM_STA_FR]).equals("false"))
					continue;
				
				int relTime = Integer.valueOf
							(cleanStr(nextLine[TLM_REAL_TIME]));
				
				if(relTime < ac.getStartTime()) continue;
				
				String diff = cleanStr(nextLine[TLM_STA_ETA]);
				ac.setSTAElements(relTime,
										Integer.valueOf(diff));
			}
		}
	}
	
	public void parse() throws IOException {	
		
		wp = new Waypoint();
		String[] nextLine = null;
		int counter = 0;
		aircrafts = new HashMap<String, Aircraft>();
		Aircraft aircraft = null;
		
		boolean parseRoutes = false;
		
		try(BufferedReader br = new
				BufferedReader(new FileReader(inputPath + 
						config.getPropertyString("adrs.data")))) {
	        String line = null;

	        while ((line = br.readLine()) != null) {
	        	//System.out.println(line);
	            nextLine = line.split("\t");
	           
				if(nextLine[START_TOK].contains(AIRCRAFT_ROUTE_START)) {
					parseRoutes = true;
				} else if(nextLine[START_TOK].contains(FLIGHT_STATE)) {
					handleFlightState(nextLine);
					
				}
				if(!parseRoutes) continue;
				
				if(cleanStr(nextLine[START_TOK]).equals(AIRCRAFT_ID)) {
					aircraft = createNewInstance(nextLine);
				} else if(cleanStr(nextLine[START_TOK]).contains(NUMBER_OF_POSITIONS)) {
					setNumberOfWaypoints(nextLine, aircraft);
				} else if(cleanStr(nextLine[START_TOK]).contains(WAYPOINT_START)) {
					aircraft.waypointReset();
					counter = 0;
					continue;
				}
				
				if(aircraft != null && aircraft.startGettingWaypoints()) {
					if(counter >= aircraft.getNumberofWaypoints()) {
						aircraft = storeAircraftInstance(aircraft);
						parseRoutes = false;
					} else {
						updateAircraftValues(nextLine, aircraft);
					}				
					counter++;		
				}
	        }
	        System.out.println("done parsing " + aircrafts.size());
	    }

	}
	
	protected Aircraft storeAircraftInstance(Aircraft aircraft) {
		if(!aircrafts.containsKey(aircraft.getId())) {
			aircrafts.put(aircraft.getId(), aircraft);
			//aircraft.print();
		}
		return null;
	}
	
	protected Aircraft createNewInstance(String[] nextLine) {
		String aircraftId = cleanStr(nextLine[AC_ID]);
		return new Aircraft(aircraftId);
	}
	
	protected void setNumberOfWaypoints(String[] nextLine, Aircraft aircraft) {
		String tmpStr = nextLine[START_TOK].replace(NUMBER_OF_POSITIONS, "");
		int number_of_positions = Integer.valueOf(cleanStr(tmpStr));
		aircraft.setNumberofWaypoints(number_of_positions);
	}

	protected void printParsedLine(String[] nextLine) {
		 for(int nextLineCount = 0; 
       			nextLineCount < nextLine.length; nextLineCount++) {
    		 System.out.print(nextLine[nextLineCount]+ " -("+ nextLineCount +")- ");
    	 }
		 System.out.println();
	}
	
	protected void updateAircraftValues(String[] nextLine,
											Aircraft aircraft) { 
		if(cleanStr(nextLine[WY_ID]).equals(TYPE_ID)) {
			String wayPointName = stripStr(nextLine[START_TOK]);
			Double lat = Double.valueOf(cleanStr(nextLine[LAT_ID]));
			Double lon = checkHemisphere
						(Double.valueOf(cleanStr(nextLine[LON_ID])));
			Double cas = Double.valueOf(cleanStr(nextLine[CAS_ID]));
			Double alt = Double.valueOf(cleanStr(nextLine[ALT_ID]));
			aircraft.setValues(wayPointName, lat, lon, cas, alt);
		
		}
	}
	
	protected Double checkHemisphere(Double longitude) {
		if(config.isWesternHemisphere() && longitude > 0)
			return -1 * longitude;
		return longitude;
	}
	
	public void generateBrahmsFiles() {
		String path = config.getAutoGenFolder();
		assert(aircrafts != null);
		
		File flightSeg = createBrandNewFile(path + "allFlightSegments.b");
		FileManagerUtils.writeToFile(flightSeg, config.getFlightSegmentPackageName(), true);
		
		File flightPlan = createBrandNewFile(path + "allFlightPlans.b");
		FileManagerUtils.writeToFile(flightPlan, config.getFlightPlanPackageName(), true);
		
		File airplane = createBrandNewFile(path + "allAirplanes.b");
		FileManagerUtils.writeToFile(airplane, config.getAirplanesPackageName(), true);
		
		File slotMarker = createBrandNewFile(path + "allSlotMarkers.b");
		FileManagerUtils.writeToFile(slotMarker, config.getSlotsPackageName(), true);
		
		for(String acid : aircrafts.keySet()) {
			Aircraft ac = aircrafts.get(acid);
			
			if(!ac.hasValidStartTime()) continue;
			
			ac.setInitialController(ATC.get(ac.getFirstWaypoint()));
			if(ac.getInitialController() == null)
				ac.setInitialController(ATC.get(ac.getSecondWaypoint()));
			if(ac.getInitialController() == null) {
				ac.setInitialController("ZNY_118");
			} 
			
			//if the plane is past the handoff waypoint then set the initial
			//controller as the controller who has been handed the plane
			if(wayPointTohandOffControllerMap.containsKey(ac.getFirstWaypoint())) {
				ac.setInitialController
					(wayPointTohandOffControllerMap.get(ac.getFirstWaypoint()));
			}
			
			if(noInputFile) {
				if(ac.getFirstWaypoint().contains
						(config.getPropertyString("depart.airport"))) {
					ac.setIsDeparture(true);
				}
			}
		
			ac.genFlightSegmentBrahmsFile(flightSeg);
			ac.genFlightPlanBrahmsFile(flightPlan);
			ac.genAircraftBrahmsFiles(airplane);
			ac.genSlotMarkerBrahmsFiles(slotMarker);
			ac.setWaypointOfInterest(wp);

		}
		
		wp.genBrahmsFile(path);
		wp.generateVisualizationWPFile(path);
		wp.generateTRACWPFile(path);
		genDepartures(createBrandNewFile(path + config.getTowerController() +".b"));
		

	}
	
	private void genDepartures(File file) {
		SortedSet<Aircraft> departures = new TreeSet<Aircraft>();
		for(String acid : aircrafts.keySet()) {
			Aircraft ac = aircrafts.get(acid);
			if(ac.isDepartureAircraft()) {
				departures.add(ac);
			}
		}
		String obj = "agent"+ config.getTowerController()  +"memberof Tower {\n";
	
		
		String beliefs = "  initial_beliefs:\n";
		String facts = "  initial_facts:\n";
		String val = "";
		int counter = 1;
		val = val + "(current hasWaypoint LGA22);\n";
		for(Aircraft ac : departures) {
		
			if(ac.getStartTime() > Integer.MIN_VALUE) {
				val = val + "\t(laGuardiaDepts.departure_queue("+counter+
									") = plane_"+ac.getId() +" );\n";
				counter++;
			
			}
			
		}
		
		String retVal = obj + beliefs + val + facts + val + "}\n";
		FileManagerUtils.writeToFile(file, retVal, false);
	}
	
	private File createBrandNewFile(String fileName) {
		File file = new File(fileName);
		if(file.exists()) {
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return file;
	}
	
	public void handleFlightState(String[] line) {
		
		String aircraftId =  cleanStr(line[AC_ID]);
		String time = cleanStr(line[FLST_START_TIME]);
		if(this.globalStart.equals("")) {
			globalStart = getDateAndTime(time);
			startTime = this.getTimeInSeconds
							(parseDateAndTime(globalStart));
		}
		if(aircrafts.containsKey(aircraftId)) {
			Aircraft ac = aircrafts.get(aircraftId);
			
			if(ac.isEntryTimeSet()) return;
			
			Double currLat = Double.valueOf(cleanStr(line[FLST_LAT_ID]));
			Double currLon = checkHemisphere(Double.valueOf
											(cleanStr(line[FLST_LON_ID])));
			Double altitude = Double.valueOf(cleanStr(line[FLST_ALT_ID]));
			Double cas = Double.valueOf(cleanStr(line[FLST_CAS_ID]));
			
			String firstWY = ac.getFirstWaypoint();
			String secondWY = ac.getSecondWaypoint();
			
			Double firstLat = ac.getLatMap().get(firstWY);
			Double firstLon = ac.getLonMap().get(firstWY);
			
			Double secondLat = ac.getLatMap().get(secondWY);
			Double secondLon = ac.getLonMap().get(secondWY);
			
			Double firstDist = ac.computeDistance(currLat, currLon, firstLat, firstLon);
			Double secondDist = ac.computeDistance(currLat, currLon, secondLat, secondLon);
			
			firstDist = Math.abs(firstDist);
			secondDist = Math.abs(secondDist);
			
			if(secondDist <= firstDist) {
				String date = getDateAndTime(time);
				int secs = getTimeInSeconds(parseDateAndTime(date));
				int offset = secs - this.startTime;
				ac.setEntryTimeInSeconds(date, offset);
				ac.setInitialLatLon(currLat, this.checkHemisphere(currLon));
				ac.setInitAlt(altitude);
				ac.setInitSpeed(cas);
				
			}
			
		}
	}
	
	
	public String getDateAndTime(String time) {
		double dbl = Double.valueOf(time);
		long longTime = (long) (dbl * 1000);
		
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date = sdf.format(new java.util.Date (longTime));
		
		return date;
	}
	
	public int[] parseDateAndTime(String time) {
		StringTokenizer str = new StringTokenizer(time, " ");
		String elem = "";
		while(str.hasMoreElements()) {
			elem = str.nextElement().toString();
		}
		StringTokenizer tok = new StringTokenizer(elem, ":");
		int[] actualTime = new int[3];
		int counter = 0;
		while(tok.hasMoreElements()) {
			actualTime[counter] = Integer.
						valueOf(tok.nextElement().toString());
			counter++;
		}
		return actualTime;
	}
	
	public int getTimeInSeconds(int[] actualTime) {
		int seconds = 0;
		for(int i = 0; i < actualTime.length; i++) {
			 if(i == 0) {
				  seconds += actualTime[i] * SECONDS_IN_ONE_HOUR;
			 } else if (i == 1) {
				 seconds += actualTime[i] * MINS_IN_ONE_HOUR;
			 } else 
				 seconds += actualTime[i];
		}
		return seconds;
	}
	
	public String cleanStr(String orgWord) {
		orgWord = orgWord.replaceAll("\\p{Cntrl}", "");
		orgWord = orgWord.replace(" ", "");
		return orgWord;
	}
	
	public String stripStr(String orgWord) {
		String[] words = orgWord.split(":");
		assert(words.length == 2);
		return cleanStr(words[1]);
	}
	
	public GenerateScenarios(String propFile) {
		config = new Config(propFile);
		inputPath = config.getPropertyString("data.folder");

	}
	
	public static void main(String[] args) {
		String propFile;
		if(args.length == 1) {
			propFile = args[0];
			assert(propFile.endsWith("properties"));
		} else {
			propFile = "global.properties";
		}
		GenerateScenarios gr = new GenerateScenarios(propFile);
		try {
			gr.parse();
			gr.parseInputFile();
			gr.parseControllersFile();
			gr.generateBrahmsFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
