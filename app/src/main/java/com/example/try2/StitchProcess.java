package com.example.try2;

import android.graphics.Bitmap;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.features2d.FlannBasedMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;

public class StitchProcess implements ImageStitchNative.onStitchResultListener {
    private Mat mat1, mat2;
    private String p1, p2;

    public StitchProcess(String path1, String path2){
        mat1 = Imgcodecs.imread(path1);
        mat2 = Imgcodecs.imread(path2);
        p1 = path1;
        p2 = path2;
    }

    public void Stitch(){
        // String[] s = {p1, p2};
        ImageStitchNative.StitchImages(mat1, mat2, this);
    }

    @Override
    public void onSuccess(Bitmap bitmap) {

    }

    @Override
    public void onError(String errorMsg) {

    }
}
