package com.klinker.droneos.arch.simulation.map;

import com.klinker.droneos.utils.math.Point;

public class LineSegmentCollision extends LineCollision {

    public LineSegmentCollision(Point p1, Point p2) {
        super(p1, p2);
    }

    @Override
    protected Point collideLine(LineCollision other) {
        Point p = this.lineLineCollision(this, other);
        return LineSegmentCollision.assertBounds(this, p) ? p : null;
    }

    @Override
    protected Point collideLineSegment(LineSegmentCollision other) {
        Point p = collideLine(other);
        return LineSegmentCollision.assertBounds(other, p)
                && LineSegmentCollision.assertBounds(this, p)
                ? p : null;
    }

    @Override
    protected Point collideCircle(BuoyCollision other) {
        Point p = this.lineCircleCollision(this, other);
        return LineSegmentCollision.assertBounds(this, p) ? p : null;
    }

    public static boolean assertBounds(LineSegmentCollision collision,
                                       Point point) {
        if (point == null) return false;
        double xMin = Math.min(collision.p1.x, collision.p2.x);
        double yMin = Math.min(collision.p1.y, collision.p2.y);
        double xMax = Math.max(collision.p1.x, collision.p2.x);
        double yMax = Math.max(collision.p1.y, collision.p2.y);
        if (point.x <= xMax && point.x >= xMin
                && point.y <= yMax && point.y >= yMin) {
            return true;
        } else {
            return false;
        }
    }


}
