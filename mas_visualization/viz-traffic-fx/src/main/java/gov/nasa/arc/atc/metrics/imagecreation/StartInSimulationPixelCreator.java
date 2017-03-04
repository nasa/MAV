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

package gov.nasa.arc.atc.metrics.imagecreation;

import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * @author ahamon
 */
public class StartInSimulationPixelCreator extends AbstractPixelCreator {

    private static final Color DEFAULT_COLOR = Color.CHARTREUSE;
    private static final double DEFAULT_CIRCLE_RADIUS = 5;
    private static final double DEFAULT_STROKE_WIDTH = 1.0;


    private double BLUE_HUE = Color.BLUE.getHue();
    private double RED_HUE = Color.RED.getHue();

    private MercatorAttributes mercatorAttributes;

    private double minimumTime = 0.0;
    private double maximumTime = 0.0;

    @Override
    public double[][] calculate(int minTime, int maxTime) {
//        super.calculate(minTime, maxTime);
//        minimumTime = minTime;
//        maximumTime = maxTime;
//        final int width = (int) mercatorAttributes.getMapWidth();
//        final int height = (int) mercatorAttributes.getMapHeight();
//        double[][] infoMatrix = new double[width][height];
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                infoMatrix[j][j] = 0;
//            }
//        }
//        // hum change API
//        // optimize
//
//        synchronized (infoMatrix) {
//            getDataModel().getHandOffs().entrySet().stream().filter(entry -> isCandidate(entry.getKey())).forEach(entry -> entry.getValue().forEach(handOff -> {
//                Point2D screenPosition = mercatorAttributes.getXYPosition(handOff.getAircraftLatitude(), handOff.getAircraftLongitude());
//                final int x = (int) screenPosition.getX();
//                final int y = (int) screenPosition.getY();
//                if (x >= 0 && x < width && y >= 0 && y < height) {
//                    infoMatrix[x][y] = infoMatrix[x][y] + 1;
//                }
//            }));
//        }
//
//        return infoMatrix;
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setProjection(MercatorAttributes attributes) {
        mercatorAttributes = attributes;
    }

    @Override
    public void display(int minTime, int maxTime, Group node) {
        //
        minimumTime = minTime;
        maximumTime = maxTime;
        //

        getDataModel().getArrivingPlanes().forEach(arrival->{
            AfoUpdate startUpdate = arrival.getUpdates().get(arrival.getStartTime());
            if(startUpdate!=null){
                Point2D screenPosition = mercatorAttributes.getXYPosition(startUpdate.getPosition().getLatitude(), startUpdate.getPosition().getLongitude());
                final double x = screenPosition.getX();
                final double y = screenPosition.getY();
                final Circle c = new Circle(x, y, DEFAULT_CIRCLE_RADIUS, null);
                c.setStroke(getColorForValue(startUpdate.getTimeStamp()));
                c.setStrokeWidth(DEFAULT_STROKE_WIDTH);
                node.getChildren().add(c);
            }else{
                System.err.println("Hum... start update == null :: TODO");
            }

        });
        //
    }

    private boolean isCandidate(int time) {
        return time >= minimumTime && time <= maximumTime;
    }

    private Color getColorForValue(double value) {
        if (value < minimumTime || value > maximumTime) {
            return Color.BLACK;
        }
        double hue = BLUE_HUE + (RED_HUE - BLUE_HUE) * (value - minimumTime) / (maximumTime - minimumTime);
        return Color.hsb(hue, 1.0, 1.0);
    }

}
