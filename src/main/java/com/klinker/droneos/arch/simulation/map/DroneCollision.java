package com.klinker.droneos.arch.simulation.map;

import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.simulation.Simulation;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;
import com.klinker.droneos.utils.math.Point;

/**
 * This class is represented as a circle. The x and y positions are based off
 */
public class DroneCollision extends CircleCollision {

    ///// Static Variables /////////////////////////////////////////////////////

    /**
     * The width of the boat in [m]. Used in torque calculations.
     */
    public static final double WIDTH = 0.25;

    /**
     * The mass of the boat in [g]. Used for moment of inertia and reference.
     */
    public static final double MASS = 0.864;

    /**
     * The max lift [g].
     */
    public static final double MAX_LIFT = 1.7776 + 0.864;

    /**
     * Max linear acceleration [m/s^2].
     */
    public static final double MAX_STRAFE = 30;

    /**
     * The max rotation speed of the boat in [rad/s]. 90 [deg/s].
     */
    public static final double MAX_OMEGA = Math.PI / 2.0;

    /**
     * The max rotation speed of the boat in [rad/s^2]. 90 [deg/s^2].
     */
    public static final double MAX_ALPHA = Math.PI / 2.0 * 60;

    /**
     * The max velocity of the boat in [m/s].
     */
    public static final Point MAX_VELOCITY = new Point(10, 10, 5);

    /**
     * Max Servo signal sent from the RPi.
     */
    public static final int MAX_PWM = 2500;

    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * Velocity in [m/s].
     */
    private Point mVelocity;

    /**
     * The change in velocity between updates in [m/s/f].
     */
    private Point mAcceleration;

    /**
     * The angle in radians, from the positive x-axis.
     */
    private double mAngle;

    /**
     * Angular Velocity in [rad/s]
     */
    private double mAngularVelocity;

    /**
     * The change in angular velocity between updates in [rad/s/f].
     */
    private double mAngularAcceleration;

    /**
     * A reference to the Simulation.
     */
    private Simulation mSimulation;

    /**
     * PWM Signal. 0 to MAX_PWM -> 0 to MAX_LIFT
     */
    private int mLiftPWM;

    /**
     * PWM Signal. 0 to MAX_PWM -> -MAX_ALPHA to MAX_ALPHA
     */
    private int mAnglePWM;

    /**
     * PWM Signal. 0 to MAX_PWM -> -MAX_STRAFE to MAX_STRAFE
     */
    private int mStrafeXPWM;

    /**
     * PWM Signal. 0 to MAX_PWM -> -MAX_STRAFE to MAX_STRAFE
     */
    private int mStrafeYPWM;

    ///// Constructors /////////////////////////////////////////////////////////

