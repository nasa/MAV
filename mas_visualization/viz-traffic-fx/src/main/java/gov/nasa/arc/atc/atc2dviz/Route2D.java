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

import gov.nasa.arc.atc.geography.ATCNode;
import gov.nasa.arc.atc.geography.Route;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ahamon
 */
public class Route2D implements Element2D {

    private static final Color DEFAULT_ROUTE_STROKE = Color.GREY;
    private static final double DEFAULT_ROUTE_STROKE_WIDTH = 0.8;

    private final Route route;
    private final MercatorAttributes mercatorAttributes;
    //
    private final Group mainNode;
    private final List<Line> lines;
    //

    /**
     * Creates a 2D representation of the route
     *
     * @param theRoute    the route to represent
     * @param mAttributes the mercator attributes used for the map projection
     */
    public Route2D(Route theRoute, MercatorAttributes mAttributes) {
        mainNode = new Group();
        lines = new ArrayList<>();
        route = theRoute;
        mercatorAttributes = mAttributes;
        initRoute2D();
        updatePosition(true);
    }

    private void initRoute2D() {
        for (int i = 0; i < route.getRoute().size() - 1; i++) {
            final Line line = new Line();
            line.setStroke(DEFAULT_ROUTE_STROKE);
            line.setSmooth(true);
            line.setStrokeWidth(DEFAULT_ROUTE_STROKE_WIDTH);
            lines.add(line);
            mainNode.getChildren().add(line);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // nothing to do
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node getNode() {
        return mainNode;
    }

    @Override
    public final void updatePosition(boolean onScreenResize) {
        for (int i = 0; i < route.getRoute().size() - 1; i++) {
            ATCNode start;
            ATCNode end;
            Point2D xyStart;
            Point2D xyEnd;
            start = route.getRoute().get(i);
            end = route.getRoute().get(i + 1);
            Line line = lines.get(i);
            xyStart = mercatorAttributes.getXYPosition(start.getLatitude(), start.getLongitude());
            xyEnd = mercatorAttributes.getXYPosition(end.getLatitude(), end.getLongitude());
            line.setStartX(xyStart.getX());
            line.setStartY(xyStart.getY());
            line.setEndX(xyEnd.getX());
            line.setEndY(xyEnd.getY());
        }
    }

    @Override
    public void setVisible(boolean visibility) {
        mainNode.setVisible(visibility);
    }

}
