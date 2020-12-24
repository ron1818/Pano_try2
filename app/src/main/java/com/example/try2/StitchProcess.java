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

    public Bitmap Stitched;

    public Mat StitchedMat; // image always displayed

    public StitchProcess(String path1){
        Mat _mat1 = Imgcodecs.imread(path1);
        // Mat _mat2 = Imgcodecs.imread(path2);
        StitchedMat = new Mat();
        // mat2 = new Mat();
        // make mat1 and mat2 smaller
        Imgproc.resize(_mat1, StitchedMat, new Size(0,0), 0.25, 0.25);
        // Imgproc.resize(_mat2, mat2, new Size(0,0), 0.25, 0.25);
        //convert stitchedmat to stitched bitmatp
        Stitched = Bitmap.createBitmap(StitchedMat.cols(), StitchedMat.rows(),Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(StitchedMat, Stitched);
    }

    public void Stitch(String path2){
        // String[] s = {p1, p2};
        Mat _mat2 = Imgcodecs.imread(path2);
        mat2 = new Mat();
        // make mat1 and mat2 smaller
        Imgproc.resize(_mat2, mat2, new Size(0,0), 0.25, 0.25);

        ImageStitchNative.StitchImages(StitchedMat, mat2, this);
    }

    @Override
    public void onSuccess(Bitmap bitmap) {
        Stitched = bitmap;
        // convert Stitched to stitched mat
        Mat _tmp = new Mat();
        org.opencv.android.Utils.bitmapToMat(Stitched, _tmp);
        Imgproc.cvtColor(_tmp, StitchedMat, Imgproc.COLOR_RGBA2BGR);

    }

    @Override
    public void onError(String errorMsg) {

    }
}
