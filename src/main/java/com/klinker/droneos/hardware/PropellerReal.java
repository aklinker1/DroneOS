package com.klinker.droneos.hardware;

import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Log;

public class PropellerReal extends Propeller {

    public PropellerReal(int pin) {
        super(pin);
    }

    @Override
    protected boolean onInitialization() {
        // TODO: initialization procedure for ESC
        return false;
    }

    @Override
    public void setThrust(double percent) {
        super.setThrust(percent);
        Log.w("hardware", "Thrust for real propeller not set up.");
    }

}
