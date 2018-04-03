package com.klinker.droneos.network.requests;

import com.google.gson.JsonObject;
import com.klinker.droneos.network.NetworkNode;

/**
 * Class for handling the GET /connect HTTP Request.
 */
public class ConnectRequestHandler extends RequestHandler {

    public ConnectRequestHandler(NetworkNode node, String method) {
        super(node, method);
    }

    @Override
    protected Object performRequest(JsonObject body) {
        getNode().setIsConnected(true);
        JsonObject result = new JsonObject();
        JsonObject message = new JsonObject();
        message.addProperty("content", "Roger Roger. Connected to GUI.");
        message.addProperty("type", "d");
        result.add("message", message);
        return result;
    }

    @Override
    public String getEndpoint() {
        return "/connect";
    }
}
