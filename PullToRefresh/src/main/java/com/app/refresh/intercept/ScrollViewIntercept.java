package com.app.refresh.intercept;

import android.view.View;
import android.widget.ScrollView;

/**
 * Created by yuandong
 * on 2019/3/22 17:06.
 */
public class ScrollViewIntercept {

    public static boolean canPullDown(View view) {
        boolean enable = false;
        if (view instanceof ScrollView) {
            if (view.getScrollY() <= 0) {
                enable = true;
            }
        }
        return enable;
    }

    public static boolean canPullUp(View view) {
        boolean enable = false;
        if (view instanceof ScrollView) {
            if (view.getScrollY() >= (((ScrollView) view).getChildAt(0).getHeight() - view.getHeight())) {
                enable = true;
            }
        }
        return enable;
    }
}
