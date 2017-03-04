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

import gov.nasa.arc.atc.geography.Runway;
import gov.nasa.arc.atc.utils.Constants;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.beans.PropertyChangeEvent;

/**
 * @author ahamon
 */
public class Runway2D implements Element2D {

    private static final double HALF_PI = Math.PI / 2.0;

    private static final double STROKE_WIDTH = 1;
    private static final double RUNWAY_LENGTH = 8;

    private final Runway runway;
    private final Line line;

    private final MercatorAttributes mercatorAttributes;


    /**
     * @param theRunway  the {@link Runway} to represent
     * @param mAttributes the mercator attributes used for the map projection
     */
    public Runway2D(Runway theRunway, MercatorAttributes mAttributes) {
        runway = theRunway;
        mercatorAttributes = mAttributes;
        line = new Line();
        initRunaway2D();
    }

    private void initRunaway2D() {
        line.setStroke(Color.DODGERBLUE);
        line.setSmooth(true);
        line.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // not responsive to property changes yet

    }

    @Override
    public void setHighlighted(boolean highlighted) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node getNode() {
        return line;
    }

    @Override
    public void updatePosition(boolean onScreenResize) {
        // the onScreenResize parameter is not necessary, calculations are identical
        Point2D xyPosition = mercatorAttributes.getXYPosition(runway.getLatitude(), runway.getLongitude());

        // TO BE OPTIMIZED AND PUT IN A METHOD
        line.setStartX(xyPosition.getX() - RUNWAY_LENGTH * Math.cos(runway.getQFU() * 10 * Constants.DEG_TO_RAD + HALF_PI));
        line.setStartY(xyPosition.getY() - RUNWAY_LENGTH * Math.sin(runway.getQFU() * 10 * Constants.DEG_TO_RAD + HALF_PI));
        line.setEndX(xyPosition.getX() + RUNWAY_LENGTH * Math.cos(runway.getQFU() * 10 * Constants.DEG_TO_RAD + HALF_PI));
        line.setEndY(xyPosition.getY() + RUNWAY_LENGTH * Math.sin(runway.getQFU() * 10 * Constants.DEG_TO_RAD + HALF_PI));

    }

    @Override
    public void setVisible(boolean visibility) {
        line.setVisible(visibility);
    }

}
