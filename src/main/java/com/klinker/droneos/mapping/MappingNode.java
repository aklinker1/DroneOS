package com.klinker.droneos.mapping;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.klinker.droneos.ControlsNode;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.communication.messages.JsonMessage;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.nodes.Node;
import com.klinker.droneos.arch.simulation.Simulation;
import com.klinker.droneos.arch.simulation.map.BoatCollision;
import com.klinker.droneos.arch.simulation.map.BuoyCollision;
import com.klinker.droneos.arch.simulation.map.Waypoint;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;
import com.klinker.droneos.utils.math.Point;

/**
 * TODO: Node specific documentation.
 */
public class MappingNode extends Node {

    ///// Constants ////////////////////////////////////////////////////////////

    ///// Member Variables /////////////////////////////////////////////////////

    ///// Construction /////////////////////////////////////////////////////////

    public MappingNode(String dataPath) {
        super(dataPath);
//        JsonObject data = getData();
//        String string = data.get("example").getAsString();
//        Log.d("mapping", "example: " + string);
    }


    ///// Node Overrides ///////////////////////////////////////////////////////
    @Override
    protected void onExit() {
        // close streams
    }


    ///// Node Callbacks ///////////////////////////////////////////////////////

    @Override
    public void onReceiveMessage(Message message) {

    }

    @Override
    protected JsonPrimitive queryProperty(String property, JsonObject inputs) {
        return null;
    }


    ///// Task Callbacks ///////////////////////////////////////////////////////

    @Override
    protected void onInitializingTask() {
        super.onInitializingTask();
        Utils.sleep(4000);
    }

    @Override
    protected void onFinishUpTask() {
        super.onFinishUpTask();

        if (Core.IS_SIMULATION) {
            Simulation sim = Simulation.getSingleton();
            BoatCollision boat = sim.getBoat();
            ArrayList<BuoyCollision> buoys = sim.getBuoys();
            LinkedList<Waypoint> waypoints = sim.geWaypoints();
            Log.d("mapping", "waypoints: " + waypoints);

            while (!waypoints.isEmpty()) {
                Waypoint goal = waypoints.peek();
                Point vectorField = new Point(0, 0);
                double x = boat.getLongitude() - goal.x;
                double y = boat.getLatitude() - goal.y;
                vectorField.x += (x > 0 ? -1 : 1) * Math.cos(Math.atan(y / x));
                vectorField.y += (x > 0 ? -1 : 1) * Math.sin(Math.atan(y / x));

                for (BuoyCollision buoy : buoys) {
                    x = buoy.c.x - goal.x;
                    y = buoy.c.y - goal.y;
                    vectorField.x += x / (Math.pow(x, 2) + Math.pow(y, 2));
                    vectorField.y += y / (Math.pow(x, 2) + Math.pow(y, 2));
                }

                JsonObject vector = new JsonObject();
                vector.addProperty("x", vectorField.x);
                vector.addProperty("y", vectorField.y);
                vector.addProperty("isManual", false);
                JsonMessage driveMessage = new JsonMessage(
                        ControlsNode.class,
                        MappingNode.class,
                        "drive",
                        vector
                );
                sendMessage(driveMessage);

                Utils.sleep(17);
                if (goal.checkPoint(boat.getPoint())) {
                    Log.v("mapping", "Reached waypoint: " + goal);
                    waypoints.remove();
                }
            }
        } else {
            Log.d("mapping", "Simulation not running");
        }
    }

}
