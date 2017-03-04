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

package gov.nasa.arc.atc.metrics.imagecreation;

import gov.nasa.arc.atc.core.DataModel;

import java.beans.PropertyChangeListener;

/**
 * @author ahamon
 */
public abstract class AbstractPixelCreator implements PixelCreator {

    private DataModel data;

    private int xOffSet = 0;
    private int yOffSet = 0;


    @Override
    public void setDataModel(DataModel dataModel) {
        data = dataModel;
    }

    @Override
    public double[][] calculate(int minTime, int maxTime) {
        if (data == null) {
            throw new IllegalArgumentException("data model is null");
        }
        if (minTime < 0) {
            throw new IllegalArgumentException("minTime cannot be negative: " + minTime);
        }
        if (maxTime < minTime) {
            throw new IllegalArgumentException("maxTime=" + maxTime + " cannot be lower than minTime=" + minTime);
        }
        return new double[0][0];
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setImageOffset(int x, int y) {
        xOffSet = x;
        yOffSet = y;
    }

    protected int getxOffSet() {
        return xOffSet;
    }

    protected int getyOffSet() {
        return yOffSet;
    }

    protected DataModel getDataModel() {
        return data;
    }

}
