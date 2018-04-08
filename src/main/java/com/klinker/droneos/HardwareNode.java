package com.klinker.droneos;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.nodes.Node;
import com.klinker.droneos.utils.Utils;

public class HardwareNode extends Node {

    ///// Constants ////////////////////////////////////////////////////////////

    public static final double FULL_TORQUE_ANGLE_CUTOFF = Math.PI / 4;

    ///// Member Variables /////////////////////////////////////////////////////

    ///// Construction /////////////////////////////////////////////////////////

    public HardwareNode(String dataPath) {
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
    protected void onManualFindTask() {
        super.onManualFindTask();
    }

    @Override
    protected void onFinishUpTask() {
        super.onFinishUpTask();
    }

    ///// Member Methods ///////////////////////////////////////////////////////

}
