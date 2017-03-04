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

package gov.nasa.arc.atc.utils;

import gov.nasa.arc.atc.core.Coordinates;
import javafx.geometry.Point2D;

import java.util.List;

/**
 * @author ahamon
 */
public class MercatorAttributes {


    private static final double quarterPI = Math.PI / 4.0;

    private static final double MIN_PADDING_BOTH_SIDES = 30.0;

    private double widthPadding;
    private double heightPadding;

    // default value at the beginning
    private double mapWidth = 800.0;
    // default value at the beginning
    private double mapHeight = 600.0 * 9.0 / 16.;

    private double mapWidthRatio;
    private double mapHeightRatio;
    private double globalRatio;
    private double zoomRatio;

    private double minX;
    private double minY;
    private double maxX;
    private double maxY;


    public MercatorAttributes() {
        resetAttributes();
    }

    public void resetAttributes() {
        mapWidthRatio = 1.0;
        mapHeightRatio = 1.0;
        globalRatio = 1.0;
        zoomRatio = 1.0;
        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxX = Double.MIN_VALUE;
        maxY = Double.MIN_VALUE;
    }

    public void setMapWidth(double newWidth) {
        mapWidth = newWidth;
        updateOtherAttributes();
    }

    public void setMapHeight(double newHeight) {
        mapHeight = newHeight;
        updateOtherAttributes();
    }

    public void processCoordinates(List<Coordinates> list) {
        list.forEach(this::processMin);
        list.forEach(this::processMax);
        updateOtherAttributes();
    }

    public void setZoomRatio(double newZoomRatio) {
        zoomRatio = newZoomRatio;
        updateOtherAttributes();
    }

    public double getMapWidth() {
        return mapWidth;
    }

    public double getMapHeight() {
        return mapHeight;
    }

    public double getZoomRatio() {
        return zoomRatio;
    }

    private void processMin(Coordinates c) {
        double x = c.getLongitude() * Math.PI / 180.0;
        double y = Math.log(Math.tan(quarterPI + 0.5 * c.getLatitude() * Math.PI / 180.0));
        // X
        if (minX >= Double.MAX_VALUE) {
            minX = x;
        } else {
            minX = Math.min(x, minX);
        }
        //Y
        if (minY >= Double.MAX_VALUE) {
            minY = y;
        } else {
            minY = Math.min(y, minY);
        }
    }

    private void processMax(Coordinates c) {
        double x = c.getLongitude() * Math.PI / 180.0 - minX;
        double y = Math.log(Math.tan(quarterPI + 0.5 * c.getLatitude() * Math.PI / 180.0)) - minY;
        // X
        if (maxX <= Double.MIN_VALUE) {
            maxX = x;
        } else {
            maxX = Math.max(x, maxX);
        }
        // Y
        if (maxY <= Double.MIN_VALUE) {
            maxY = y;
        } else {
            maxY = Math.max(y, maxY);
        }
    }

    private void updateOtherAttributes() {
        mapWidthRatio = (mapWidth - MIN_PADDING_BOTH_SIDES) / maxX;
        mapHeightRatio = (mapHeight - MIN_PADDING_BOTH_SIDES) / maxY;
        globalRatio = Math.min(mapWidthRatio, mapHeightRatio);
        widthPadding = (mapWidth - (globalRatio * maxX * zoomRatio)) / 2;
        heightPadding = (mapHeight - (globalRatio * maxY * zoomRatio)) / 2;
    }

    public Point2D getXYPosition(Coordinates pos) {
        return getXYPosition(pos.getLatitude(), pos.getLongitude());
    }

    public Point2D getXYPosition(double lat, double lng) {
        double pX = lng * Math.PI / 180.0 - minX;
        double pY = Math.log(Math.tan(quarterPI + lat * Math.PI / 360.0)) - minY;
        //
        double adjustedX = widthPadding + (pX * globalRatio * zoomRatio);
        // need to invert the Y since 0,0 starts at top left
        double adjustedY = mapHeight - heightPadding - (pY * globalRatio * zoomRatio);
        //
        return new Point2D(adjustedX, adjustedY);
    }

    public Coordinates getCoordinates(double x, double y) {

        double pX = (x - widthPadding) / (globalRatio * zoomRatio);
        double pY = (mapHeight - heightPadding - y) / (globalRatio * zoomRatio);

        double lat = (Math.atan(Math.exp(pY + minY)) - quarterPI) * 360.0 / Math.PI;
        double lng = (pX + minX) * 180.0 / Math.PI;

        return new Coordinates(lat, lng);
    }


    @Override
    public String toString() {
        return "(" + minX + ", " + minY + ")  (" + maxX + ", " + maxY + ") ratio=" + globalRatio + " zoomRatio=" + zoomRatio;
    }


}
