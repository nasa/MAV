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

package gov.nasa.arc.atc.metrics.comparison.radar;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.*;

/**
 * Created by ahamon on 9/12/16.
 */
public class SimpleRadarWidget<T> {

    //---- Constants
    private static final Color DEFAULT_BACKGROUND_FILL = Color.WHITESMOKE;
    private static final int MIN_NUMBER_AXES = 3;
    private static final double DEFAULT_RADAR_RADIUS_RATIO = 1.0 / 3.0;
    private static final double DEFAULT_CENTER_RADIUS = 2.0;

    private static final List<Color> COLORS = createColors();
    private static final double DEFAULT_PADDING = 15;
    private static final double DEFAULT_BOX_SPACING = 12.0;

    private static final double DEFAULT_AXIS_VALUE = 3.0;
    private static final double DEFAULT_MIN_AXIS = 0.0;
    private static final double DEFAULT_MAX_AXIS = 5.0;
    private static final double DEFAULT_AXIS_RADIUS_MULTIPLIER = DEFAULT_AXIS_VALUE / (DEFAULT_MAX_AXIS - DEFAULT_MIN_AXIS);

    private static List<Color> createColors() {
        List<Color> colors = new ArrayList<>();
        colors.add(Color.ORANGERED);
        colors.add(Color.LIMEGREEN);
        colors.add(Color.DODGERBLUE);
        colors.add(Color.VIOLET);
        return colors;
    }

    //---- Data part
    private final List<T> dataList;
    private final List<RadarAxisFunction<T>> axesFunctions;

    private int numberOfAxes = MIN_NUMBER_AXES;


    //---- Graphical part
    private final Group mainNode;
    private Rectangle clip;

    private Canvas background;
    private GraphicsContext graphicsContext;
    private Group projectionsGroup;
    private HBox labelBox;
    private Label title;

    //TODO: merge with dataList?
    private final List<RadarProjection> radarProjections;
    private final List<DataSetLabel> dataSetLabels;
    private final Map<RadarAxisFunction<T>, Label> axesLabels;
    private int nextColorIndex = 0;

    private double width = 400;
    private double height = 400;
    private Point2D center = new Point2D(width / 2.0, height / 2.0);
    private double radius = Math.min(width, height) * DEFAULT_RADAR_RADIUS_RATIO;
    private double footerHeight = (height - 2.0 * DEFAULT_RADAR_RADIUS_RATIO * height) / 2.0 - 2.0 * DEFAULT_PADDING;


    public SimpleRadarWidget() {
        dataList = new LinkedList<>();
        axesFunctions = new LinkedList<>();
        radarProjections = new LinkedList<>();
        dataSetLabels = new LinkedList<>();
        axesLabels = new HashMap<>();
        //
        mainNode = new Group();
        createGraphics();
    }

    public Node getNode() {
        return mainNode;
    }

    public void setPosition(double newPosX, double newPosY) {
        mainNode.setTranslateX(newPosX);
        mainNode.setTranslateY(newPosY);
    }

    public void setSize(double newWidth, double newHeight) {
        width = newWidth;
        height = newHeight;
        background.setWidth(width);
        background.setHeight(height);
        center = new Point2D(width / 2.0, height / 2.0);
        // 0.8 is temporary for label not to overlap legend
        radius = Math.min(width, height) * DEFAULT_RADAR_RADIUS_RATIO * 0.8;
        footerHeight = (height - 2.0 * DEFAULT_RADAR_RADIUS_RATIO * height) / 2.0 - 2.0 * DEFAULT_PADDING;
        updateGraphics();
    }

    public void setTitle(String aTitle) {
        title.setText(aTitle);
    }

    //---- data part

    public void addDataSet(T dataSet) {
        dataList.add(dataSet);
        axesFunctions.forEach(f -> f.addDataSet(dataSet));
        RadarProjection radarProjection = new RadarProjection(dataSet);
        radarProjections.add(radarProjection);
        updateRadarProjection(radarProjection);
        //
        DataSetLabel dataSetLabel = new DataSetLabel(dataSet, radarProjection);
        dataSetLabels.add(dataSetLabel);
        labelBox.getChildren().add(dataSetLabel.labelContent);
        Platform.runLater(this::updateLegend);
    }

