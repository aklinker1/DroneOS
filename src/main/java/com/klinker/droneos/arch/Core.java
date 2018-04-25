package com.klinker.droneos.arch;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;

import com.klinker.droneos.arch.manifest.Manifest;
import com.klinker.droneos.arch.nodes.Node;
import com.klinker.droneos.arch.nodes.NodeManager;
import com.klinker.droneos.arch.simulation.Simulation;
import com.klinker.droneos.utils.Log;
import com.klinker.droneos.utils.async.RunnableExecutor;

public class Core {

    ///// Constants ////////////////////////////////////////////////////////////

    /**
     * When the project stated it's run.
     */
    private static String START_TIME = new SimpleDateFormat(
            "[YYYY-MM-dd] HH.mm.ss.S"
    ).format(System.currentTimeMillis());

    /**
     * The path to the resources for operation.
     */
    public static String DIR_RESOURCES = "src/main/resources";

    /**
     * The path to the resources for testing.
     */
    public static String DIR_TEST_RESOURCES = "src/test/resources";

    /**
     * The path to the output for logs. There will be a log for every run,
     * named after the time it started at.
     */
    public static String DIR_LOG_OUTPUT = "output/" + START_TIME;

    /**
     * The path to the output for computer vision.
     */
    public static String DIR_CV_OUTPUT = "output/" + START_TIME + "/cv";

    /**
     * The path to the output for mapping.
     */
    public static String DIR_MAPPING_OUTPUT = "output/" + START_TIME +
            "/mapping";

    /**
     * The path to the output for sensors.
     */
    public static String DIR_SENSOR_OUTPUT = "output/" + START_TIME + "/sensor";

    /**
     * The default exit code for a successful run.
     */
    private static final int EXIT_CODE_SUCCESS = 0;

    /**
     * Use this when a fatal error occurs in the Architecture node.
     */
    public static final int EXIT_CODE_ARCH_FATAL = 1;

    /**
     * Use this when a fatal error occurs in the Computer Vision node.
     */
    public static final int EXIT_CODE_CV_FATAL = 2;

    /**
     * Use this when a fatal error occurs in the Navigation node.
     */
    public static final int EXIT_CODE_MAPPING_FATAL = 3;

    /**
     * Use this when a fatal error occurs in the Sensor node.
     */
    public static final int EXIT_CODE_SENSOR_FATAL = 4;

    /**
     * Use this when a fatal error occurs in the Simulation.
     */
    public static final int EXIT_CODE_SIMULATION_FATAL = 5;

    /**
     * Use this when a fatal error occurs in the Network node.
     */
    public static final int EXIT_CODE_NETWORK_FATAL = 6;

    /**
     * Use this when a fatal error occurs in the controls.
     */
    public static final int EXIT_CODE_CONTROLS_FATAL = 7;

    /**
     * Set to false to disable colors in terminal. Helpful when running on a
     * windows cmd.t
     */
    private static boolean IS_LOG_COLORED = true;

    /**
     * Set whether or not the run in a simulation.json or not.
     */
    public static boolean IS_SIMULATION = false;

    /**
     * The physical simulation.json in use if IS_SIMULATION is true;
     */
    public static Simulation SIMULATION = null;

    /**
     * Whether or not the OS is windows.
     */
    public static boolean OS_IS_WINDOWS = false;

    /**
     * Whether or not the OS is linux based.
     */
    public static boolean OS_IS_LINUX = false;

    /**
     * Whether or not the OS is OSX.
     */
    public static boolean OS_IS_MAC = false;

    private static NodeManager sManager;



    ///// System-Wide Methods //////////////////////////////////////////////////

    /**
     * Starts the application with the properties set in the manifest.
     * <p>
     * Arguments: (* implies they are nessisary, do not include the * or '
     * characters)
     * <ul style="list-style: none;">
     * <li>* <code>'-manifest /path/to/manifest'</code></li>
     * </ul>
     *
     * @param args The arguments that specifies which
     *             {@link Node} start up.
     */
    public static void main(String[] args) {
        createOutputFolders();
        Log.open();
        LinkedList<String> arguments = new LinkedList<>();
        arguments.addAll(Arrays.asList(args));

        // Construct the Node Manager
        sManager = parseArguments(arguments);

        // Start the simulation.json if necessary
        RunnableExecutor executor = null;
        if (IS_SIMULATION) {
            executor = RunnableExecutor.newSeries();
            executor.addRunnable(SIMULATION.startRunnable());
            executor.start();
        }

        // Start NodeManager
        sManager.start();

        // Stop the simulation.json if necessary
        if (IS_SIMULATION && executor != null) {
            SIMULATION.stop();
            executor.join();
        }

        // It will finish successfully when the Manager finishes it's start
        exit(EXIT_CODE_SUCCESS);
    }

    /**
     * Stops the robot's nodes process. Should only be called after a
     * successful run or after a fatal error.
     *
     * @param exitCode Where the exit comes from. Examples include:
     *                 {@link Core#EXIT_CODE_SUCCESS} for successes, {@link
     *                 Core#EXIT_CODE_CV_FATAL} for computer vision fatal
     *                 errors, {@link Core#EXIT_CODE_MAPPING_FATAL} for navigation
     *                 fatal errors, and so on.
     */
    public static void exit(int exitCode) {
        sManager.forceStop();
        Log.close();
        System.exit(exitCode);
    }

    public static boolean isLogColored() {
        return IS_LOG_COLORED;
    }

    private static void createOutputFolders() {
        new File(DIR_CV_OUTPUT).mkdirs();
        new File(DIR_LOG_OUTPUT).mkdirs();
        new File(DIR_SENSOR_OUTPUT).mkdirs();
        new File(DIR_MAPPING_OUTPUT).mkdirs();
    }



    ///// Helper Methods ///////////////////////////////////////////////////////

    /**
     * Parses the provided arguments and creates a NodeManager instance.
     *
     * @param args The arguments to parse.
     * @return The NodeManager for this device.
     */
    private static NodeManager parseArguments(LinkedList<String> args) {
        Manifest manifest = null;
        String device = null;

        while (!args.isEmpty()) {
            String arg = args.removeFirst();
            if (arg.equals("-simulation")) {
                IS_SIMULATION = true;
                SIMULATION = new Simulation();
            } else if (arg.startsWith("-")) {
                if (args.isEmpty()) {
                    Log.e(
                            "arch",
                            "no path specified after command '" +
                                    arg + "'"
                    );
                    exit(EXIT_CODE_ARCH_FATAL);
                } else {
                    String param = args.removeFirst();
                    if (param.startsWith("-")) {
                        Log.w(
                                "arch",
                                "Did not provide a parameter for argument '"
                                        + arg + "'"
                        );
                        args.addFirst(param);
                    } else {
                        switch (arg) {
                            case "-manifest":
                                manifest = Manifest.fromPath(param);
                                break;
                            case "-device":
                                device = param;
                                break;
                            case "-colored-log":
                                IS_LOG_COLORED = Boolean.parseBoolean(param);
                                break;
                        }
                    }
                }
            } else {
                Log.w(
                        "arch",
                        "error parsing argument '" + arg + "': '-' is needed" +
                                " before each command."
                );
            }
        }

        // make sure the manifest is not null
        if (manifest == null) {
            Log.e("arch", "Manifest not specified in the arguements");
            Core.exit(EXIT_CODE_ARCH_FATAL);
        }

        // Create and return the Node Manager for this device.
        return new NodeManager(manifest, device);
    }

}