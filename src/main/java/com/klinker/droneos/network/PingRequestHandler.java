package com.klinker.droneos.network;

import com.google.gson.JsonObject;
import com.klinker.droneos.NetworkNode;

/**
 * Class for handling the GET /ping HTTP Request.
 */
public class PingRequestHandler extends RequestHandler {

    public PingRequestHandler(NetworkNode node, String method) {
        super(node, method);
    }

    @Override
    protected Object performRequest(JsonObject parameter) {
        if (parameter.has("calledAt")) {
            return parameter;
        }
        
        JsonObject pingMessage = new JsonObject();
        pingMessage.addProperty(
                "error",
                "Ping request did not have proper body. See Documentation"
        );
        return pingMessage;
    }

    @Override
    public String getEndpoint() {
        return "/ping";
    }

}
