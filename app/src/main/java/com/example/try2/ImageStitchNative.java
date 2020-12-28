package com.example.try2;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import org.opencv.core.Mat;

import java.io.File;

public class ImageStitchNative {
    public final static int OK = 0;
    public final static int ERR_NEED_MORE_IMGS = 1;
    public final static int ERR_HOMOGRAPHY_EST_FAIL = 2;
    public final static int ERR_CAMERA_PARAMS_ADJUST_FAIL = 3;

    public static Mat Stitched = new Mat();
    public static Mat Img1 = new Mat();
    public static Mat Img2 = new Mat();

    static {
        System.loadLibrary("native-stitch2");
    }

    private static long stitcherpointer;

    /* static {
        stitcherpointer = initStitcher();
    } */

    public static void StitchImages(Mat img1, Mat img2, @NonNull onStitchResultListener listener) {
        // wh[0] status code, wh[1] bitmap width, wh[2] bitmap height
        int wh[] = stitchMats2(img1.getNativeObjAddr(), img2.getNativeObjAddr(), Stitched.getNativeObjAddr(), Img1.getNativeObjAddr(), Img2.getNativeObjAddr(), true);
        switch (wh[0]) {
            case OK: {

                // empty bimap of the stitched image size
                // Bitmap bitmap = Bitmap.createBitmap(wh[1], wh[2], Bitmap.Config.ARGB_8888);
                boolean result = Stitched.size().width>0;
                if (result && Stitched != null){
                    listener.onSuccess(Stitched);
                }else{
                    listener.onError("图片合成失败");
                }
            }
            break;
            case ERR_NEED_MORE_IMGS: {
                listener.onError("需要更多图片");
                return;
            }
            case ERR_HOMOGRAPHY_EST_FAIL: {
                listener.onError("图片对应不上");
                return;
            }
            case ERR_CAMERA_PARAMS_ADJUST_FAIL: {
                listener.onError("图片参数处理失败");
                return;
            }
        }
    }


    private native static int[] stitchMats(long mat1, long mat2);
    private native static int[] stitchMats2(long mat1, long mat2, long stitched, long mask1, long mask2, boolean isleft);

    private native static int getBitmap(Bitmap bitmap);

    public native static int initStitcher();



    public interface onStitchResultListener {

        // void onSuccess(Bitmap bitmap);
        void onSuccess(Mat mat);

        void onError(String errorMsg);
    }

}
