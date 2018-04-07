package com.klinker.droneos.hardware;

import com.klinker.droneos.arch.Core;

public abstract class FlightController {

    public static FlightController newInstance(int strafeXPin, int strafeYPin, int anglePin, int liftPin) {
        if (Core.IS_SIMULATION) {
            // return new FlightControllerSim(strafeXPin, strafeYPin, anglePin, liftPin);
            return null;
        } else {
            return new FlightControllerReal(strafeXPin, strafeYPin, anglePin, liftPin);
        }
    }

    private int mStrafeXPin;
    private int mStrafePinY;
    private int mAnglePin;
    private int mLiftPin;

    protected FlightController(int strafeXPin, int strafeYPin, int anglePin, int liftPin) {
        this.mStrafeXPin = strafeXPin;
        this.mStrafePinY = strafeYPin;
        this.mAnglePin = anglePin;
        this.mLiftPin = liftPin;
    }

    public abstract void move(double strafeX, double strafeY, double angle, double lift);

}