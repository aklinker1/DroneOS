package com.klinker.droneos.arch.simulation.map;

import com.klinker.droneos.arch.simulation.Simulation;
import com.klinker.droneos.arch.simulation.SimulationUtils;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;
import com.klinker.droneos.utils.math.Point;

/**
 * This class is represented as a circle. The x and y positions are based off
 */
public class BoatCollision extends CircleCollision {

    ///// Static Variables /////////////////////////////////////////////////////

    /**
     * The width of the boat in [m]. Used in torque calculations.
     */
    public static final double WIDTH = 0.5;

    /**
     * The mass of the boat in [kg]. Used for moment of inertia and reference.
     * Equivalent to 50 [lbs] at g = 9.8 [m/s^2].
     */
    public static final double MASS = 22.6796185;
    /**
     * The second moment of inertia of the boat in [kg m^2]. This boat is
     * represented by a circle, hence the equation used: <pre>I = mr^2</pre>
     */
    public static final double M_OF_INERTIA = MASS * Math.pow(WIDTH / 2.0, 2);

    /**
     * The max thrust of the propulsion systems in [N]. Equivalent to 7.5 [lbf].
     */
    public static final double MAX_THRUST = 33.36166221188144;

    /**
     * The max rotation speed of the boat in [rad/s]. Equivalent to 15 [deg/s]
     */
    public static final double MAX_OMEGA = Math.PI / 6.0;

    /**
     * The max velocity of the boat in [m/s].
     */
    public static final double MAX_VELOCITY = 3; // m/s

    /**
     * How much the boat drifts through turns. 0 is none, and at 1 it drifts 
     * compeletely, no turning whatso ever. Think of drifting as the combination
     * of two vectors who's length equals the length of the original vector, and
     * the difference in angles is less than what the original difference would 
     * have been.
     */
    public static final double DRIFT_FACTOR = 0.5; // percent

    /**
     * The max torque that can be caused by the motor in [Nm]. Equivalent to
     * the max thrust of each motor, one in reverse, times the width (Draw a
     * FBD if you are confused why I use WIDTH rather than WIDTH / 2).
     */
    public static final double MAX_TORQUE = 2 * MAX_THRUST * WIDTH;

    /**
     * The simulated pin the left propeller is plugged into.
     */
    public static final int PROP_LEFT_PIN = 0;

    /**
     * The simulated pin the right propeller is plugged into.
     */
    public static final int PROP_RIGHT_PIN = 1;


    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * Velocity in [m/s].
     */
    private double mVelocity;

    /**
     * The change in velocity between updates in [m/s/f].
     */
    private double mLinearAcceleration;

    /**
     * Angular Velocity in [rad/s]
     */
    private double mAngularVelocity;

    /**
     * The change in angular velocity between updates in [rad/s/f].
     */
    private double mAngularAcceleration;

    /**
     * The angle in radians, from the positive x-axis.
     */
    private double mAngle;

    /**
     * The thrust from the left thruster in N.
     */
    private double mLThruster;

    /**
     * The thrust from the right thruster in N.
     */
    private double mRThruster;

    /**
     * A reference to the Simulation.
     */
    private Simulation mSimulation;


    ///// Constructors /////////////////////////////////////////////////////////

    /**
     * Default constructor. Creates a buoy at a given location and gives it a
     * color.
     * @param x The x, or longitude, location of the buoy.
     * @param y The y, or latitude, location of the buoy.
     * @param angle The angle, in radians, that the boat is facing.
     */
    public BoatCollision(Simulation simulation, double x, double y,
                         double angle) {
        super(new Point(x, y), SimulationUtils.mToGPS(0.5));
        mAngle = angle;
        mVelocity = 0;
        mLinearAcceleration = 0;
        mAngularVelocity = 0;
        mAngularAcceleration = 0;
        mLThruster = 0;
        mRThruster = 0;
        mSimulation = simulation;
    }


    ///// Member Methods ///////////////////////////////////////////////////////

