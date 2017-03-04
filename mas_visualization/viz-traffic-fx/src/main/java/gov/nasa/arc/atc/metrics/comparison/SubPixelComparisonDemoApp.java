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

package gov.nasa.arc.atc.metrics.comparison;


import gov.nasa.arc.atc.AfoUpdate;
import gov.nasa.arc.atc.MainResources;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.core.Coordinates;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.metrics.imagecreation.MeteringPixelCreator;
import gov.nasa.arc.atc.parsing.log.ThreadedAlternateLogParser;
import gov.nasa.arc.atc.parsing.xml.queue.ATCGeographyQueueParser;
import gov.nasa.arc.atc.parsing.xml.queue.XMLMaster;
import gov.nasa.arc.atc.utils.ConsoleUtils;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SubPixelComparisonDemoApp extends Application {

    private static final File GEOGRAPHY_FILE = new File(MainResources.class.getResource("Geography_Week2.xml").getPath());

    private static final String SIM_DUR = "3000";

    private static final File CONFIG_FILE = new File(MainResources.class.getResource("config.properties").getPath());
    private static final File SCENARIO_1_FILE = new File("/Comparison/log_conf1_t" + SIM_DUR + ".log");
    private static final File SCENARIO_2_FILE = new File("/Comparison/log_conf2_t" + SIM_DUR + ".log");
    private static final File SCENARIO_3_FILE = new File("/Comparison/log_conf3-1200_t" + SIM_DUR + ".log");
    private static final File SCENARIO_4_FILE = new File("/Comparison/log_conf3-10_t" + SIM_DUR + ".log");

    private static final int SIM_DURATION = 2999;

    private static final double IMAGE_RATIO = 1;

    public static final double IMAGE_WIDTH = 1000;
    public static final double IMAGE_HEIGHT = IMAGE_WIDTH * IMAGE_RATIO;


    public static final double PIXEL_SIZE = 15;
    public static final double PIXEL_RADIUS = PIXEL_SIZE / 2.0;


    private static final int WIDTH = (int) (IMAGE_WIDTH / PIXEL_SIZE);
    private static final int HEIGHT = (int) (IMAGE_HEIGHT / PIXEL_SIZE);


    public static final double MIN_PERCENTAGE = 0.25;

    private static final boolean WITH_GRID = false;
    private static final boolean WITH_ALL_UPDATES = false;

    private ThreadedAlternateLogParser threadedAlternateLogParser1;
    private ThreadedAlternateLogParser threadedAlternateLogParser2;
    private ThreadedAlternateLogParser threadedAlternateLogParser3;
    private ThreadedAlternateLogParser threadedAlternateLogParser4;

    private ATCGeography geography;
    private MercatorAttributes mercatorAttributes;

    private MeteringPixelCreator meteringPixelCreator1;
    private MeteringPixelCreator meteringPixelCreator2;
    private MeteringPixelCreator meteringPixelCreator3;
    private MeteringPixelCreator meteringPixelCreator4;


    private double[][] matrix1;
    private double[][] matrix2;
    private double[][] matrix3;
    private double[][] matrix4;

    private double max;
    private double max1;
    private double max2;
    private double max3;
    private double max4;

    private Canvas canvas;
    private GraphicsContext gc;

    private Color color1;
    private Color color2;
    private Color color3;
    private Color color4;

    private double valueRatio;

    private Scene scene;
    private TabPane tabPane;
    // 2D
    private AnchorPane pane2D;
    // 3D
    private AnchorPane pane3D;
    private SubPixelComparisonDemo3D subPixelComparisonDemo3D;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ConsoleUtils.setLoggingLevel(Level.SEVERE);
        tabPane = new TabPane();
        tabPane.setPrefSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        createTab2D();
        createTab3D();
        scene = new Scene(tabPane, IMAGE_WIDTH, IMAGE_HEIGHT + 40, Color.BLACK);

        canvas = new Canvas();
        canvas.setWidth(WIDTH * PIXEL_SIZE);
        canvas.setHeight(HEIGHT * PIXEL_SIZE);
        gc = canvas.getGraphicsContext2D();

        pane2D.getChildren().add(canvas);

        primaryStage.setScene(scene);
        primaryStage.show();
        //
        generate();
    }

    private void createTab2D() {
        pane2D = new AnchorPane();
        Tab tab2d = new Tab("2D");
        tab2d.setContent(pane2D);
        tab2d.setClosable(false);
        tabPane.getTabs().add(tab2d);
    }

    private void createTab3D() {
        pane3D = new AnchorPane();
        Tab tab3d = new Tab("3D");
        tab3d.setContent(pane3D);
        tab3d.setClosable(false);
        tabPane.getTabs().add(tab3d);
        //
        subPixelComparisonDemo3D = new SubPixelComparisonDemo3D();
        pane3D.getChildren().add(subPixelComparisonDemo3D.getNode());
    }


    ///////


    public void generate() {

        //for 3
//        color1 = Color.RED;
//        color2 = Color.GREEN;
//        color3 = Color.BLUE;

        // for 4
        color1 = Color.YELLOW;
        color2 = Color.DEEPSKYBLUE;
        color3 = Color.PINK;
        color4 = Color.CHARTREUSE;


        System.err.println(" Starting parsing AtcGeography");
        geography = (ATCGeography) XMLMaster.requestParsing(GEOGRAPHY_FILE, new ATCGeographyQueueParser(), this);
        SimulationManager.setATCGeography(geography);

        System.err.println("Starting scenario 1 parsing ... ");
        threadedAlternateLogParser1 = new ThreadedAlternateLogParser(SCENARIO_1_FILE, CONFIG_FILE, this::handleSimu1Parsed);
        threadedAlternateLogParser1.start();
    }

    private void handleSimu1Parsed(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                System.err.println("> !! Scenario 1 parsing FAILED!");
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                System.err.println("> Scenario 1 parsed!");
                //
                System.err.println("Starting scenario 2 parsing ... ");
                threadedAlternateLogParser2 = new ThreadedAlternateLogParser(SCENARIO_2_FILE, CONFIG_FILE, this::handleSimu2Parsed);
                threadedAlternateLogParser2.start();
                break;
            default:
                break;
        }
    }

    private void handleSimu2Parsed(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                System.err.println("> !! Scenario 2 parsing FAILED!");
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                System.err.println("> Scenario 2 parsed!");
                System.err.println("Starting scenario 3 parsing ... ");
                threadedAlternateLogParser3 = new ThreadedAlternateLogParser(SCENARIO_3_FILE, CONFIG_FILE, this::handleSimu3Parsed);
                threadedAlternateLogParser3.start();
                break;
            default:
                break;
        }
    }

    private void handleSimu3Parsed(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                System.err.println("> !! Scenario 3 parsing FAILED!");
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                System.err.println("> Scenario 3 parsed!");
                System.err.println("Starting scenario 4 parsing ... ");
                threadedAlternateLogParser4 = new ThreadedAlternateLogParser(SCENARIO_4_FILE, CONFIG_FILE, this::handleSimu4Parsed);
                threadedAlternateLogParser4.start();
                break;
            default:
                break;
        }
    }

    private void handleSimu4Parsed(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ThreadedAlternateLogParser.OPENING_FILE:
                break;
            case ThreadedAlternateLogParser.READING_LINE_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.ERROR_PARSING:
                System.err.println("> !! Scenario 4 parsing FAILED!");
                break;
            case ThreadedAlternateLogParser.CREATING_AGENT_PERCENTAGE:
                break;
            case ThreadedAlternateLogParser.DATA_MODEL_PARSED:
                System.err.println("> Scenario 4 parsed!");
                System.err.println(" PARSING COMPLETED");
                calculatePicture();
                break;
            default:
                break;
        }
    }

    private void calculatePicture() {
        System.err.println("\nCalculating mercator attributes...");
        mercatorAttributes = new MercatorAttributes();
        mercatorAttributes.setMapWidth(WIDTH);
        mercatorAttributes.setMapHeight(HEIGHT);
        //mercatorAttributes.processCoordinates(geography.getWaypoints().stream().map(wpt -> new Coordinates(wpt.getLatitude(), wpt.getLongitude())).collect(Collectors.toList()));
        List<AfoUpdate> updates = new LinkedList<>();
        threadedAlternateLogParser1.getDataModel().getAllDataUpdates().values().forEach(map -> updates.addAll(map.values()));
        if (WITH_ALL_UPDATES) {
            mercatorAttributes.processCoordinates(updates.stream().map(wpt -> new Coordinates(wpt.getPosition().getLatitude(), wpt.getPosition().getLongitude())).collect(Collectors.toList()));
        } else {
            // bounds 1
            List<Coordinates> bounds1 = new LinkedList<>();
            bounds1.add(new Coordinates(38.266, -81.565));
            bounds1.add(new Coordinates(43.627, -82.035));
            bounds1.add(new Coordinates(43.58, -72.422));
            bounds1.add(new Coordinates(38.244, -72.403));
            // bounds 2

            List<Coordinates> bounds2 = new LinkedList<>();
            bounds2.add(new Coordinates(42.292, -78.827));
            bounds2.add(new Coordinates(42.211, -71.176));
            bounds2.add(new Coordinates(38.028, -71.192));
            bounds2.add(new Coordinates(37.997, -78.436));
            // bounds 3
            List<Coordinates> bounds3 = new LinkedList<>();
            bounds3.add(new Coordinates(41.82006394706533 , -76.73005666017285));
            bounds3.add(new Coordinates(39.54200210251736 , -76.7402068673739));
            bounds3.add(new Coordinates(39.54200210251736 , -72.70549950495854));
            bounds3.add(new Coordinates(41.82006394706533 , -72.71564971215959));

            mercatorAttributes.processCoordinates(bounds3);
        }

        System.err.println(" -> " + mercatorAttributes);
        System.err.println("... done");
        //
        meteringPixelCreator1 = new MeteringPixelCreator();
        meteringPixelCreator1.setDataModel(threadedAlternateLogParser1.getDataModel());
        meteringPixelCreator1.setProjection(mercatorAttributes);
        //
        meteringPixelCreator2 = new MeteringPixelCreator();
        meteringPixelCreator2.setDataModel(threadedAlternateLogParser2.getDataModel());
        meteringPixelCreator2.setProjection(mercatorAttributes);
        //
        meteringPixelCreator3 = new MeteringPixelCreator();
        meteringPixelCreator3.setDataModel(threadedAlternateLogParser3.getDataModel());
        meteringPixelCreator3.setProjection(mercatorAttributes);
        //
        meteringPixelCreator4 = new MeteringPixelCreator();
        meteringPixelCreator4.setDataModel(threadedAlternateLogParser4.getDataModel());
        meteringPixelCreator4.setProjection(mercatorAttributes);

        // temp
        System.err.println(" processing data model 1 ...");
        matrix1 = meteringPixelCreator1.calculate(0, SIM_DURATION);
        synchronized (matrix1) {
            for (int i = 0; i < mercatorAttributes.getMapWidth(); i++) {
                for (int j = 0; j < mercatorAttributes.getMapHeight(); j++) {
                    max1 = Math.max(max1, matrix1[i][j]);
                }
            }
        }
        System.err.println("... 1 is done");
        //
        System.err.println(" processing data model 2 ...");
        matrix2 = meteringPixelCreator2.calculate(0, SIM_DURATION);
        synchronized (matrix2) {
            for (int i = 0; i < mercatorAttributes.getMapWidth(); i++) {
                for (int j = 0; j < mercatorAttributes.getMapHeight(); j++) {
                    max2 = Math.max(max2, matrix2[i][j]);
                }
            }
        }
        System.err.println("... 2 is done");
        //
        System.err.println(" processing data model 3 ...");
        matrix3 = meteringPixelCreator3.calculate(0, SIM_DURATION);
        synchronized (matrix3) {
            for (int i = 0; i < mercatorAttributes.getMapWidth(); i++) {
                for (int j = 0; j < mercatorAttributes.getMapHeight(); j++) {
                    max3 = Math.max(max3, matrix3[i][j]);
                }
            }
        }
        System.err.println("... 3 is done");
        //
        System.err.println(" processing data model 4 ...");
        matrix4 = meteringPixelCreator4.calculate(0, SIM_DURATION);
        synchronized (matrix4) {
            for (int i = 0; i < mercatorAttributes.getMapWidth(); i++) {
                for (int j = 0; j < mercatorAttributes.getMapHeight(); j++) {
                    max4 = Math.max(max4, matrix4[i][j]);
                }
            }
        }
        System.err.println("... 3 is done");

        System.err.println("- max1=" + max1);
        System.err.println("- max2=" + max2);
        System.err.println("- max3=" + max3);
        System.err.println("- max4=" + max4);

        max = Math.max(Math.max(max1, max2), Math.max(max3, max4));
        System.err.println("--> max=" + max);

        valueRatio = 1.0 / max;
        System.err.println("--> valueRatio=" + valueRatio);

        System.err.println(" filling canvas...");
        representData2D();
        System.err.println("... done");

        System.err.println(" filling 3D...");
        representData3D();
        System.err.println("... done");


        //Platform.runLater(this::saveImage);
    }

    private void representData2D() {

        gc.setFill(Color.BLACK);
        gc.beginPath();
        gc.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        gc.closePath();


        // humm
        if (WITH_GRID) {
            //not optimized
            for (int i = 0; i < mercatorAttributes.getMapWidth(); i++) {
                for (int j = 0; j < mercatorAttributes.getMapHeight(); j++) {
                    double pixelX = i * PIXEL_SIZE + PIXEL_RADIUS;
                    double pixelY = j * PIXEL_SIZE + PIXEL_RADIUS;
                    gc.beginPath();
                    gc.setStroke(Color.DARKGREY);
                    gc.setLineWidth(0.5);
                    gc.rect(pixelX - PIXEL_RADIUS, pixelY - PIXEL_RADIUS, PIXEL_SIZE, PIXEL_SIZE);
                    gc.stroke();
                    gc.closePath();
                }
            }
        }


        gc.setLineWidth(1);

        for (int i = 0; i < mercatorAttributes.getMapWidth(); i++) {
            for (int j = 0; j < mercatorAttributes.getMapHeight(); j++) {

                // create sub pixel
                double pixelX = i * PIXEL_SIZE + PIXEL_RADIUS;
                double pixelY = j * PIXEL_SIZE + PIXEL_RADIUS;

                //// creating data1 sub pixel
                double value1 = matrix1[i][j];
                if (value1 > 0) {
                    double data1Radius = (MIN_PERCENTAGE + (1 - MIN_PERCENTAGE) * value1 * valueRatio) * PIXEL_RADIUS;
                    gc.setFill(color1);
                    gc.beginPath();
                    gc.fillRect(pixelX - data1Radius, pixelY - data1Radius, data1Radius, data1Radius);
                    gc.closePath();
                }
                //// creating data2 sub pixel
                double value2 = matrix2[i][j];
                if (value2 > 0) {
                    double data2Radius = (MIN_PERCENTAGE + (1 - MIN_PERCENTAGE) * matrix2[i][j] * valueRatio) * PIXEL_RADIUS;
                    gc.setFill(color2);
                    gc.beginPath();
                    gc.fillRect(pixelX, pixelY - data2Radius, data2Radius, data2Radius);
                    gc.closePath();
                }
                //// creating data3 sub pixel
                double value3 = matrix3[i][j];
                if (value3 > 0) {
                    double data3Radius = (MIN_PERCENTAGE + (1 - MIN_PERCENTAGE) * matrix3[i][j] * valueRatio) * PIXEL_RADIUS;
                    gc.setFill(color3);
                    gc.beginPath();
                    gc.fillRect(pixelX, pixelY, data3Radius, data3Radius);
                    gc.closePath();
                }
                //// creating data4 sub pixel
                double value4 = matrix4[i][j];
                if (value4 > 0) {
                    double data4Radius = (MIN_PERCENTAGE + (1 - MIN_PERCENTAGE) * matrix4[i][j] * valueRatio) * PIXEL_RADIUS;
                    gc.setFill(color4);
                    gc.beginPath();
                    gc.fillRect(pixelX - data4Radius, pixelY, data4Radius, data4Radius);
                    gc.closePath();
                }
            }
        }
    }


    private void representData3D() {
        subPixelComparisonDemo3D.setMercatorAttributes(mercatorAttributes);
        subPixelComparisonDemo3D.displayData(matrix1, max, color1, SubPixelPos.TOP_LEFT);
        subPixelComparisonDemo3D.displayData(matrix2, max, color2, SubPixelPos.TOP_RIGHT);
        subPixelComparisonDemo3D.displayData(matrix3, max, color3, SubPixelPos.BOTTOM_RIGHT);
        subPixelComparisonDemo3D.displayData(matrix4, max, color4, SubPixelPos.BOTTOM_LEFT);
    }

    private void saveImage() {
        File f = new File("/Comparison/output_" + System.currentTimeMillis() + ".png");
        WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), null);
        BufferedImage bufferedImage = new BufferedImage((int) (WIDTH * PIXEL_SIZE), (int) (HEIGHT * PIXEL_SIZE), BufferedImage.TYPE_INT_ARGB);
        BufferedImage image = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, bufferedImage);
        try {
            Graphics2D gd = (Graphics2D) image.getGraphics();
            gd.translate(canvas.getWidth(), canvas.getHeight());
            ImageIO.write(image, "png", f);
        } catch (IOException ex) {
            System.err.println("Exception while saving: {0}" + ex);
        }
    }

}
