package com.klinker.droneos.hardware;

import java.io.IOException;
import java.util.List;

import com.pi4j.component.servo.ServoProvider;
import com.klinker.droneos.utils.Log;
import com.pi4j.component.servo.ServoDriver;
import com.pi4j.component.servo.impl.RPIServoBlasterProvider;

public class FlightControllerReal extends FlightController {

    private ServoDriver mThrust;

    FlightControllerReal(int strafeXPin, int strafeYPin, int anglePin, int liftPin) {
        super(strafeXPin, strafeYPin, anglePin, liftPin);
        try {
            ServoProvider servoProvider = new RPIServoBlasterProvider();
            mThrust = servoProvider.getServoDriver(servoProvider.getDefinedServoPins().get(7));
            Log.d("FC", "Thrust Pin: " + mThrust.getPin().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void move(double strafeX, double strafeY, double angle, double lift) {
		mThrust.setServoPulseWidth((int) (lift * 1000) + 1000);
    }
    
}