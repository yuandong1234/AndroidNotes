package com.app.loadmore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class RecyclerViewPageHelper {

    private Context mContext;
    private WapRecyclerViewAdapter mWapAdapter;

    public RecyclerViewPageHelper(Context mContext, WapRecyclerViewAdapter wapAdapter) {
        this.mContext = mContext;
        this.mWapAdapter = wapAdapter;
    }

    public void setLoadMoreEnable(boolean enable) {
        if (mWapAdapter == null) {
            return;
        }
        View footerView = mWapAdapter.getFooterView();
        if (enable) {
            if (footerView == null) {
                View footer = LayoutInflater.from(mContext).inflate(R.layout.layout_recyclerview_footer, null);
                if (mWapAdapter.getRealAdapter() != null) {
                    mWapAdapter.addFooterView(footer);
                    mWapAdapter.getRealAdapter().notifyDataSetChanged();
                }
            }
        } else {
            if (footerView != null) {
                mWapAdapter.addFooterView(null);
                if (mWapAdapter.getRealAdapter() != null) {
                    mWapAdapter.getRealAdapter().notifyDataSetChanged();
                }
            }
        }
    }

}
