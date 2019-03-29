package com.app.refresh.intercept;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by yuandong
 * on 2019/3/22 17:07.
 */
public class RecyclerViewIntercept {
    public static boolean canPullDown(View child) {
        boolean intercept = false;

        RecyclerView recyclerView = (RecyclerView) child;
        if (recyclerView.getChildCount() == 0) {
            return true;
        }
        if (recyclerView.computeVerticalScrollOffset() <= 0)
            intercept = true;
        return intercept;
    }

    public static boolean canPullUp(View child) {
        boolean intercept = false;
        RecyclerView recyclerView = (RecyclerView) child;
        if (recyclerView.getChildCount() == 0) {
            return false;
        }
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
            intercept = true;

        return intercept;
    }
}
