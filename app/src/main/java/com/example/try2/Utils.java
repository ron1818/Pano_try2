package com.example.try2;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import static org.opencv.imgproc.Imgproc.COLOR_RGBA2BGR;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class Utils {
    // convert rgba image (from camera) to bgr (for store)
    public static Mat ConvRgba2Bgr(Mat img_rgba){
        Mat img_bgr = new Mat(img_rgba.width(), img_rgba.height(), CvType.CV_8UC3);
        cvtColor(img_rgba, img_bgr, COLOR_RGBA2BGR);
        return img_bgr;
    }

    public static Mat Rot90(Mat mat){
        // rotate 90 clockwise then save image
        Mat rot = new Mat();
        Core.rotate(mat, rot, Core.ROTATE_90_CLOCKWISE);
        return rot;
    }
}
