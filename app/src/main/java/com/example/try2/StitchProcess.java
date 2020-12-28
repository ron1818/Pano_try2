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
    private Mat mask1, mask2;
    private String p1, p2;

    public Bitmap Stitched;

    public Mat StitchedMat; // image always displayed
    public Bitmap Img1, Img2;

    public boolean IsSuccess=true;
    public String Msg;

    private double scaleFactor = 0.25;

    public StitchProcess(String path1){
        Mat _mat1 = Imgcodecs.imread(path1);
        loadFirstImage(_mat1);
    }

    public StitchProcess(Mat mat){
        loadFirstImage(mat);
    }

    private void loadFirstImage(Mat mat){
        StitchedMat = new Mat();
        // mat2 = new Mat();
        // make mat1 and mat2 smaller
        Imgproc.resize(mat, StitchedMat, new Size(0,0), scaleFactor, scaleFactor);
        // Imgproc.resize(_mat2, mat2, new Size(0,0), 0.25, 0.25);
        //convert stitchedmat to stitched bitmatp
        Stitched = Bitmap.createBitmap(StitchedMat.cols(), StitchedMat.rows(),Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(StitchedMat, Stitched);
        Img1 = Stitched;
    }

    public void Stitch(String path2){
        // String[] s = {p1, p2};
        Mat _mat2 = Imgcodecs.imread(path2);
        Stitch(_mat2);
    }

    public void Stitch(Mat mat){
        mat2 = new Mat();
        // make mat1 and mat2 smaller
        Imgproc.resize(mat, mat2, new Size(0,0), scaleFactor, scaleFactor);

        ImageStitchNative.StitchImages(StitchedMat, mat2, this);

        // show masked image
        mask1 = ImageStitchNative.Img1;
        mask2 = ImageStitchNative.Img2;
        Img1 = Bitmap.createBitmap(mask1.cols(), mask1.rows(),Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(mask1, Img1);
        Img2 = Bitmap.createBitmap(mask2.cols(), mask2.rows(),Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(mask2, Img2);

    }

    @Override
    public void onSuccess(Mat mat) {
        // public void onSuccess(Bitmap bitmap) {
        StitchedMat = mat;
        Stitched = Bitmap.createBitmap(StitchedMat.cols(), StitchedMat.rows(),Bitmap.Config.ARGB_8888);
        // convert Stitched mat to stitched
        org.opencv.android.Utils.matToBitmap(StitchedMat, Stitched);
        // Mat _tmp = new Mat();
        // org.opencv.android.Utils.bitmapToMat(Stitched, _tmp);
        // Imgproc.cvtColor(_tmp, StitchedMat, Imgproc.COLOR_RGBA2BGR);
        IsSuccess = true;
        Msg = "Success";

    }

    @Override
    public void onError(String errorMsg) {
        IsSuccess = false;
        Msg = errorMsg;

    }
}
