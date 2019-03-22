package com.app.refresh.intercept;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;

/**
 * Created by yuandong
 * on 2019/3/22 16:56.
 */
public class ViewInterceptManager {

    public static boolean canPullDown(View view, int touchSlop, float distance) {
        if (view instanceof ScrollView) {
            return ScrollViewIntercept.canPullDown(view);
        } else if (view instanceof AdapterView) {
            return true;
        } else if (view instanceof RecyclerView) {
            return true;
        } else {
            if (Math.abs(distance) >= touchSlop) {
                return true;
            }
            return false;
        }
    }

    public static boolean canPullUp(View view, int touchSlop, float distance, int viewGroupHeight) {
        if (view instanceof ScrollView) {
            return ScrollViewIntercept.canPullUp(view);
        } else if (view instanceof AdapterView) {
            return true;
        } else if (view instanceof RecyclerView) {
            return true;
        } else {
            if (Math.abs(distance) >= touchSlop) {
                return true;
            }
            return false;
        }
    }

}
