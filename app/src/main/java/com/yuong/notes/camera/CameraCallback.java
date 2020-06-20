package com.yuong.notes.camera;

import android.graphics.Bitmap;

public interface CameraCallback {
    void captureResult(Bitmap bitmap, String path, boolean isVertical);
}
