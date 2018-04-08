package com.klinker.droneos.arch.nodes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.communication.messages.Query;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.async.RunnableExecutor;
import com.klinker.droneos.utils.io.JsonFile;

/**
 * Nodes are the main threads in which processes take place. They are all ran
 * in parallel, and each a part of a {@link NodeManager}. This node manager
 * is the the center for all {@link Node} communication. To get an instance
 * of it, simply call {@link Node#getNodeManager()}
 * <p>
 * Nodes are instantiated synchronously on the main thread, so all
 * operations should be done quickly. All other nodes are also
 * initialized one after another.
 * <p>
 *     To get the provided data, call {@link Node#getData()}
 * </p>
 */
public abstract class Node {

    ///// Constants ////////////////////////////////////////////////////////////

    /**
     * The current task the node will be running in.
     */
    public enum Task {
    /**
     * @see Node#onInitializingTask()
     */
    INITIALIZING,
    /**
     * @see Node#onManualFindTask()
     */
    MANUAL_FIND,
    /**
     * @see Node#onFinishUpTask()
     */
    FINISH_UP
    }

    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * The manager of this node, and link to all the other node's currently
     * running.
     */
    private NodeManager mNodeManager = null;

    private RunnableExecutor mMessageExecutor;

    private int mTaskCount;

    private JsonObject mData;

    ///// Construction /////////////////////////////////////////////////////////
    protected Node(String dataPath) {
        mMessageExecutor = RunnableExecutor.newParallel(10);
        mTaskCount = 0;
        JsonFile dataFile = new JsonFile(dataPath);
        if (dataFile.exists()) {
            mData = dataFile.read().getAsJsonObject();
        } else {
            mData = new JsonObject();
        }
        Log.d("arch", "Constructed " + getClass().getSimpleName());
    }

    ///// Member Methods ///////////////////////////////////////////////////////

    /**
     * Contains this node's (nearly) infinite loop. Inside, the program checks
     * for messages, and calls {@link Node#onReceiveMessage(Message)} in a
     * separate, sequential thread. Once this method finishes for each node,
     * then main program will finish.
     * @param task The task to run.
     */
    public void runTask(Task task) {
        switch (task) {
        case INITIALIZING:
            onInitializingTask();
            break;
        case MANUAL_FIND:
            onManualFindTask();
            break;
        case FINISH_UP:
            onFinishUpTask();
            break;
        }
    }

    ///// Member Methods ///////////////////////////////////////////////////////

    /**
     * This is the callback for when a {@link Node} receives a message.
     * called in parallel to the node's task executables.
     * @param message The message being received.
     */
    protected abstract void onReceiveMessage(Message message);

    /**
     * The callback for when a query needs a property filled out.
     * @param property The property to fill
     * @param inputs The inputs of the query in case the property requires
     *               input, or null if no input was given.
     * @return The object to return for the query. Must be of the
     */
    protected abstract JsonPrimitive queryProperty(String property, JsonObject inputs);

    /**
     * Called when the system is exiting and the run is finished.
     */
    protected void onExit() {
        Log.d("arch", "Exiting " + getClass().getSimpleName());
    }

    /**
     * Sends a message to the NodeManager, if it is not null.
     *
     * @param message The message to send out. The message contains the class
     *                in which to send it to.
     */
    public void sendMessage(Message message) {
        if (mNodeManager == null) {
            Log.w("arch", "NodeManager for " + this.getClass().getSimpleName() + " is null");
            return;
        }

        mNodeManager.sendMessage(message);
    }

    public synchronized void receiveMessage(Message message) {
        mMessageExecutor.executeRunnable(() -> {
            addToTaskCount(1);
            onReceiveMessage(message);
            addToTaskCount(-1);
        });
        int warningTaskCount = RunnableExecutor.MAX_PARALLEL_COUNT / 3;
        if (mTaskCount > warningTaskCount) {
            Log.w("arch",
                    getClass().getSimpleName() + " has more than " + warningTaskCount + " active threads in parallel. "
                            + "Be careful to not excede " + RunnableExecutor.MAX_PARALLEL_COUNT);
        }
        if (mTaskCount > RunnableExecutor.MAX_PARALLEL_COUNT) {
            Log.e("arch", getClass().getSimpleName() + " has more than 25 "
                    + "tasks running. Change how you handle the message" + " results.");
        }
    }

    public JsonObject sendQuery(Query query) {
        if (mNodeManager == null) {
            Log.w("arch", "NodeManager for " + this.getClass().getSimpleName() + " is null");
            return null;
        }

        return mNodeManager.sendQuery(query);
    }

    public JsonObject receiveQuery(Query query) {
        JsonObject queryData = query.getData();
        JsonObject input;
        if (queryData.has("input")) {
            input = queryData.get("input").getAsJsonObject();
        } else {
            input = new JsonObject();
        }

        JsonObject result = new JsonObject();
        for (String property : queryData.keySet()) {
            if (!property.equals("input")) {
                JsonPrimitive value = queryProperty(property, input);
                result.add(property, value);
            }
        }
        return result;
    }

    private synchronized void addToTaskCount(int amount) {
        mTaskCount += amount;
    }

    ///// Task Methods /////////////////////////////////////////////////////////

    /**
     * The Node doesn't have to be doing anything during this time. This method
     * does not have to be overridden, except in special circumstances. Ex:
     * The networking node should prevent further execution until it is
     * connected to the GUI.
     */
    protected void onInitializingTask() {
        Log.d("arch", getClass().getSimpleName() + " - onInitializingTask()");
    }

    /**
     * The Node doesn't have to be doing anything during this time. This method
     * does not have to be overridden, except in special circumstances. Ex:
     * The networking node should prevent further execution until it is
     * connected to the GUI.
     */
    protected void onManualFindTask() {
        Log.d("arch", getClass().getSimpleName() + " - onManualFindTask()");
    }

    /**
     * This task tells all processing nodes to shutdown because the run
     * is over and the boat is back safely on the dock. The ControlsNode,
     * NetworkNode, and a few other nodes will keep running.
     */
    protected void onFinishUpTask() {
        Log.d("arch", getClass().getSimpleName() + " - onFinishUpTask()");
    }

    ///// Getters //////////////////////////////////////////////////////////////

    /**
     * @return The singleton instance of this node's manager. It will be null
     * unless set first by {@link Node#setNodeManager(NodeManager)}.
     */
    public NodeManager getNodeManager() {
        return mNodeManager;
    }

    public synchronized int getTaskCount() {
        return mTaskCount;
    }

    protected JsonObject getData() {
        return mData;
    }

    ///// Setters //////////////////////////////////////////////////////////////

    /**
     * This method is called inside {@link NodeManager} for Nodes declared in
     * the manifest. Unless this method is called, this Node's
     * {@link Node#mNodeManager} will be null.
     *
     * @param manager The {@link Node#mNodeManager} for this Node.
     */
    public void setNodeManager(NodeManager manager) {
        mNodeManager = manager;
    }

}
