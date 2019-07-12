package com.app.loadmore;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        WapRecyclerViewAdapter wapAdapter = warpRecyclerView.getAdapter();
        if (wapAdapter == null) {
            return;
        }
        View footerView = wapAdapter.getFooterView();
        if (enable) {
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
        if (recyclerView == null || warpRecyclerView.getAdapter() == null) {
            return false;
        }
        WapRecyclerViewAdapter wapAdapter = warpRecyclerView.getAdapter();

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        Log.e("RecyclerViewPageHelper", "*********************");
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int lastPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            Log.e("RecyclerViewPageHelper", "lastPosition : "+lastPosition);
            Log.e("RecyclerViewPageHelper", "itemCount : "+wapAdapter.getItemCount());
            if (lastPosition >= wapAdapter.getItemCount() - 1) {
                return true;
            }
        }

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int lastPosition = gridLayoutManager.findLastCompletelyVisibleItemPosition();
            return lastPosition >= wapAdapter.getItemCount() - 1;
        }

        return false;
    }
}
