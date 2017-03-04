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

package gov.nasa.arc.atc.metrics;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 * @author Kelsey
 */
public final class MetricsUtil {

	public static final String DELAY = "Delay Information";
	public static final String METER = "Meter Information";
	public static final String CUMULATIVE = " (cumulative)";
	public static final String NON_CUMULATIVE = " (non-cumulative)";
	public static final String DEPARTURE = "Multiple Departure Information";
	public static final String WORKLOAD = "Workload Information";
	public static final String SIMPLE_METRICS_BOX = "BoxMetrics.fxml";
	public static final String METRIC_CHART = "MetricChart.fxml";
	public static final String ALL_CHARTS = "AllCharts.fxml";
    public static final String ARGMAX_POPUP = "argmaxPopUp.fxml";
	public static final String AIRPLANE = "Airplane";
	public static final String TEMP = "fill in";
	public static final String TIME = "Time";
	public static final String CONTROLLER = "Controller";
	public static final String SECTOR = "Sector";
	public static final String WAYPOINT = "Waypoint";
	public static final String PLANE_AGENT_PREFIX = "plane_";
	public static final String SLOT_AGENT_PREFIX = "slot_";
	public static final String CONTROLLER_AGENT_PREFIX = "ZNY_";
	// DONT CHANGE must match tableview properties
	public static final String COLUMN_ID = "columnID";
	public static final String COLUMN_VIEW = "view";
	public static final String COLUMN_SORT_1 = "sort1";
	public static final String COLUMN_SORT_2 = "sort2";
	public static final String COLUMN_SORT_3 = "sort3";
	public static final String COLUMN_SORT_4 = "sort4";
	
//	public static Map<String,String> controllerToSectorMap = new HashMap<>();



	public static final int RGB_MAX = 255;
    public static final Color[] COLORS_OPTIONS = {Color.INDIANRED, Color.LIGHTCORAL, Color.LIGHTSALMON, Color.YELLOWGREEN, Color.DARKSEAGREEN, Color.CORNFLOWERBLUE, Color.DARKSLATEBLUE,
        Color.MEDIUMPURPLE, Color.LIGHTPINK, Color.MEDIUMVIOLETRED, Color.RED, Color.ORANGE, Color.GREENYELLOW, Color.FORESTGREEN, Color.DODGERBLUE, Color.DARKSLATEBLUE, Color.HOTPINK};
    public static final int COLOR_COUNT = COLORS_OPTIONS.length;

    private static final Map<String, String> AIRLINE_CODE_MAP = new HashMap<>();
	
	private MetricsUtil(){
		init();
	}

	private void init(){
	}
	
	public static String getAirline(String plane){
        if (AIRLINE_CODE_MAP.isEmpty()) {
			fillAirlineMap();
		}
		String airlineCode = plane.replace("plane_", "").substring(0, 3);
        String airline = AIRLINE_CODE_MAP.get(airlineCode);
		if (airline == null){
			return airlineCode;
		}else{
			return airline;
		}
	}
	
	private static void fillAirlineMap(){
        AIRLINE_CODE_MAP.put("SWA", "SouthWest");
        AIRLINE_CODE_MAP.put("JBU", "JetBlue");
        AIRLINE_CODE_MAP.put("TCF", "ShuttleAmerica");
        AIRLINE_CODE_MAP.put("ASQ", "ExpressJet");
        AIRLINE_CODE_MAP.put("DAL", "Delta");
        AIRLINE_CODE_MAP.put("KLM", "Royal Dutch");
        AIRLINE_CODE_MAP.put("EGF", "American Eagle");
        AIRLINE_CODE_MAP.put("AWI", "Air Wisconsin");
        AIRLINE_CODE_MAP.put("AAL", "American");
        AIRLINE_CODE_MAP.put("AWE", "American West");
        AIRLINE_CODE_MAP.put("UAL", "United");
        AIRLINE_CODE_MAP.put("CPZ", "Compass");
        AIRLINE_CODE_MAP.put("GJS", "GoJet");
        AIRLINE_CODE_MAP.put("CHQ", "Chautauqua");
	}
	
	public static String toRGBCode(final Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * RGB_MAX), (int) (color.getGreen() * RGB_MAX),
				(int) (color.getBlue() * RGB_MAX));
	}
	
//	public static void setControllerToSectorMap(Map<String,String> map){
//		controllerToSectorMap = map;
//	}
//
//	public static String findSector(String controller){
//		throw new UnsupportedOperationException();
////		return controllerToSectorMap.get(controller);
//	}
	

	
}
