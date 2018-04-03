package com.klinker.droneos.arch.communication;

import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.utils.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

/**
 * The client is part a connection to a {@link Server}. It can receive and
 * send {@link Message}s one at a time. Unlike the Client, starting the
 * server will not pause the current thread.
 *
 * A server can be connected to multiple clients, while a client can only be
 * connected to a single server.
 */
public class Server {

    /**
     * Starts an instance of the server. This method is used to test out the
     * relationship between the client and server on the same computer. To
     * start both, run
     * <pre>src/main/resources/scripts/start-messenger-test.bat</pre>
     * @param args No args are used to run this configuration.
     */
    public static void main(String[] args) {
        System.out.println("Server Window");
        Server server = new Server(7777);
        server.setOnMessageReceivedListener(message ->
                System.out.println("Received: " + message.getData())
        );
        server.start();
        server.stop();
    }


    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * The port that the {@link Server} is running on.
     */
    private final int mPort;

    /**
     * Whether or not the server is running. THSI SHOULD NEVER BE SET
     * DIRECTLY, only through {@link Server#setIsRunning(boolean)}. This is
     * due to the multiple threads reading it's value, so changes must be
     * syncronized.
     */
    private boolean mIsRunning;

    /**
     * A map that maps the device id to the handler thread. This is done to
     * keep track of which thread is used to send a message to which device.
     */
    private HashMap<Long, ClientHandler> mClients;

    /**
     * The callback interface for listening for messages.
     */
    private OnMessageReceivedListener mMessageListener;


    ///// Constructors /////////////////////////////////////////////////////////

    /**
     * Creates a server located at this device's IP-address at the given port.
     * @param port The port that the sever will run on.
     */
    public Server(int port) {
        mPort = port;
        mIsRunning = false;
        mClients = new HashMap<>(3);
    }


    ///// Member Methods ///////////////////////////////////////////////////////

    /**
     * Starts the server. If the server is already running, nothing will
     * happen. Otherwise, a new thread will start with a loop that listens
     * for new clients. Every time a client connects, yet another thread is
     * started to handle interaction with that client. It then loops and
     * waits for another client to connect until {@link Server#stop()} is
     * called.
     */
    public void start() {
        if (isRunning()) return;

        new Thread(() -> {
            setIsRunning(true);
            try {
                ServerSocket serverSocket = new ServerSocket(mPort);
                while (isRunning()) {
                    Socket clientConnection = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(clientConnection);
                    new Thread(handler).start();
                }
                serverSocket.close();
            } catch (SocketException e) {
                Log.d("com", "server stopped.");
            } catch (IOException e) {
                Log.e("comm", "", e);
            }
            setIsRunning(false);
        }).start();
    }

    /**
     * Stops the server. It will no longer wait for new servers. The
     * {@link ClientHandler}s will also stop because they loop based on
     * {@link Server#mIsRunning}, just like above in {@link Server#start()}.
     */
    public void stop() {
        setIsRunning(false);
    }

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
     * Adds a {@link ClientHandler} to {@link Server#mClients}.
     * @param deviceId The id of the device. This is sent right after the
     *                 client connects to the {@link Server}.
     * @param handler The {@link ClientHandler} thant handles client
     *                interaction (sending and receiving messages).
     */
    private synchronized void addClient(long deviceId, ClientHandler handler) {
        mClients.put(deviceId, handler);
    }


    ///// Setters //////////////////////////////////////////////////////////////

    /**
     * Sets whether or not the server is running. This is the only way to set
     * {@link Server#mIsRunning}. It must be set synchronously due to the
     * multiple {@link ClientHandler} threads that read it's value in parallel.
     * @param isRunning Wether or not the server should be running.
     */
    public synchronized void setIsRunning(boolean isRunning) {
        mIsRunning = isRunning;
    }


    ///// Getters //////////////////////////////////////////////////////////////

    /**
     * The only way to read the value of {@link Server#mIsRunning}. It must
     * be read synchronously due to the multiple {@link ClientHandler}
     * threads that read it's value in parallel.
     * @return The syncronized value of {@link Server#mIsRunning}.
     */
    public synchronized boolean isRunning() {
        return mIsRunning;
    }


    ///// Inner Classes ////////////////////////////////////////////////////////

    /**
     * A class that runs inside a thread, listening and sending messages to
     * the {@link Client} it is connected to.
     */
    class ClientHandler implements Runnable {

        ///// Member Variables /////////////////////////////////////////////////

        /**
         * The stream used to send messages.
         */
        private ObjectOutputStream mWriter;

        /**
         * The stream that messages are received from.
         */
        private ObjectInputStream mReader;

        /**
         * The actual connection to the {@link Client}.
         */
        private Socket mClientConnection;


        ///// Constructors /////////////////////////////////////////////////////

        /**
         * Creates a new runnable that handles interaction with the
         * {@link Client}
         * @param clientConnection The socket that the client connects with.
         */
        private ClientHandler(Socket clientConnection) {
            mClientConnection = clientConnection;
        }


        ///// Runnable Overrides ///////////////////////////////////////////////

        /**
         * Initializes the streams,
         */
        @Override
        public void run() {
            try {
                mReader = new ObjectInputStream(
                        mClientConnection.getInputStream()
                );
                mWriter = new ObjectOutputStream(
                        mClientConnection.getOutputStream()
                );

                try {
                    // Gets the client's device id for the server to know
                    // where to send messages.
                    Long connectedDeviceId = mReader.readLong();
                    addClient(connectedDeviceId, this);
                    // The client does not need to know which device it is
                    // connected to. It will send any message to the server, and
                    // it will forward it to the necessary location.
                } catch (Exception e) {
                    Log.e(
                            "comm",
                            "Did not receive message from connected client " +
                                    "with device id.",
                            e
                    );
                    Core.exit(Core.EXIT_CODE_ARCH_FATAL);
                }
                while (isRunning()) {
                    try {
                        Message message = (Message) mReader.readObject();
                        if (mMessageListener != null) new Thread(() -> {
                            mMessageListener.onReceiveMessage(message);
                        }).start();
                    } catch (Exception e) {}
                }
                mReader.close();
                mWriter.close();
                mClientConnection.close();
            } catch (IOException e) {
                Log.e("comm", "Client handler stopped", e);
            }
        }


        ///// Member Methods ///////////////////////////////////////////////////////

        /**
         * Sends a message. It will be sent from the same thread it is called
         * from. The message is received on a different thread. The message
         * contains the destination, and by the time this method is called,
         * the {@link Messenger} should have sent it to the corrent
         * {@link ClientHandler}.
         * @param message The message to be sent.
         */
        public void sendMessage(Message message) {
            if (mWriter == null) return;
            try {
                mWriter.writeObject(message);
            } catch (Exception e) {}
        }


        ///// Object Overrides /////////////////////////////////////////////////

        /**
         * The Clients are stored in a HashMap, so a unique hashcode is
         * needed. In this case, it is just taken from the
         * {@link ClientHandler#mClientConnection}.
         */
        @Override
        public int hashCode() {
            return mClientConnection != null ? mClientConnection.hashCode() : 0;
        }
    }

}
