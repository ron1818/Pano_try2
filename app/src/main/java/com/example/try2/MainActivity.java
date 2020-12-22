package com.example.try2;

import android.annotation.SuppressLint;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.try2.MyCameraView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends CameraActivity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private MyCameraView mOpenCvCameraView;
    private List<Size> mResolutionList;
    private Menu mMenu;
    private boolean mCameraStarted = false;
    private boolean mMenuItemsCreated = false;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;

    private Button captureBtn;
    private ToggleButton recordBtn;

    private Mat img_rgba, img_bgr;

    private int counter = 0;
    private int step = 5;
    private boolean isRecording = false;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    // mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (MyCameraView) findViewById(R.id.myCameraView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        oflk = new OpticalFlowProcess();

        /* // create capture button, will be removed
        captureBtn = (Button) findViewById(R.id.CaptureBtn);
        captureBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onCapture(v);
                // counter ++;
                // t.setText(String.valueOf(counter));
            }
        });

        // create record button
        recordBtn = (ToggleButton) findViewById(R.id.RecordBtn);
        recordBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onStartStopVideo(buttonView, isChecked);
            }
        }); */



    }

    private void onStartStopVideo(CompoundButton buttonView, boolean isChecked) {
        isRecording = isChecked;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mCameraStarted = true;
        // setupMenuItems();
        resizeImage();
        img_rgba = new Mat(height, width, CvType.CV_8UC4);
        img_bgr = new Mat(height, width, CvType.CV_8UC3);
    }

    public void onCameraViewStopped() {
        img_rgba.release();
        img_bgr.release();
    }

    private OpticalFlowProcess oflk;

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        if(isRecording) { // if recording, take pictures every step frames
            if(counter % step == 0){
                // String fileName = getFilename();
                // debug take picture
                // mOpenCvCameraView.takePicture(fileName);
                // Toast.makeText(this, String.format("frame %d Saved", counter), Toast.LENGTH_SHORT).show();
                // UC: get gftt
                oflk.GFTT(inputFrame.gray());
            }
            counter++;

        }
        img_rgba = inputFrame.rgba();
        String size = String.format("Image Size: %d * %d", img_rgba.height(), img_rgba.width());
        return inputFrame.rgba();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        mMenu = menu;
        setupMenuItems(); */
        return true;
    }

    private void resizeImage() {
        mResolutionList = mOpenCvCameraView.getResolutionList();
        Size resolution = mResolutionList.get(0);
        mOpenCvCameraView.setResolution(resolution);
        resolution = mOpenCvCameraView.getResolution(true);
        Toast.makeText(this, String.format("Preview resolution: %s", String.valueOf(resolution)), Toast.LENGTH_SHORT).show();
        resolution = mOpenCvCameraView.getResolution(false);
        Toast.makeText(this, String.format("Picture resolution: %s", String.valueOf(resolution)), Toast.LENGTH_SHORT).show();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        /* Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item.getGroupId() == 1)
        {
            mOpenCvCameraView.setEffect((String) item.getTitle());
            Toast.makeText(this, mOpenCvCameraView.getEffect(), Toast.LENGTH_SHORT).show();
        }
        else if (item.getGroupId() == 2)
        {
            int id = item.getItemId();
            Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            resolution = mOpenCvCameraView.getResolution(true);
            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
        } */

        return true;
    }

    @SuppressLint("SimpleDateFormat")
    private String getFilename(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String fileName = "DSC_"+currentDateandTime+".jpg";
        File file = new File(path, fileName);
        Boolean bool = null;
        fileName = file.toString();
        return fileName;
    }

    private boolean onCapture(View v) {
        Log.i(TAG,"onCapture event");
        String fileName = getFilename();
        mOpenCvCameraView.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        return false;
    }
}