    public void addAxisFunction(RadarAxisFunction<T> function) {
        axesFunctions.add(function);
        numberOfAxes = Math.max(MIN_NUMBER_AXES, axesFunctions.size());
        Label axisLabel = new Label(function.getFunctionName());
        mainNode.getChildren().add(axisLabel);
        axesLabels.put(function, axisLabel);
        updateGraphics();
    }


    //---- Private methods

    private void createGraphics() {
        clip = new Rectangle(width, height);
        mainNode.setClip(clip);
        //
        background = new Canvas();
        graphicsContext = background.getGraphicsContext2D();
        //
        projectionsGroup = new Group();
        labelBox = new HBox();
        labelBox.setSpacing(DEFAULT_BOX_SPACING);
        double hBoxPadding = 8;
        labelBox.setFillHeight(true);
        labelBox.setAlignment(Pos.CENTER);
        labelBox.setPadding(new Insets(hBoxPadding, hBoxPadding, hBoxPadding, hBoxPadding));
        labelBox.setStyle("-fx-border-color: black;");
        //
        title = new Label();
        title.setFont(new Font(16));
        title.setTextAlignment(TextAlignment.CENTER);
        title.setAlignment(Pos.CENTER);
        title.setTranslateX(DEFAULT_PADDING);
        title.setTranslateY(DEFAULT_PADDING);
        //
        mainNode.getChildren().addAll(background, labelBox, title, projectionsGroup);
        //
        setSize(width, height);
    }

    // update methods

    private void updateGraphics() {
        clip.setWidth(width);
        clip.setHeight(height);
        redrawBackground();
        updateProjections();
        updateLegend();
        updateAxesLabels();
        title.setMinSize(width - 2.0 * DEFAULT_PADDING, footerHeight);
        title.setMaxSize(width - 2.0 * DEFAULT_PADDING, footerHeight);
    }

    private void updateLegend() {
        labelBox.setTranslateX(((width - 2.0 * DEFAULT_PADDING) - labelBox.getWidth()) / 2.0 + DEFAULT_PADDING);
        labelBox.setTranslateY(height - footerHeight - DEFAULT_PADDING);
    }

    private void updateAxesLabels() {
        // not so nice, keeping the map for the moment though
        for (int i = 0; i < numberOfAxes; i++) {
            if (i < axesFunctions.size()) {
                Label axisLabel = axesLabels.get(axesFunctions.get(i));
                double x = center.getX() + radius * 1.1 * Math.cos(i * 2.0 * Math.PI / numberOfAxes - Math.PI / 2.0);
                double y = center.getY() + radius * 1.1 * Math.sin(i * 2.0 * Math.PI / numberOfAxes - Math.PI / 2.0);
                //TODO simplify equation
                if( Math.sin(i * 2.0 * Math.PI / numberOfAxes )< 0){
                    axisLabel.setTranslateX(x - axisLabel.getWidth());
                    axisLabel.setTranslateY(y - axisLabel.getHeight());
                }else{
                    axisLabel.setTranslateX(x);
                    axisLabel.setTranslateY(y);
                }
            }
        }
    }

