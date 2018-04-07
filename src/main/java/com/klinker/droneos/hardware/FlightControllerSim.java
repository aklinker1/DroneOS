package com.klinker.droneos.hardware;

import com.klinker.droneos.arch.simulation.Simulation;
import com.klinker.droneos.arch.simulation.map.DroneCollision;
import com.klinker.droneos.utils.Utils;

public class FlightControllerSim extends FlightController {

    private DroneCollision mDrone;

    FlightControllerSim(int strafeXPin, int strafeYPin, int anglePin, int liftPin) {
        super(strafeXPin, strafeYPin, anglePin, liftPin);
        mDrone = Simulation.getSingleton().getDrone();
    }

	@Override
	public void move(double strafeX, double strafeY, double angle, double lift) {
        mStrafeXPWM = (int) Math.round(Utils.map(strafeX, -1, 1, 0, MAX_PWM));
        mStrafeYPWM = (int) Math.round(Utils.map(strafeY, -1, 1, 0, MAX_PWM));
        mLiftPWM = (int) Math.round(Utils.map(lift, 0, 1, 0, MAX_PWM));
        mAnglePWM = (int) Math.round(Utils.map(angle, -1, 1, 0, MAX_PWM));
        mDrone.setStrafeXPWM(mStrafeXPWM);
        mDrone.setStrafeYPWM(mStrafeYPWM);
        mDrone.setStrafeXPWM(mLiftPWM);
        mDrone.setStrafeXPWM(mAnglePWM);
    }
    
}