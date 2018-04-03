package com.klinker.droneos.arch.manifest;

import com.google.gson.annotations.SerializedName;

/**
 * The info about a node.
 */
public class NodeInfo {

    /**
     * The full classpath to the node's class.
     */
    @SerializedName("class")
    private String mClasspath;

    /**
     * The path to the json file containing any specific parameters for the
     * node. They are gotten in the Node's constructor.
     */
    @SerializedName("data")
    private String mDataPath;


    ///// Getters //////////////////////////////////////////////////////////////
    public String getClasspath() {
        return mClasspath;
    }

    public String getDataPath() {
        return mDataPath;
    }

}
