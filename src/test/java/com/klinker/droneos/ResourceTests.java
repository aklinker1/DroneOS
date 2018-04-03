package com.klinker.droneos;

import com.klinker.droneos.arch.Core;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.assertFalse;

public class ResourceTests {

    @Test
    public void ensureNoSpaces() {
        File resources = new File(Core.DIR_RESOURCES);
        File testResources = new File(Core.DIR_TEST_RESOURCES);

        LinkedList<File> files = new LinkedList<>();
        files.addFirst(resources);
        files.addFirst(testResources);

        // Depth first search through the resource directories
        while (!files.isEmpty()) {
            File file = files.removeFirst();
            assertFalse(
                    "'" + file.getPath() + "' contains at least one space",
                    file.getPath().contains(" ")
            );

            if (file.isDirectory()) {
                files.addAll(Arrays.asList(file.listFiles()));
            }
        }
    }

}
