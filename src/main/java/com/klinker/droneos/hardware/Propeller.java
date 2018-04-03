package com.klinker.droneos.hardware;

import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.simulation.map.BoatCollision;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.simulation.map.BoatCollision;
import com.klinker.droneos.utils.Log;

public abstract class Propeller {

    public static Propeller newInstance(int pin) {
        if (Core.IS_SIMULATION) {
            if (pin == BoatCollision.PROP_LEFT_PIN
                    || pin == BoatCollision.PROP_RIGHT_PIN) {
                return new PropellerSim(pin);
            } else {
                Log.e(
                        "hardware",
                        "FATAL: Simulated pin must be 0 (left) or 1 (right)" +
                                " for propellers"
                );
                Core.exit(Core.EXIT_CODE_CONTROLS_FATAL);
                return null;
            }
        } else {
            return new PropellerReal(pin);
        }
    }

    private int mPin;

    private double mThrust;

    private boolean mInitialized;

    public Propeller(int pin) {
        this.mPin = pin;
        mThrust = 0;
        mInitialized = false;
    }

    public int getPin() {
        return mPin;
    }

    public double getThrust() {
        return mThrust;
    }

    public final void initialize() {
        boolean success = onInitialization();
        if (success) {
            onInitialized();
        } else {
            Log.e(
                    "hardware",
                    "FATAL: Propeller at pin " + mPin
                            + " did not successfully initialize"
            );
            Core.exit(Core.EXIT_CODE_CONTROLS_FATAL);
        }
    }

    protected abstract boolean onInitialization();

    private void onInitialized() {
        mInitialized = true;
    }

    protected final boolean isInitialized() {
        return mInitialized;
    }

    public void setThrust(double percent) {
        if (!isInitialized()) {
            Log.e(
                    "hardware",
                    "FATAL: Tried to set thrust of uninitialized propeller"
            );
            Core.exit(Core.EXIT_CODE_CONTROLS_FATAL);
        }
        if (percent < -1 || percent > 1) {
            Log.e(
                    "hardware",
                    "Tried setting speed of propeller at pin " + getPin()  +
                            " to " + percent + "%"
            );
            percent = percent < 0 ? -1 : 1;
        }
        mThrust = percent;
    }

}