    private void redrawBackground() {
        // optimization could be made to only calculate polygon edges once
        graphicsContext = background.getGraphicsContext2D();
        //background
        graphicsContext.setFill(DEFAULT_BACKGROUND_FILL);
        graphicsContext.beginPath();
        graphicsContext.fillRect(0, 0, width, height);
        graphicsContext.closePath();
        //-- in dark grey
        graphicsContext.setStroke(Color.DARKGREY);
        graphicsContext.beginPath();
        graphicsContext.setLineDashes(2, 4);
        for (int i = 0; i < numberOfAxes; i++) {
            graphicsContext.moveTo(center.getX(), center.getY());
            graphicsContext.lineTo(center.getX() + radius * Math.cos(i * 2.0 * Math.PI / numberOfAxes - Math.PI / 2.0), center.getY() + radius * Math.sin(i * 2.0 * Math.PI / numberOfAxes - Math.PI / 2.0));
        }
        graphicsContext.stroke();
        graphicsContext.closePath();
        //-- in black
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineDashes(0);
        graphicsContext.beginPath();
        // center
        graphicsContext.fillOval(center.getX() - 4, center.getY() - 4, 8, 8);
        // polygon border -- also black
        graphicsContext.moveTo(center.getX(), center.getY() - radius);
        for (int i = 1; i < numberOfAxes; i++) {
            graphicsContext.lineTo(center.getX() + radius * Math.cos(i * 2.0 * Math.PI / numberOfAxes - Math.PI / 2.0), center.getY() + radius * Math.sin(i * 2.0 * Math.PI / numberOfAxes - Math.PI / 2.0));
        }
        graphicsContext.lineTo(center.getX(), center.getY() - radius);
        //
        graphicsContext.stroke();
        graphicsContext.closePath();


    }


    private void updateProjections() {
        radarProjections.forEach(this::updateRadarProjection);
    }

    private void updateRadarProjection(RadarProjection radarProjection) {
        radarProjection.projection.getPoints().clear();
        RadarAxisFunction<T> function;
        double ratio;
        for (int i = 0; i < numberOfAxes; i++) {
            if (i < axesFunctions.size()) {
                function = axesFunctions.get(i);
                //TODO epsilon
                if (Math.abs(function.getMax().doubleValue() - function.getMin().doubleValue()) <= 0.0000001) {
                    ratio = DEFAULT_AXIS_RADIUS_MULTIPLIER;
                } else {
                    ratio = function.calculate(radarProjection.data).doubleValue() / (function.getMax().doubleValue() - function.getMin().doubleValue());
                }
            } else {
                ratio = DEFAULT_AXIS_RADIUS_MULTIPLIER;
            }
            radarProjection.projection.getPoints().addAll(
                    center.getX() + radius * ratio * Math.cos(i * 2.0 * Math.PI / numberOfAxes - Math.PI / 2.0),
                    center.getY() + radius * ratio * Math.sin(i * 2.0 * Math.PI / numberOfAxes - Math.PI / 2.0));
        }
    }

    //---- Projection class

    private class RadarProjection {

        private static final double DEFAULT_OPACITY = 0.0;

        private final T data;
        private final Polygon projection;
        private Color color;

        RadarProjection(T data) {
            this.data = data;
            projection = new Polygon();
            initProjection();
            projectionsGroup.getChildren().add(projection);
        }

        private void initProjection() {
            color = COLORS.get(nextColorIndex % COLORS.size());
            nextColorIndex++;
            projection.setFill(new Color(color.getRed(), color.getGreen(), color.getBlue(), DEFAULT_OPACITY));
            projection.setStroke(color);
            projection.setStrokeWidth(2.0);
        }

        private Color getColor() {
            return color;
        }


    }

    //---- Data set label class

    private class DataSetLabel {


        private final T data;
        private final HBox labelContent;
        private final Rectangle colorRectangle;
        private final Label dataSetLabel;

        DataSetLabel(T data, RadarProjection projection) {
            this.data = data;
            labelContent = new HBox();
            labelContent.setSpacing(4);
            colorRectangle = new Rectangle(15, 15);
            //
            Color c = projection.color;
            colorRectangle.setFill(new Color(c.getRed(), c.getGreen(), c.getBlue(), RadarProjection.DEFAULT_OPACITY));
            colorRectangle.setStroke(c.darker());
            colorRectangle.setStrokeWidth(2.5);
            //
            dataSetLabel = new Label(data.toString());
            //
            labelContent.getChildren().addAll(colorRectangle, dataSetLabel);
        }

    }


}
