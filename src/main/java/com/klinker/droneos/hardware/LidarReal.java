package com.klinker.droneos.hardware;

import com.fazecast.jSerialComm.SerialPort;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;
import com.klinker.droneos.utils.io.TextFile;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;
import com.klinker.droneos.utils.io.TextFile;

import java.io.InputStream;

public class LidarReal extends Lidar {

    private SerialPort mPort;
    private Thread mCollectionThread;

    protected LidarReal(int port, int motorSpeed, int sampleRate)
            throws ArrayIndexOutOfBoundsException {
        super(port, motorSpeed, sampleRate);
        try {
            mPort = SerialPort.getCommPorts()[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d(
                    "sensors",
                    "Failed to connect to the LIDAR. Is it unplugged?"
            );
            throw e;
        }
        mCollectionThread = null;
    }

    @Override
    public void open() throws Exception {
        mPort.openPort();
        assert mPort.isOpen();

        mPort.setComPortParameters(
                BAUD_RATE,
                DATA_BITS,
                STOP_BITS,
                SerialPort.NO_PARITY
        );
        mPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        mPort.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_BLOCKING,
                10000,
                10000
        );
        setIsOpen(true);
        mPort.getOutputStream().flush();

        reset();
        //setMotorSpeed(getMotorSpeed());
        //setSampleRate(getSampleRate());

        mCollectionThread = new CollectionThread();
        mCollectionThread.start();
    }

    @Override
    public void close() {
        setIsOpen(false);
        if (mCollectionThread != null) mCollectionThread.interrupt();
        sendCommand("DX\n", null);
        mPort.closePort();
    }

    private synchronized boolean sendCommand(String command, String expected) {
        System.out.print("command: " + command);
        System.out.print("expected: " + expected);
        mPort.writeBytes(command.getBytes(), command.length());

        String response = "";
        if (expected != null) {
            byte[] buffer = new byte[expected.length()];
            mPort.readBytes(buffer, buffer.length);
            response = new String(buffer);
            if (!"".equals(response.trim()))
                System.out.print("Response: " + response);
        } else {
            System.out.println("\nResponse: No response expected.");
        }
        return response.equals(expected);
    }

    private void reset() {
        String resetCommand = "RR\n";
        sendCommand(resetCommand, null);
        waitForReady();
    }

    private void waitForReady() {
        while (!isReady()) {
            Utils.sleep(500);
        }
    }

    private boolean isReady() {
        String command = "MZ\n";
        String expected = "MZ00\n";
        boolean success = sendCommand(command, expected);
        System.out.println(success);
        return success;
    }

    private boolean setMotorSpeed(int speed) {
        String command = String.format("MS%02d\n", speed);
        String expected = command;
        boolean success = sendCommand(command, expected);
        System.out.println(success);
        return success;
    }

    private boolean setSampleRate(int sampleRate) {
        String command = String.format("LR%02d\n", sampleRate);
        String expected = command;
        boolean success = sendCommand(command, expected);
        System.out.println(success);
        return success;
    }

    private class CollectionThread extends Thread {
        @Override
        public void run() {
            super.run();
            String command = "DS\n";
            String expected = "DS00P\n";
            final int scanSize = 7;
            sendCommand(command, expected);
            /*mPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                    0, 0);*/
            InputStream stream = mPort.getInputStream();
            TextFile file = new TextFile(Core.DIR_SENSOR_OUTPUT, "lidar.txt");
            StringBuilder builder = new StringBuilder();
            int index = 0;
            int[] data = new int[scanSize];
            while (!isInterrupted()) {
                try {
                    if (stream.available() != 0) {
                        int c = stream.read();
                        data[index] = c;
                        builder.append(c);
                        index++;
                        if (index == scanSize) {
                            index = 0;
                            builder.append('\n');
                            parseBytes(data);
                        } else {
                            builder.append(',');
                        }
                    }
                } catch (Exception e) {

                }
                    /*byte[] scan = new byte[scanSize];
                    mPort.readBytes(scan, scan.length);
                    System.out.println(Arrays.toString(scan));*/
            }
            file.write(builder.toString());
        }

        private void parseBytes(int[] data) {
            int distance = (data[4] << 8) + data[3];
            float angle = ((data[4] << 8) + data[3])/16f;
        }
    }

}
