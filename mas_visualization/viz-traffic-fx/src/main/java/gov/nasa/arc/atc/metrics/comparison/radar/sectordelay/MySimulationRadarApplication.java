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

package gov.nasa.arc.atc.metrics.comparison.radar.sectordelay;

import gov.nasa.arc.atc.metrics.comparison.radar.SimpleRadarWidget;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author ahamon
 */
public class MySimulationRadarApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.LIGHTGRAY);
        primaryStage.setScene(scene);
        primaryStage.show();
        //

        // for all dur=300s
//        SimulationW2R6 sim1 = new SimulationW2R6("TSS only",0,39,0,0,23,73,27);
//        SimulationW2R6 sim2 = new SimulationW2R6("TSS+DSAS",0,71,51,0,24,93,27);
//        SimulationW2R6 sim3 = new SimulationW2R6("TSS+DSAS t=600",0,17,0,0,3,62,27);
//        SimulationW2R6 sim4 = new SimulationW2R6("TSS+DSAS t=10",0,35,0,0,3,62,27);

        // for all dur = 3000s
//        SimulationW2R6 sim1 = new SimulationW2R6("TSS only",31,122,516,41,150,407,230);
//        SimulationW2R6 sim2 = new SimulationW2R6("TSS+DSAS",32,199,345,28,174,442,185);
//        SimulationW2R6 sim3 = new SimulationW2R6("TSS+DSAS t=600",32,72,298,28,118,299,188);
//        SimulationW2R6 sim4 = new SimulationW2R6("TSS+DSAS t=10",32,72,298,28,118,299,181);


        // for LANDED dur = 3000s 24-25 landed
//        SimulationW2R6 sim1 = new SimulationW2R6("TSS only",24,121,305,27,26,132,190);
//        SimulationW2R6 sim2 = new SimulationW2R6("TSS+DSAS",25,198,251,28,27,165,173);
//        SimulationW2R6 sim3 = new SimulationW2R6("TSS+DSAS t=1200",25,71,209,28,6,106,176);
//        SimulationW2R6 sim4 = new SimulationW2R6("TSS+DSAS t=10",25,71,209,28,6,106,169);


        // for LANDED dur = 8000s 57 landed
//        SimulationW2R6 sim1 = new SimulationW2R6("TSS only",103,367,708,164,229,440,325);
//        SimulationW2R6 sim2 = new SimulationW2R6("TSS+DSAS",114,325,358,151,240,475,304);
//        SimulationW2R6 sim3 = new SimulationW2R6("TSS+DSAS t=1200",77,174,313,151,166,332,308);
//        SimulationW2R6 sim4 = new SimulationW2R6("TSS+DSAS t=10",77,134,313,151,166,332,290);


        // for LANDED dur = 8000s 57 landed conf 3
//        SimulationW2R6 sim1 = new SimulationW2R6("TSS+DSAS t=1200",77,174,313,151,166,332,308);
//        SimulationW2R6 sim2 = new SimulationW2R6("TSS+DSAS t=900",77,227,313,151,166,332,265);
//        SimulationW2R6 sim3 = new SimulationW2R6("TSS+DSAS t=600",77,220,313,151,166,332,290);
//        SimulationW2R6 sim4 = new SimulationW2R6("TSS+DSAS t=10",77,134,313,151,166,332,290);


        // for LANDED dur = 8000s 57 landed conf 3 start at t=500s
//        SimulationW2R6 sim1 = new SimulationW2R6("TSS+DSAS t=1200",75,122,285,151,163,242,258);
//        SimulationW2R6 sim2 = new SimulationW2R6("TSS+DSAS t=900",75,175,285,151,163,243,215);
//        SimulationW2R6 sim3 = new SimulationW2R6("TSS+DSAS t=600",75,168,285,151,163,243,240);
//        SimulationW2R6 sim4 = new SimulationW2R6("TSS+DSAS t=10",75,116,285,151,163,242,240);


        // for LANDED dur = 8000s 57 landed conf 3 start at t=0 Alternate delay calculation
//        SimulationW2R6 sim1 = new SimulationW2R6("TSS+DSAS t=1200",0,174,310,151,166,286,304);
//        SimulationW2R6 sim2 = new SimulationW2R6("TSS+DSAS t=900",0,227,310,151,166,286,261);
//        SimulationW2R6 sim3 = new SimulationW2R6("TSS+DSAS t=600",0,220,310,151,166,286,286);
//        SimulationW2R6 sim4 = new SimulationW2R6("TSS+DSAS t=10",0,134,310,151,166,286,286);


        // for LANDED dur = 8000s 57 landed conf 3 start at t=500 Alternate delay calculation
        SimulationW2R6 sim1 = new SimulationW2R6("TSS+DSAS t=1200",0,122,282,151,163,205,254);
        SimulationW2R6 sim2 = new SimulationW2R6("TSS+DSAS t=900",0,175,282,151,163,206,211);
        SimulationW2R6 sim3 = new SimulationW2R6("TSS+DSAS t=600",0,168,282,151,163,206,236);
        SimulationW2R6 sim4 = new SimulationW2R6("TSS+DSAS t=10",0,116,282,151,163,205,236);


        SimpleRadarWidget<SimulationW2R6> radarWidget = new SimpleRadarWidget<>();
        root.getChildren().add(radarWidget.getNode());
        radarWidget.setPosition(50, 50);
        radarWidget.setSize(700, 500);

        // adding functions
        radarWidget.addAxisFunction(new Z118Function());
        radarWidget.addAxisFunction(new Z112Function());
        radarWidget.addAxisFunction(new Z114Function());
        radarWidget.addAxisFunction(new Z27Function());
        radarWidget.addAxisFunction(new Z29Function());
        radarWidget.addAxisFunction(new Z28Function());
        radarWidget.addAxisFunction(new Z110Function());
        radarWidget.addAxisFunction(new TotalFunction());

        // Adding data
        radarWidget.addDataSet(sim1);
        radarWidget.addDataSet(sim2);
        radarWidget.addDataSet(sim3);
        radarWidget.addDataSet(sim4);

        //
        radarWidget.setTitle(" Simulations 8000s comparison");

        scene.widthProperty().addListener((obs, old, newV) -> radarWidget.setSize(scene.getWidth() - 100, scene.getHeight() - 100));
        scene.heightProperty().addListener((obs, old, newV) -> radarWidget.setSize(scene.getWidth() - 100, scene.getHeight() - 100));

        System.err.println(" sim 1 delay = "+sim1.getTotal());
        System.err.println(" sim 2 delay = "+sim2.getTotal());
        System.err.println(" sim 3 delay = "+sim3.getTotal());
        System.err.println(" sim 4 delay = "+sim4.getTotal());

    }


    public static void main(String[] args) {
        Application.launch(args);
    }

}
