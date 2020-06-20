package com.yuong.notes.camera;

import android.content.Context;
import android.view.SurfaceHolder;

public interface ICamera {
    void checkAvailableCameras();

    void launchCamera(CameraStatusListener listener);

    void openCamera(int cameraId);

    void startPreview(Context context, SurfaceHolder holder, float width, float height);

    void takePicture(Context context, CameraCallback callback);

    void stopPreview();

    void destroyCamera();

    void registerSensor(Context context);

    void unRegisterSensor(Context context);
}
