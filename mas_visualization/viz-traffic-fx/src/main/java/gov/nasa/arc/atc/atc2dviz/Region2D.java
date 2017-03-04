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

import gov.nasa.arc.atc.geography.Region;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.beans.PropertyChangeEvent;

import static javafx.application.Platform.runLater;

/**
 * @author ahamon
 */
public class Region2D implements Element2D {

    private static final Color DEFAULT_COLOR = new Color(0.14, 0.14, 0.14, 1);
    private static final double DEFAULT_STROKE_WIDTH = 1;
    private static final Color DEFAULT_STROKE_COLOR = new Color(0.20, 0.20, 0.20, 1);
    private static final double DEFAULT_OPACITY = 1;

    private final Region region;
    private final MercatorAttributes mercatorAttributes;
    private final Color color;

    private final Polygon mainNode;

    /**
     * Creates a 2D representation of the region r
     *
     * @param r           the region to represent
     * @param mAttributes the mercator attributes used for the map projection
     */
    public Region2D(Region r, MercatorAttributes mAttributes) {
        region = r;
        mercatorAttributes = mAttributes;
        mainNode = new Polygon();
        mainNode.setOpacity(DEFAULT_OPACITY);
        mainNode.setMouseTransparent(true);
        color = DEFAULT_COLOR;
        runLater(Region2D.this::setRegionShape);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // nothing to do yet

    }

    @Override
    public Node getNode() {
        return mainNode;
    }

    @Override
    public void updatePosition(boolean onScreenResize) {
        if (onScreenResize) {
            setRegionShape();
        }
    }

    @Override
    public void setVisible(boolean visibility) {
        mainNode.setVisible(visibility);
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        throw new UnsupportedOperationException();
    }

    private void setRegionShape() {
        mainNode.getPoints().clear();
        region.getVertices().forEach(p2D -> {
            final Point2D screenP = mercatorAttributes.getXYPosition(p2D.getLatitude(), p2D.getLongitude());
            mainNode.getPoints().add(screenP.getX());
            mainNode.getPoints().add(screenP.getY());
        });
        mainNode.setFill(color);
        mainNode.setStroke(DEFAULT_STROKE_COLOR);
        mainNode.setStrokeWidth(DEFAULT_STROKE_WIDTH);
    }

}
