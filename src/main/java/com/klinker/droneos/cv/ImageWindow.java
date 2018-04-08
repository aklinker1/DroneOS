package com.klinker.droneos.cv;

import org.opencv.core.Mat;

import javax.swing.*;

public class ImageWindow extends Window {

    private ImageIcon mImageView;
    private JLabel mLabel;

    public ImageWindow(String title) {
        super(title);
        mImageView = new ImageIcon();
        mLabel = new JLabel(mImageView);
        add(mLabel);
    }

    @Override
    public void loadImage(Mat image) {
        super.loadImage(image);
        setSize(image.width(), image.height());
        mImageView.setImage(CVUtils.matToImage(image));
        mLabel.updateUI();
    }

}
