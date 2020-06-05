package com.app.loadmore;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

public class WarpRecyclerView extends RecyclerView {
    private static final String TAG = WarpRecyclerView.class.getSimpleName();
    private WapRecyclerViewAdapter wapAdapter;
    private DataObserver mDataObserver = new DataObserver();

    public WarpRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public WarpRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public WarpRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        wapAdapter = (WapRecyclerViewAdapter) adapter;
        super.setAdapter(adapter);
        wapAdapter.getRealAdapter().registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    public WapRecyclerViewAdapter getAdapter(){
        return wapAdapter;
    }


    public class DataObserver extends RecyclerView.AdapterDataObserver {


        @Override
        public void onChanged() {
            Log.e(TAG, "[onChanged]..." );
            if (wapAdapter != null) {
                wapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            Log.e(TAG, "[onItemRangeChanged] positionStart : " + positionStart + "  itemCount : " + itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            Log.e(TAG, "[onItemRangeInserted] positionStart : " + positionStart + "  itemCount : " + itemCount);
            wapAdapter.notifyItemRangeInserted(positionStart+wapAdapter.getHeadViewCount(),itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            Log.e(TAG, "[onItemRangeRemoved] positionStart : " + positionStart + "  itemCount : " + itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            Log.e(TAG, "[onItemRangeMoved] fromPosition : " + fromPosition + "  toPosition : " + toPosition + "  itemCount : " + itemCount);
        }
    }
}
