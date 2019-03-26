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

        RecyclerView recyclerChild = (RecyclerView) child;
        if (recyclerChild.computeVerticalScrollOffset() <= 0)
            intercept = true;
        return intercept;
    }

    public static boolean canPullUp(View child) {
        boolean intercept = false;
        RecyclerView recyclerChild = (RecyclerView) child;
        if (recyclerChild.computeVerticalScrollExtent() + recyclerChild.computeVerticalScrollOffset() >= recyclerChild.computeVerticalScrollRange())
            intercept = true;

        return intercept;
    }
}
