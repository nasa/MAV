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

package gov.nasa.arc.atc.utils;

import javafx.scene.paint.Color;

import java.util.*;

/**
 * @author ahamon
 */
public final class ColorFXFactory {

    private static final int DEFAULT_MIN_SPEED = 110;
    // will go higher
    private static final int DEFAULT_MAX_SPEED = 300;
    private static final double BLUE_HUE = Color.BLUE.getHue();
    private static final double RED_HUE = Color.RED.getHue();

    private static final List<Color> COLORS = createFXColors();
    private static final List<Color> INTERACTIVE_COLORS = createFXInteractiveColors();

    private static final Map<String, Color> ATC_COLORS = new HashMap<>();

    private static final List<Color> ATC_COLORS_LIST = createATCColors();
    //
    private static int interactiveColorIndex = 0;
    private static int controllerColorIndex = 0;
    private static int colorIndex = 0;


    private ColorFXFactory() {
        // private utility constructor
    }

    public static Color getInteractiveColor() {
        interactiveColorIndex++;
        return INTERACTIVE_COLORS.get(interactiveColorIndex % INTERACTIVE_COLORS.size());
    }

    public static Color getATCColor(String controllerName) {
        if (!ATC_COLORS.containsKey(controllerName)) {
            ATC_COLORS.put(controllerName, getATCColor());
        }
        return ATC_COLORS.get(controllerName);
    }

    public static Color getColor() {
        colorIndex++;
        return COLORS.get(colorIndex % COLORS.size());
    }

    /**
     * @param color the color to test
     * @return the color's luminance
     * @see <a href="https://www.w3.org/TR/WCAG20/relative-luminance.xml">Relative luminance</a>
     */
    public static double getColorLuminance(Color color) {
        return 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
    }

    /**
     * tests if black is better than white on a colored background
     *
     * @param color to test
     * @return if black is better than white
     */
    public static boolean useBlack(Color color) {
        double l = getColorLuminance(color);
        return (l + 0.05) / (0.0 + 0.05) > (1.0 + 0.05) / (l + 0.05);
    }

    /**
     * @param color the background color
     * @return the text color adapted to the background (either back or white smoke)
     */
    public static Color getTextColor(Color color) {
        if (useBlack(color)) {
            return Color.BLACK;
        } else {
            return Color.WHITESMOKE;
        }
    }


    public static Color getColorForSpeed(double value) {
        if (value < DEFAULT_MIN_SPEED || value > DEFAULT_MAX_SPEED) {
            return Color.MAGENTA;
        }
        double hue = BLUE_HUE + (RED_HUE - BLUE_HUE) * (value - DEFAULT_MIN_SPEED) / (DEFAULT_MAX_SPEED - DEFAULT_MIN_SPEED);
        return Color.hsb(hue, 1.0, 1.0);
    }


    private static List<Color> createFXInteractiveColors() {
        List<Color> result = new ArrayList<>();
        //result.add(Color.AQUA);
        result.add(Color.FUCHSIA);
        result.add(Color.DARKGREY);
        result.add(Color.GOLD);
        result.add(Color.CHARTREUSE);
        result.add(Color.ORANGE);
        return Collections.unmodifiableList(result);
    }

    private static List<Color> createFXColors() {
        List<Color> result = new ArrayList<>();
        result.add(Color.ANTIQUEWHITE);
        result.add(Color.GREEN);
        result.add(Color.BLUE);
        result.add(Color.RED);
        result.add(Color.BLUEVIOLET);
        result.add(Color.BEIGE);
        result.add(Color.GOLD);
        result.add(Color.CYAN);
        result.add(Color.ORANGERED);
        result.add(Color.FORESTGREEN);
        return Collections.unmodifiableList(result);
    }

    private static List<Color> createATCColors() {
        List<Color> result = new ArrayList<>();
        result.add(Color.BISQUE);
        result.add(Color.CADETBLUE);
        result.add(Color.CHOCOLATE);
        result.add(Color.DARKKHAKI);
        result.add(Color.ORANGE);
        result.add(Color.THISTLE);
        return result;
    }

    private static Color getATCColor() {
        controllerColorIndex++;
        return ATC_COLORS_LIST.get(controllerColorIndex % ATC_COLORS_LIST.size());
    }

}
