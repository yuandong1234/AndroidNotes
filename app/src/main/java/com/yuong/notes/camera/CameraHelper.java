package com.yuong.notes.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraHelper implements ICamera {
    private static String TAG = CameraHelper.class.getSimpleName();
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();

    private int CAMERA_FRONT = -1;
    private int CAMERA_BACK = -1;
    private int CAMERA_DEFAULT = CAMERA_FRONT;//默认前摄像头

    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing;
    private int mCameraAngle = 90;//摄像头角度   默认为90度
    private boolean isCanCapture;
    private float mWidth, mHeight;
    private int mPreviewWidth, mPreviewHeight;

    private int mAngle, nowAngle;
    private SensorController mSensorController;

    public CameraHelper() {
        checkAvailableCameras();
    }

    //检查可使用的摄像头
    @Override
    public void checkAvailableCameras() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraNum = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraNum; i++) {
            Camera.getCameraInfo(i, info);
            switch (info.facing) {
                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    CAMERA_FRONT = info.facing;
                    break;
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    CAMERA_BACK = info.facing;
                    break;
            }
        }
        CAMERA_DEFAULT = CAMERA_FRONT;
    }

    //启动相机
    @Override
    public void launchCamera(CameraStatusListener listener) {
        if (!isCameraAvailable(CAMERA_DEFAULT)) {
            if (listener != null) {
                listener.onCameraUnavailable();
            }
            return;
        }
        if (mCamera == null) {
            openCamera(CAMERA_DEFAULT);
        }

        if (listener != null) {
            listener.onCameraOpened();
        }
    }

    //开启摄像头
    @Override
    public void openCamera(int cameraId) {
        try {
            this.mCamera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1 && this.mCamera != null) {
            try {
                this.mCamera.enableShutterSound(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //开启预览
    @Override
    public void startPreview(Context context, SurfaceHolder holder, float width, float height) {
        if (width == 0 || height == 0) {
            return;
        }

        if (mCamera == null || holder == null) {
            return;
        }

        Log.e(TAG, "窗口大小： width : " + width + "  height : " + height);
        Log.i(TAG, "camera start preview ");
        if (isPreviewing) {
            Log.i(TAG, "camera isPreviewing");
            return;
        }

        this.mWidth = width;
        this.mHeight = height;

        mCameraAngle = getCameraDisplayOrientation(context, CAMERA_DEFAULT);

        try {
            mParams = mCamera.getParameters();
            Camera.Size previewSize = getOptimalPreviewSize(mParams.getSupportedPreviewSizes(), (int) width, (int) height);
            Camera.Size pictureSize = getBestPictureSize(mParams.getSupportedPictureSizes(), height, width, width);

            mParams.setPreviewSize(previewSize.width, previewSize.height);
            Log.e(TAG, "preview_width : " + previewSize.width + "  preview_height : " + previewSize.height);
            Log.e(TAG, "picture_width : " + pictureSize.width + "  picture_height : " + pictureSize.height);

            mPreviewWidth = previewSize.width;
            mPreviewHeight = previewSize.height;

            if (pictureSize != null) {
                mParams.setPictureSize(pictureSize.width, pictureSize.height);
            } else {
                mParams.setPictureSize(1280, 720);
            }

            if (isSupportedFocusMode(mParams.getSupportedFocusModes(), Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (isSupportedFocusMode(mParams.getSupportedFocusModes(), Camera.Parameters.FOCUS_MODE_AUTO)) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }


            if (isSupportedPictureFormats(mParams.getSupportedPictureFormats(), ImageFormat.JPEG)) {
                mParams.setPictureFormat(ImageFormat.JPEG);
                mParams.setJpegQuality(100);
            }

            mParams.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

            float step = mParams.getExposureCompensationStep();
            int min = mParams.getMinExposureCompensation();
            int max = mParams.getMaxExposureCompensation();
            int cur = mParams.getExposureCompensation();
            Log.e(TAG, "step : " + step);
            Log.e(TAG, "min : " + min);
            Log.e(TAG, "max : " + max);
            Log.e(TAG, "cur : " + cur);
            mParams.setExposureCompensation((mParams.getMaxExposureCompensation() / 2));
            mCamera.setParameters(mParams);
            mParams = mCamera.getParameters();
            mCamera.setPreviewDisplay(holder);  //SurfaceView
            mCamera.setDisplayOrientation(mCameraAngle);//浏览角度
//                mCamera.setPreviewCallback(this); //每一帧回调
            mCamera.startPreview();//启动浏览
            isCanCapture = true;
            isPreviewing = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stopPreview() {
        if (null != mCamera) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                //这句要在stopPreview后执行，不然会卡顿或者花屏
                mCamera.setPreviewDisplay(null);
                isPreviewing = false;
                isCanCapture = false;
                Log.i(TAG, "=== Stop Preview ===");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroyCamera() {
        if (null != mCamera) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                //这句要在stopPreview后执行，不然会卡顿或者花屏
                mCamera.setPreviewDisplay(null);
                isPreviewing = false;
                isCanCapture = false;
                mCamera.release();
                mCamera = null;
                Log.i(TAG, "=== Destroy Camera ===");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //拍照
    @Override
    public void takePicture(Context context, CameraCallback callback) {
        if (mCamera == null || !isPreviewing || !isCanCapture) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        String focusMode = parameters.getFocusMode();
        if (focusMode.contains("continuous")) {
            mCamera.cancelAutoFocus();
        }

        takePictureInternal(context, callback);
        isCanCapture = false;
    }

    public void takePictureInternal(final Context context, final CameraCallback callback) {
        if (mCamera == null) {
            return;
        }
        switch (mCameraAngle) {
            case 90:
                nowAngle = Math.abs(mAngle + mCameraAngle) % 360;
                break;
            case 270:
                nowAngle = Math.abs(mCameraAngle - mAngle);
                break;
        }

        Log.i(TAG, mAngle + " = " + mCameraAngle + " = " + nowAngle);
        Log.i(TAG, "开始拍照 " + System.currentTimeMillis());
        mCamera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                //快门
            }
        }, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.i(TAG, "拍照结果 " + System.currentTimeMillis());
                isPreviewing = false;
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                if (CAMERA_DEFAULT == CAMERA_BACK) {
                    matrix.setRotate(nowAngle);
                } else if (CAMERA_DEFAULT == CAMERA_FRONT) {
                    matrix.setRotate(360 - nowAngle);
                    matrix.postScale(-1, 1);
                }

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Log.e(TAG, "picture result ---->  width : " + bitmap.getWidth() + "  height : " + bitmap.getHeight());
                String fileName = System.currentTimeMillis() + ".jpg";
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

                if (callback != null) {
                    if (nowAngle == 90 || nowAngle == 270) {
                        callback.captureResult(bitmap, file.getAbsolutePath(), true);
                    } else {
                        callback.captureResult(bitmap, file.getAbsolutePath(), false);
                    }

                    savePicture(bitmap, file.getAbsolutePath());
                }
            }
        });
    }

    /**
     * 相机是否可用
     */
    private boolean isCameraAvailable(int cameraID) {
        Log.e(TAG, "cameraID : " + cameraID);
        boolean isAvailable = false;
        Camera mCamera = null;
        try {
            mCamera = Camera.open(cameraID);
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            e.printStackTrace();
            isAvailable = false;
        } finally {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
                isAvailable = true;
            }
        }
        return isAvailable;
    }

    /**
     * 相机是否支持聚焦
     */
    public boolean isSupportedFocusMode(List<String> focusList, String focusMode) {
        for (int i = 0; i < focusList.size(); i++) {
            if (focusMode.equals(focusList.get(i))) {
                Log.i(TAG, "FocusMode supported " + focusMode);
                return true;
            }
        }
        Log.i(TAG, "FocusMode not supported " + focusMode);
        return false;
    }

    /**
     * 相机图片是否支持某种格式
     */
    public boolean isSupportedPictureFormats(List<Integer> supportedPictureFormats, int format) {
        for (int i = 0; i < supportedPictureFormats.size(); i++) {
            if (format == supportedPictureFormats.get(i)) {
                Log.i(TAG, "Formats supported " + format);
                return true;
            }
        }
        Log.i(TAG, "Formats not supported " + format);
        return false;
    }

    public int getCameraDisplayOrientation(Context context, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = wm.getDefaultDisplay().getRotation();
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
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public boolean isSupportPreviewSize(double ratio) {
        boolean isSupport = false;
        Camera mCamera = null;
        try {
            mCamera = Camera.open(CAMERA_DEFAULT);
            Camera.Parameters mParameters = mCamera.getParameters();
            List<Camera.Size> sizes = mParameters.getSupportedPreviewSizes();
            Collections.sort(sizes, sizeComparator);
            for (Camera.Size size : sizes) {
                Log.e(TAG, "preview support size : ratio : " + size.height * 1.0d / size.width + "  " + size.height + "x" + size.width);
                if (size.height * 1.0d / size.width == ratio) {
                    isSupport = true;
                }
            }
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
        return isSupport;
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        Collections.sort(sizes, sizeComparator);

        int targetWidth = w;

        //先判断有没有相同比例的尺寸，选出最接近目标宽度的
        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (targetRatio == ratio && Math.abs(size.height - targetWidth) < minDiff) {
                Log.i(TAG, "相同尺寸 ratio " + ratio + "   -----> " + size.width + "x" + size.height);
                optimalSize = size;
                minDiff = Math.abs(size.height - targetWidth);
            }
        }

        if (optimalSize != null)
            return optimalSize;

        //没有相同的比列，则选择最接近的比列
        minDiff = Double.MAX_VALUE;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            Log.i(TAG, "ratio " + ratio + "   -----> " + size.width + "x" + size.height);
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetWidth) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetWidth);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetWidth) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetWidth);
                }
            }
        }
        Log.e(TAG, "finally choose size:optimalSize width=" + optimalSize.width + "   optimalSize height=" + optimalSize.height);
        return optimalSize;

    }

    /**
     * 获得适合的图片分辨率
     *
     * @param list
     * @param th
     * @param tw
     * @param maxWidth 最大图片的宽度
     * @return
     */
    public Camera.Size getBestPictureSize(List<Camera.Size> list, float th, float tw, float maxWidth) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) tw / (double) th;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        Collections.sort(list, sizeComparator);

        StringBuilder builder = new StringBuilder();
        for (Camera.Size supportedPicResolution : list) {
            double rate = (double) supportedPicResolution.height / (double) supportedPicResolution.width;
            builder.append("\n")
                    .append(supportedPicResolution.width)
                    .append('x')
                    .append(supportedPicResolution.height)
                    .append("   rate : " + rate);
        }

        Log.i(TAG, "Supported picture size: " + builder.toString());
        Log.i(TAG, "targetRatio " + targetRatio);

        //先判断有没有相同比例的尺寸，选出最接近目标宽度的
        for (Camera.Size size : list) {
            double ratio = (double) size.height / size.width;
            if (targetRatio == ratio && Math.abs(size.height - maxWidth) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - maxWidth);
            }
        }

        if (optimalSize != null)
            return optimalSize;

        //没有相同的比列，则选择最接近的比列
        minDiff = Double.MAX_VALUE;
        for (Camera.Size size : list) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            Log.i(TAG, " size width : " + size.width + "  height : " + size.height);
            if (Math.abs(size.height - maxWidth) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - maxWidth);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : list) {
                if (Math.abs(size.height - maxWidth) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - maxWidth);
                }
            }
        }
        Log.i(TAG, "picture size:  width : " + optimalSize.width + "  height : " + optimalSize.height);
        return optimalSize;
    }

    /**
     * 图片保存到本地
     *
     * @param bitmap
     * @param path
     */
    private void savePicture(final Bitmap bitmap, final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap2File(bitmap, path);
            }
        }).start();
    }


    public static void bitmap2File(Bitmap bitmap, String path) {
        FileOutputStream os = null;
        try {
            File file = new File(path);
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            Log.i("FileUtil", "picture path : " + file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerSensor(Context context) {
        mSensorController = SensorController.getInstance(context);
        mSensorController.setCameraSensorListener(new SensorController.CameraSensorListener() {
            @Override
            public void onAngle(int angle) {
                mAngle = angle;
            }
        });
        mSensorController.start();
    }

    @Override
    public void unRegisterSensor(Context context) {
        if (mSensorController != null) {
            mSensorController.stop();
        }
    }

    private class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
