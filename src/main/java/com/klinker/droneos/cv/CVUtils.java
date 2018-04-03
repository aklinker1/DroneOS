package com.klinker.droneos.cv;

import java.awt.image.BufferedImage;

import org.opencv.core.Mat;

public class CVUtils {

    public static BufferedImage matToImage(Mat frame) {
        BufferedImage image;
        int cols = frame.cols();
        int rows = frame.rows();
        int elemSize = (int) frame.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;
        frame.get(0, 0, data);
        switch (frame.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;
                // bgr to rgb
                byte b;
                for (int i = 0; i < data.length; i = i + 3) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;
            default:
                return null;
        }

        // Reuse existing BufferedImage if possible
        image = new BufferedImage(cols, rows, type);
        image.getRaster().setDataElements(0, 0, cols, rows, data);

        return image;
    }

}
