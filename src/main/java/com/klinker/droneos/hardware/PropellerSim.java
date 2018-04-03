package com.klinker.droneos.hardware;

import com.klinker.droneos.arch.simulation.Simulation;
import com.klinker.droneos.arch.simulation.map.BoatCollision;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;

public class PropellerSim extends Propeller {

    private BoatCollision mBoat;

    public PropellerSim(int pin) {
        super(pin);
        mBoat = Simulation.getSingleton().getBoat();
    }

    @Override
    public boolean onInitialization() {
        Log.v("hardware", "Propeller at pin " + getPin() + " initialized...");
        Utils.sleep(3000);
        return true;
    }

    public void setThrust(double percent) {
        super.setThrust(percent);
        if (getPin() == BoatCollision.PROP_LEFT_PIN) {
            mBoat.setThrustLeft(getThrust());
        } else {
            mBoat.setThrustRight(getThrust());
        }
    }

}
