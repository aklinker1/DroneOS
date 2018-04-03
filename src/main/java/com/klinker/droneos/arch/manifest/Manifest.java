package com.klinker.droneos.arch.manifest;

import java.io.File;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.klinker.droneos.arch.Core;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.io.JsonFile;

/**
 * A class that stores all the information in the Manifest file from the
 * arguements. It is a glorified JsonObject.
 */
public class Manifest {

    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * The list of devices in the manifest. This class is built using
     * {@link Gson#fromJson(JsonElement, Class)}, so the json tag for this
     * variables in the manifest file is "devices".
     */
    @SerializedName("devices")
    private List<Device> mDevices;

    /**
     * The path to the json file that contains the map data.
     */
    @SerializedName("simulation_map")
    private String mSimulationMap;


    ///// Static Factory Methods ///////////////////////////////////////////////

    /**
     * Creates an instance based of a json file.
     *
     * @param jsonPath The path to the json file.
     * @return An instance.
     */
    public static Manifest fromPath(String jsonPath) {
        try {
            JsonFile file = new JsonFile(jsonPath);
            Gson gson = new GsonBuilder().create();
            JsonElement jsonElement = file.read();
            return gson.fromJson(jsonElement, Manifest.class);
        } catch (Exception e) {
            Log.e(
                    "arch",
                    "Error parsing Manifest located in '" +
                            new File(jsonPath).getAbsolutePath() + '\'',
                    e
            );
            Core.exit(Core.EXIT_CODE_ARCH_FATAL);
            return null;
        }
    }

    ///// Getters //////////////////////////////////////////////////////////////
    public List<Device> getDevices() {
        return mDevices;
    }

    public String getSimulationMap() {
        return mSimulationMap;
    }


    ///// Object Callbacks /////////////////////////////////////////////////////
    @Override
    public String toString() {
        return "Manifest: \n" +
                mDevices + '\n';
    }

}
