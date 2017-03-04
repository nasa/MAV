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

import gov.nasa.arc.atc.WorldGeography;
import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.geography.GeographyElementBounds;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javafx.geometry.Point2D;

/**
 * @author ahamon
 */
public final class MapDrawingUtils {

    public static final String NEW_WORLD_GEOGRAPHY = "newWorldGeography";

    public static final String NEW_VIEW_POINT = "newViewPoint";

    public static final String NEW_ZOOM_LEVEL = "newZoomLevel";

    public static final double DEFAULT_ZOOM_STEP = 1.5;

    /**
     * percentage of the max min latitude added when zoom on new view point
     */
    public static final double ZOOM_MARGIN = 0.1;

    private static final WorldGeography WORLD_GEOGRAPHY = new WorldGeography();

    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(new MapDrawingUtils());


    private static double scale = 1.0;
    private static double xOffset = 0.0;
    private static double yOffset = 0.0;

    private static double minLongitude;
    private static double maxTanLatitude;
    // ....
    private static double sceneWidth;
    private static double sceneHeight;
    private static double scaleFactor = 1;

    private static double latitudeOffset = 0;
    private static double longitudeOffset = 0;

    private MapDrawingUtils() {
        // private utility constructor
    }

    // hum...
    public static WorldGeography getWorldGeography() {
        return WORLD_GEOGRAPHY;
    }

    /**
     * Allows to listen to changed of
     *
     * @param listener the change listener
     */
    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
        // hum...
        WORLD_GEOGRAPHY.addPropertyChangeListener(listener);
    }

    /**
     * @param listener listener to remove
     */
    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.removePropertyChangeListener(listener);
        // hum...
        WORLD_GEOGRAPHY.removePropertyChangeListener(listener);
    }

    public static void setATCGeography(ATCGeography atcGeography) {
        WORLD_GEOGRAPHY.setATCGeography(atcGeography);
    }

    public static void updateOnGeographyChange() {
        latitudeOffset = WORLD_GEOGRAPHY.getSectorLatOffset();
        longitudeOffset = WORLD_GEOGRAPHY.getSectorLongOffset();
        calculateViewingAttributes(sceneWidth, sceneHeight, 1);
    }

    public static double getLatitudeOffset() {
        return latitudeOffset;
    }

    public static double getLongitudeOffset() {
        return longitudeOffset;
    }

    // hum...
    public static double getSceneWidth() {
        return sceneWidth;
    }

    public static double getSceneHeight() {
        return sceneHeight;
    }

    public static void calculateViewingAttributes(double newSceneWidth, double newSceneHeight, double factor) {
        // new code
        sceneWidth = newSceneWidth;
        sceneHeight = newSceneHeight;
        scaleFactor = scaleFactor * factor;
        //
        double scaleX = sceneWidth / longitudeOffset;
        double scaleY = sceneHeight / latitudeOffset;
        if (Double.isFinite(scaleX) && Double.isFinite(scaleY)) {
            scale = Math.min(scaleX, scaleY) * scaleFactor;
        }
        //
        xOffset = (sceneWidth - (longitudeOffset * scale)) / 2.0;
        yOffset = (sceneHeight - (latitudeOffset * scale)) / 2.0;
        minLongitude = WORLD_GEOGRAPHY.getMinLongitude();
        maxTanLatitude = WORLD_GEOGRAPHY.getMaxTanLatitude();
    }

    public static double getXOffset() {
        return xOffset;
    }

    public static double getYOffset() {
        return yOffset;
    }

    public static Point2D updateViewingAttributes(double longitude, double tanLatitude) {
        double x = (longitude - minLongitude) * scale + xOffset;
        double y = (maxTanLatitude - tanLatitude) * scale + yOffset;
        return new Point2D(x, y);
    }

    /**
     * @param x X pixel coordinate on the screen
     * @param y Y pixel coordinate on the screen
     * @return corresponding coordinates (latitude,longitude)
     */
    public static Coordinates getCoordinatesFromPixelPosition(double x, double y) {
        // longitude => minLongitude + (sceneWidth - x - xOffset) / scale;
        double longitude = minLongitude + (x - xOffset) / scale;
        double tanLatitude = maxTanLatitude - (y - yOffset) / scale;
        return new Coordinates(MathUtils.atanLatitude(tanLatitude), longitude);
    }

    public static void updateMapsViewPoint(GeographyElementBounds bounds) {
        double minLat = bounds.getMaxLatitude();
        double minLong = bounds.getMinLongitude();
        double localScaleX = sceneWidth / bounds.getLatitudeOffset();
        double localScaleY = sceneHeight / bounds.getLongitudeOffset();

        latitudeOffset = bounds.getLatitudeOffset();
        longitudeOffset = bounds.getLongitudeOffset();

        double scaleX = sceneWidth / longitudeOffset;
        double scaleY = sceneHeight / latitudeOffset;
        scaleFactor = 1.0;
        if (Double.isFinite(scaleX) && Double.isFinite(scaleY)) {
            scale = Math.min(scaleX, scaleY) * scaleFactor;
        }
        xOffset = (sceneWidth - (longitudeOffset * scale)) / 2.0;
        yOffset = (sceneHeight - (latitudeOffset * scale)) / 2.0;

        double localScale;
        if (Double.isFinite(localScaleX) && Double.isFinite(localScaleY)) {
            localScale = Math.min(localScaleX, localScaleY); // *scaleFactor
            if (scale > 0) {
                // update scale factor and scale so next zoom is smooth
                scaleFactor = localScale / scale;// scaleFactor*
                scale = localScale;
            }
        }
        //
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(NEW_VIEW_POINT, null, new Coordinates(minLat, minLong));
    }

    public static void discreteZoomIn() {
        calculateViewingAttributes(sceneWidth, sceneHeight, scaleFactor * DEFAULT_ZOOM_STEP);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(NEW_ZOOM_LEVEL, null, null);
        throw new UnsupportedOperationException("Not working code, use World2D requestNewZoom instead");
    }


    public static void discreteZoomOut() {
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(NEW_ZOOM_LEVEL, null, null);
        throw new UnsupportedOperationException("Not working code, use World2D requestNewZoom instead");
    }
}
