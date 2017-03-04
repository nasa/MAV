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

package gov.nasa.arc.atc.physics;

import gov.nasa.arc.atc.factories.WindFactory;
import gov.nasa.arc.atc.geography.Position;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ahamon
 */
public class SimpleAltitudeConstantWind implements WindData {


    private final List<SimpleWindLayer> layers;


    public SimpleAltitudeConstantWind(List<SimpleWindLayer> windLayers) {
        layers = windLayers.stream().sorted((l1, l2) -> Integer.compare(l1.getMinimumAltitude(), l2.getMaximumAltitude())).collect(Collectors.toList());
        for (int i = 0; i < layers.size() - 1; i++) {
            if (layers.get(i).getMaximumAltitude() > layers.get(i + 1).getMinimumAltitude()) {
                throw new IllegalArgumentException("tow layers overlap: " + layers.get(i) + " & " + layers.get(i + 1));
            }
        }
    }

    public SimpleAltitudeConstantWind() {
        this(Collections.emptyList());
    }


    @Override
    public WindParameters getWindAt(Position position) {
        for (SimpleWindLayer layer : layers) {
            if (layer.contains(position.getAltitude())) {
                return layer.getWindParameters();
            }
        }
        return WindFactory.NO_WIND_PARAMETER;
    }

    @Override
    public List<WindLayer> getLayers() {
        return Collections.unmodifiableList(layers);
    }
}
