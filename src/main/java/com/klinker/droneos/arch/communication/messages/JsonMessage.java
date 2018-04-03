package com.klinker.droneos.arch.communication.messages;

import com.google.gson.JsonObject;
import com.klinker.droneos.arch.nodes.Node;

public class JsonMessage extends Message<JsonObject> {
    public JsonMessage(Class<? extends Node> to, Class<? extends Node> from,
                       String name, JsonObject data) {
        super(to, from, name, data);
    }
}
