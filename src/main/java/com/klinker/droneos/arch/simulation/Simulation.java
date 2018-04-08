package com.klinker.droneos.arch.simulation;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.simulation.map.DroneCollision;
import com.klinker.droneos.arch.simulation.map.BuoyCollision;
import com.klinker.droneos.arch.simulation.map.CollisionObject;
import com.klinker.droneos.arch.simulation.map.LineSegmentCollision;
import com.klinker.droneos.arch.simulation.map.Waypoint;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.Utils;
import com.klinker.droneos.utils.async.RunnableExecutor;
import com.klinker.droneos.utils.io.JsonFile;
import com.klinker.droneos.utils.math.Point;

/**
 * This is the class that holds all the data for the simulation. Once the
 * runnable from {@link Simulation#startRunnable()} is started, the simulation
 * will run in parallel and update at a given {@link Simulation#FPS}.
 *
 * To get an instance of the simulation, call {@link Simulation#getSingleton()}.
 * The single instance is stored in the core.
 */
public class Simulation {

    ///// Static Methods ///////////////////////////////////////////////////////

    /**
     * Gets the singleton framework for the simulation.
     * @return A single instance of the simulation.
     */
    public static Simulation getSingleton() {
        return Core.SIMULATION;
    }


    ///// Static Variables /////////////////////////////////////////////////////

    /**
     * The updates per second that the simulation runs at. Denoted as
     * 'Frames' per second because it is more common than 'updates'.
     * <p>
     * If Your computer is lagging, you can turn this down. THE MINIMUM IS
     * 25. 30 is safer.
     */
    public static final double FPS = 60.0;


    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * The list of all objects. They all extend {@link CollisionObject}, so
     * any interference can be easily found.
     */
    private ArrayList<CollisionObject> mObjects;

    private ArrayList<BuoyCollision> mBuoys;

    private LinkedList<Waypoint> mWaypoints;

    private DroneCollision mDrone;

    private boolean mIsRunning;


    ///// Constructors /////////////////////////////////////////////////////////

    /**
     * Constructs the simulation and it's environment. The path to the map
     * data should be given in the manifest
     */
    public Simulation() {
        mIsRunning = false;
        // set boat start location
        mDrone = new DroneCollision(
                this,
                Utils.random(-9, 9),
                Utils.random(-9, 9),
                Utils.random(0, Math.PI * 2)
        );

        Log.v("simulation", "##### SIMULATION #####");
    }


    ///// Member Methods ///////////////////////////////////////////////////////

    /**
     * This will start the simulation. It will run parallel to the main
     * thread, and all classes shall be able to update it.
     *
     * This method should be called from the main thread, it should not
     * be called from a separate thread.
     *
     * @return A runnable that should be started in a
     *         {@link RunnableExecutor}.
     */
    public Runnable startRunnable() {
        return () -> {
            Log.d("simulation", "Simulation started");
            setIsRunning(true);
            while (isRunning()) {
                long start = System.currentTimeMillis();
                Simulation.this.loop();

                // 60 fps is ~17ms / frame, so:
                // sleep = 17 - (time it took to loop)
                Utils.sleep((long) (
                        (1000f / FPS) - (System.currentTimeMillis() - start)
                ));
            }
            Log.d("simulation", "Simulation finished");
        };
    }

    /**
     * The main loop for the simulation. It checks for boat collisions and
     * updates the boat position.
     *
     * Check for boat collisions, then update.
     */
    private void loop() {
        mDrone.updatePosition();
    }

    public void stop() {
        setIsRunning(false);
    }


    ///// Getters //////////////////////////////////////////////////////////////

    public DroneCollision getDrone() {
        return mDrone;
    }

    public ArrayList<CollisionObject> getObjects() {
        return mObjects;
    }

    public synchronized boolean isRunning() {
        return mIsRunning;
    }

    public double getFPS() {
        return FPS;
    }

    public ArrayList<BuoyCollision> getBuoys() {
        return mBuoys;
    }
 
    public LinkedList<Waypoint> geWaypoints() {
        return mWaypoints;
    }


    ///// Setters //////////////////////////////////////////////////////////////

    private synchronized void setIsRunning(boolean isRunning) {
        this.mIsRunning = isRunning;
    }

}