    /**
     * Default constructor. Creates a buoy at a given location and gives it a
     * color.
     * @param x The x, or longitude, location of the buoy.
     * @param y The y, or latitude, location of the buoy.
     * @param angle The angle, in radians, that the boat is facing.
     */
    public DroneCollision(Simulation simulation, double x, double y, double angle) {
        super(new Point(x, y), WIDTH);
        this.c.z = 0;
        mVelocity = new Point(0, 0, 0);
        mAcceleration = new Point(0, 0, 0);
        mAngle = angle;
        mAngularVelocity = 0;
        mAngularAcceleration = 0;
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
        // Update third order
        this.mAcceleration.x = Utils.map(getStrafeXPWM(), 0, MAX_PWM, -MAX_STRAFE, MAX_STRAFE);
        this.mAcceleration.y = Utils.map(getStrafeYPWM(), 0, MAX_PWM, -MAX_STRAFE, MAX_STRAFE);
        this.mAcceleration.z = (Utils.map(getLiftPWM(), 0, MAX_PWM, 0, MAX_LIFT) - MASS) * 9.81 / MASS;
        this.mAngularAcceleration = Utils.map(getAnglePWM(), 0, MAX_PWM, -MAX_ALPHA, MAX_ALPHA);


        // Update Second order
        mVelocity.x += (mAcceleration.x * mVelocity.x < 0 ? 2 : 1) * mAcceleration.x / Simulation.FPS;
        mVelocity.y += (mAcceleration.y * mVelocity.y < 0 ? 2 : 1) * mAcceleration.y / Simulation.FPS;
        mVelocity.z += (mAcceleration.z * mVelocity.z < 0 ? 2 : 1) * mAcceleration.z / Simulation.FPS;
        mAngularVelocity = mAngularAcceleration / Simulation.FPS;

        if (mAcceleration.x == 0) mVelocity.x -= 0.1 * mVelocity.x;
        if (mAcceleration.y == 0) mVelocity.y -= 0.1 * mVelocity.y;
        // if (mAngularAcceleration == 0) mAngularVelocity -= 0.1 * mAngularVelocity;
        // if (Math.abs(mVelocity.x) < 0.01) mVelocity.x = 0;
        // if (Math.abs(mVelocity.y) < 0.01) mVelocity.y = 0;

        if (Math.abs(mVelocity.x) > MAX_VELOCITY.x)
            mVelocity.x = MAX_VELOCITY.x * (mVelocity.x < 0 ? -1 : 1);
        if (Math.abs(mVelocity.y) > MAX_VELOCITY.y)
            mVelocity.y = MAX_VELOCITY.y * (mVelocity.y < 0 ? -1 : 1);
        if (Math.abs(mVelocity.z) > MAX_VELOCITY.z)
            mVelocity.z = MAX_VELOCITY.z * (mVelocity.z < 0 ? -1 : 1);

        // Update first order
        this.c.z += mVelocity.z / 60.0;

        if (this.c.z < 0) {
            if (mVelocity.z < -0.5) {
                Log.w("simulation", "Hit the ground hard at " + mVelocity.z + " m/s");
            } else if (mVelocity.z < -1) {
                Log.e("simulation", "Crashed into the ground at " + mVelocity.z + " m/s");
                Core.exit(Core.EXIT_CODE_SIMULATION_FATAL);
            }
            this.c.z = 0;
            mVelocity.z = 0;
            mAcceleration.z = 0;
        }
        if (this.c.z > 0) {
            double xSpeed = mVelocity.x * Math.cos(mAngle) + mVelocity.y * Math.sin(mAngle);
            double ySpeed = mVelocity.x * Math.sin(mAngle) + mVelocity.y * Math.cos(mAngle);
            this.c.x += xSpeed / 60.0;
            this.c.y += ySpeed / 60.0;
            mAngle += mAngularVelocity / 60.0;
        }
        // if (this.c.z > 5) {
        //     Log.e("simulation", "Crashed into the ceiling at " + mVelocity.z + " m/s");
        //     Core.exit(Core.EXIT_CODE_SIMULATION_FATAL);
        // }

        // Log.d("simulation", "position: " + this.c.toString());
        Log.v("simulation", "velocity: " + mVelocity.toString());
        // Log.w("simulation", "accelera: " + mAcceleration.toString());
    }

    ///// Getters //////////////////////////////////////////////////////////////

    public synchronized Point getLinearAcceleration() {
        return mAcceleration;
    }

    public synchronized double getAngularAcceleration() {
        return mAngularAcceleration;
    }

    public synchronized double getAngle() {
        return mAngle;
    }

    public synchronized Point getPoint() {
        return c;
    }

    public synchronized int getStrafeXPWM() {
        return this.mStrafeXPWM;
    }

    public synchronized int getStrafeYPWM() {
        return this.mStrafeYPWM;
    }

    public synchronized int getLiftPWM() {
        return this.mLiftPWM;
    }

    public synchronized int getAnglePWM() {
        return this.mAnglePWM;
    }

    ///// Setters //////////////////////////////////////////////////////////////

    public synchronized void setStrafeXPWM(int signal) {
        this.mStrafeXPWM = signal;
    }

    public synchronized void setStrafeYPWM(int signal) {
        this.mStrafeYPWM = signal;
    }

    public synchronized void setLiftPWM(int signal) {
        this.mLiftPWM = signal;
    }

    public synchronized void setAnglePWM(int signal) {
        this.mAnglePWM = signal;
    }

}
