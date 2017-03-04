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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.atc.geography.Airport;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * @author ahamon
 */
public class Airport2D implements Element2D {

    private static final double POLYGON_RADIUS = 4;
    private static final Color POLYGON_COLOR = Color.CHARTREUSE;

    private final Group mainNode;
    private final Airport airport;
    private final List<Runway2D> runways;
    private final MercatorAttributes mercatorAttributes;

    private Polygon shape;

    /**
     * Creates the 2D representation of theAirport
     *
     * @param theAirport  the airport to represent
     * @param mAttributes the mercator attributes used for the map projection
     */
    public Airport2D(Airport theAirport, MercatorAttributes mAttributes) {
        airport = theAirport;
        mainNode = new Group();
        runways = new ArrayList<>();
        mercatorAttributes = mAttributes;
        initAirport2D();
    }

    private void initAirport2D() {
        if (airport.getRunways().isEmpty()) {
            Double[] edges = {0.0, -POLYGON_RADIUS, POLYGON_RADIUS, POLYGON_RADIUS, -POLYGON_RADIUS, POLYGON_RADIUS};
            shape = new Polygon();
            shape.getPoints().setAll(edges);
            shape.setFill(POLYGON_COLOR);
            mainNode.getChildren().add(shape);
        } else {
            airport.getRunways().forEach(runway -> {
                Runway2D r2D2 = new Runway2D(runway, mercatorAttributes);
                runways.add(r2D2);
                mainNode.getChildren().add(r2D2.getNode());

            });
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // noting to do
    }

    @Override
    public Node getNode() {
        return mainNode;
    }

    @Override
    public void updatePosition(boolean onScreenResize) {
        if (shape != null || airport.getRunways().isEmpty()) {
            Point2D pos = mercatorAttributes.getXYPosition(airport.getLatitude(), airport.getLongitude());
            Double[] edges = {
                    pos.getX(), pos.getY() - POLYGON_RADIUS,
                    pos.getX() + POLYGON_RADIUS, pos.getY() + POLYGON_RADIUS,
                    pos.getX() - POLYGON_RADIUS, pos.getY() + POLYGON_RADIUS};
            shape.getPoints().setAll(edges);
        }
        runways.forEach(r2D2 -> r2D2.updatePosition(onScreenResize));
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setVisible(boolean visibility) {
        mainNode.setVisible(visibility);
    }

}
