package com.klinker.droneos.arch.simulation.map;

import com.klinker.droneos.utils.Utils;
import com.klinker.droneos.utils.math.Point;

public class Waypoint extends Point {

    private final double radius;

    public Waypoint(double x, double y, double radius) {
        super(x, y);
        this.radius = radius;
    }

    public double getRadius() {
        return this.radius;
    }

    public boolean checkPoint(Point other) {
        return Utils.distanceConversion(this, other) < this.radius;
    }

}