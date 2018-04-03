package com.klinker.droneos.utils.io;

import com.klinker.droneos.utils.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * A wrapper class for a {@link File}. This class provides easy ways to read
 * and write to a file.
 * <p>
 * See {@link TextFile} for an implementation.
 * </p>
 * @param <T> The type of data to read in and write to. An example is
 *            {@link String} for a simple text file.
 */
public abstract class IOFile<T> {

    ///// Member Variables /////////////////////////////////////////////////////

    /**
     * The file the resource is located in/will be written to.
     */
    private File mFile;


    ///// Construction /////////////////////////////////////////////////////////

    /**
     * Creates an instance with a given path to the file.
     *
     * @param path The path to the file that will be read and written to.
     */
    public IOFile(String path) {
        mFile = new File(path);
    }

    /**
     * Creates an instance with a given path to the file.
     *
     * @param parent   The parent directory's path that will be read and written
     *                 to.
     * @param filename The name that will be given to the file.
     */
    public IOFile(String parent, String filename) {
        mFile = new File(parent, filename);
    }

    /**
     * Creates an instance with a given file.
     *
     * @param file The file that will be read and written to.
     */
    public IOFile(File file) {
        mFile = file;
    }


    ///// Getters //////////////////////////////////////////////////////////////

    /**
     * @return The path to the file.
     */
    public String getPath() {
        return mFile.getAbsolutePath();
    }

    /**
     * @return The path to the file.
     */
    public File getFile() {
        return mFile;
    }

    /**
     * @return <code>true</code> if the file exists, <code>false</code>
     * otherwise.
     */
    public boolean exists() {
        return mFile.exists();
    }


    ///// Setters //////////////////////////////////////////////////////////////

    /**
     * Sets the file.
     *
     * @param file The file to read and write from.
     */
    public void setFile(File file) {
        mFile = file;
    }

    /**
     * Sets the path to a file.
     *
     * @param path The path to a file to read and write from.
     */
    public void setPath(String path) {
        mFile = new File(path);
    }

    /**
     * Sets the path to a file.
     *
     * @param parent   The path to the parent directory.
     * @param filename The filename of the file.
     */
    public void setPath(String parent, String filename) {
        mFile = new File(parent, filename);
    }


    ///// IO Operations ////////////////////////////////////////////////////////

    /**
     * Reads and returns a object of type {@link T} from the file at the
     * given path.
     *
     * @return <code>null</code> if the file does not exist, or there was an
     * error while reading the file.
     */
    public T read() {
        if (!mFile.exists()) {
            Log.e(
                    "io",
                    "'" + mFile.getAbsolutePath() + "' does not exist"
            );
            return null;
        }

        try (FileReader stream = new FileReader(mFile)) {
            return readObject(stream, mFile);
        } catch (Exception e) {
            Log.e(
                    "io",
                    "Error reading from '" + getPath() + "'",
                    e
            );
            return null;
        }
    }

    /**
     * Reads the object from the given path. This is the function that
     * subclasses will override.
     *
     * @param fReader The input stream for the file the {@link T} is written in.
     * @param file The file being read from.
     * @return The object the file contained.
     * @throws Exception If there is an error, it will be caught in
     *                   {@link IOFile#write(Object)}.
     */
    protected abstract T readObject(FileReader fReader, File file)
            throws Exception;

    /**
     * Writes an object to a file with the given path. If the file does
     * not exist, it will make a new file. Otherwise it will overwrite the
     * existing on.
     * @param t The object of type {@link T} to write out.
     * @return <code>true</code> if the write was a success. <code>false</code>
     * otherwise.
     */
    public boolean write(T t) {
        if (!mFile.getParentFile().exists()) {
            boolean r = mFile.getParentFile().mkdirs();
            if (!r) {
                Log.e(
                        "arch",
                        "Error: Could not create parent directory for '"
                        + getPath() + "'"
                );
                return false;
            }
        }

        try (FileWriter stream = new FileWriter(mFile)) {
            return writeObject(t, stream, mFile);
        } catch (Exception e) {
            Log.e(
                    "io",
                    "Error writing " + t + " to '" + getPath() + "'",
                    e
            );
            return false;
        }
    }

    /**
     * Writes out the object to the given path. This is the function that
     * subclasses will override.
     *
     * @param t  The object to write.
     * @param fWriter The output stream to write the object to.
     * @param file The file being written to.
     * @return <code>true</code> if the write was a success. <code>false</code>
     * @throws Exception If there is an error, it will be caught in
     *                   {@link IOFile#write(Object)}.
     */
    protected abstract boolean writeObject(T t, FileWriter fWriter, File file)
            throws Exception;

}
