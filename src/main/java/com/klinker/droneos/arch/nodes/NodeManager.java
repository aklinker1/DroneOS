package com.klinker.droneos.arch.nodes;

import com.google.gson.JsonObject;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.communication.messages.Query;
import com.klinker.droneos.arch.manifest.Device;
import com.klinker.droneos.arch.manifest.Manifest;
import com.klinker.droneos.arch.manifest.NodeInfo;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.async.RunnableExecutor;
import com.klinker.droneos.arch.communication.messages.Message;
import com.klinker.droneos.arch.communication.messages.Query;
import com.klinker.droneos.arch.manifest.Device;
import com.klinker.droneos.arch.manifest.Manifest;
import com.klinker.droneos.arch.manifest.NodeInfo;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.async.RunnableExecutor;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public class NodeManager {

    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * Contains current run information, where all the nodes are located, and
     * how to connect to them all.
     */
    private Manifest mManifest;

    /**
     * The {@link Device} this node manager is running on.
     */
    private Device mDevice;

    /**
     * The map that links the node class to device it is running on.
     */
    private HashMap<Class, Device> mDeviceMap;

    /**
     * The map the links the node class to the physical nodes on this device.
     */
    private HashMap<Class, Node> mNodeMap;

    /**
     * The current task that is being ran.
     */
    private Node.Task mTask;


    ///// Construction /////////////////////////////////////////////////////////

    public NodeManager(Manifest manifest, String deviceName) {
        mManifest = manifest;
        mDeviceMap = new HashMap<>();
        mNodeMap = new HashMap<>();

        // setup maps and initialize nodes.
        try {
            List<Device> devices = manifest.getDevices();
            for (Device d : devices)
                for (NodeInfo nodeInfo : d.getNodes()) {
                    // add class and device to mDeviceMap
                    Class<?> klass = Class.forName(nodeInfo.getClasspath());
                    mDeviceMap.put(klass, d);

                    if (d.getName().equals(deviceName)) {
                        // add node instance to mNodeMap if this node should run
                        // on this device.
                        Node instance = (Node) klass
                                .getConstructor(String.class)
                                .newInstance(nodeInfo.getDataPath());
                        instance.setNodeManager(this);
                        mNodeMap.put(klass, instance);
                        mDevice = d;
                    }
                }
                assert mDevice != null;
        } catch (ClassNotFoundException e) {
            Log.e(
                    "arch",
                    "Could not instantiate Node class. Is the classpath" +
                            " spelled correctly in the manifest?",
                    e
            );
            Core.exit(Core.EXIT_CODE_ARCH_FATAL);
        } catch (NoSuchMethodException | IllegalAccessException |
                InstantiationException | InvocationTargetException e) {
            Log.e(
                    "arch",
                    "Could not instantiate class, make sure constructor only" +
                            " has a single string as the arguement. If you " +
                            "want more data, add it to the json file.",
                    e
            );
            Core.exit(Core.EXIT_CODE_ARCH_FATAL);
        }
    }


    ///// Member Methods ///////////////////////////////////////////////////////

    /**
     * Starts all the nodes, then goes through each task to get the boat to
     * the next task. This method will return if and only if the run did not
     * throw any fatal errors.
     */
    public void start() {
        Node.Task[] orderedTasks = new Node.Task[] {
                Node.Task.INITIALIZING,
                Node.Task.FINISH_UP
        };

        for (Node.Task currentTask : orderedTasks) {
            mTask = currentTask;
            Log.v("arch", "Task: " + currentTask.name());
            RunnableExecutor executor = RunnableExecutor.newParallel(
                    mNodeMap.size()
            );
            for (Node node : mNodeMap.values()) {
                executor.addRunnable(() -> node.runTask(currentTask));
            }
            executor.start();
            executor.join();
        }
        Log.w("arch", "finished running tasks, calling onExit()");

        RunnableExecutor executor = RunnableExecutor.newParallel(
                mNodeMap.size()
        );
        for (Node node : mNodeMap.values()) {
            executor.addRunnable(() -> node.onExit());
        }
        executor.start();
        executor.join();
    }

    public void forceStop() {
        for (Node node : mNodeMap.values()) {
            node.onExit();
        }
    }

    /**
     * Sends a message to another node. That node must have been declared in
     * the manifest in order to send it.
     * @param message The {@link Message} to send.
     */
    public void sendMessage(Message message) {
        Class<?> to = message.getTo();
        if (!mDeviceMap.containsKey(to)) {
            // the requested node from Message#getTo() was not on the manifest.
            Log.w("arch", to.getName() + " is not a node in the manifest");
            return;
        }

        Device deviceRunningTo = mDeviceMap.get(to);
        if (deviceRunningTo.equals(mDevice)) {
            // the node we want to send the message to is on the same device
            Node toNode = mNodeMap.get(to);
            // receive message starts a new thread and runs the code there,
            // so it isn't blocking the thread that sent the message.
            toNode.receiveMessage(message);
            return;
        }

        // the node we want to send the message to is not on this device.
        Log.e("arch", "CROSS DEVICE COMMUNICATION NOT SET UP: " + message);
        // TODO: Call the messenger's send method.
    }

    public JsonObject sendQuery(Query query) {
        Class<?> to = query.getTo();
        if (!mDeviceMap.containsKey(to)) {
            // the requested node from Message#getTo() was not on the manifest.
            Log.w("arch", to.getName() + " is not a node in the manifest");
            return null;
        }

        Device deviceRunningTo = mDeviceMap.get(to);
        if (deviceRunningTo.equals(mDevice)) {
            // the node we want to send the message to is on the same device
            Node toNode = mNodeMap.get(to);
            // Perform Query will block the current thread, making this wait
            // until it has finished getting the necessary data.
            return toNode.receiveQuery(query);
        }
        // the node we want to send the message to is not on this device.
        Log.e("arch", "CROSS DEVICE COMMUNICATION NOT SET UP: " + query);
        // TODO: Implement waiting for the other device to respond...
        return null;
    }


    ///// Getters //////////////////////////////////////////////////////////////
    public Manifest getManifest() {
        return mManifest;
    }

    public String getDeviceName() {
        return mDevice.getName();
    }

    public synchronized Node.Task getTask() {
        return mTask;
    }
}
