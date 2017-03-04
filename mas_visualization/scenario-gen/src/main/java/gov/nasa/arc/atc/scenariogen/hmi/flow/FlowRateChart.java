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
import javafx.scene.effect.Lighting;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ahamon
 */
public class FlowRateChart extends AnchorPane {

    private static final double PADDING = 20;
    private static final double AXIS_LABEL_WIDTH = 30;
    private static final double BAR_SEPARATION = 8;

    private static final Logger LOG = Logger.getGlobal();

    private final List<Bar> bars;

    private Flow flow;
    private List<SubFlow> subFlows;


    // graphical primitives
    private Line xLine;
    private Line yLine;

    // calculation variables
    private double height = heightProperty().doubleValue();
    private int nbQuarters = 0;
    private int max = 0;
    private double usableHeight = 0;
    private double barWidth = 0;

    public FlowRateChart() {
        super();
        bars = new LinkedList<>();
        init();
    }

    public void setFlow(Flow aFlow) {
        flow = aFlow;
        if (flow != null) {
            updateFlowBars();
        }
    }


    private void init() {
        xLine = new Line();
        yLine = new Line();

        getChildren().addAll(xLine, yLine);

        initListeners();
        updateSize();
    }

    private void initListeners() {
        widthProperty().addListener((obs, old, newV) -> {
            LOG.log(Level.FINE, "width changed {0} {1} {2}", new Object[]{obs, old, newV});
            updateSize();
        });
        heightProperty().addListener((obs, old, newV) -> {
            LOG.log(Level.FINE, "height changed {0} {1} {2}", new Object[]{obs, old, newV});
            updateSize();
        });
    }

    private void updateSize() {
        double width = widthProperty().doubleValue();
        height = heightProperty().doubleValue();

        xLine.setStartX(PADDING + AXIS_LABEL_WIDTH);
        xLine.setEndX(width - PADDING);
        xLine.setStartY(height - PADDING);
        xLine.setEndY(height - PADDING);
        //
        yLine.setStartX(PADDING + AXIS_LABEL_WIDTH);
        yLine.setEndX(PADDING + AXIS_LABEL_WIDTH);
        yLine.setStartY(PADDING);
        yLine.setEndY(height - PADDING);
        //
        double usableWidth = width - 2 * PADDING - AXIS_LABEL_WIDTH;
        usableHeight = height - 2 * PADDING;
        barWidth = (usableWidth - (nbQuarters + 1) * BAR_SEPARATION) / nbQuarters;
        //
        bars.forEach(Bar::update);
    }

    public void updateValues() {
        // calculate the max quarterly value
        max = 0;
        for (int i = 0; i < nbQuarters; i++) {
            final int quarterID = i;
            // better in one loop?
            max = Math.max(subFlows.stream().mapToInt(subFlow -> subFlow.getRateAtQuarter(quarterID)).sum(), max);
        }
        bars.forEach(Bar::updateValue);
    }

    public void updateFlowBars() {
        getChildren().removeAll(bars);
        bars.clear();
        nbQuarters = flow.getNbQuarters();
        subFlows = flow.getSubFlows();
        if (nbQuarters == 0 || subFlows.isEmpty()) {
            return;
        }
        // calculate the max quarterly value
        max = 0;
        for (int i = 0; i < nbQuarters; i++) {
            final int quarterID = i;
            // better in one loop?
            max = Math.max(subFlows.stream().mapToInt(subFlow -> subFlow.getRateAtQuarter(quarterID)).sum(), max);
            subFlows.forEach(subFlow -> {
                Bar bar = new Bar(subFlow, quarterID, subFlows.indexOf(subFlow), this);
                bars.add(bar);
            });
        }
        getChildren().addAll(bars);
        updateSize();

    }

    private class Bar extends Rectangle {

        private final SubFlow subFlow;
        private final int quarterID;
        private final int flowIndex;
        private final FlowRateChart rateChart;
        private int value;
        private int valueOffset;


        private Bar(SubFlow subFlow, int quarterID, int flowIndex, FlowRateChart rateChart) {
            super();
            this.subFlow = subFlow;
            this.quarterID = quarterID;
            this.flowIndex = flowIndex;
            this.rateChart = rateChart;
            init();
        }

        private void init() {
            setFill(subFlow.getColor());
            setArcWidth(4);
            setArcHeight(4);
            updateValue();
            initInteractivity();
        }

        private void initInteractivity() {
            setOnMouseEntered(event -> {
                LOG.log(Level.FINE, " mouse entered {0} on {1}", new Object[]{this, event});
                setEffect(new Lighting());
            });
            setOnMouseExited(event -> {
                LOG.log(Level.FINE, " mouse exited {0} on {1}", new Object[]{this, event});
                setEffect(null);
            });
            this.setOnScroll(event -> {
                LOG.log(Level.FINE, " scrolling {0} on {1}", new Object[]{this, event});
                subFlow.setRateAtQuarter(quarterID, subFlow.getRateAtQuarter(quarterID) + (int) Math.signum(event.getDeltaY()));
            });
        }

        private void updateValue() {
            value = subFlow.getRateAtQuarter(quarterID);
            valueOffset = 0;
            for (int i = 0; i < flowIndex; i++) {
                valueOffset += rateChart.subFlows.get(i).getRateAtQuarter(quarterID);
            }
            update();
        }

        private void update() {
            setWidth(rateChart.barWidth);
            setHeight(Math.max(rateChart.usableHeight * value / rateChart.max, 0));
            setX(PADDING + AXIS_LABEL_WIDTH + BAR_SEPARATION + (rateChart.barWidth + BAR_SEPARATION) * quarterID);
            setY(rateChart.height - PADDING - rateChart.usableHeight * valueOffset / rateChart.max - getHeight());
        }

    }


}
