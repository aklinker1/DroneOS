package com.klinker.droneos.hardware;

import com.fazecast.jSerialComm.SerialPort;
import static com.fazecast.jSerialComm.SerialPort.*;

import java.util.Deque;
import java.util.LinkedList;

import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;

import arduino.Arduino;

public class FlightControllerReal extends FlightController {

    private String MOVE = "m%04d%04d%04d%04d";
    private final String INITIAL = "m1000150015001500";

    private SerialPort mPort;
    private Arduino arduino;

    private Thread commandThread = null;
    private LinkedList<Command> queue = new LinkedList<>();

    private boolean open = false;

    private class Command {
        String command;
        long time;

        Command(String command) {
            this.command = command;
            this.time = System.currentTimeMillis();
        }

        void execute() {
            Log.d("flight_controller", "Comand: " + command);
            arduino.serialWrite(this.command);
            // mPort.writeBytes(command.getBytes(), command.length());
        }
    }

    FlightControllerReal(int strafeXPin, int strafeYPin, int anglePin, int liftPin) {
        super(strafeXPin, strafeYPin, anglePin, liftPin);
        arduino = new CArduino("ttyACM0", 9600);
        commandThread = new Thread(() -> {
            Command c;
            while (open) if (hasCommands()) {
                c = dequeueCommand();
                if (System.currentTimeMillis() - c.time < 20) c.execute();
                else Log.d("flight_controller", "Skipped command");
            }
        });
    }

    @Override
    public void initialize() {
        arduino.openConnection();
        open = true;
        commandThread.start();
        sendCommand(INITIAL);
    }

    @Override
    public void move(double strafeX, double strafeY, double angle, double lift) {
        if (armed) {
            sendCommand(String.format(
                    MOVE, 
                    (int) Math.round(lift * 1000) + 1000, 
                    (int) Math.round(strafeX * 1000) + 1500,
                    (int) Math.round(strafeY * 1000) + 1500, 
                    (int) Math.round(angle * 1000) + 1500
            ));
        }
    }

    @Override
    public void stop() {
        sendCommand(INITIAL);
        Utils.sleep(2000);
        sendCommand("a0");
        open = false;
        arduino.closeConnection();
        commandThread.interrupt();
    }

    @Override
    public void arm(boolean armed) {
        super.arm(armed);
        sendCommand("a" + (armed ? 1 : 0));
    }

    @Override
    public void hover(boolean hover) {
        sendCommand("h" + (hover ? 1 : 0));
    }

    @Override
    public void drop(boolean drop) {
        sendCommand("d" + (drop ? 1 : 0));
    }

    private void sendCommand(String command) {
        enqueueCommand(new Command(command + ';'));
    }

    public synchronized void enqueueCommand(Command c) {
        queue.addLast(c);
    }

    public synchronized Command dequeueCommand() {
        return queue.removeFirst();
    }

    public synchronized boolean hasCommands() {
        return !queue.isEmpty();
    }

}