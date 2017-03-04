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

import gov.nasa.arc.atc.geography.Sector;
import gov.nasa.arc.atc.utils.ColorFXFactory;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import org.controlsfx.control.PopOver;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;

import java.util.stream.Collectors;
import java.util.stream.Collectors.*;
/**
 * @author ahamon
 * @author kjewell
 */
public class Sector2D implements Element2D {

    private static final Logger LOG = Logger.getGlobal();

    private static final double DEFAULT_STROKE_WIDTH = 1;

    private final Sector sector;
    private final MercatorAttributes mercatorAttributes;

    private final Color defaultColor;

    private final Group mainNode;
    private Shape background = null;
    private Shape foreground = null;

    private final PopOver sectorInfoPopOver;
    private SectorPopUpController controller;

    /**
     * Creates a 2D representation of the sector s
     *
     * @param s           sector to represent
     * @param mAttributes the mercator attributes used for the map projection
     */
    public Sector2D(Sector s, MercatorAttributes mAttributes) {
        sector = s;
        mercatorAttributes = mAttributes;
        mainNode = new Group();
        defaultColor = ColorFXFactory.getColor();
        sectorInfoPopOver = new PopOver();
        runLater(() -> {
            createSectorShapes();
            configurePopUp();
        });
    }

    private void addInteractivity() {
        foreground.setOnMouseEntered(this::handleMouseEntered);
        foreground.setOnMouseExited(this::handleMouseExited);
        foreground.setOnMouseClicked(this::handleMouseClicked);
    }

    private void handleMouseEntered(MouseEvent event) {
        LOG.log(Level.FINE, "handleMouseEntered {0}", event);
        background.setStrokeWidth(3.0);
        mainNode.toFront();
    }

    private void handleMouseExited(MouseEvent event) {
        LOG.log(Level.FINE, "handleMouseExited {0}", event);
        background.setStrokeWidth(1.0);
    }

    private void handleMouseClicked(MouseEvent event) {
        LOG.log(Level.FINE, "handleMouseClicked {0}", event);
        sectorInfoPopOver.show(mainNode);
    }

    public void openPopUp() {
        sectorInfoPopOver.show(mainNode);
    }

    private void handleClose(PropertyChangeEvent event) {
        if (event.getNewValue().equals(false)) {
            sectorInfoPopOver.hide();
        }
    }

    private void configurePopUp() {
        sectorInfoPopOver.setAutoHide(false);
        sectorInfoPopOver.setHideOnEscape(false);
        sectorInfoPopOver.setTitle("Sector: " + sector.getName());
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("Sector2DPopUp.fxml"));
            Parent node = fxml.load();
            controller = fxml.getController();
            controller.setSector(sector);
            controller.addPropertyListener(this::handleClose);
            sectorInfoPopOver.setContentNode(node);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Could not load pop up because of exception {0}", e);
        }

        sectorInfoPopOver.setOnHidden(event -> {
            LOG.log(Level.INFO, "Hiding pop up {0}", event);
            if (controller.isAllowingBlick()) {
                controller.dontAllowBlinking();
                controller.stopBlinking();
            } else {
                controller.dontAllowBlinking();
            }
        });
        sectorInfoPopOver.setOnShowing(event -> {
            LOG.log(Level.INFO, "showing pop up {0}", event);
            controller.allowBlinking();
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("Handle sector property change"); // TODO

    }

    @Override
    public Node getNode() {
        return mainNode;
    }

    @Override
    public void updatePosition(boolean onScreenResize) {
        if (onScreenResize) {
            mainNode.getChildren().clear();
            createSectorShapes();
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

    private void createSectorShapes() {
        if (!sector.getRegions().isEmpty()) {

            background = buildShape();
            background.setStroke(defaultColor);
            background.setStrokeWidth(DEFAULT_STROKE_WIDTH);
            background.setFill(null);
            // foreground
            foreground = buildShape();
            foreground.setOpacity(0.0);

            //
            mainNode.getChildren().addAll(background,foreground);
        }
        addInteractivity();
    }

    // not optimized yet
    private Shape buildShape(){
        List<Polygon> regions = new ArrayList<>();
        sector.getRegions().forEach(region -> {
            if (!region.getVertices().isEmpty()) {
                Polygon p = new Polygon();
                region.getVertices().forEach(p2D -> {
                    final Point2D screenP = mercatorAttributes.getXYPosition(p2D.getLatitude(), p2D.getLongitude());
                    p.getPoints().add(screenP.getX());
                    p.getPoints().add(screenP.getY());
                });
                regions.add(p);
            }
        });

        // background
        Shape shape = regions.get(0);
        for (int i = 1; i < regions.size(); i++) {
            shape = Shape.union(shape, regions.get(i));
        }
        return shape;
    }



    public Sector getSector() {
        return sector;
    }

    private void foo(){
        List<String> names = Arrays.asList("Alphonse","Bob","Charly");
        names.forEach(name-> System.err.println(name));
        names.forEach(System.err::println);
        System.err.println(names.stream().map(name->name.toUpperCase()).collect(Collectors.joining(",")));
    }

}
