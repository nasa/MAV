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

package gov.nasa.arc.atc.viewer.flightplan;

import gov.nasa.arc.atc.FlightPlanUpdate;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.geography.FlightPlan;
import gov.nasa.arc.atc.geography.FlightSegment;
import gov.nasa.arc.atc.utils.AfoUtils;
import gov.nasa.arc.atc.utils.ColorFXFactory;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class FlightPlanViewer extends Pane {

    private static final Logger LOG = Logger.getGlobal();

    private static final double PADDING = 14;
    private static final double TIME_WIDTH = 60;
    private static final double DISTANCE_HEIGHT = 25;
    private static final Color DEFAULT_LINE_COLOR = Color.WHITESMOKE;

    private final Line timeLine;
    private final Line altitudeLine;
    private final Label maxAltitudeLabel;
    private final Label maxDistanceLabel;

    private final Map<FlightSegment, Double> startDistances = new HashMap<>();
    private final Map<FlightSegment, Double> endDistances = new HashMap<>();


    private final Map<FlightSegment, Line> segmentLines = new HashMap<>();

    // hum...
    private FlightPlan flightPlan;
    private FlightPlanUpdate flightPlanUpdate;

    private double maxAltitude;
    private double totalDistance;

    private double width;
    private double height;

    public FlightPlanViewer() {
        super();
        timeLine = new Line();
        altitudeLine = new Line();
        maxAltitudeLabel = new Label();
        maxDistanceLabel = new Label();
        init();
    }

    private void init() {
        setStyle("-fx-background-color: black;");
        timeLine.setStroke(DEFAULT_LINE_COLOR);
        altitudeLine.setStroke(DEFAULT_LINE_COLOR);
        maxAltitudeLabel.setTextFill(DEFAULT_LINE_COLOR);
        maxDistanceLabel.setTextFill(DEFAULT_LINE_COLOR);

        getChildren().addAll(timeLine, altitudeLine, maxAltitudeLabel, maxDistanceLabel);
        //
        widthProperty().addListener((obs, old, newV) -> {
            LOG.log(Level.FINE, "FlightPlanViewer width changed: {0} {1} {2}", new Object[]{obs, old, newV});
            update();
        });
        heightProperty().addListener((obs, old, newV) -> {
            LOG.log(Level.FINE, "FlightPlanViewer height changed: {0} {1} {2}", new Object[]{obs, old, newV});
            update();
        });
    }

    protected void displayFlightPlanUpdate(FlightPlanUpdate flpUpdate) {
        clear();
        flightPlanUpdate = flpUpdate;
        // for each segment calculate distance
        for (FlightSegment s : flightPlanUpdate.getFlightSegments()) {
            createSegmentLine(s);
        }
        concludeDisplay();
    }


    protected void displayFlightPlan(FlightPlan flp) {
        clear();
        flightPlan = flp;
        // for each segment calculate distance
        for (FlightSegment s : flightPlan.getSegments()) {
            createSegmentLine(s);
        }
        concludeDisplay();
    }

    private void update() {
        width = widthProperty().doubleValue();
        height = heightProperty().doubleValue();
        timeLine.setStartX(PADDING + TIME_WIDTH);
        timeLine.setEndX(width - PADDING);
        timeLine.setStartY(height - PADDING - DISTANCE_HEIGHT);
        timeLine.setEndY(height - PADDING - DISTANCE_HEIGHT);
        //
        altitudeLine.setStartX(PADDING + TIME_WIDTH);
        altitudeLine.setEndX(PADDING + TIME_WIDTH);
        altitudeLine.setStartY(PADDING);
        altitudeLine.setEndY(height - PADDING - DISTANCE_HEIGHT);
        //
        maxAltitudeLabel.setTranslateX(PADDING);
        maxAltitudeLabel.setTranslateY(PADDING);
        //
        maxDistanceLabel.setTranslateX(width - PADDING - TIME_WIDTH);
        maxDistanceLabel.setTranslateY(height - PADDING - DISTANCE_HEIGHT + 4);
        //
        if (flightPlan != null) {
            updateFlightPlan();
        }
        if (flightPlanUpdate != null) {
            updateFlightPlanUpdate();
        }
    }

    private void updateFlightPlan() {
        FlightSegment s;
        for (int i = 0; i < flightPlan.getSegments().size(); i++) {
            s = flightPlan.getSegment(i);
            Line line = segmentLines.get(s);
            if (i == 0) {
                if (SimulationManager.getATCGeography() != null && SimulationManager.getATCGeography().isRunwayOrAirport(s.getFromWaypoint().getName())) {
                    line.setStartY(getY(0));
                } else {
                    line.setStartY(getY(s.getdEndAltitude()));
                }
            } else {
                line.setStartY(getY(flightPlan.getSegment(i - 1).getdEndAltitude()));
            }
            line.setStartX(getX(startDistances.get(s)));
            line.setEndX(getX(endDistances.get(s)));
            line.setEndY(getY(s.getdEndAltitude()));
        }
    }

    private void updateFlightPlanUpdate() {
        FlightSegment s;
        for (int i = 0; i < flightPlanUpdate.getFlightSegments().size(); i++) {
            s = flightPlanUpdate.getFlightSegments().get(i);
            Line line = segmentLines.get(s);
            if (i == 0) {
                if (SimulationManager.getATCGeography() != null && SimulationManager.getATCGeography().isRunwayOrAirport(s.getFromWaypoint().getName())) {
                    line.setStartY(getY(0));
                } else {
                    line.setStartY(getY(s.getdEndAltitude()));
                }
            } else {
                line.setStartY(getY(flightPlanUpdate.getFlightSegments().get(i - 1).getdEndAltitude()));
            }
            line.setStartX(getX(startDistances.get(s)));
            line.setEndX(getX(endDistances.get(s)));
            line.setEndY(getY(s.getdEndAltitude()));
        }
    }


    private void clear() {
        getChildren().removeAll(segmentLines.values());
        segmentLines.clear();
        maxAltitude = 0;
        totalDistance = 0;
    }

    private void createSegmentLine(FlightSegment s) {
        startDistances.put(s, totalDistance);
        double distance = AfoUtils.getHorizontalDistance(
                s.getFromWaypoint().getLatitude(), s.getFromWaypoint().getLongitude(),
                s.getToWaypoint().getLatitude(), s.getToWaypoint().getLongitude());
        maxAltitude = Math.max(maxAltitude, s.getdEndAltitude());
        final Line line = new Line();
        line.setStroke(ColorFXFactory.getColorForSpeed(s.getEndSpeed()));
        segmentLines.put(s, line);
        totalDistance += distance;
        endDistances.put(s, totalDistance);
    }

    private void concludeDisplay() {
        maxAltitudeLabel.setText(Integer.toString((int) maxAltitude) + " ft");
        maxDistanceLabel.setText(Integer.toString((int) totalDistance) + " Nm");
        getChildren().addAll(segmentLines.values());
        update();
    }

    private double getY(double altitude) {
        return height - PADDING - DISTANCE_HEIGHT - altitude * (height - 2.0 * PADDING - DISTANCE_HEIGHT) / maxAltitude;
    }

    private double getX(double distance) {
        return PADDING + TIME_WIDTH + distance * (width - 2.0 * PADDING - TIME_WIDTH) / totalDistance;
    }

}
