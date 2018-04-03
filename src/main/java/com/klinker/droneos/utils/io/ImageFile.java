package com.klinker.droneos.utils.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import com.klinker.droneos.cv.CVUtils;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * An implementation of {@link IOFile} that writes our {@link Mat} to
 * JPG files.
 */
public class ImageFile extends IOFile<Mat> {

    ///// Constructors /////////////////////////////////////////////////////////

    public ImageFile(String path) {
        super(path);
    }

    public ImageFile(String parent, String filename) {
        super(parent, filename);
    }

    public ImageFile(File file) {
        super(file);
    }


    ///// IOFile Callbacks /////////////////////////////////////////////////////

    @Override
    protected Mat readObject(FileReader fReader, File file) throws Exception {
        return Imgcodecs.imread(getPath());
        /*BufferedImage image = ImageIO.read(file);
        return CVUtils.imageToMat(image);*/
    }

    @Override
    protected boolean writeObject(Mat mat, FileWriter fWriter, File file)
            throws Exception {
        if (mat == null) return false;
        BufferedImage image = CVUtils.matToImage(mat);
        if (image == null) return false;
        return ImageIO.write(image, "JPG", file);

    }

}
