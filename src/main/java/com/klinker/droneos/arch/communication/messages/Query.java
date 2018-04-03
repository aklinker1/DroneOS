package com.klinker.droneos.arch.communication.messages;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.klinker.droneos.arch.nodes.Node;
import com.klinker.droneos.utils.random.RandomString;

public class Query extends JsonMessage {

    public interface QueryCallback {
        void onQueryReturned(JsonObject result);
    }

    private static RandomString sRandomStringGen;

    private String mUUID;

    public Query(Class<? extends Node> to, Class<? extends Node> from) {
        super(to, from, "", new JsonObject());
        if (sRandomStringGen == null) {
            sRandomStringGen = new RandomString();
        }
        this.mUUID = sRandomStringGen.nextString();
    }

    public String getUUID() {
        return mUUID;
    }

    public void requestProperty(String property) {
        getData().addProperty(property, "");
    }

    public void addInput(String name, String value) {
        addInput(name, new JsonPrimitive(value));
    }

    public void addInput(String name, boolean value) {
        addInput(name, new JsonPrimitive(value));
    }

    public void addInput(String name, Number value) {
        addInput(name, new JsonPrimitive(value));
    }

    public void addInput(String name, char value) {
        addInput(name, new JsonPrimitive(value));
    }

    private void addInput(String name, JsonPrimitive value) {
        if (!getData().has("input")) {
            getData().add("input", new JsonObject());
        }
        getData().getAsJsonObject("input").add(name, value);
    }

}
