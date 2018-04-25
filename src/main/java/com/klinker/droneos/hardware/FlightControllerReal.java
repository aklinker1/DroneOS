package com.klinker.droneos.hardware;

import java.io.IOException;
import java.util.List;

import com.klinker.droneos.utils.Log;
import com.pi4j.wiringpi.Gpio;

public class FlightControllerReal extends FlightController {

    FlightControllerReal(int strafeXPin, int strafeYPin, int anglePin, int liftPin) {
        super(strafeXPin, strafeYPin, anglePin, liftPin);
        Gpio.pinMode(7, Gpio.PWM_OUTPUT);
        Gpio.pwmSetMode(Gpio.PWM_MODE_MS);
        Gpio.pwmSetClock(384);
        Gpio.pwmSetRange(1000);
    }

	@Override
	public void move(double strafeX, double strafeY, double angle, double lift) {
        Gpio.pwmWrite(7, 75);
    }
    
}