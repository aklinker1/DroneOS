package com.klinker.droneos.network.requests;

import com.google.gson.JsonObject;
import com.klinker.droneos.network.NetworkNode;

/**
 * Class for handling the GET /tasks/start HTTP Request.
 */
public class StartRequestHandler extends RequestHandler {

    public StartRequestHandler(NetworkNode node, String method) {
        super(node, method);
    }

    @Override
    protected Object performRequest(JsonObject body) {
        getNode().setIsStarted(true);
        JsonObject result = new JsonObject();
        JsonObject message = new JsonObject();
        message.addProperty("content", "Roger Roger. Starting Tasks");
        message.addProperty("type", "d");
        result.add("message", message);
        return result;
    }

    @Override
    public String getEndpoint() {
        return "/tasks/start";
    }

}