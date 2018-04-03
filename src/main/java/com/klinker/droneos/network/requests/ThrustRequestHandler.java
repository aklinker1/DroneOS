package com.klinker.droneos.network.requests;

import com.google.gson.JsonObject;
import com.klinker.droneos.arch.communication.messages.Query;
import com.klinker.droneos.controls.ControlsNode;
import com.klinker.droneos.network.NetworkNode;

public class ThrustRequestHandler extends RequestHandler{


    public ThrustRequestHandler(NetworkNode node, String method) {
        super(node, method);
    }

    @Override
    protected Object performRequest(JsonObject parameters) {
        Query query = new Query(ControlsNode.class, NetworkNode.class);
        query.requestProperty("leftThrust");
        query.requestProperty("rightThrust");
        return getNode().sendQuery(query);

    }

    @Override
    public String getEndpoint() {
        return "/thrust";
    }
}
