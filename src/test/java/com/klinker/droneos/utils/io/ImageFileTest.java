package com.klinker.droneos.utils.io;

import com.imarc.marc_i.arch.Core;
import com.imarc.marc_i.hardware.Camera;
import com.klinker.droneos.arch.Core;
import org.junit.Test;
import org.opencv.core.Mat;

import static org.junit.Assert.assertTrue;

public class ImageFileTest {

    @Test
    public void read() throws Exception {
        ImageFile file = new ImageFile(
                Core.DIR_TEST_RESOURCES,
                "ImageFile_read.jpg"
        );

        Mat image = file.read();

        // we know the dimensions of the read file, so lets test that.
        assertTrue(
                "Failed to read image from " + file.getPath() +
                        ", dimensions were wrong (" +
                                image.width() + "!=640, " + image.height() +
                        "!=480)",
                image.width() == 640 && image.height() == 480
        );
    }

    @Test
    public void write() throws Exception {
        Camera camera = new Camera(0);
        camera.open();
        Mat expected = camera.getFrame();
        camera.close();

        ImageFile file = new ImageFile(
                Core.DIR_TEST_RESOURCES,
                "ImageFile_write.jpg"
        );
        file.write(expected);
        Mat actual = file.read();

        assertTrue(
                String.format(
                        "Images of different sizes: Actual[%d x %d] vs " +
                        "Expected[%d x %d]",
                        actual.width(), actual.height(),
                        expected.width(), expected.height()
                ),
                actual.width() == expected.width() &&
                        actual.height() == expected.height()
        );
    }

}