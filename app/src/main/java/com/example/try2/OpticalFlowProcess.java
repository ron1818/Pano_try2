package com.example.try2;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import java.util.ArrayList;

public class OpticalFlowProcess {
    private final String TAG = "OFLK";
    private Mat old_frame, old_gray;
    private Mat new_frame, new_gray;
    private MatOfPoint p0MatofPoint;
    private MatOfPoint2f p0, p1;

    public double orientation, amplitude;

    public OpticalFlowProcess(){}

    // Since the function Imgproc.goodFeaturesToTrack requires MatofPoint
    // therefore first p0MatofPoint is passed to the function and then converted to MatOfPoint2f
    public void GFTT(Mat frame){
        p0MatofPoint = new MatOfPoint();
        // convColor2Gray(frame, true);
        // frame is input_frame.gray;
        old_gray = frame.clone();
        Imgproc.goodFeaturesToTrack(old_gray, p0MatofPoint,100,0.3,7, new Mat(),7,false,0.04);
        p0 = new MatOfPoint2f(p0MatofPoint.toArray());
    }

    private void convColor2Gray(Mat frame, boolean isold){
        if(isold) {
            old_gray = new Mat(frame.width(), frame.height(), CvType.CV_8UC1);
            // Imgproc.cvtColor(frame, old_gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.cvtColor(frame, old_gray, Imgproc.COLOR_RGBA2GRAY);
        }
        else{
            new_gray = new Mat(frame.width(), frame.height(), CvType.CV_8UC1);
            Imgproc.cvtColor(frame, new_gray, Imgproc.COLOR_RGBA2GRAY);
        }
    }

    public void OFLK(Mat frame){

        p1 = new MatOfPoint2f(); // initialize p1
        // convColor2Gray(frame, false);
        // frame is input_frame.gray;
        new_gray = frame.clone();
        // calculate optical flow
        MatOfByte status = new MatOfByte();
        MatOfFloat err = new MatOfFloat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT + TermCriteria.EPS,10,0.03);
        Video.calcOpticalFlowPyrLK(old_gray, new_gray, p0, p1, status, err, new Size(15,15),4, criteria);
        byte StatusArr[] = status.toArray();
        Point p0Arr[] = p0.toArray();
        Point p1Arr[] = p1.toArray();
        // get good points
        ArrayList<Point> good_new = new ArrayList<>();
        ArrayList<Point> good_old = new ArrayList<>();
        ArrayList<Point> good_delta = new ArrayList<>();
        for (int i = 0; i<StatusArr.length ; i++ ) {
            if (StatusArr[i] == 1) {
                good_new.add(p1Arr[i]);
                good_old.add(p0Arr[i]);
                good_delta.add(new Point(p1Arr[i].x - p0Arr[i].x, p1Arr[i].y-p0Arr[i].y));
            }
        }

        // calculate mean good delta
        calcDelta(good_delta);
    }


    private void calcDelta(ArrayList<Point> delta){
        double x = 0;
        double y = 0;
        for (int i = 0; i < delta.size(); i++) {
            x += delta.get(i).x;
            y += delta.get(i).y;
        }
        double meanx = x/delta.size();
        double meany = y/delta.size();

        orientation = Math.atan2(meany, meanx);
        amplitude = Math.hypot(meanx, meany);

    }
}
