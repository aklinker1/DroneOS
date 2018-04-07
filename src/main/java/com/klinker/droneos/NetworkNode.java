package com.klinker.droneos;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.nodes.Node;
import com.klinker.droneos.network.ConnectRequestHandler;
import com.klinker.droneos.network.InfoHandler;
import com.klinker.droneos.network.ManualControlHandler;
import com.klinker.droneos.network.PingRequestHandler;
import com.klinker.droneos.network.RequestHandler;
import com.klinker.droneos.network.StartRequestHandler;
import com.klinker.droneos.network.ThrustRequestHandler;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class NetworkNode extends Node {

    public static void main(String[] args) {
        NetworkNode node = new NetworkNode(
                "src/main/resources/node-data/network/test.json"
        );
        node.onInitializingTask();
    }

    ///// Static Methods ///////////////////////////////////////////////////////

    public static void writeBody(HttpExchange exchange, Object responseBody,
                                 String endpoint) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Headers","x-prototype-version,x-requested-with");
        headers.add("Access-Control-Allow-Methods","GET,POST");
        headers.add("Access-Control-Allow-Origin","*");
        try {
            String contentString = responseBody.toString();
            contentString = contentString.replaceAll(" +", " ");
            exchange.sendResponseHeaders(200, contentString.length());
            OutputStream os = exchange.getResponseBody();
            os.write(contentString.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            String error = "{ error: \"Error performing request: "
                    + endpoint + "\" }";
            exchange.sendResponseHeaders(404, error.length());
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
        }
    }

    public static JsonObject readQuery(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();

        try {
            if (query != null) {
                JsonObject q = new JsonObject();
                for (String pair : query.split("&")) {
                    String split[] = pair.split("=");
                    q.addProperty(
                            URLDecoder.decode(split[0], "UTF-8"),
                            URLDecoder.decode(split[1], "UTF-8")
                    );
                }
                return q;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return new JsonObject();
    }


    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * The server running to host the endpoints for the HTTP Request endpoints.
     */
    private HttpServer mServer;

    /**
     * Whether or not the system should wait for the GUI on initialization.
     * If set to true in the node's data file, it will skip waiting for the gui.
     */
    private boolean mAutoStart;

    /**
     * Whether or not there has been a POST to '/connect', generally done
     * automatically when opening the GUI.
     *
     * TODO: Added run IDs
     */
    private boolean mIsConnected;

    /**
     * Whether or not there has been a POST to '/start', generally done by
     * the GUI's Start button in the task lists.
     */
    private boolean mIsStarted;


    ///// Constructors /////////////////////////////////////////////////////////

    /**
     * The data has 2 properties.
     * <ol>
     *     <li>port</li>
     *     <li>auto-start</li>
     * </ol>
     * @param dataPath
     */
    public NetworkNode(String dataPath) {
        super(dataPath);
        mAutoStart = getData().get("auto-start").getAsBoolean();
        mIsStarted = false;
        mIsConnected = false;
        try {
            int port = getData().get("port").getAsInt();
            mServer = HttpServer.create(new InetSocketAddress(port), 0);

            RequestHandler[] requestEndpointHandlers = new RequestHandler[] {
                    new ConnectRequestHandler(this, "POST"),
                    new StartRequestHandler(this, "POST"),
                    new PingRequestHandler(this, "GET"),
                    new InfoHandler(this, "GET"),
                    new ThrustRequestHandler(this, "GET"),
                    new ManualControlHandler(this,"POST")
            };
            for (RequestHandler handler : requestEndpointHandlers) {
                mServer.createContext(handler.getEndpoint(), handler);
            }

            mServer.setExecutor(null); // creates a default executor
        } catch (IOException e) {
            Log.e("network", "Could not start HTTP server", e);
            Core.exit(Core.EXIT_CODE_NETWORK_FATAL);
        }
    }


    ///// Member Methods ///////////////////////////////////////////////////////

    @Override
    protected void onReceiveMessage(Message message) {

    }

    @Override
    protected JsonPrimitive queryProperty(String property, JsonObject inputs) {
        return null;
    }

    @Override
    protected void onExit() {
        mServer.stop(0);
        super.onExit();
    }

    ///// Task Methods /////////////////////////////////////////////////////////

    @Override
    protected void onInitializingTask() {
        super.onInitializingTask();
        mServer.start();
        if (!mAutoStart) {
            Log.d("network", "Awaiting connection to GUI");
            while (!isConnected()) {
                Utils.sleep(500);
            }
            Log.d("network", "Connected to GUI");
            Log.d("network", "Awaiting start from GUI");
            while (!isStarted()) {
                Utils.sleep(500);
            }
            Log.d("network", "GUI gave the go ahead");
        } else {
            Log.d("network", "Skipping GUI (set in NetworkNode's data file)");
        }
    }


    ///// Getters //////////////////////////////////////////////////////////////

    public synchronized boolean isStarted() {
        return mIsStarted;
    }

    public synchronized boolean isConnected() {
        return mIsConnected;
    }


    ///// Setters //////////////////////////////////////////////////////////////

    public synchronized void setIsStarted(boolean isStarted) {
        mIsStarted = isStarted;
    }

    public synchronized void setIsConnected(boolean isConnected) {
        mIsConnected = isConnected;
    }

}
