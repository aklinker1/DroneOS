package com.klinker.droneos.hardware;

import java.io.IOException;
import java.util.List;

import com.klinker.droneos.utils.Log;
import com.pi4j.wiringpi.Gpio;

public class FlightControllerReal extends FlightController {

    Runtime runTime;

    FlightControllerReal(int strafeXPin, int strafeYPin, int anglePin, int liftPin) {
        super(strafeXPin, strafeYPin, anglePin, liftPin);
        try {
            runTime = Runtime.getRuntime();
            runTime.exec("gpio mode " + 1 + " pwm");
            runTime.exec("gpio pwm-ms");
            runTime.exec("gpio pwmc 192");
            runTime.exec("gpio pwmr 2000");
        } catch (Exception e) {
            System.out.println("Exception occured: " + e.getMessage());
        }
    }

	@Override
	public void move(double strafeX, double strafeY, double angle, double lift) {
        try {
            System.out.println("gpio pwm " + 1 + " " + (lift * 100 + 100));
            runTime.exec("gpio pwm " + 1 + " " + (lift * 100 + 100));
        } catch (Exception e) {
            System.out.println("Exception occured: " + e.getMessage());
        }
    }
    
}