package com.klinker.droneos.arch.simulation.map;

import com.klinker.droneos.arch.simulation.SimulationUtils;
import com.klinker.droneos.utils.math.Point;

/**
 * This class is represented as a circle.
 */
public class BuoyCollision extends CircleCollision {

    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * The color of the buoy, stored as an int
     */
    private int color;


    ///// Constructors /////////////////////////////////////////////////////////

    /**
     * Default constructor. Creates a buoy at a given location and gives it a
     * color.
     * @param x The x location of the buoy.
     * @param y The y location of the buoy.
     * @param color A hexadecimal string starting with a '#'. Check out
     *              {@link java.awt.Color} for more details.
     */
    public BuoyCollision(double x, double y, String color) {
        super(new Point(x, y), SimulationUtils.mToGPS(0.25));
        this.color = Integer.parseInt(color.substring(1), 16); // remove the #
    }


    ///// Getters //////////////////////////////////////////////////////////////

    public int getColor() {
        return color;
    }

}
