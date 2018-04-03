package com.klinker.droneos.arch.manifest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Contains information about the device pulled from the manifest.
 */
public class Device {

    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * The name of the device. The json tag for this variables in the
     * manifest file is "name".
     */
    @SerializedName("id")
    private long mId;

    /**
     * The name of the device. The json tag for this variables in the
     * manifest file is "name".
     */
    @SerializedName("name")
    private String mName;

    /**
     * The IP Address of the device. The json tag for this variables in the
     * manifest file is "ip_address".
     */
    @SerializedName("ip_address")
    private String mIpAddress;

    /**
     * The port of the device. The json tag for this variables in the
     * manifest file is "port".
     */
    @SerializedName("port")
    private int mPort;

    /**
     * The list of mNodes to be ran on this device. The json tag for this
     * variables in the manifest file is "mNodes". Each item is the full
     * package location of each node.
     */
    @SerializedName("nodes")
    private List<NodeInfo> mNodes;


    ///// Getters //////////////////////////////////////////////////////////////
    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getIpAddress() {
        return mIpAddress;
    }

    public int getPort() {
        return mPort;
    }

    public List<NodeInfo> getNodes() {
        return mNodes;
    }


    ///// Object Callbacks /////////////////////////////////////////////////////
    @Override
    public String toString() {
        return "Device {\n" +
                "  id='" + mId + '\'' + ",\n" +
                "  name='" + mName + '\'' + ",\n" +
                "  ipAddress='" + mIpAddress + '\'' + ",\n" +
                "  port=" + mPort + ",\n" +
                "  nodes=" + mNodes + "\n" +
                '}';
    }

    /**
     * Checks whether or not this object is equal to another.
     * @param o The other object to check equality with.
     * @return <code>true</code> when the pointers are the same, or o is a
     * {@link Device} and it has the same id as this one.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return mId == device.mId;
    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }
}
