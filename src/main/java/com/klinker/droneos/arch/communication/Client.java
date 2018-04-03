package com.klinker.droneos.arch.communication;

import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.manifest.Device;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.utils.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * The client is part a connection to a {@link Server}. It can receive and
 * send {@link Message}s one at a time. Warning: The Server must be started
 * before the client can connect, otherwise it will pause the thread it is
 * opened on until the server is connected.
 *
 * A client can only be connected to a single server, while a server can be
 * connected to multiple clients.
 *
 * If a client must send a message to another client, the message will be
 * forwarded through the server, that way clients don't have to know who is
 * connected where.
 */
public class Client {

    /**
     * Starts an instance of the client. This method is used to test out the
     * relationship between the client and server on the same computer. To
     * start both, run
     * <pre>src/main/resources/scripts/start-messenger-test.bat</pre>
     * @param args No args are used to run this configuration.
     */
    public static void main(String[] args) {
        System.out.println("Client Window");
        Client client = new Client(new Device(), "127.0.0.1", 7777);
        client.setOnMessageReceivedListener(message ->
                System.out.println("Received: " + message.getData())
        );
        client.open();
        String text = "";
        while (!"exit".equals(text)) {
            System.out.print("Enter Message: ");
            Scanner input = new Scanner(System.in);
            text = input.nextLine();
            input.close();
            //TextMessage message = new TextMessage(null, null, text);
            //client.sendMessage(message);
        }
        client.close();
    }


    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * The IP-Address of the {@link Server} to connect to.
     */
    private final Device mDevice;

    /**
     * The IP-Address of the {@link Server} to connect to.
     */
    private final String mIpAddress;

    /**
     * The port that the {@link Server} is running on.
     */
    private final int mPort;

    /**
     * The socket that contains the connection to the {@link Server}.
     */
    private Socket mSocket;

    /**
     * Whether or not the {@link Server} is still connected.
     */
    private boolean mIsOpen;

    /**
     * The thread that listens for messages. Only a single instance is setup
     * per client.
     */
    private MessageHandler mMessageHandler;

    /**
     * The callback interface for listening for messages.
     */
    private OnMessageReceivedListener mMessageListener;


    /**
     * The stream that writes data to the {@link Server}'s
     * {@link ObjectInputStream}.
     */
    private ObjectOutputStream mOutput;


    ///// Constructors /////////////////////////////////////////////////////////

    /**
     * @param device    The device this client is running on.
     * @param ipAddress The IP Address to connect to.
     * @param port      The port the server is running on.
     */
    public Client(Device device, String ipAddress, int port) {
        mDevice = device;
        mIpAddress = ipAddress;
        mPort = port;
        mIsOpen = false;
    }


    ///// Member Methods ///////////////////////////////////////////////////////

    /**
     * Opens a connection to the {@link Server}. This will pause the thread
     * it is called from until the connection is made.
     */
    public void open() {
        try {
            mSocket = new Socket(mIpAddress, mPort);
            mOutput = new ObjectOutputStream(mSocket.getOutputStream());
            mMessageHandler = new MessageHandler(
                    new ObjectInputStream(mSocket.getInputStream())
            );
            // write the device's id to the sever, so it knows where to find
            // nodes that are on this device.
            setIsOpen(true);
            mOutput.writeLong(mDevice.getId());
            mOutput.flush();
            new Thread(mMessageHandler).start();
        } catch (IOException e) {
            Log.e("comm", "Error opening client socket", e);
        }
    }

    /**
     * Closes the connection to the {@link Server}.
     */
    public void close() {
        try {
            mOutput.close();
            mSocket.close();
        } catch (Exception e) {}
    }


    ///// Setters //////////////////////////////////////////////////////////////

    /**
     * Sets a listener whose
     * {@link OnMessageReceivedListener#onReceiveMessage(Message)} will be
     * called whenever a message is received.
     * @param listener The listener that will be called.
     */
    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {
        mMessageListener = listener;
    }

    /**
     * This method should only be called from {@link Client#open()} and
     * {@link Client#close()}.
     * @param isOpen Whether or not this client is connected to the
     * {@link Server}.
     */
    private synchronized void setIsOpen(boolean isOpen) {
        mIsOpen = isOpen;
    }


    ///// Getters //////////////////////////////////////////////////////////////

    /**
     * @return Whether or not this client is connected to the {@link Server}.
     */
    public synchronized boolean isOpen() {
        return mIsOpen;
    }


    ///// Inner Classes ////////////////////////////////////////////////////////

    /**
     * A thread that runs a loop, listening for messages, then repeating once
     * one is read. Only a single instance of this thread is instancieated
     * per {@link Client}.
     */
    class MessageHandler implements Runnable {

        ///// Member Variables /////////////////////////////////////////////////

        /**
         * The object input stream for the {@link Client}. This is the stream
         * that listens for new {@link Message}s.
         */
        private ObjectInputStream mReader;


        ///// Constructors /////////////////////////////////////////////////////

        /**
         * Creates a separate thread for listening for {@link Message}s.
         * @param reader The stream from a socket.
         */
        private MessageHandler(ObjectInputStream reader) {
            mReader = reader;
        }


        ///// Runnable Overrides ///////////////////////////////////////////////

        /**
         * Runs a loop. When it receives a message, it will notify the
         * listener on yet another thread, then repeats. It repeats while the
         * connection is open.
         */
        @Override
        public void run() {
            try {
                while (isOpen()) {
                    final Message message = (Message) mReader.readObject();
                    if (mMessageListener != null) new Thread(() -> {
                        mMessageListener.onReceiveMessage(message);
                    }).start();
                }
            } catch (SocketException e) {
                Log.e("comm", "socket closed");
            } catch (ClassNotFoundException | IOException e) {
                if (isOpen()) Log.e("comm", "error receiving message.", e);
            } finally {
                setIsOpen(false);
            }
        }

    }


    ///// Member Methods ///////////////////////////////////////////////////////

    /**
     * Sends a message to the connected {@link Server}.
     * @param message The message to send.
     */
    public void sendMessage(Message message) {
        if (!isOpen()) {
            Log.e("comm", "Connection closed, couldn't send message: " + message);
            return;
        }
        try {
            mOutput.writeObject(message);
        } catch (IOException e) {
            Log.e("comm", "Error sending message: " + message, e);
        }
    }

}
