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


import gov.nasa.arc.atc.MainResources;
import gov.nasa.arc.atc.SimulationManager;
import gov.nasa.arc.atc.geography.ATCGeography;
import gov.nasa.arc.atc.metrics.SimulationCalculations;
import gov.nasa.arc.atc.parsing.log.ThreadedAlternateLogParser;
import gov.nasa.arc.atc.parsing.xml.queue.ATCGeographyQueueParser;
import gov.nasa.arc.atc.parsing.xml.queue.XMLMaster;
import gov.nasa.arc.atc.utils.ConsoleUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
import java.util.logging.Level;

public class DelayDistributionComparisonDemoApp extends Application {

    private static final File GEOGRAPHY_FILE = new File(MainResources.class.getResource("Geography_Week2.xml").getPath());

    private static final String SIM_DUR = "300";

    private static final File CONFIG_FILE = new File(MainResources.class.getResource("config.properties").getPath());
    private static final File SCENARIO_1_FILE = new File("/Desktop/Comparison/log_conf1_t" + SIM_DUR + ".log");
    private static final File SCENARIO_2_FILE = new File("/Desktop/Comparison/log_conf2_t" + SIM_DUR + ".log");
    private static final File SCENARIO_3_FILE = new File("/Desktop/Comparison/log_conf3-600_t" + SIM_DUR + ".log");
    private static final File SCENARIO_4_FILE = new File("/Desktop/Comparison/log_conf3-10_t" + SIM_DUR + ".log");

    private static final int SIM_DURATION = 2999;

    private static final double IMAGE_RATIO = 1;

    public static final double IMAGE_WIDTH = 800;
    public static final double IMAGE_HEIGHT = IMAGE_WIDTH * IMAGE_RATIO;


    public static final double MIN_PERCENTAGE = 0.20;

    private static final boolean WITH_ALL_UPDATES = false;


    private static final double AXIS_LENGTH = 250.0;

    private ThreadedAlternateLogParser threadedAlternateLogParser1;
    private ThreadedAlternateLogParser threadedAlternateLogParser2;
    private ThreadedAlternateLogParser threadedAlternateLogParser3;
    private ThreadedAlternateLogParser threadedAlternateLogParser4;

    private ATCGeography geography;

    private SimulationCalculations simCal1;
    private SimulationCalculations simCal2;
    private SimulationCalculations simCal3;
    private SimulationCalculations simCal4;

    private double max;
    private double max1;
    private double max2;
    private double max3;
    private double max4;

    private Color color1;
    private Color color2;
    private Color color3;
    private Color color4;

    private double valueRatio;

    private Scene scene;
    private TabPane tabPane;
    // 2D
    private AnchorPane pane2D;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ConsoleUtils.setLoggingLevel(Level.SEVERE);
        tabPane = new TabPane();
        tabPane.setPrefSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        createTab2D();
        scene = new Scene(tabPane, IMAGE_WIDTH, IMAGE_HEIGHT + 40, Color.BLACK);


