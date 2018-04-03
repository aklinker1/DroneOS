package com.klinker.droneos.arch.simulation.map;

import com.klinker.droneos.utils.math.Point;
import com.klinker.droneos.utils.math.Point;

/**
 * This class is represented as a infinitely long line
 */
public class LineCollision extends CollisionObject {

    public Point p1;
    public Point p2;

    public LineCollision(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    protected Point collideLine(LineCollision other) {
        return this.lineLineCollision(this, other);
    }

    @Override
    protected Point collideLineSegment(LineSegmentCollision other) {
        Point p = collideLine(other);
        return LineSegmentCollision.assertBounds(other, p) ? p : null;
    }

    @Override
    protected Point collideCircle(BuoyCollision other) {
        return this.lineCircleCollision(this, other);
    }

    @Override
    public String toString() {
        return String.format(
                "%s { p1: %s, p2: %s }",
                getClass().getSimpleName(),
                p1.toString(),
                p2.toString()
        );
    }
}
