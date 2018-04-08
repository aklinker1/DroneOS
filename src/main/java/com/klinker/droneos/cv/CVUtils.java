package com.klinker.droneos.cv;

import java.awt.image.BufferedImage;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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

    public static Point findChessboard(Mat image) {
        
        Mat greyscale = new Mat();
        Imgproc.cvtColor(image, greyscale, Imgproc.COLOR_BGR2GRAY);

//        Size size = new Size(4, 3);
//        Size size = new Size(9, 7);
        Size size = new Size(6, 4);
        MatOfPoint2f corners = new MatOfPoint2f();
        boolean found = Calib3d.findChessboardCorners(
                greyscale,
                size,
                corners,
                Calib3d.CALIB_CB_FAST_CHECK
        );

        Point3 sums = new Point3(0, 0, 0);
        for (Point p : corners.toArray()) {
            sums.x += p.x;
            sums.y += p.y;
            sums.z++;
        }
        Point goal = new Point(sums.x / sums.z, sums.y / sums.z);
        Point center = new Point(image.width() / 2.0, image.height() / 2.0);

        if (!found) return null;
        
        Scalar lineColor = new Scalar(0, 0, 255);
        Scalar pointColor = new Scalar(0, 180, 255);
        Calib3d.drawChessboardCorners(image, size, corners, found);
        Imgproc.line(image, center, goal, lineColor, 2);
        Imgproc.circle(image, center, 2, pointColor, 4);
        Imgproc.circle(image, goal, 2, pointColor, 4);

        return new Point(goal.x - center.x, goal.y - center.y);
    }

}
