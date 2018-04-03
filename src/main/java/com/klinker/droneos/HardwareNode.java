package com.klinker.droneos;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.klinker.droneos.arch.communication.messages.JsonMessage;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.nodes.Node;
import com.klinker.droneos.arch.simulation.Simulation;
import com.klinker.droneos.arch.simulation.map.BoatCollision;
import com.klinker.droneos.hardware.Propeller;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;

public class HardwareNode extends Node {

    ///// Constants ////////////////////////////////////////////////////////////

    public static final double FULL_TORQUE_ANGLE_CUTOFF = Math.PI / 4;

    ///// Member Variables /////////////////////////////////////////////////////

    private boolean mIsManual;

    private Propeller mLeftPropeller;

    private Propeller mRightPropeller;

    ///// Construction /////////////////////////////////////////////////////////

    public HardwareNode(String dataPath) {
        super(dataPath);
        mLeftPropeller = Propeller.newInstance(getData().get("leftPin").getAsInt());
        mRightPropeller = Propeller.newInstance(getData().get("rightPin").getAsInt());
    }

    ///// Node Callbacks ///////////////////////////////////////////////////////

    /**
     * The {@link ControlsNode} only acts on a couple of messages
     * <ol>
     *     <li>To change to set the next destination vector: <code>{
    *           "x": double,
    *           "y": double,
     *       "manual": boolean
     *     }</code></li>
     *     <li>To switch between manual and automatic: <code>{
     *       "manual": boolean
    *         }</code></li>
     * </ol>
     * @param message The message received
     */
    @Override
    protected void onReceiveMessage(Message message) {
        if (message.getClass() != JsonMessage.class)
            return;
        JsonObject json = ((JsonMessage) message).getData();

        if (message.getName().equals("drive") && json.get("isManual").getAsBoolean() == mIsManual) {
            if (mIsManual) {
                // Call manual driving
            } else {
                setThrustFromVector(json.get("x").getAsDouble(), json.get("y").getAsDouble());
            }
        } else if (message.getName().equals("control-switch")) {
            mIsManual = json.get("manual").getAsBoolean();
        } else {
            System.out.println("ignored");
        }
    }

    @Override
    protected JsonPrimitive queryProperty(String property, JsonObject inputs) {
        switch (property) {
        case "leftThrust":
            return new JsonPrimitive(mLeftPropeller.getThrust());
        case "rightThrust":
            return new JsonPrimitive(mRightPropeller.getThrust());
        default:
            return null;
        }
    }

    ///// Task Callbacks ///////////////////////////////////////////////////////

    @Override
    protected void onInitializingTask() {
        super.onInitializingTask();
        mLeftPropeller.initialize();
        mRightPropeller.initialize();
    }

    @Override
    protected void onFinishUpTask() {
        super.onFinishUpTask();
    }

    ///// Member Methods ///////////////////////////////////////////////////////

    private void setThrustFromVector(double x, double y) {
        // atan2 returns the angle of a vector, not the arctan of a value. This 
        // is because the atan function doesn't take into account the inacuracy
        // when x < 0. atan2 does.
        double diffAngle = Math.atan2(y, x) - Simulation.getSingleton().getBoat().getAngle();
        Log.d("controls", "x: " + x);
        Log.d("controls", "y: " + y);
        Log.d("controls", "angle: " + Math.toDegrees(diffAngle));
        double moment = Utils.mapLimit(diffAngle, -FULL_TORQUE_ANGLE_CUTOFF, FULL_TORQUE_ANGLE_CUTOFF,
                BoatCollision.MAX_TORQUE, -BoatCollision.MAX_TORQUE);
        double velocityPercent = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        double diffThrust = Utils.map(moment, -BoatCollision.MAX_TORQUE, BoatCollision.MAX_TORQUE, 1, -1);
        double lThrust = -diffThrust;
        double rThrust = diffThrust;
        double offset = 1 - Math.max(lThrust, rThrust);
        lThrust += offset * velocityPercent;
        rThrust += offset * velocityPercent;

        mLeftPropeller.setThrust(lThrust);
        mRightPropeller.setThrust(rThrust);
    }

}
