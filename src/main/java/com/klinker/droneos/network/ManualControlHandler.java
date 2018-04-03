package com.klinker.droneos.network.requests;

import com.google.gson.JsonObject;
import com.klinker.droneos.arch.communication.messages.JsonMessage;
import com.klinker.droneos.ControlsNode;
import com.klinker.droneos.network.NetworkNode;

public class ManualControlHandler extends RequestHandler{
    public ManualControlHandler(NetworkNode node, String method) {
        super(node, method);
    }

    @Override
    protected Object performRequest(JsonObject parameters) {
        if (parameters.has("lThrust") && parameters.has("rThrust")) {
            JsonObject data = new JsonObject();
            data.addProperty("lThrust", parameters.get("lThrust").getAsDouble());
            data.addProperty("rThrust", parameters.get("rThrust").getAsDouble());
            data.addProperty("isManuel",true);
            JsonMessage message = new JsonMessage(
                    ControlsNode.class,
                    NetworkNode.class,
                    "drive",
                    data
            );
            this.getNode().sendMessage(message);

            return parameters;
        }

        JsonObject m = new JsonObject();
        m.addProperty(
                "error",
                "Controller request did not have proper body. See Documentation"
        );
        return m;
    }

    @Override
    public String getEndpoint() {
        return "/controller";
    }
}
