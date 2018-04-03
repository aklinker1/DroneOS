package com.klinker.droneos.utils.io;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class JsonFile extends IOFile<JsonElement> {

    ///// Constructors /////////////////////////////////////////////////////////

    public JsonFile(String path) {
        super(path);
    }

    public JsonFile(String parent, String filename) {
        super(parent, filename);
    }

    public JsonFile(File file) {
        super(file);
    }


    ///// IOFile Callbacks /////////////////////////////////////////////////////

    @Override
    public JsonElement readObject(FileReader fReader, File file) throws Exception {
        // I found this algorithm when looking at the src code for
        // JsonElement#toString(), then extended it to file IO
        JsonReader reader = new JsonReader(fReader);
        JsonElement json = Streams.parse(reader);
        reader.close();
        return json;
    }

    @Override
    protected boolean writeObject(JsonElement object, FileWriter fWriter, File file)
            throws Exception {
        // I found this algorithm when looking at the src code for
        // JsonElement#toString(), then extended it to file IO
        JsonWriter writer = new JsonWriter(fWriter);
        Streams.write(object, writer);
        writer.close();
        return true;
    }

}
