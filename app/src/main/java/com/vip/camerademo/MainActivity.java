package com.vip.camerademo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /**
     * relative classes
     */
    // Camera
    // SurfaceView
    // MediaRecorder

    /**
     * 创建一个独立自定义的相机app基本遵循如下步骤：
     *
     * ○ 检测和访问相机 - 首先代码检测该设备相机是否存在，如果存在才能请求访问设备相机.
     * ○ 创建一个预览来显示相机图像 - 在你的布局中使用SurfaceView控件，然后在代码中继承SurfaceHolder.Callback接口并且实现接口中的方法来显示来自相机的图像信息。
     * ○ 设置相机基本参数 - 根据需求设置相机预览尺寸，图片大小，预览方向，图片方向等。
     * ○ 设置拍照录像监听 - 当用户按下按钮时调用Camera#takePicture或者MediaRecorder#start()来进行拍照或录像。
     * ○ 文件保存 - 当拍照结束或者录像视频结束时，需要开启一个后台线程去保存图片或者视频文件。
     * ○ 释放相机资源 - Camera硬件是一个共享资源，所以你必须小心的编写你的应用代码来管理相机资源。一般在Activity的生命周期的onResume中开机相机，在onPause中释放相机。
     *
     * todo: 注意:
     * 当你不在使用相机资源时，记得调用Camera#release方法来释放相机资源，否则其他应用甚至你自己的应用再次请求访问设备相机时会失败，并且crash。
     */

    // 检测相机硬件是否存在
    // 一般情况，我们会在运行代码时检测该设备是否有相机硬件，如果有相机硬件，才进一步去访问相机，如下是检测相机硬件是否存在是代码示例:
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    // Android 设备可以有多个相机硬件，现在一般手机都是前后两个camera，
    // 因此我们在Android2.3以后也可以使用Camera#getNumberOfCameras()方法来获得当前设备camera个数来判断相机硬件是否存在。


    // 访问相机设备
    private void changeCamera(int id) {
        if (null != mCamera) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            mCameraId = -1;
        }

        if (id < 0) {
            return;
        }

        Camera camera = Camera.open(id);
        if (null == camera) {
            return;
        }

        mCamera = camera;
        mCameraId = id;
        checkSurfaceHolder();

//        camera.setParameters();
    }

    private void checkSurfaceHolder() {
        Camera camera = mCamera;
        if (null == camera) {
            return;
        }

        SurfaceHolder sfHolder = sf.getHolder();
        if (null == sfHolder.getSurface()) {
            // do nothing but waiting for surface created
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // 为了改变预览方向，请在surfaceChanged()方法中显示调用Camera#stopPreview()来停止预览，
        // 改变方向以后再次调用Camera#stratPreview()启动预览。
        fixDisplayVsCameraOrientation(getWindowManager().getDefaultDisplay(), mCameraId, camera);

        // start preview with new settings
        try {
            camera.setPreviewDisplay(sfHolder);
            camera.startPreview();

            // re-start face detection feature
            // startFaceDetection();
        } catch (Exception e){
            Log.d("yytest", "Error starting camera preview: " + e.getMessage());
        }
    }

    public void startFaceDetection(Camera camera) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // Try starting Face Detection
            Camera.Parameters params = camera.getParameters();
            // start face detection only *after* preview has started
            if (params.getMaxNumDetectedFaces() > 0){
                // camera supports face detection, so can start it:
                camera.startFaceDetection();
            }
        }
    }

    public static boolean fixDisplayVsCameraOrientation(Display display,
                                                        int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        boolean mirror = false;
        Log.d("yytest", "fixDisplayVsCameraOrientation degrees = " + degrees);
        Log.d("yytest", "fixDisplayVsCameraOrientation info.orientation = " + info.orientation);

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mirror = true;
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
            mirror = false;
        }

        if (upsideDownModel()) {
            result = (result + 180) % 360;
        }

        camera.setDisplayOrientation(result);
        return mirror;
    }

    public static boolean upsideDownModel() {
        String mode = Build.MODEL;
        if(mode != null){
            if (mode.equals("Nexus 6")){
                return true;
            }
        }
        return false;
    }

    // 创建一个预览类
    private class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        public MySurfaceView(Context context) {
            super(context);
            mHolder = getHolder();
            mHolder.addCallback(this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e("yytest", "surfaceCreated");
            checkSurfaceHolder();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d("yytest", "surfaceChanged");
            checkSurfaceHolder();
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }
    }

    private FrameLayout sfC;
    private MySurfaceView sf;
    private Camera mCamera;
    private int mCameraId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        sfC = (FrameLayout) findViewById(R.id.sf);
        sf = new MySurfaceView(this);
        sfC.addView(sf);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        changeCamera(-1);
    }

    public void openFront(View view) {
        int id = getAvailableCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        if (id >= 0) {
            changeCamera(id);
        } else {
            Toast.makeText(this, "不支持前置摄像头", Toast.LENGTH_SHORT).show();
        }
    }

    public void openBack(View view) {
        int id = getAvailableCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        if (id >= 0) {
            changeCamera(id);
        } else {
            Toast.makeText(this, "不支持后置摄像头", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param facing {@link Camera.CameraInfo#CAMERA_FACING_FRONT}、{@link Camera.CameraInfo#CAMERA_FACING_BACK}
     */
    private int getAvailableCameraId(int facing) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                return i;
            }
        }
        return -1;
    }

}
