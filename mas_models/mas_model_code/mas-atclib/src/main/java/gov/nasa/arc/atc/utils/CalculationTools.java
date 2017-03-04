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

/********************************************************************
 *                                                                  *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.atc.utils;

import gov.nasa.arc.atc.geography.Segment;

import static java.lang.Double.valueOf;

/**
 * @author krantz
 * @author hamon
 */
public class CalculationTools {

    public static final double EPSILON = 0.0000000000001;

    private CalculationTools() {
        // private utility constructor
    }

    /**
     * calculateBearing. Calculates the bearing between two points given the
     * latitudes and longitudes for these points.
     * http://www.movable-type.co.uk/scripts/latlong.html
     *
     * @param lat1  From Latitude
     * @param long1 From Longitude
     * @param lat2  To Latitude
     * @param long2 To Longitude
     * @return double bearing, the bearing in degrees.
     */
    public static double calculateBearing(double lat1, double long1, double lat2, double long2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double lambda1 = Math.toRadians(long1);
        double lambda2 = Math.toRadians(long2);
        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    public static double calculateBearing(Segment segment) {
        return calculateBearing(
                segment.getFromWaypoint().getLatitude(), segment.getFromWaypoint().getLongitude(),
                segment.getToWaypoint().getLatitude(), segment.getToWaypoint().getLongitude());
    }

    /**
     * Calculates the distance from current position to input latitude and
     * longitude based on Haversine formula (see
     * http://www.movable-type.co.uk/scripts/latlong.html#cosine-law)
     *
     * @param lat1  to travel from
     * @param long1 to travel from
     * @param lat2  to travel to
     * @param long2 to travel to
     * @return distance from current position to new position
     */
    public static double distanceFromTo(double lat1, double long1, double lat2, double long2) {
        double r = Constants.EARTH_RADIOUS * Constants.KM2NM; // Earth radious
        // in NM
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(long2 - long1);

        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2) + Math.cos(phi1) * Math.cos(phi2) * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }// calculateDistance

    /**
     * angularDistance. Calculates the angular distance given the flight
     * altitude in feet, and the traveled distance in nautical miles.
     *
     * @param altitude
     * @param distance
     * @return angDist angular distance in radiant
     */
    public static double calculateAngularDistance(double distance, double altitude) {
        double rConstant = Constants.EARTH_RADIOUS * Constants.KM2METER;
        double altitudeMeter = altitude * Constants.FT2METER;
        double distanceMeter = distance * Constants.NM2METER;
        return distanceMeter / (altitudeMeter + rConstant);
    }

    /**
     * newLatitude. Calculates a new latitude given initial latitude, angular
     * distance between points, and bearing.
     * http://www.movable-type.co.uk/scripts/latlong.html
     *
     * @param lat1            starting latitude
     * @param angularDistance angular distance between points, use angular
     *                        distance method.
     * @param bearing         in degrees
     * @return phi2, new latitude in degrees.
     */
    public static double newLatitude(double lat1, double angularDistance, double bearing) {
        double phi1 = Math.toRadians(lat1);
        double zeta = angularDistance; /// in radiant
        double brng = Math.toRadians(bearing);
        double phi2 = Math.asin(Math.sin(phi1) * Math.cos(zeta) + Math.cos(phi1) * Math.sin(zeta) * Math.cos(brng));
        phi2 = Math.toDegrees(phi2);
        return phi2;
    }

    /**
     * newLongitude. Method for calculating a new longitude given initial
     * latitude and longitude, destination latitude, angular distance between
     * the points, and the bearing in degrees.
     * http://www.movable-type.co.uk/scripts/latlong.html
     *
     * @param lat1            initial latitude
     * @param long1           initial longitude
     * @param lat2            final latitude
     * @param angularDistance angular distance between initial and final point
     * @param bearing         in degrees.
     * @return final longitude in degrees
     */
    public static double newLongitude(double lat1, double long1, double lat2, double angularDistance, double bearing) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double lambda1 = Math.toRadians(long1);
        double zeta = angularDistance; // in radiant
        double brng = Math.toRadians(bearing);
        double lambda2 = lambda1 + Math.atan2(Math.sin(brng) * Math.sin(zeta) * Math.cos(phi1), Math.cos(zeta) - Math.sin(phi1) * Math.sin(phi2));
        return Math.toDegrees(lambda2);
    }

    // /**
    // *
    // *
    // * @param lead
    // * @param trail
    // * @return
    // */
    // public static double calculateInTrailSeparation(Flight lead, Flight
    // trail) {
    //
    // return 0;
    // }

    /**
     * minumumRequiredSeparation. Method for deciding the minimum separation
     * required, given the distance to the airport.
     *
     * @param distance the distance to the airport in [NM]
     * @return
     */
    public static double minumumRequiredSeparation(double distance) {
        // double distance = CalculationTools.distanceFromTo(wpt1.getLatitude(),
        // wpt1.getLongitude(),
        // wpt2.getLatitude(), wpt2.getLongitude());
        if (distance <= 10) {
            return Constants.MIN_SEP_10;
        } else if (distance > 10 && distance <= 40) {
            return Constants.MIN_SEP_40;
        }
        return Constants.MIN_SEP_150;
    }

    /**
     * Truncate double value to 2 decimal places
     *
     * @param value to truncate
     * @return value truncated to 2 decimal places
     */
    public static double truncateToTwoDecimals(double value) {
        if (value > 0)
            return Math.floor(value * 100) / 100;
        else
            return Math.ceil(value * 100) / 100;
    }// truncateToTwoDecimals


    public static double toCoordinate(String string) {
        //TODO find right class to store this method
        int multiplier = 1;
        boolean hasSuffix = false;
        if (string.contains("N")) {
            hasSuffix = true;
        } else if (string.contains("E")) {
            hasSuffix = true;
        } else if (string.contains("S")) {
            hasSuffix = true;
            multiplier = -1;
        } else if (string.contains("W")) {
            hasSuffix = true;
            multiplier = -1;
        }
        //
        if ((string.length() == 5) && !hasSuffix || (string.length() == 6 && hasSuffix)) {
            // only one digit for hours
            return multiplier * (valueOf(string.substring(0, 1)) + valueOf(string.substring(1, 3)) / 60.0 + valueOf(string.substring(3, 5)) / 3600.0);
        } else if ((string.length() == 6) && !hasSuffix || (string.length() == 7 && hasSuffix)) {
            // two digits for hours
            return multiplier * (valueOf(string.substring(0, 2)) + valueOf(string.substring(2, 4)) / 60.0 + valueOf(string.substring(4, 6)) / 3600.0);
        } else if ((string.length() == 7) && !hasSuffix || (string.length() == 8 && hasSuffix)) {
            // three digits for hours
            return multiplier * (valueOf(string.substring(0, 3)) + valueOf(string.substring(3, 5)) / 60.0 + valueOf(string.substring(5, 7)) / 3600.0);
        } else {
            throw new IllegalStateException("Wrong format for coordinate _" + string + "_");
        }
    }


    /**
     * For testing
     *
     * @param args
     */

    public static void main(String[] args) {
        System.out.println("Distance: " + distanceFromTo(38.09972, -76.66417, 38.69444, -76.02278));
        System.out.println("Bearing:" + calculateBearing(38.09972, -76.66417, 38.69444, -76.02278));

        System.out.println("Distance from 39*71 to 39*70.5 ->" + distanceFromTo(39, 71, 39, 70.5));
        System.out.println("Distance from 39.5*70.5 to 39*70.5 ->" + distanceFromTo(39.38858, 70.5, 39, 70.5));
    }

}
