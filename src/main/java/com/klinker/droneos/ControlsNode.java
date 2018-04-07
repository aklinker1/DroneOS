package com.klinker.droneos;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.klinker.droneos.arch.communication.messages.JsonMessage;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.nodes.Node;
import com.klinker.droneos.hardware.FlightController;

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

    ///// Member Variables /////////////////////////////////////////////////////

    private boolean mIsManual;

    private FlightController mFlightController;


    ///// Construction /////////////////////////////////////////////////////////

    public ControlsNode(String dataPath) {
        super(dataPath);
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
        if (message.getClass() != JsonMessage.class) return;
        JsonObject json = ((JsonMessage) message).getData();

        if (message.getName().equals("drive") && json.get("isManual").getAsBoolean() == mIsManual) {
            if (mIsManual) {
                // Call manual driving
            } else {
                // call automatic flight
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
            // case "leftThrust":
            //     return new JsonPrimitive(mLeftPropeller.getThrust());
            // case "rightThrust":
            //     return new JsonPrimitive(mRightPropeller.getThrust());
            default:
                return null;
        }
    }


    ///// Task Callbacks ///////////////////////////////////////////////////////

    @Override
    protected void onInitializingTask() {
        super.onInitializingTask();
    }

    @Override
    protected void onFinishUpTask() {
        super.onFinishUpTask();
    }


    ///// Member Methods ///////////////////////////////////////////////////////

}
