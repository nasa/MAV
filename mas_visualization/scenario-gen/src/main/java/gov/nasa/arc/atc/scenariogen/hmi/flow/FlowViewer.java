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

package gov.nasa.arc.atc.scenariogen.hmi.flow;

import gov.nasa.arc.atc.scenariogen.core.Flow;
import gov.nasa.arc.atc.scenariogen.core.SubFlow;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.beans.PropertyChangeEvent;

/**
 * @author ahamon
 */
public class FlowViewer extends Pane {

    private static final double PREFERRED_WIDTH = 400;
    private static final double PREFERRED_HEIGHT = 800;


    private static final double MAJOR_LINE_SPACING = 20;
    private static final double PADDING = 8;
    ;

    private final Flow flow;

    // graphic attributes
    private final Canvas canvas;
    private final GraphicsContext context;

    //
    private double width;
    private double height;
    private double scaleStartX = 0;
    private double scaleEndX = 0;
    private double leftVLineEndY = 0;
    private double leftLineX;
    private double rightLineX;
    private double secondsToPixelRatio;
    private double columnWidth = 1;
    private int currentMaxTime;


    public FlowViewer(Flow aFlow) {
        super();
        flow = aFlow;
        canvas = new Canvas();
        context = canvas.getGraphicsContext2D();
        init();
        flow.addPropertyChangeListener(FlowViewer.this::handleFlowChange);
    }

    private void init() {
        currentMaxTime = flow.getNbQuarters() * 60 * 15;
        getChildren().add(canvas);
        setStyle("-fx-background-color: black;");
        setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        drawCanvas();
        widthProperty().addListener((obs, old, newV) -> {
            //todo log
            drawCanvas();
        });
        heightProperty().addListener((obs, old, newV) -> {
            //todo log
            drawCanvas();
        });
    }

    private void drawCanvas() {
        width = widthProperty().doubleValue();
        height = heightProperty().doubleValue();

        // resize
        canvas.setWidth(width);
        canvas.setHeight(height);

        // variables
        double footerTextHeight = 25;

        // clear previous drawings

        // font attribute
        context.setFont(new Font(8));

        // background
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, width, height);

        context.setFill(Color.WHITESMOKE);
        context.setStroke(Color.WHITESMOKE);
        // footer
        double footerY = height - PADDING;
        double footerTextWidth = 30; // hum...
        context.fillText(flow.getRunway(), PADDING, footerY);
        context.fillText("Sch. Time", (width - MAJOR_LINE_SPACING) / 2.0 - footerTextWidth / 2.0 + PADDING, footerY);

        // arrival horizontal lines
        double y = height - PADDING - footerTextHeight;
        leftLineX = PADDING;
        rightLineX = MAJOR_LINE_SPACING + PADDING;
        double leftVLineStartY = PADDING;
        leftVLineEndY = y - MAJOR_LINE_SPACING;

        double nbPixels = leftVLineEndY - leftVLineStartY;
        secondsToPixelRatio = nbPixels / currentMaxTime;

        //// upper line
        context.beginPath();
        context.moveTo(PADDING, leftVLineEndY);
        context.lineTo(leftLineX, leftVLineEndY);
        context.moveTo(rightLineX, leftVLineEndY);
        context.lineTo(width - PADDING, leftVLineEndY);
        //// lower lines
        context.moveTo(PADDING, y);
        context.lineTo(width - PADDING, y);

//        if (overScale) {
//            context.setStroke(Color.CHARTREUSE);
//        }

        // vertical lines
        //// left
        context.moveTo(leftLineX, leftVLineStartY);
        context.lineTo(leftLineX, y);
        //// right
        context.moveTo(rightLineX, PADDING);
        context.lineTo(rightLineX, leftVLineEndY);

        // distance lines and labels
        scaleStartX = PADDING + 4;
        scaleEndX = MAJOR_LINE_SPACING + PADDING - 4;
        double labelX = leftLineX + 4;
        double dY;
        int nbMin;
        for (int i = 0; i < currentMaxTime + 1; i++) {
            if (i % (15 * 60) == 0 && i != 0) {
                nbMin = i / 60;
                dY = PADDING + (currentMaxTime - i) * secondsToPixelRatio + 5; // !!
                context.fillText(Integer.toString(nbMin), labelX, dY);
            } else if (i % (5 * 60) == 0 && i != 0) {
                dY = PADDING + (currentMaxTime - i) * secondsToPixelRatio;
                context.moveTo(scaleStartX, dY);
                context.lineTo(scaleEndX, dY);
            }
        }

        // closing
        context.stroke();
        context.closePath();

        // drawing schedule
        flow.getSubFlows().forEach(this::drawSubFlow);
    }

    private void drawSubFlow(SubFlow subFlow) {
        //
        context.beginPath();
        context.setFill(subFlow.getColor());
        context.setStroke(subFlow.getColor());
        subFlow.getGeneratedFlightPlans().forEach(scheduledFlightPlan -> {
            double dY = PADDING + (currentMaxTime - scheduledFlightPlan.getTimeAtDeliveryWPT()) * secondsToPixelRatio;
            context.moveTo(rightLineX + PADDING, dY);
            context.lineTo(rightLineX + PADDING + 25, dY);
        });
        // closing
        context.stroke();
        context.closePath();
    }

    private void handleFlowChange(PropertyChangeEvent event) {
        drawCanvas();
    }


}
