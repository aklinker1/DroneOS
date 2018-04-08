package com.klinker.droneos.network;

import com.google.gson.JsonObject;
import com.klinker.droneos.arch.communication.messages.JsonMessage;
import com.klinker.droneos.ControlsNode;
import com.klinker.droneos.NetworkNode;

public class ManualControlHandler extends RequestHandler{
    public ManualControlHandler(NetworkNode node, String method) {
        super(node, method);
    }

    @Override
    protected Object performRequest(JsonObject parameters) {
        if (parameters.has("strafeX") && parameters.has("strafeY") && parameters.has("angle")
                && parameters.has("lift")) {
            JsonObject data = new JsonObject();
            data.addProperty("strafeX", parameters.get("strafeX").getAsDouble());
            data.addProperty("strafeY", parameters.get("strafeY").getAsDouble());
            data.addProperty("angle", parameters.get("angle").getAsDouble());
            data.addProperty("lift", parameters.get("lift").getAsDouble());
            data.addProperty("isManual", true);
            JsonMessage message = new JsonMessage(
                    ControlsNode.class,
                    NetworkNode.class,
                    ControlsNode.MESSAGE_CONTROL,
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
