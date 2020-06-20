package com.yuong.notes.camera;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorController implements SensorEventListener {

    private static final String TAG = SensorController.class.getSimpleName();

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static SensorController mInstance;
    private CameraSensorListener mCameraSensorListener;

    public void setCameraSensorListener(CameraSensorListener listener) {
        this.mCameraSensorListener = listener;
    }

    private SensorController(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    public static SensorController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SensorController(context);
        }
        return mInstance;
    }

    public void start() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        mSensorManager.unregisterListener(this, mSensor);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        int x = (int) event.values[0];
        int y = (int) event.values[1];
        int z = (int) event.values[2];

        int angle = getSensorAngle(x, y);
        Log.i(TAG, "angle : " + angle);
        if (mCameraSensorListener != null) {
            mCameraSensorListener.onAngle(angle);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static int getSensorAngle(float x, float y) {
        if (Math.abs(x) > Math.abs(y)) {
            /**
             * 横屏倾斜角度比较大
             */
            if (x > 4) {
                /**
                 * 左边倾斜
                 */
                return 270;
            } else if (x < -4) {
                /**
                 * 右边倾斜
                 */
                return 90;
            } else {
                /**
                 * 倾斜角度不够大
                 */
                return 0;
            }
        } else {
            if (y > 7) {
                /**
                 * 左边倾斜
                 */
                return 0;
            } else if (y < -7) {
                /**
                 * 右边倾斜
                 */
                return 180;
            } else {
                /**
                 * 倾斜角度不够大
                 */
                return 0;
            }
        }
    }

    public interface CameraSensorListener {
        void onAngle(int angle);
    }
}
