package com.klinker.droneos.arch.simulation.map;

import com.klinker.droneos.utils.math.Point;
import com.klinker.droneos.utils.math.Point;

public class CircleCollision extends CollisionObject {

    public Point c;
    public double r;

    public CircleCollision(Point c, double r) {
        this.c = c;
        this.r = r;
    }

    @Override
    protected Point collideLine(LineCollision other) {
        return this.lineCircleCollision(other, this);
    }

    @Override
    protected Point collideLineSegment(LineSegmentCollision other) {
        Point p = this.lineCircleCollision(other, this);
        return LineSegmentCollision.assertBounds(other, p) ? p : null;
    }

    @Override
    protected Point collideCircle(BuoyCollision other) {
        return this.circleCircleCollision(this, other);
    }

    @Override
    public String toString() {
        return String.format(
                "%s { center: %s, radius: %f }",
                getClass().getSimpleName(),
                c.toString(),
                r
        );
    }

}
