package com.klinker.droneos.network.requests;

import com.google.gson.JsonObject;
import com.klinker.droneos.network.NetworkNode;
import com.klinker.droneos.utils.Log;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public abstract class RequestHandler implements HttpHandler {

    private String mMethod;
    private NetworkNode mNode;

    public RequestHandler(NetworkNode node, String method) {
        mNode = node;
        mMethod = method;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (mMethod.equals(httpExchange.getRequestMethod())) {
            try {
                Object response = performRequest(
                        NetworkNode.readQuery(httpExchange)
                );
                NetworkNode.writeBody(
                        httpExchange,
                        response,
                        getEndpoint()
                );
            } catch (Exception e) {
                Log.e(
                        "network",
                        "Failed " + mMethod + " request to " + getEndpoint(),
                        e
                );
                NetworkNode.writeBody(
                        httpExchange,
                        "{ \"error\":\"Failed " + httpExchange.getRequestMethod() + " request to " + getEndpoint()
                                + "\" }",
                        getEndpoint()
                );
            }
        } else {
            NetworkNode.writeBody(
                    httpExchange,
                    "{ \"error\":\"Wrong request method: " + httpExchange
                            .getRequestMethod() + " should be " + mMethod
                            + "\" }",
                    getEndpoint()
            );
        }
    }

    protected abstract Object performRequest(JsonObject parameters);

    public abstract String getEndpoint();

    protected NetworkNode getNode() {
        return mNode;
    }

}
