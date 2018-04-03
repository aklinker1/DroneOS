package com.klinker.droneos.utils;

import com.klinker.droneos.utils.math.Point;

public class Utils {

    ///// Async Helpers ////////////////////////////////////////////////////////

    /**
     * Makes the current thread sleep/pause for the specified amount of time.
     * <p>
     * <code>1 s = 1000 ms</code>
     *
     * @param ms The number of milliseconds the current thread should sleep for.
     */
    public static void sleep(long ms) {
        if (ms <= 0) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Log.e("utils", "Error trying to sleep", e);
        }
    }


    ///// General Utilities ////////////////////////////////////////////////////

    /**
     * This function takes remaps a value in a range to a different range
     * linearly.
     *
     * For example: map(1, 0, 2, 1, 3) = 2.
     * Explanation: The number 1 is 50% of the range between
     * 0 and 2, therefor 50% the range of 1 to 3 is 2.
     *
     * @param value The value respect to the original range.
     * @param lowOriginal The lower point for the original range.
     * @param highOriginal The upper point for the original range.
     * @param lowNew The lower point for the new range.
     * @param highNew The upper point for the new range.
     * @return The scaled value for value in the original range scaled to a
     *         new range.
     */
    public static double map(double value, 
                             double lowOriginal, double highOriginal, 
                             double lowNew, double highNew) {
        return lowNew + (highNew - lowNew) * (value - lowOriginal) / (highOriginal - lowOriginal);
    }

    /**
     * This function takes remaps a value in a range to a different range
     * linearly, but if the value is outside the initial range it will limit the 
     * ouput to the new ranges.
     * 
     * @param value The value respect to the original range.
     * @param lowOriginal The lower point for the original range.
     * @param highOriginal The upper point for the original range.
     * @param lowNew The lower point for the new range.
     * @param highNew The upper point for the new range.
     * @return The scaled value for value in the original range scaled to a
     *         new range.
     */
    public static double mapLimit(double value, 
                                  double lowOriginal, double highOriginal, 
                                  double lowNew, double highNew) {
        if (value < lowOriginal) 
            return lowNew;
        if (value > highOriginal)
            return highNew;
        return map(value, lowOriginal, highOriginal, lowNew, highNew);
    }

    // Source: https://stackoverflow.com/questions/639695/how-to-convert-latitude-or-longitude-to-meters
    public static double distanceConversion(Point p1, Point p2) { // generally used geo measurement function
        double lat1 = p1.y;
        double lon1 = p1.x;
        double lat2 = p2.y;
        double lon2 = p2.x;

        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180)
                * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d * 1000; // meters
    }

}
