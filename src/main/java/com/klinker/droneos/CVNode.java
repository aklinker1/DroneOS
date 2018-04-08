package com.klinker.droneos;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.nodes.Node;
import com.klinker.droneos.cv.CVUtils;
import com.klinker.droneos.cv.ImageWindow;
import com.klinker.droneos.hardware.Camera;

import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 * TODO: Node specific documentation.
 */
public class CVNode extends Node {

    ///// Constants ////////////////////////////////////////////////////////////

    public static final String MESSAGE_MANUAL_FIND = "manual-find";

    ///// Member Variables /////////////////////////////////////////////////////

    private ImageWindow mWindow;

    private Camera mCamera;

    private boolean mIsLandingVisible;

    private boolean mIsManualFindFinsihed;

    ///// Construction /////////////////////////////////////////////////////////

    public CVNode(String dataPath) {
        super(dataPath);
        mCamera = new Camera();
        mIsLandingVisible = false;
        mIsManualFindFinsihed = false;
        mWindow = new ImageWindow("CVNode");
    }


    ///// Node Callbacks ///////////////////////////////////////////////////////

    /**
     * @see Node#onReceiveMessage(Message) Node's documentation for this method
     */
    @Override
    public void onReceiveMessage(Message message) {
        switch (message.getName()) {
            case MESSAGE_MANUAL_FIND:
                if (mIsLandingVisible) mIsManualFindFinsihed = true;
                break;
        }
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
        mCamera.open();
        mWindow.setVisible(true);
    }

    @Override
    protected void onManualFindTask() {
        super.onManualFindTask();
        while (!mIsManualFindFinsihed) {
            Mat frame = mCamera.getFrame();
            Point vector = CVUtils.findChessboard(frame);
            mWindow.loadImage(frame);
            mIsLandingVisible = vector != null;
        }
    }

    @Override
    protected void onFinishUpTask() {
        super.onFinishUpTask();
        mWindow.closeWindow();
        mCamera.close();
    }

}
