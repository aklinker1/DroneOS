package com.klinker.droneos.hardware;

public class FlightControllerReal extends FlightController {

    FlightControllerReal(int strafeXPin, int strafeYPin, int anglePin, int liftPin) {
        super(strafeXPin, strafeYPin, anglePin, liftPin);
    }

	@Override
	public void move(double strafeX, double strafeY, double angle, double lift) {
		
    }
    
}