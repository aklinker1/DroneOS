package com.klinker.droneos.hardware;

import com.klinker.droneos.arch.Core;
import com.klinker.droneos.utils.Utils;

public abstract class FlightController {

    public static int MAX_PWM = 2500;

    public static FlightController newInstance(int strafeXPin, int strafeYPin, int anglePin, int liftPin) {
        if (Core.IS_SIMULATION) {
            return new FlightControllerSim(strafeXPin, strafeYPin, anglePin, liftPin);
        } else {
            return new FlightControllerReal(strafeXPin, strafeYPin, anglePin, liftPin);
        }
    }

    protected int mStrafeXPin;
    protected int mStrafeYPin;
    protected int mAnglePin;
    protected int mLiftPin;

    protected int mStrafeXPWM;
    protected int mStrafeYPWM;
    protected int mAnglePWM;
    protected int mLiftPWM;

    protected boolean mIsInitialized = false;

    protected FlightController(int strafeXPin, int strafeYPin, int anglePin, int liftPin) {
        this.mStrafeXPin = strafeXPin;
        this.mStrafeYPin = strafeYPin;
        this.mAnglePin = anglePin;
        this.mLiftPin = liftPin;
        
        this.mStrafeXPWM = 0;
        this.mStrafeYPWM = 0;
        this.mAnglePWM = 0;
        this.mLiftPWM = 0;
    }

    /**
     * The percent from -1 to 1 for each movement
     */
    public abstract void move(double strafeX, double strafeY, double angle, double lift);

    public int getStrafeXPWM() {
        return mStrafeXPWM;
    }

    public int getStrafeYPWM() {
        return mStrafeYPWM;
    }

    public int getAnglePWM() {
        return mAnglePWM;
    }

    public int getLiftPWM() {
        return mLiftPWM;
    }

    public void initialize() {
        move(0, 0, 0, 0);
        Utils.sleep(100);
        move(0, 0, 0, 1);
        Utils.sleep(100);
        move(0, 0, 0, 0);
        Utils.sleep(2000);
        mIsInitialized = true;
    }

}