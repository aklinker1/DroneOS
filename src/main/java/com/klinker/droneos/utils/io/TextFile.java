package com.klinker.droneos.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class TextFile extends IOFile<String> {

    ///// Constructors /////////////////////////////////////////////////////////

    public TextFile(String path) {
        super(path);
    }

    public TextFile(String parent, String filename) {
        super(parent, filename);
    }

    public TextFile(File file) {
        super(file);
    }


    ///// IOFile Callbacks /////////////////////////////////////////////////////

    @Override
    public String readObject(FileReader fReader, File file) throws Exception {
        // create reader to read the fReader as strings.
        BufferedReader reader = new BufferedReader(fReader);

        // read each line and add it to the string. This pattern prevents
        // the addition of an extra line, unlike the shorter version.
        StringBuilder builder = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            builder.append(line);
            line = reader.readLine();
            if (line != null) builder.append('\n');
        }
        reader.close();
        return builder.toString();
    }

    @Override
    protected boolean writeObject(String s, FileWriter os, File file) throws Exception {
        os.write(s);
        return true;
    }

}
