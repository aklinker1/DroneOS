package com.klinker.droneos.network;

import com.google.gson.JsonObject;
import com.klinker.droneos.ControlsNode;
import com.klinker.droneos.NetworkNode;
import com.klinker.droneos.arch.communication.messages.JsonMessage;
import com.klinker.droneos.utils.Log;

/**
 * Class for handling the GET /arm HTTP Request.
 */
public class DropRequestHandler extends RequestHandler {

    public DropRequestHandler(NetworkNode node, String method) {
        super(node, method);
    }

    @Override
    protected Object performRequest(JsonObject parameter) {
        Log.d("drop", "drop: " + parameter);
        if (parameter.has("drop")) {
            JsonObject o = new JsonObject();
            o.addProperty("drop", parameter.get("drop").getAsBoolean());
            JsonMessage message = new JsonMessage(
                    ControlsNode.class,
                    NetworkNode.class,
                    ControlsNode.MESSAGE_DROP,
                    o
            );
            getNode().sendMessage(message);
            return parameter;
        }

        
        JsonObject m = new JsonObject();
        m.addProperty(
                "error",
                "Arm request did not have proper body. See Documentation"
        );
        return m;
    }

    @Override
    public String getEndpoint() {
        return "/drop";
    }

}