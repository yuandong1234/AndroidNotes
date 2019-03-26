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
        AdapterView adapterChild = (AdapterView) child;
        // 判断AbsListView是否已经到达内容最顶部
        if (adapterChild.getFirstVisiblePosition() != 0
                || adapterChild.getChildAt(0).getTop() != 0) {
            // 如果没有达到最顶端，则仍然将事件下放
            intercept = false;
        }
        return intercept;
    }

    public static boolean canPullUp(View child, int viewGroupHeight) {
        boolean intercept = false;
        AdapterView adapterChild = (AdapterView) child;

        // 判断AbsListView是否已经到达内容最底部
        if (adapterChild.getLastVisiblePosition() == adapterChild.getCount() - 1
                && (adapterChild.getChildAt(adapterChild.getChildCount() - 1).getBottom() == viewGroupHeight)) {
            // 如果到达底部，则拦截事件
            intercept = true;
        }
        return intercept;
    }
}
