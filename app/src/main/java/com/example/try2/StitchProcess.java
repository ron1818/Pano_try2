package com.example.try2;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.features2d.FlannBasedMatcher;
import org.opencv.features2d.ORB;

public class StitchProcess {
    public StitchProcess(){}
    private ORB orb;
    private FlannBasedMatcher flann;

    public void Stitch(Mat img1, Mat img2){
        // warpPerspective()
        Calib3d.findHomography()
    }
}
