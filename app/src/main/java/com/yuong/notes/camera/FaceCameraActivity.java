package com.yuong.notes.camera;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.yuong.notes.R;

import androidx.appcompat.app.AppCompatActivity;

public class FaceCameraActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback, CameraStatusListener, CameraCallback {
    private FrameLayout flFaceRecognitionFrame;
    private ImageView ivFaceRecognitionOutline;
    private ImageView ivFaceRecognitionFrame;
    private VideoView videoPreview;
    private ImageView ivResult;

    private LinearLayout llUpload;
    private LinearLayout llTakePicture;
    private TextView tvTakePicture;
    private LinearLayout llAlbum;

    private CameraHelper mCameraHelper;

    private boolean reTake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_camera);
        initView();
    }

    private void initView() {
        flFaceRecognitionFrame = findViewById(R.id.fl_face_recognition_frame);
        ivFaceRecognitionOutline = findViewById(R.id.iv_face_recognition_outline);
        ivFaceRecognitionFrame = findViewById(R.id.iv_face_recognition_frame);
        videoPreview = findViewById(R.id.video_preview);
        ivResult = findViewById(R.id.iv_result);
        llUpload = findViewById(R.id.ll_upload);
        llTakePicture = findViewById(R.id.ll_take_picture);
        tvTakePicture = findViewById(R.id.tv_take_picture);
        llAlbum = findViewById(R.id.ll_album);

        llUpload.setOnClickListener(this);
        llTakePicture.setOnClickListener(this);
        llAlbum.setOnClickListener(this);
        videoPreview.getHolder().addCallback(this);

        mCameraHelper = new CameraHelper();

        boolean isSupport1_1 = mCameraHelper.isSupportPreviewSize(1d);
        Log.e("FaceCameraActivity", "isSupport1_1 : " + isSupport1_1);
        if (!isSupport1_1) {
            adjustPreviewWindow();
        }
        Log.e("FaceCameraActivity", "initView..... " + System.currentTimeMillis());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_take_picture:
                if (reTake) {
                    reTake = false;
                    tvTakePicture.setText("拍照");
                    ivResult.setVisibility(View.GONE);
                    llUpload.setVisibility(View.GONE);
                    mCameraHelper.startPreview(this, videoPreview.getHolder(), videoPreview.getWidth(), videoPreview.getHeight());
                } else {
                    mCameraHelper.takePicture(this, this);
                }
                break;
            case R.id.ll_album:
                PictureSelectorHelper.openAlbum(this);
                break;
            case R.id.ll_upload:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("FaceCameraActivity", "onResume..... " + System.currentTimeMillis());
        if (!reTake) {
            mCameraHelper.startPreview(this, videoPreview.getHolder(), videoPreview.getWidth(), videoPreview.getHeight());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCameraHelper.stopPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("FaceCameraActivity", "surfaceCreated..... " + System.currentTimeMillis());
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCameraHelper.launchCamera(FaceCameraActivity.this);
            }
        }).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("FaceCameraActivity", "width : " + width + "  height : " + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCameraHelper.destroyCamera();
    }

    @Override
    public void onCameraUnavailable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FaceCameraActivity.this, "相机不可用！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCameraOpened() {
        Log.e("FaceCameraActivity", "onCameraOpened..... ");
        if (!reTake) {
            mCameraHelper.startPreview(this, videoPreview.getHolder(), videoPreview.getWidth(), videoPreview.getHeight());
        }
    }

    @Override
    public void captureResult(Bitmap bitmap, String path, boolean isVertical) {
        reTake = true;
        tvTakePicture.setText("重拍");
        ivResult.setVisibility(View.VISIBLE);
        llUpload.setVisibility(View.VISIBLE);
        ivResult.setImageBitmap(bitmap);
    }

    private void adjustPreviewWindow() {
        /**
         * 默认预览窗口1x1(780x780),相机不支持1x1则使用4x3(780x1040)
         */
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flFaceRecognitionFrame.getLayoutParams();
        params.width = 792;
        params.height = 1056;
        flFaceRecognitionFrame.setLayoutParams(params);
        ivFaceRecognitionOutline.setImageResource(R.mipmap.img_face_recognition_outline43);
        ivFaceRecognitionFrame.setImageResource(R.mipmap.img_face_recognition_frame43);
    }
}
