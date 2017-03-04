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
import gov.nasa.arc.atc.geography.Segment;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.beans.PropertyChangeEvent;

/**
 * @author ahamon
 */
public class Segment2D implements Element2D {

    private static final Color DEFAULT_SEGMENT_STROKE = Color.GREY;
    private static final double DEFAULT_SEGMENT_STROKE_WIDTH = 0.5;

    private final ATCNode start;
    private final ATCNode end;

    private final MercatorAttributes mercatorAttributes;

    private final Line line;

    public Segment2D(Segment aSegment, MercatorAttributes mAttributes) {
        start = aSegment.getFromWaypoint();
        end = aSegment.getToWaypoint();
        //
        mercatorAttributes= mAttributes;
        //
        line = new Line();
        line.setStroke(DEFAULT_SEGMENT_STROKE);
        line.setStrokeWidth(DEFAULT_SEGMENT_STROKE_WIDTH);
        line.getStrokeDashArray().addAll(2d, 5d);
    }


    @Override
    public Node getNode() {
        return line;
    }

    @Override
    public void updatePosition(boolean onScreenResize) {
        Point2D xyStart = mercatorAttributes.getXYPosition(start.getLatitude(), start.getLongitude());
        Point2D xyEnd = mercatorAttributes.getXYPosition(end.getLatitude(), end.getLongitude());
        line.setStartX(xyStart.getX());
        line.setStartY(xyStart.getY());
        line.setEndX(xyEnd.getX());
        line.setEndY(xyEnd.getY());
    }

    @Override
    public void setVisible(boolean visibility) {
        line.setVisible(visibility);
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // nothing to do in this class
    }
}
