package com.app.refresh.intercept;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by yuandong
 * on 2019/3/22 17:04.
 */
public class AdapterViewIntercept {

    public static boolean canPullDown(View child) {
        boolean intercept = true;
        AdapterView adapterView = (AdapterView) child;
        if (adapterView.getCount() == 0) {
            return true;
        }
        // 判断AbsListView是否已经到达内容最顶部
        if (adapterView.getFirstVisiblePosition() != 0 || adapterView.getChildAt(0).getTop() != 0) {
            // 如果没有达到最顶端，则仍然将事件下放
            intercept = false;
        }
        return intercept;
    }

    public static boolean canPullUp(View child, int viewGroupHeight) {
        boolean intercept = false;
        AdapterView adapterView = (AdapterView) child;
        if (adapterView.getCount() == 0) {
            return false;
        }
        // 判断AbsListView是否已经到达内容最底部
        if (adapterView.getLastVisiblePosition() == adapterView.getCount() - 1
                && (adapterView.getChildAt(adapterView.getChildCount() - 1).getBottom() == viewGroupHeight)) {
            // 如果到达底部，则拦截事件
            intercept = true;
        }
        return intercept;
    }
}
