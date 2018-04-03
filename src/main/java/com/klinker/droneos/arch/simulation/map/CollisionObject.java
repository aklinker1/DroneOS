package com.klinker.droneos.arch.simulation.map;

import com.klinker.droneos.utils.math.Point;

public abstract class CollisionObject {

    /**
     * Checks whether or not this collision object collides with another
     * collision object
     *
     * @param other The other collision object to check with this one.
     * @return The {@link Point} that there is a collision or null if there is
     * no collision.
     */
    public Point collide(CollisionObject other) {
        if (other.getClass() == LineCollision.class) {
            return collideLine((LineCollision) other);
        }
        if (other.getClass() == LineSegmentCollision.class) {
            return collideLineSegment((LineSegmentCollision) other);
        }
        if (other instanceof CircleCollision) {
            return collideCircle((BuoyCollision) other);
        }
        return null;
    }

    protected abstract Point collideLine(LineCollision other);

    protected abstract Point collideLineSegment(LineSegmentCollision other);

    protected abstract Point collideCircle(BuoyCollision other);

    /**
     * Determines the collision between a line and a circle.
     * @param l The {@link LineCollision}
     * @param cir The {@link CircleCollision}
     * @return The Point where they collide or null if there is no collision.
     */
    protected Point lineCircleCollision(LineCollision l, CircleCollision cir) {
        double m1 = (l.p2.y - l.p1.y) / (l.p2.x - l.p1.x);
        double A = l.p1.y - m1 * l.p1.x - cir.c.y;
        double a = m1 * m1 + 1f;
        double b = 2f * m1 * A - 2f * cir.c.x;
        double c = A * A + cir.c.x * cir.c.x - cir.r * cir.r;
        double b2 = b * b;
        double ac4 = 4f * a * c;
        if (b2 > ac4) {
            // There are 2 collision points (1 and 2), currently just choosing
            // one.
            // TODO: Determine which point is closer
            double x1 = (-b + Math.sqrt(b2 - ac4)) / (2f * a);
            double y1 = m1 * (x1 - l.p1.x) + l.p1.y;
            double x2 = (-b +  Math.sqrt(b2 - ac4)) / (2f * a);
            double y2 = m1 * (x1 - l.p1.x) + l.p1.y;
            return new Point(x1, y1);
        } else if (b2 == ac4) {
            // Only 1 intersect.
            double x = -b / 2f / a;
            double y = m1 * (x - l.p1.x) + l.p1.y;
            return new Point(x, y);
        } else {
            // No intersects
            return null;
        }
    }

    protected Point lineLineCollision(LineCollision l1, LineCollision l2) {
        double m1 = (l1.p2.y - l1.p1.y) / (l1.p2.x - l1.p1.x);
        double m2 = (l2.p2.y - l2.p1.y) / (l2.p2.x - l2.p1.x);
        if (m1 == m2) { // parallel
            return null;
        } else {
            double x =
                    (m1 * l1.p1.x - m2 * l2.p1.x - l1.p1.y + l2.p1.y)
                            / (m1 - m2);
            double y = m1 * (x - l1.p1.x) + l1.p1.y;
            return new Point(x, y);
        }
    }

    protected Point circleCircleCollision(CircleCollision c1,
                                          CircleCollision c2) {
        double d = Math.sqrt(Math.pow(c1.c.x - c2.c.x, 2)
                + Math.pow(c1.c.y - c2.c.y, 2)
        );
        if (d <= (c1.r + c2.r)) {
            double x = (c1.c.x + c2.c.x) / 2f;
            double y = (c1.c.y + c2.c.y) / 2f;
            return new Point(x, y);
        }
        return null;
    }

}
