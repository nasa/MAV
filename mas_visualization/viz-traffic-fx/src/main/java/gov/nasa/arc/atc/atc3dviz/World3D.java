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
 * Copyright (C) 2014 Arnaud
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gov.nasa.arc.atc.atc3dviz;

import java.beans.PropertyChangeEvent;

import gov.nasa.arc.atc.WorldVisualization;
import gov.nasa.arc.atc.core.DataModel;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 *
 * @author Arnaud
 */
public class World3D implements WorldVisualization {

	//
	private static final double AXIS_LENGTH = 250.0;

	private final PerspectiveCamera camera = new PerspectiveCamera(true);
	private final Group worldGroup;
	private final Group objectsGroup;
	private SubScene subScene;
	//

	public World3D() {
		worldGroup = new Group();
		objectsGroup = new Group();
		create3DWorld();
	}

	private void create3DWorld() {
		// Box
		Box testBox = new Box(5, 5, 5);
		testBox.setMaterial(new PhongMaterial(Color.RED));
		testBox.setDrawMode(DrawMode.FILL);
		//
		Sphere sphere = new Sphere(4.0);
		sphere.setMaterial(new PhongMaterial(Color.DARKTURQUOISE));
		sphere.setDrawMode(DrawMode.FILL);
		//

		// Create lights
		buildLights();
		// Create and position camera
		buildCamera();
		//
		buildAxes();

		// Build the Scene Graph
		objectsGroup.getChildren().add(camera);
		objectsGroup.getChildren().add(sphere);

		// Use a SubScene
		subScene = new SubScene(objectsGroup, 800, 600);
		subScene.setFill(Color.ALICEBLUE);
		subScene.setCamera(camera);
		worldGroup.getChildren().add(subScene);

	}

	@Override
	public Node getNode() {
		return worldGroup;
	}

	public void setPosition(double pX, double pY) {
		worldGroup.setTranslateX(pX);
		worldGroup.setTranslateY(pY);
	}

	public void bindSize(ReadOnlyDoubleProperty widthProperty, ReadOnlyDoubleProperty heightProperty) {
		subScene.widthProperty().bind(widthProperty);
		subScene.heightProperty().bind(heightProperty);
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
		xAxis.setTranslateX(-AXIS_LENGTH / 2);
		final Box yAxis = new Box(1, AXIS_LENGTH, 1);
		yAxis.setTranslateY(-AXIS_LENGTH / 2);
		final Box zAxis = new Box(1, 1, AXIS_LENGTH);
		zAxis.setTranslateZ(-AXIS_LENGTH / 2);

		xAxis.setMaterial(redMaterial);
		yAxis.setMaterial(greenMaterial);
		zAxis.setMaterial(blueMaterial);
		// Xform axisGroup = new Xform();
		objectsGroup.getChildren().addAll(xAxis, yAxis, zAxis);
		objectsGroup.setVisible(true);
		// world.getChildren().addAll(axisGroup);
	}

	private void buildCamera() {
		camera.getTransforms().addAll(new Rotate(-20, Rotate.Y_AXIS), new Rotate(180, Rotate.X_AXIS), new Translate(0, 0, -75)
		// new Rotate(-20, Rotate.Y_AXIS)
		);
	}

	private void buildLights() {
		PointLight light = new PointLight(Color.WHITE);
		light.setTranslateX(50);
		light.setTranslateY(300);
		light.setTranslateZ(400);
		PointLight light1 = new PointLight(Color.WHITE);
		light1.setTranslateX(50);
		light1.setTranslateY(-300);
		light1.setTranslateZ(-400);
		PointLight light2 = new PointLight(Color.color(0.6, 0.3, 0.4));
		light2.setTranslateX(400);
		light2.setTranslateY(0);
		light2.setTranslateZ(-400);

		AmbientLight ambientLight = new AmbientLight(Color.color(0.2, 0.2, 0.2));
		objectsGroup.setRotationAxis(new Point3D(2, 1, 0).normalize());
		objectsGroup.setTranslateX(180);
		objectsGroup.setTranslateY(180);
		objectsGroup.getChildren().addAll(ambientLight, light, light1, light2);
	}

	@Override
	public void setVisible(boolean visibility) {
		worldGroup.setVisible(visibility);
	}

	@Override
	public void setSimulationDataModel(DataModel simulationDataModel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

}
