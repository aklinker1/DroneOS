package com.klinker.droneos.hardware;

import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;

import java.util.Arrays;

public abstract class Lidar {

    public static void main(String[] args) {
        Lidar lidar = Lidar.newInstance(0, 5, SAMPLE_RATE_500);
        try {
            Log.v("lidar", "##### Opening #####");
            lidar.open();
            Log.v("lidar", "##### Opened #####");
            Log.v("lidar", "##### Waiting #####");
            Utils.sleep(2000);
            Log.v("lidar", "##### Closing #####");
            lidar.close();
            Log.v("lidar", "##### Closed #####");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Defines a new Lidar Sensor.
     * @param serialPort Generally 0, but may very with multiple connected
     *                   devices.
     * @param motorSpeed In revolutions per second (hz). Must be between 1
     *                   and ten.
     * @param sampleRate The number of scans per second (hz). Must be either
     *                   500, 750, or 1000.
     * @return Either a real or simulated lidar.
     */
    public static Lidar newInstance(int serialPort,
                                    int motorSpeed,
                                    int sampleRate) {
        return new LidarReal(serialPort, motorSpeed, sampleRate);
    }

    public static final int SAMPLE_RATE_500 = 1;
    public static final int SAMPLE_RATE_750 = 2;
    public static final int SAMPLE_RATE_1000 = 3;
    public static final int BAUD_RATE = 115200;
    public static final int DATA_BITS = 8;
    public static final int STOP_BITS = 1;

    private int mSerialPort;
    private int mMotorSpeed;
    private int mSampleRate;
    private final int[] mData;
    private boolean mIsOpen;

    protected Lidar(int port, int motorSpeed, int sampleRate) {
        assert motorSpeed >= 1 && motorSpeed <= 10;
        assert sampleRate == SAMPLE_RATE_500 || sampleRate == SAMPLE_RATE_750
                || sampleRate == SAMPLE_RATE_1000;
        mSerialPort = port;
        mMotorSpeed = motorSpeed;
        mSampleRate = sampleRate;
        mData = new int[360];
        mIsOpen = false;
    }

    public final int getSerialPort() {
        return mSerialPort;
    }

    public final int getMotorSpeed() {
        return mMotorSpeed;
    }

    public final int getSampleRate() {
        return mSampleRate;
    }

    public final int[] getPoints() {
        return Arrays.copyOf(mData, mData.length);
    }

    /**
     * Open the serial communication port, and wait for the motor to reach
     * full speed. It then starts data collection on a seperate thread.
     * @throws Exception If the LIDAR fails to start, it will throw an exception
     */
    public abstract void open() throws Exception;

    /**
     * Closes the connection to the port, and stops all other threads that
     * are running in the background.
     */
    public abstract void close();

    protected synchronized void setIsOpen(boolean isOpen) {
        mIsOpen = isOpen;
    }

    public synchronized boolean isOpen() {
        return mIsOpen;
    }

}