        primaryStage.setScene(scene);
        primaryStage.show();
        //
        generate();
    }

    private void createTab2D() {
        pane2D = new AnchorPane();
        pane2D.setPrefSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        Tab tab2d = new Tab("2D");
        tab2d.setContent(pane2D);
        tab2d.setClosable(false);
        tabPane.getTabs().add(tab2d);
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
        //
        System.err.println("Creating simulation calculations ...");
        System.err.println(" > 1");
        simCal1 = new SimulationCalculations(threadedAlternateLogParser1.getDataModel());
        System.err.println(" > 1 done");
        System.err.println(" > 2");
        simCal2 = new SimulationCalculations(threadedAlternateLogParser2.getDataModel());
        System.err.println(" > 2 done");
        System.err.println(" > 3");
        simCal3 = new SimulationCalculations(threadedAlternateLogParser3.getDataModel());
        System.err.println(" > 3 done");
        System.err.println(" > 4");
        simCal4 = new SimulationCalculations(threadedAlternateLogParser4.getDataModel());
        System.err.println(" > 4 done");
        //
        Platform.runLater(() -> {
            representData2D();
        });


        //Platform.runLater(this::saveImage);
    }

    private void representData2D() {
        //
        // delay on scenario 1
        DelayDistribution delayDistribution1 = new DelayDistribution();
        synchronized (delayDistribution1) {
            simCal1.getAllPlanesCalculatedInfo().values().forEach(planeCalculation -> delayDistribution1.addDelayValue(planeCalculation.getDelayTime()));
        }
        System.err.println(" max1 delay is :: " + delayDistribution1.getMax());
        // delay on scenario 1
        DelayDistribution delayDistribution2 = new DelayDistribution();
        synchronized (delayDistribution2) {
            simCal2.getAllPlanesCalculatedInfo().values().forEach(planeCalculation -> delayDistribution2.addDelayValue(planeCalculation.getDelayTime()));
        }
        System.err.println(" max2 delay is :: " + delayDistribution2.getMax());
        // delay on scenario 3
        DelayDistribution delayDistribution3 = new DelayDistribution();
        synchronized (delayDistribution3) {
            simCal3.getAllPlanesCalculatedInfo().values().forEach(planeCalculation -> delayDistribution3.addDelayValue(planeCalculation.getDelayTime()));
        }
        System.err.println(" max3 delay is :: " + delayDistribution3.getMax());
        // delay on scenario 1
        DelayDistribution delayDistribution4 = new DelayDistribution();
        synchronized (delayDistribution4) {
            simCal4.getAllPlanesCalculatedInfo().values().forEach(planeCalculation -> delayDistribution4.addDelayValue(planeCalculation.getDelayTime()));
        }
        System.err.println(" max4 delay is :: " + delayDistribution4.getMax());

        // creating chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("delay");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("aircraft count");

        BarChart barChart = new BarChart(xAxis, yAxis);
        barChart.setPrefSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        pane2D.getChildren().add(barChart);
        AnchorPane.setTopAnchor(barChart, 8.0);
        AnchorPane.setBottomAnchor(barChart, 8.0);
        AnchorPane.setRightAnchor(barChart, 8.0);
        AnchorPane.setLeftAnchor(barChart, 8.0);

        //

        XYChart.Series dataSeries1 = new XYChart.Series();
        dataSeries1.setName("TSS only");
        // hum...
        for (int i = 0; i < delayDistribution1.getMax() + 1; i++) {
            if (delayDistribution1.getDelayCounts().containsKey(i)) {
                dataSeries1.getData().add(new XYChart.Data(Integer.toString(i), delayDistribution1.getDelayCounts().get(i)));
            } else {
                dataSeries1.getData().add(new XYChart.Data(Integer.toString(i), 0));
            }
        }
//        delayDistribution1.getDelayCounts().forEach((delay,count)->dataSeries1.getData().add(new XYChart.Data(Integer.toString(delay), count)));


        XYChart.Series dataSeries2 = new XYChart.Series();
        dataSeries2.setName("TSS+DSAS");
        // hum...
        for (int i = 0; i < delayDistribution1.getMax() + 1; i++) {
            if (delayDistribution2.getDelayCounts().containsKey(i)) {
                dataSeries2.getData().add(new XYChart.Data(Integer.toString(i), delayDistribution2.getDelayCounts().get(i)));
            } else {
                dataSeries2.getData().add(new XYChart.Data(Integer.toString(i), 0));
            }
        }

        XYChart.Series dataSeries3 = new XYChart.Series();
        dataSeries3.setName("TSS+DSAS t=1200");
        // hum...
        for (int i = 0; i < delayDistribution1.getMax() + 1; i++) {
            if (delayDistribution3.getDelayCounts().containsKey(i)) {
                dataSeries3.getData().add(new XYChart.Data(Integer.toString(i), delayDistribution3.getDelayCounts().get(i)));
            } else {
                dataSeries3.getData().add(new XYChart.Data(Integer.toString(i), 0));
            }
        }


        XYChart.Series dataSeries4 = new XYChart.Series();
        dataSeries4.setName("TSS+DSAS t=10");
        // hum...
        for (int i = 0; i < delayDistribution4.getMax() + 1; i++) {
            if (delayDistribution4.getDelayCounts().containsKey(i)) {
                dataSeries4.getData().add(new XYChart.Data(Integer.toString(i), delayDistribution4.getDelayCounts().get(i)));
            } else {
                dataSeries4.getData().add(new XYChart.Data(Integer.toString(i), 0));
            }
        }


        barChart.getData().add(dataSeries1);
        barChart.getData().add(dataSeries2);
        barChart.getData().add(dataSeries3);
        barChart.getData().add(dataSeries4);



    }


    private void saveImage() {
        File f = new File("/Desktop/Comparison/output_" + System.currentTimeMillis() + ".png");
        WritableImage snapshot = pane2D.snapshot(new SnapshotParameters(), null);
        BufferedImage bufferedImage = new BufferedImage((int) IMAGE_WIDTH, (int) IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        BufferedImage image = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, bufferedImage);
        try {
            Graphics2D gd = (Graphics2D) image.getGraphics();
            gd.translate(pane2D.getWidth(), pane2D.getHeight());
            ImageIO.write(image, "png", f);
        } catch (IOException ex) {
            System.err.println("Exception while saving: {0}" + ex);
        }
    }

}
