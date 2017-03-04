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

package gov.nasa.arc.atc.viewer;

import gov.nasa.arc.atc.atc2dviz.World2D;
import javafx.stage.Stage;

/**
 * This class serves as a repository for each HMI component' instance
 *
 * @author hamon
 */
public final class ViewerComponents {
    // TODO: find a better way

    private static Stage primaryStage;
    private static World2D world2d;

    private ViewerComponents() {
        // private constructor for utility class
    }

    static void registerStage(Stage stage) {
        primaryStage = stage;
    }

    static void registerWorld2D(World2D w2d) {
        world2d = w2d;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static World2D getWorld2D() {
        return world2d;
    }

}
