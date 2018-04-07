package com.klinker.droneos.network;

import com.google.gson.JsonObject;
import com.klinker.droneos.NetworkNode;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.simulation.Simulation;
import com.klinker.droneos.arch.simulation.map.DroneCollision;

/**
 * Class for handling the GET /boat HTTP Request.
 */
public class InfoHandler extends RequestHandler {

    public InfoHandler(NetworkNode node, String method) {
        super(node, method);
    }

    @Override
    protected Object performRequest(JsonObject body) {
        JsonObject data = new JsonObject();
        if (Core.IS_SIMULATION) {
            Simulation sim = Simulation.getSingleton();
            DroneCollision boat = sim.getDrone();

            data.addProperty("x", boat.getPoint().x);
            data.addProperty("y", boat.getPoint().y);
            data.addProperty("z", boat.getPoint().z);
            data.addProperty("angle", boat.getAngle());
        } else {
            data.addProperty(
                    "message",
                    "Simulation is not running, could not get drone " +
                            "parameters"
            );
        }
        return data;
    }

    @Override
    public String getEndpoint() {
        return "/sim-info";
    }

}
