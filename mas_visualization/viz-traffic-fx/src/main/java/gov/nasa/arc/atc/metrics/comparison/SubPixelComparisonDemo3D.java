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

/*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nasa.arc.atc.metrics.comparison;

import gov.nasa.arc.atc.atc3dviz.Xform;
import gov.nasa.arc.atc.utils.MercatorAttributes;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;

/**
 * @author cmcastil
 */
public class SubPixelComparisonDemo3D {

    private final Xform axisGroup = new Xform();
    private final Xform moleculeGroup = new Xform();
    private final Xform world = new Xform();
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Xform cameraXform = new Xform();
    private final Xform cameraXform2 = new Xform();
    private final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -1450;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 15000.0;
    private static final double AXIS_LENGTH = 250.0;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private final Group worldGroup;
    private final Group root;

    private MercatorAttributes mercatorAttributes;

    public SubPixelComparisonDemo3D() {
        worldGroup = new Group();
        root = new Group();

        buildCamera();
        buildAxes();
        buildFloor();


        world.getChildren().addAll(moleculeGroup);

        root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);

        SubScene scene = new SubScene(root, SubPixelComparisonDemoApp.IMAGE_WIDTH, SubPixelComparisonDemoApp.IMAGE_HEIGHT, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.GREY);
        handleKeyboard(scene);
        handleMouse(scene);

        scene.setCamera(camera);


        worldGroup.getChildren().add(scene);
    }

    private void buildCamera() {
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    private void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(true);
        world.getChildren().addAll(axisGroup);
    }

    private void buildFloor() {
        double floorThickness = 4;
        Box floor = new Box();
        PhongMaterial floorMaterial = new PhongMaterial();
        floorMaterial.setDiffuseColor(Color.BLACK);
        floorMaterial.setSpecularColor(Color.rgb(10, 10, 10));
        floor.setMaterial(floorMaterial);
        floor.setWidth(2.0 * SubPixelComparisonDemoApp.IMAGE_WIDTH);
        floor.setHeight(floorThickness);
        floor.setDepth(2.0 * SubPixelComparisonDemoApp.IMAGE_HEIGHT);
        floor.setTranslateY(-floorThickness);

        moleculeGroup.getChildren().add(floor);

    }

    private void handleMouse(SubScene scene) {
        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged((MouseEvent me) -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);

            double modifier = 1.0;

            if (me.isControlDown()) {
                modifier = CONTROL_MULTIPLIER;
            }
            if (me.isShiftDown()) {
                modifier = SHIFT_MULTIPLIER;
            }
            if (me.isPrimaryButtonDown()) {
                cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
                cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
            } else if (me.isSecondaryButtonDown()) {
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
                camera.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown()) {
                cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
                cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
            }
        });
    }

    private void handleKeyboard(SubScene scene) {
        scene.setOnKeyPressed((KeyEvent event) -> {
            switch (event.getCode()) {
                case Z:
                    cameraXform2.t.setX(0.0);
                    cameraXform2.t.setY(0.0);
                    camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
                    cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                    cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                    break;
                case X:
                    axisGroup.setVisible(!axisGroup.isVisible());
                    break;
                case V:
                    moleculeGroup.setVisible(!moleculeGroup.isVisible());
                    break;
                default:
                    break;
            }
        });
    }

    public Node getNode() {
        return worldGroup;
    }

    public void setVisible(boolean visibility) {
        worldGroup.setVisible(visibility);
    }


    public void setMercatorAttributes(MercatorAttributes attributes) {
        mercatorAttributes = attributes;
    }

    public void displayData(double[][] data, double max, Color color, SubPixelPos subPixelPos) {
        // center at LGA
        Point2D lgaCenterP = mercatorAttributes.getXYPosition(40.77725, -73.872611);
        double scale3D = 1;

        Platform.runLater(() -> {

            for (int i = 0; i < mercatorAttributes.getMapWidth(); i++) {
                for (int j = 0; j < mercatorAttributes.getMapHeight(); j++) {

                    // create sub pixel
                    double pixelX = (i - lgaCenterP.getX()) * SubPixelComparisonDemoApp.PIXEL_SIZE + SubPixelComparisonDemoApp.PIXEL_RADIUS;
                    double pixelY = (j - lgaCenterP.getY()) * SubPixelComparisonDemoApp.PIXEL_SIZE + SubPixelComparisonDemoApp.PIXEL_RADIUS;

                    PhongMaterial pMaterial = new PhongMaterial();
                    pMaterial.setDiffuseColor(color);
                    pMaterial.setSpecularColor(color);//Color.DARKGREY

                    double value = data[i][j];
                    if (value > 0) {
                        //// creating data sub pixel
                        Point3D center;
                        switch (subPixelPos) {
                            case TOP_LEFT:
                                center = new Point3D(pixelX - SubPixelComparisonDemoApp.PIXEL_RADIUS / 2.0, 0, pixelY - SubPixelComparisonDemoApp.PIXEL_RADIUS / 2.0);
                                break;
                            case TOP_RIGHT:
                                center = new Point3D(pixelX + SubPixelComparisonDemoApp.PIXEL_RADIUS / 2.0, 0, pixelY - SubPixelComparisonDemoApp.PIXEL_RADIUS / 2.0);
                                break;
                            case BOTTOM_RIGHT:
                                center = new Point3D(pixelX + SubPixelComparisonDemoApp.PIXEL_RADIUS / 2.0, 0, pixelY + SubPixelComparisonDemoApp.PIXEL_RADIUS / 2.0);
                                break;
                            case BOTTOM_LEFT:
                                center = new Point3D(pixelX - SubPixelComparisonDemoApp.PIXEL_RADIUS / 2.0, 0, pixelY + SubPixelComparisonDemoApp.PIXEL_RADIUS / 2.0);
                                break;
                            default:
                                throw new IllegalArgumentException("unknown sub pixel type: " + subPixelPos);
                        }

                        double boxHeight = value * 1.5;
                        Box box = new Box();
                        box.setMaterial(pMaterial);
                        box.setTranslateX(center.getX() * scale3D);
                        box.setTranslateY(boxHeight / 2.0);
                        box.setTranslateZ(center.getZ() * scale3D);

                        box.setWidth(SubPixelComparisonDemoApp.PIXEL_RADIUS / 2.0);
                        box.setHeight(boxHeight);
                        box.setDepth(SubPixelComparisonDemoApp.PIXEL_RADIUS / 2.0);

                        moleculeGroup.getChildren().add(box);
                    }
                }
            }
        });


    }


}
