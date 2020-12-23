package com.example.try2;

import android.graphics.Bitmap;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.features2d.FlannBasedMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class StitchProcess implements ImageStitchNative.onStitchResultListener {
    private Mat mat1, mat2;
    private String p1, p2;

    public StitchProcess(String path1, String path2){
        Mat _mat1 = Imgcodecs.imread(path1);
        Mat _mat2 = Imgcodecs.imread(path2);
        mat1 = new Mat();
        mat2 = new Mat();
        // make mat1 and mat2 smaller
        Imgproc.resize(_mat1, mat1, new Size(0,0), 0.25, 0.25);
        Imgproc.resize(_mat2, mat2, new Size(0,0), 0.25, 0.25);
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
