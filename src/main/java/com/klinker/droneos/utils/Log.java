package com.klinker.droneos.utils;


import com.klinker.droneos.arch.Core;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;


/**
 * The logging class for this project. It is a better way to output
 * messages, warnings, and errors.
 * <p>
 * Tags should be the project you are working on. for example, "cv" for
 * computer vision, "mapping" for navigation
 * <p>
 * If it is a utility, either the tag should be the parent package's name.
 * For example: a tag for {@link com.klinker.droneos.arch.Core}
 * would be 'arch'.
 */
public class Log {

    ///// Constants ////////////////////////////////////////////////////////////

    /**
     * A class to help color the console output.
     */
    private static class Color {
        public static final String RESET = "\u001B[0m";
        public static final String BLACK = "\u001B[30m";
        public static final String RED = "\u001B[31m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String BLUE = "\u001B[34m";
        public static final String PURPLE = "\u001B[35m";
        public static final String CYAN = "\u001B[36m";
        public static final String WHITE = "\u001B[37m";
    }

    /**
     * A constant to help format the current date.
     */
    private static SimpleDateFormat FORMATTER = new SimpleDateFormat(
            "YYYY/MM/dd hh:mm:ss.S a"
    );

    private static PrintStream LOG_FILE;


    ///// Constructor //////////////////////////////////////////////////////////

    /**
     * Prevents instantiation of a Log instance. Makes it so static methods
     * are the only choice.
     */
    private Log() { }


    ///// Printing Help Methods ////////////////////////////////////////////////

    /**
     * Prints formatted text to the command line.
     * @param color     The color the message should be displayed in, a
     *                  constant from {@link Color}.
     * @param debugType Any character, but generally d, w, e, and v for debug,
     *                  warning, error, and verbose.
     * @param tag       The tag used to identify here the statement came from.
     * @param message   The actual message to be displayed.
     * @param e         An Exception if there was one, otherwise
     *                  <code>null</code>;
     * @param stream    The stream to write the text out to.
     */
    private static void print(
            String color,
            char debugType,
            String tag,
            String message,
            Exception e,
            PrintStream stream,
            boolean isColored) {
        String reset = Color.RESET;
        if (!isColored) {
            color = "";
            reset = "";
        }
        if (stream != null) stream.println(color + getHeader(debugType, tag)
                + message + (e == null ? "" : '\n' + stackTraceToString(e))
                + reset);
    }

    /**
     * Returns the prefix for the debug messsage.
     *
     * @param debugType The type of debug called, d, w, v, or e
     * @param tag       The tag used to identify here the statement came from.
     * @return A string in the format:
     *         "[YYYY/MM/DD hh/mm/ss.ms am/pm - v/d/e]tag: "
     */
    private static String getHeader(char debugType, String tag) {
        return String.format(
                "[%s] %s_%c: ",
                FORMATTER.format(System.currentTimeMillis()), tag, debugType
        );
    }

    /**
     * Converts an error into a stack trace string for debugging purposes.
     *
     * @param e The exception to convert to a string.
     * @return The string containing the stack trace for {@param e}.
     */
    private static String stackTraceToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Opens the connection to the log file.
     */
    public static void open() {
        try {
            LOG_FILE = new PrintStream(
                    Core.DIR_LOG_OUTPUT + "/output.log"
            );
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Closes the connection to the log file.
     */
    public static void close() {
        LOG_FILE.close();
    }


    ///// Logging Methods //////////////////////////////////////////////////////

    /**
     * 'd' stands for 'Debug'. Prints a message to the console, with default
     * coloring. This is a regular old print statement.
     *
     * @param tag     The tag used to identify here the statement came from.
     * @param message The actual message to be displayed.
     */
    public static void d(String tag, String message) {
        print(Color.RESET, 'd', tag, message, null, System.out, Core.isLogColored());
        print(Color.RESET, 'd', tag, message, null, LOG_FILE, false);
    }

    /**
     * 'w' stands for 'warning'. Prints a message to the console, colored
     * yellow.
     *
     * @param tag     The tag used to identify here the statement came from.
     * @param message The actual message to be displayed.
     */
    public static void w(String tag, String message) {
        print(Color.YELLOW, 'w', tag, message, null, System.out, Core.isLogColored());
        print(Color.YELLOW, 'w', tag, message, null, LOG_FILE, false);
    }

    /**
     * 'v' stands for 'verbose'. Prints a message to the console with focus,
     * verbose. The message will be displayed in blue.
     *
     * @param tag     The tag used to identify here the statement came from.
     * @param message The actual message to be displayed.
     */
    public static void v(String tag, String message) {
        print(Color.BLUE, 'v', tag, message, null, System.out, Core.isLogColored());
        print(Color.BLUE, 'v', tag, message, null, LOG_FILE, false);
    }

    /**
     * 'e' stands for 'error'. Prints a message to the console. The message will
     * be displayed in red.
     *
     * @param tag     The tag used to identify here the statement came from.
     * @param message The actual message to be displayed.
     */
    public static void e(String tag, String message) {
        e(tag, message, null);
    }

    /**
     * 'e' stands for 'error'. Prints a message to the console. The message
     * will be displayed in red. This method also include a Exception, meant
     * to print out debugging info as to why that exception was called.
     *
     * @param tag     The tag used to identify here the statement came from.
     * @param message The actual message to be displayed.
     * @param e       An error whose message stack trace will be printed to
     *                <code>System.err</code>
     */
    public static void e(String tag, String message, Exception e) {
        print(Color.RED, 'e', tag, message, e, System.out, Core.isLogColored());
        print(Color.RED, 'e', tag, message, e, LOG_FILE, false);
    }

}
