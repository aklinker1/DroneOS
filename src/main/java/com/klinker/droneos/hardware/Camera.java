package com.klinker.droneos.hardware;

import java.io.File;

import com.klinker.droneos.arch.Core;
import com.klinker.droneos.utils.Log;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

/**
 * Sensor wrapper for getting images from a camera.
 */
public class Camera {

    ///// Native Library Declarations //////////////////////////////////////////

    static {
        File openCV;
        String bits = System.getProperty("sun.arch.data.model");
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            Core.OS_IS_WINDOWS = true;
            openCV = new File("libs/opencv" + bits, "opencv_java320.dll");
        } else if (os.contains("mac")) {
            Core.OS_IS_MAC = true;
            openCV = new File("libs/opencv" + bits, "opencv_java320.dylib");
        } else {
            Core.OS_IS_LINUX = true;
            openCV = new File("libs/opencv" + bits, "opencv_java320.so");
        }
        System.load(openCV.getAbsolutePath());
    }

    ///// Member Variable //////////////////////////////////////////////////////

    /**
     * OpenCV object used to get {@link Mat} of image.
     */
    private VideoCapture mCapture;

    /**
     * The prot that the camera is connected to.
     */
    private int mPort;


    ///// Constructors /////////////////////////////////////////////////////////

    /**
     * Default constructor. Calls {@link Camera#Camera(int)} with a port of 1.
     *
     * @see Camera#Camera(int) Camera(int)
     */
    public Camera() {
        this(0);
    }

    /**
     * Constructs a Camera connected to the given port.
     *
     * @param port The port that the camera is connected to, default is 1.
     */
    public Camera(int port) {
        mPort = port;
        mCapture = new VideoCapture();
    }


    ///// Member Methods ///////////////////////////////////////////////////////

    /**
     * Opens the connection to the {@link VideoCapture} to start getting
     * frames. This must be called before {@link Camera#getFrame()}.
     */
    public void open() {
        try {
            mCapture.open(mPort);
        } catch (Exception e) {
            Log.e(
                    "cv",
                    "Could not open Camera",
                    e
            );
            Core.exit(Core.EXIT_CODE_CV_FATAL);
        }
    }

    /**
     * Closes connection to camera. Must be opened again to get another frame.
     */
    public void close() {
        if (!Core.OS_IS_MAC) {
            mCapture.release();
        }
    }

    /**
     * Gets the next frame. If this method is called consecutively, it will
     * wait a certain amount of time before it is able to capture again. This
     * is because of the WebCam's frame-rate limit.
     *
     * @return The image from the camera. If the {@link Camera#mCapture} is
     * closed, it will return null.
     */
    public Mat getFrame() {
        if (!mCapture.isOpened()) return null;
        Mat frame = new Mat();
        mCapture.read(frame);
        return frame;
    }

}
