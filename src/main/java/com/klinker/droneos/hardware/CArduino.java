package com.klinker.droneos.hardware;

import java.io.OutputStream;
import java.io.PrintWriter;

import com.fazecast.jSerialComm.SerialPort;

import arduino.Arduino;

public class CArduino extends Arduino {
    private OutputStream stream;

    public CArduino(String desc, int baud) {
        super(desc, baud);
    }

    @Override
    public boolean openConnection() {
        try {
            boolean s = super.openConnection();
            stream = getSerialPort().getOutputStream();
            return s;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void serialWrite(String s) {
        SerialPort comPort = getSerialPort();
        comPort.writeBytes(s.getBytes(), s.length());
        // comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        // PrintWriter pout = new PrintWriter(comPort.getOutputStream());
        // pout.print(s);
        // pout.flush();
    }
}