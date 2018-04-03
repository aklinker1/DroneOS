package com.klinker.droneos.network.requests;

import com.google.gson.JsonObject;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.simulation.Simulation;
import com.klinker.droneos.arch.simulation.map.BoatCollision;
import com.klinker.droneos.network.NetworkNode;

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
            BoatCollision boat = sim.getBoat();

            data.addProperty("lat", boat.getLatitude());
            data.addProperty("long", boat.getLongitude());
            data.addProperty("pitch", boat.getLinearAcceleration());
            data.addProperty("roll", boat.getAngularAcceleration());
            data.addProperty("compass", boat.getAngle());
        } else {
            data.addProperty(
                    "message",
                    "Simulation is not running, could not get boat " +
                            "parameters"
            );
        }
        return data;
    }

    @Override
    public String getEndpoint() {
        return "/gps";
    }

}
