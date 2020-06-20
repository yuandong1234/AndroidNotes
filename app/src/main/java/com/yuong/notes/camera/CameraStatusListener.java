package com.yuong.notes.camera;

public interface CameraStatusListener {
    void onCameraUnavailable();

    void onCameraOpened();
}
