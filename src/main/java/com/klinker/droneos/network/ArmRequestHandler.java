package com.klinker.droneos.network;

import com.google.gson.JsonObject;
import com.klinker.droneos.ControlsNode;
import com.klinker.droneos.NetworkNode;
import com.klinker.droneos.arch.communication.messages.JsonMessage;
import com.klinker.droneos.utils.Log;

/**
 * Class for handling the GET /arm HTTP Request.
 */
public class ArmRequestHandler extends RequestHandler {

    public ArmRequestHandler(NetworkNode node, String method) {
        super(node, method);
    }

    @Override
    protected Object performRequest(JsonObject parameter) {
        Log.d("armer", "Arm: " + parameter);
        if (parameter.has("arm")) {
            JsonObject o = new JsonObject();
            o.addProperty("arm", parameter.get("arm").getAsBoolean());
            JsonMessage message = new JsonMessage(
                    ControlsNode.class,
                    NetworkNode.class,
                    ControlsNode.MESSAGE_ARM,
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
        return "/arm";
    }

}