    /**
     * Applies the physics of the boat's motion resistance to the actual
     * velocity of the boat.
     *
     * Update position then change velocity and angle.
     */
    public void updatePosition() {
        // update the drifting
        double driftV = mVelocity * DRIFT_FACTOR;
        this.c.x += mPerSecToGPSPerFrame(driftV * Math.cos(mAngle));
        this.c.y += mPerSecToGPSPerFrame(driftV * Math.sin(mAngle));

        // update position - translate m/s to gps/s
        this.mAngle += mAngularVelocity / mSimulation.getFPS() * (1 - DRIFT_FACTOR * DRIFT_FACTOR); // rad/s / f/s =rad/f
        if (Math.abs(mAngularVelocity) < 0.0001) mAngularVelocity = 0;
        double v = mVelocity * (1 - DRIFT_FACTOR);
        this.c.x += mPerSecToGPSPerFrame(v * Math.cos(mAngle));
        this.c.y += mPerSecToGPSPerFrame(v * Math.sin(mAngle));

        // Update Velocities: F = ma = mv/t => v = Ft/m
        double forces = linearResistance(mVelocity) + getThrustRight()
                + getThrustLeft();
        if (mVelocity > MAX_VELOCITY) Log.d("simulation.json", "forces: " +
                forces);
        if (mVelocity > MAX_VELOCITY) Log.d("simulation.json", "resistance: " +
                linearResistance(mVelocity));
        setLinearAcceleration(forces / mSimulation.getFPS() / MASS);
        mVelocity += getLinearAcceleration();
        if (Math.abs(mVelocity) < 0.0001) mVelocity = 0;

        // Update Angular velocity:
        // I alpha = sum(Q) = w/2*R - w/2*L + resistance, alpha = omega / t
        // omega = [w/2 (R - L) + resistance] * (t / I)
        double torques = WIDTH / 2.0 * (getThrustRight() - getThrustLeft()) +
                angularResistance(mAngularVelocity);
        setAngularAcceleration(torques / mSimulation.getFPS() / M_OF_INERTIA);
        this.mAngularVelocity += getAngularAcceleration();

        if (mVelocity > MAX_VELOCITY) Log.d("simulation.json", "Surpassed max " +
                "velocity: " + mVelocity);
        if (mAngularVelocity > MAX_OMEGA) Log.d("simulation.json", "Surpassed max " +
                "angular velocity: " + mAngularVelocity);
    }

    /**
     * Unit conversion from m/s to (lat or long)/frame
     * @param mps The velocity in meters per second
     * @return The distance moved per frame in terms of GPS coordinates.
     */
    private double mPerSecToGPSPerFrame(double mps) {
        // x [m/s] * (101.038674 [m/gps] * FPS [f/s])^-1 = y [gps/f]
        return mps / (114300 * mSimulation.getFPS());
    }

    /**
     * Returns a resistance due to the hull's friction at a given velocity.
     *
     * The relationship is linear based off convenience.
     *
     * @param velocity The velocity of the boat in m/s.
     * @return At 3m/s, the resistance will be the
     *         -{@link BoatCollision#MAX_THRUST} in N. At 0m/s, the
     *         friction will be 0 N. This limits the boat to it's max speed.
     */
    private double linearResistance(double velocity) {
        int sign = velocity < 0 ? -1 : 1;
        return sign * Utils.map(Math.abs(velocity),
                0 /*m/s*/, MAX_VELOCITY /*m/s*/,
                0 /*N*/, -2 * MAX_THRUST /*N*/
        );
    }

    /**
     * Returns a resistance due to the hull's rotating friction at a given
     * angular velocity.
     *
     * The relationship is linear based off convenience.
     *
     * @param angularVelocity The angular velocity of the boat in rad/s.
     * @return At {@link BoatCollision#MAX_OMEGA} in rad/s, the angular
     *         resistance should be equal to the oposite of the maximum
     *         possible torque caused by the propellers to prevent
     *         acceleration. At 0 rad/s, the resistance should be zero.
     */
    private double angularResistance(double angularVelocity) {
        int sign = angularVelocity < 0 ? -1 : 1;
        return sign * Utils.map(
                Math.abs(angularVelocity),
                0 /*rad/s*/, MAX_OMEGA /*rad/s*/,
                0 /*Nm*/, -MAX_TORQUE /*Nm*/
        );
    }


    ///// Getters //////////////////////////////////////////////////////////////

    public synchronized double getThrustLeft() {
        return mLThruster;
    }

    public synchronized double getThrustRight() {
        return mRThruster;
    }

    public synchronized double getLinearAcceleration() {
        return mLinearAcceleration;
    }

    public synchronized double getAngularAcceleration() {
        return mAngularAcceleration;
    }

    public synchronized double getLatitude() {
        return c.y;
    }

    public synchronized double getLongitude() {
        return c.x;
    }

    public synchronized double getVelocity() {
        return mVelocity;
    }

    public synchronized double getAngle() {
        return mAngle;
    }

    public synchronized Point getPoint() {
        return c;
    }


    ///// Setters //////////////////////////////////////////////////////////////

    private synchronized void setLinearAcceleration(double a) {
        this.mLinearAcceleration = a;
    }

    private synchronized void setAngularAcceleration(double a) {
        this.mAngularAcceleration = a;
    }

    public synchronized void setThrustLeft(double leftPercent) {
        assert leftPercent >= -1 && leftPercent <= 1;
        this.mLThruster = MAX_THRUST * leftPercent;
    }

    public synchronized void setThrustRight(double rightPercent) {
        assert rightPercent >= -1 && rightPercent <= 1;
        this.mRThruster = MAX_THRUST * rightPercent;
    }

    public synchronized void setThrusters(double leftPercent,
                                          double rightPercent) {
        assert leftPercent >= -1 && leftPercent <= 1;
        assert rightPercent >= -1 && rightPercent <= 1;
        this.mLThruster = MAX_THRUST * leftPercent;
        this.mRThruster = MAX_THRUST * rightPercent;
    }

}
