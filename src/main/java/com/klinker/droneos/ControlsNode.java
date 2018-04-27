package com.klinker.droneos;

import javax.naming.ldap.ControlFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.klinker.droneos.arch.communication.messages.JsonMessage;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.nodes.Node;
import com.klinker.droneos.hardware.FlightController;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;

/**
 * To enter MANUAL mode, send a message with the following format:
 * <code>{
 *     "name": "switch-controls",
 *     "manual": true
 * }</code> and a name="drive"
 *
 * and to enter back into autonomous:
 * <code>{
 *     "name": "switch-controls",
 *     "manual": false
 * }</code> and a name="control-switch"
 *
 * {@link ControlsNode#onReceiveMessage(Message)}
 */
public class ControlsNode extends Node {

    ///// Constants ////////////////////////////////////////////////////////////

    public static final double FULL_TORQUE_ANGLE_CUTOFF = Math.PI / 4;

    public static final String MESSAGE_CONTROL = "control";
    public static final String MESSAGE_ARM = "arm";

    ///// Member Variables /////////////////////////////////////////////////////

    private boolean mIsManual;

    private FlightController mFlightController;

    ///// Construction /////////////////////////////////////////////////////////

    public ControlsNode(String dataPath) {
        super(dataPath);
        mIsManual = true;
        mFlightController = FlightController.newInstance(0, 1, 2, 3);
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

        if (message.getName().equals(MESSAGE_ARM)) {
            mFlightController.arm(json.get("arm").getAsBoolean());
        } else if (message.getName().equals(MESSAGE_CONTROL) && json.get("isManual").getAsBoolean() == mIsManual) {
            mFlightController.move(
                json.get("strafeX").getAsDouble(), 
                json.get("strafeY").getAsDouble(),
                json.get("angle").getAsDouble(), 
                json.get("lift").getAsDouble()
            );
        } else if (message.getName().equals("control-switch")) {
            mIsManual = json.get("manual").getAsBoolean();
        } else {
            Log.d("controls", "Ignored message: " + message.toString());
        }
    }

    @Override
    protected JsonPrimitive queryProperty(String property, JsonObject inputs) {
        switch (property) {
        case "strafeX":
            return new JsonPrimitive(mFlightController.getStrafeXPWM());
        case "strafeY":
            return new JsonPrimitive(mFlightController.getStrafeYPWM());
        case "angle":
            return new JsonPrimitive(mFlightController.getAnglePWM());
        case "lift":
            return new JsonPrimitive(mFlightController.getLiftPWM());
        default:
            return null;
        }
    }

    ///// Task Callbacks ///////////////////////////////////////////////////////

    @Override
    protected void onInitializingTask() {
        super.onInitializingTask();
        mFlightController.initialize();
    }

    @Override
    protected void onManualFindTask() {
        super.onManualFindTask();
        while (!mFlightController.isArmed()) 
            Utils.sleep(100);
        Utils.sleep(1000);
        while (mFlightController.isArmed())
            Utils.sleep(100);
    }

    @Override
    protected void onFinishUpTask() {
        super.onFinishUpTask();
        mFlightController.stop();
    }

    ///// Member Methods ///////////////////////////////////////////////////////
}
