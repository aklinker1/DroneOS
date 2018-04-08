package com.klinker.droneos.cv;

import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public abstract class Window extends JFrame implements WindowListener {

    private double startTime;
    private double count;
    private String title;
    private OnWindowClosedListener onCloseListener;


    public interface OnWindowClosedListener {
        void onWindowClosed();
    }


    public Window(String title) {
        this.title = title;
        setTitle(title);
        addWindowListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void showWindow() {
        setVisible(true);
        startTime = System.currentTimeMillis();
    }

    public void closeWindow() {
        setVisible(false);
    }

    public String getTitle() {
        return title;
    }

    public void setOnCloseListener(OnWindowClosedListener listener) {
        this.onCloseListener = listener;
    }

    public void loadImage(Mat image) {
        count += 1;
    }

    public double getFPS() {
        return count * 1000 / (System.currentTimeMillis() - startTime);
    }

    // region Window Listeners
    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        if (onCloseListener != null) onCloseListener.onWindowClosed();
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
    // endregion

}
