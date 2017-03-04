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

package gov.nasa.arc.atc.atc2dviz;

import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.Route;
import gov.nasa.arc.atc.geography.Waypoint;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.scene.Group;
import javafx.scene.Node;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ahamon
 */
public class ATCGeography2D {

    private static final boolean WITH_SECTORS = true;

    private final ATCGeography geography;
    private final MercatorAttributes mercatorAttributes;
    //
    private final List<Airport2D> airport2Ds;
    private final List<Segment2D> segment2Ds;
    private final List<Route2D> routes2D;
    private final List<Waypoint2D> waypoint2Ds;
    private final List<Sector2D> sector2Ds;
    //
    private final Group waypointsGroup;
    private final Group airportsGroup;
    private final Group segmentsGroup;
    private final Group routesGroup;
    private final Group sectorsGroup;
    //
    private boolean waypointVisibility = true;
    private boolean airportsVisibility = true;
    private boolean segmentVisibility = true;
    private boolean routesVisibility = true;
    private boolean sectorsVisibility = true;

    /**
     * @param atcGeography the {@link ATCGeography} to represent
     * @param mAttributes  the mercator attributes used for the map projection calculations
     */
    public ATCGeography2D(ATCGeography atcGeography, MercatorAttributes mAttributes) {
        SimulationManager.addPropertyChangeListener(this::handleDataModelChange);
        geography = atcGeography;
        mercatorAttributes = mAttributes;
        airport2Ds = new LinkedList<>();
        segment2Ds = new LinkedList<>();
        routes2D = new LinkedList<>();
        waypoint2Ds = new LinkedList<>();
        sector2Ds = new LinkedList<>();
        //
        waypointsGroup = new Group();
        airportsGroup = new Group();
        segmentsGroup = new Group();
        routesGroup = new Group();
        sectorsGroup = new Group();
        //
        if (WITH_SECTORS) {
            createSector2Ds();
        }
        createAirport2Ds();
        createWaypoint2Ds();
        createSegment2Ds();
        createRoute2Ds();
    }

     Node getWaypoints() {
        return waypointsGroup;
    }

     List<Waypoint2D> getWaypointsList() {
        return waypoint2Ds;
    }

     Node getAirports() {
        return airportsGroup;
    }

     Node getRoutes() {
        return routesGroup;
    }

     Node getSectors() {
        return sectorsGroup;
    }

     Node getSegments() {
        return segmentsGroup;
    }

    private void createSector2Ds() {
        geography.getSectors().forEach(sector -> {
            Sector2D sector2D = new Sector2D(sector,mercatorAttributes);
            sector2Ds.add(sector2D);
            sectorsGroup.getChildren().add(sector2D.getNode());
        });
    }

    private void createAirport2Ds() {
        geography.getAirports().forEach(airport -> {
            Airport2D airport2D = new Airport2D(airport,mercatorAttributes);
            airport2Ds.add(airport2D);
            airportsGroup.getChildren().add(airport2D.getNode());
        });
    }

    private void createWaypoint2Ds() {
        geography.getWaypoints().forEach(wpt -> {
            Waypoint2D wpt2D = new Waypoint2D((Waypoint) wpt,mercatorAttributes);
            waypoint2Ds.add(wpt2D);
            waypointsGroup.getChildren().add(wpt2D.getNode());
        });
    }

    private void createSegment2Ds() {
        geography.getSegments().forEach(segment -> {
            Segment2D segment2D = new Segment2D(segment,mercatorAttributes);
            segment2Ds.add(segment2D);
            segmentsGroup.getChildren().add(segment2D.getNode());
        });
    }

    private void createRoute2Ds() {
        routesGroup.getChildren().clear();
        routes2D.clear();
        List<Route> routes = SimulationManager.getSimulationDataModel() != null ? SimulationManager.getSimulationDataModel().getMainRoutes() : Collections.emptyList();
        routes.forEach(route -> {
            Route2D route2D = new Route2D(route,mercatorAttributes);
            routes2D.add(route2D);
            routesGroup.getChildren().add(route2D.getNode());
        });
    }

    public void setVisibility(final boolean visibility) {
        // run later ?
        if (!visibility) {
            waypointsGroup.setVisible(false);
            airportsGroup.setVisible(false);
            routesGroup.setVisible(false);
            sectorsGroup.setVisible(false);
        } else {
            waypointsGroup.setVisible(waypointVisibility);
            airportsGroup.setVisible(airportsVisibility);
            routesGroup.setVisible(routesVisibility);
        }
    }

    /**
     * @param onScreeResize whether the position update follows a screen resize
     */
    void updatePositionsOnScreen(boolean onScreeResize) {
        airport2Ds.forEach(airport2D -> airport2D.updatePosition(onScreeResize));
        segment2Ds.forEach(segment2D -> segment2D.updatePosition(onScreeResize));
        routes2D.forEach(route2D -> route2D.updatePosition(onScreeResize));
        waypoint2Ds.forEach(waypoint2D -> waypoint2D.updatePosition(onScreeResize));
        sector2Ds.forEach(sector2D -> sector2D.updatePosition(onScreeResize));
    }

    void setWaypointVisibility(boolean visibility) {
        waypointVisibility = visibility;
        waypoint2Ds.forEach(wayPoint2D -> wayPoint2D.setVisible(waypointVisibility));
    }

    void setWaypointNameVisibility(boolean visibility) {
        waypoint2Ds.forEach(wayPoint2D -> wayPoint2D.setNameVisible(visibility));
    }

    void setRouteVisibility(boolean visibility) {
        routesVisibility = visibility;
        routes2D.forEach(route2D -> route2D.setVisible(routesVisibility));
    }

    void setSegmentVisibility(boolean visibility) {
        segmentVisibility = visibility;
        segment2Ds.forEach(segment2D -> segment2D.setVisible(segmentVisibility));
    }

    void setAirportVisibility(boolean visibility) {
        airportsVisibility = visibility;
        airport2Ds.forEach(airport2D -> airport2D.setVisible(airportsVisibility));
    }

    List<Sector2D> getSectorsList() {
        return sector2Ds;
    }


    private void handleDataModelChange(PropertyChangeEvent event) {
        if (SimulationManager.DATA_MODEL_CHANGED.equals(event.getPropertyName())) {
            createRoute2Ds();
        }
    }

}
