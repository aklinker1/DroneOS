package com.klinker.droneos.cv;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.nodes.Node;
import com.klinker.droneos.hardware.Camera;
import com.klinker.droneos.utils.io.ImageFile;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.nodes.Node;
import com.klinker.droneos.utils.io.ImageFile;
import org.opencv.core.Mat;

/**
 * TODO: Node specific documentation.
 */
public class CVNode extends Node {

    ///// Constants ////////////////////////////////////////////////////////////

    ///// Member Variables /////////////////////////////////////////////////////

    private Camera mCamera;

    ///// Construction /////////////////////////////////////////////////////////

    public CVNode(String dataPath) {
        super(dataPath);
        mCamera = new Camera();
        mCamera.open();
    }


    ///// Node Callbacks ///////////////////////////////////////////////////////

    /**
     * @see Node#onReceiveMessage(Message) Node's documentation for this method
     */
    @Override
    public void onReceiveMessage(Message message) {

    }

    /**
     * @see Node#queryProperty(String, JsonObject) Node's Documentation for
     * this method.
     */
    @Override
    protected JsonPrimitive queryProperty(String property, JsonObject inputs) {
        return null;
    }

    @Override
    protected void onExit() {
        mCamera.close();
    }


    ///// Task Callbacks ///////////////////////////////////////////////////////


    @Override
    protected void onInitializingTask() {
        super.onInitializingTask();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 3000) {
            Mat frame = mCamera.getFrame();
            ImageFile file = new ImageFile(
                    Core.DIR_SENSOR_OUTPUT + "/camera",
                    System.currentTimeMillis() + ".jpg"
            );
            file.write(frame);
        }
    }

    @Override
    protected void onFinishUpTask() {
        super.onFinishUpTask();
    }

}
