package com.app.loadmore;

import android.content.Context;
import android.util.Log;
import android.view.View;

public class RecyclerViewPageHelper {

    private Context mContext;
    private WarpRecyclerView warpRecyclerView;

    public RecyclerViewPageHelper(Context mContext, WarpRecyclerView recyclerView) {
        this.mContext = mContext;
        this.warpRecyclerView = recyclerView;
    }

    public void setLoadMoreEnable(boolean enable) {
       final WapRecyclerViewAdapter wapAdapter = warpRecyclerView.getAdapter();
        if (wapAdapter == null) {
            return;
        }
       final View footerView = wapAdapter.getFooterView();
        if (enable) {
            warpRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    boolean addable = canAddFooter(warpRecyclerView);
                    Log.e("RecyclerViewPageHelper", "canAddFooter : " + addable);
                    if (footerView == null && addable) {
                        LoadingFooter footer = new SimpleLoadingFooter(mContext);
                        footer.setFooterState(FooterState.NORMAL);
                        if (wapAdapter.getRealAdapter() != null) {
                            wapAdapter.addFooterView(footer);
                            wapAdapter.getRealAdapter().notifyDataSetChanged();
                        }
                    }
                }
            });

        } else {
            if (footerView != null) {
                wapAdapter.addFooterView(null);
                if (wapAdapter.getRealAdapter() != null) {
                    wapAdapter.getRealAdapter().notifyDataSetChanged();
                }
            }
        }
    }

    public void setLoadMoreState(FooterState state) {
        WapRecyclerViewAdapter wapAdapter = warpRecyclerView.getAdapter();
        if (wapAdapter == null) {
            return;
        }
        LoadingFooter footer = wapAdapter.getFooterView();
        if (footer != null) {
            footer.setFooterState(state);
        }
    }

    private FooterState getLoadMoreState() {
        WapRecyclerViewAdapter wapAdapter = warpRecyclerView.getAdapter();
        if (wapAdapter == null) {
            return null;
        }
        LoadingFooter footer = wapAdapter.getFooterView();
        if (footer != null) {
            return footer.getFooterState();
        }
        return null;
    }

    public boolean isCanLoadMore() {
        WapRecyclerViewAdapter wapAdapter = warpRecyclerView.getAdapter();
        if (wapAdapter == null) {
            return false;
        }

        FooterState state = getLoadMoreState();

        return state != null && state == FooterState.NORMAL;
    }

    private boolean canAddFooter(WarpRecyclerView recyclerView) {
        if (recyclerView == null) {
            return false;
        }


        int height = recyclerView.getHeight();
        Log.i("RecyclerViewPageHelper", "【RecyclerViewPageHelper】recyclerView 高度 : " + height);

        int scrollExtent = recyclerView.computeVerticalScrollExtent();
        int scrollOffset = recyclerView.computeVerticalScrollOffset();
        int scrollRange = recyclerView.computeVerticalScrollRange();
        Log.i("RecyclerViewPageHelper", "【RecyclerViewPageHelper】屏幕可见高度 : " + scrollExtent);
        Log.i("RecyclerViewPageHelper", "【RecyclerViewPageHelper】向下滑动高度 : " + scrollOffset);
        Log.i("RecyclerViewPageHelper", "【RecyclerViewPageHelper】整体全部高度 : " + scrollRange);
        return scrollRange >= height;
        //return recyclerView.canScrollVertically(1);

    }
}
