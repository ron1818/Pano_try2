package com.example.try2;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;

import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileOutputStream;
import java.util.List;

public class MyCameraView extends JavaCameraView implements Camera.PictureCallback {

    private static final String TAG = "Sample::MyCameraView";
    private String mPictureFileName;

    public MyCameraView(Context context, int cameraId) {
        super(context, cameraId);
    }

    public MyCameraView(Context context, AttributeSet attr){
        super(context, attr);
    }

    public List<Camera.Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(Camera.Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.set("jpeg-quality", 70);
        // parameters.setPictureFormat(PixelFormat.)
        // parameters.setPictureSize(getWidth(), getHeight());
        // set picture size to maximum
        parameters.setPictureSize(mMaxWidth, mMaxHeight);
        mCamera.setParameters(parameters);
    }

    public Camera.Size getResolution(boolean isPreview){
        if(isPreview)
            return mCamera.getParameters().getPreviewSize();
        else
            return mCamera.getParameters().getPictureSize();

    }

    public void takePicture(final String fileName) {
        Log.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        // onRawTaken = new RawPictureCallback(mMaxWidth, mMaxHeight);
        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }
    // private RawPictureCallback onRawTaken;

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);
        Mat colored = Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.IMREAD_UNCHANGED);


        // Write the image in a file (in jpeg format)
        try {
            // rotate 90 clockwise then save image
            Mat rot = Utils.Rot90(colored);

            Imgcodecs.imwrite(mPictureFileName, rot);
            // FileOutputStream fos = new FileOutputStream(mPictureFileName);

            // fos.write(data);
            // fos.close();

        } catch (Exception e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

    }

}
