package com.app.loadmore;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class WapRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_HEADER = 1;
    private static final int ITEM_TYPE_FOOTER = 2;
    private static final int ITEM_TYPE_NORMAL = 3;

    private View mHeadView;
    private LoadingFooter mFooterView;
    private RecyclerView.Adapter mRealAdapter;//实际的adapter
    private int mOrientation = -1;

    public void addHeadView(View headView) {
        this.mHeadView = headView;
    }

    public void addFooterView(LoadingFooter footerView) {
        this.mFooterView = footerView;
    }

    public View getHeadView() {
        return mHeadView;
    }

    public LoadingFooter getFooterView() {
        return mFooterView;
    }

    public RecyclerView.Adapter getRealAdapter() {
        return mRealAdapter;
    }

    public int getHeadViewCount() {
        return mHeadView != null ? 1 : 0;
    }

    public int getFooterViewCount() {
        return mFooterView != null ? 1 : 0;
    }

    public WapRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        this.mRealAdapter = adapter;
    }


    @Override
    public int getItemViewType(int position) {
        if (mHeadView != null && mFooterView != null) {
            if (position == 0) {
                return ITEM_TYPE_HEADER;
            } else if (position == getItemCount() - 1) {
                return ITEM_TYPE_FOOTER;
            } else {
                if (mRealAdapter.getItemCount() > 1) {
                    return mRealAdapter.getItemViewType(position);
                }
                return ITEM_TYPE_NORMAL;
            }
        } else if (mHeadView != null) {
            if (position == 0) {
                return ITEM_TYPE_HEADER;
            } else {
                if (mRealAdapter.getItemCount() > 1) {
                    return mRealAdapter.getItemViewType(position);
                }
                return ITEM_TYPE_NORMAL;
            }
        } else if (mFooterView != null) {
            if (position == getItemCount() - 1) {
                return ITEM_TYPE_FOOTER;
            } else {
                if (mRealAdapter.getItemCount() > 1) {
                    return mRealAdapter.getItemViewType(position);
                }
                return ITEM_TYPE_NORMAL;
            }
        } else {
            if (mRealAdapter.getItemCount() > 1) {
                return mRealAdapter.getItemViewType(position);
            }
            return ITEM_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        int headViewCount = getHeadViewCount();
        int footerViewCount = getFooterViewCount();
        return mRealAdapter.getItemCount() + headViewCount + footerViewCount;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new HeaderViewHolder(mHeadView);
        } else if (viewType == ITEM_TYPE_FOOTER) {
            return new FooterViewHolder(mFooterView);
        }
        return mRealAdapter.createViewHolder(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (mHeadView != null && mFooterView != null) {
            if (position == 0) {
                return;
            } else if (position == getItemCount() - 1) {
                return;
            } else {
                mRealAdapter.onBindViewHolder(viewHolder, position - 1);
            }
        } else if (mHeadView != null) {
            if (position == 0) {
                return;
            } else {
                mRealAdapter.onBindViewHolder(viewHolder, position - 1);
            }
        } else if (mFooterView != null) {
            if (position == getItemCount() - 1) {
                return;
            } else {
                mRealAdapter.onBindViewHolder(viewHolder, position);
            }
        } else {
            mRealAdapter.onBindViewHolder(viewHolder, position);
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        // super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mHeadView != null && mFooterView != null) {
                        if (position == 0) {
                            return gridLayoutManager.getSpanCount();
                        } else if (position == getItemCount() - 1) {
                            return gridLayoutManager.getSpanCount();
                        } else {
                            return 1;
                        }
                    } else if (mHeadView != null) {
                        if (position == 0) {
                            return gridLayoutManager.getSpanCount();
                        } else {
                            return 1;
                        }
                    } else if (mFooterView != null) {
                        if (position == getItemCount() - 1) {
                            return gridLayoutManager.getSpanCount();
                        } else {
                            return 1;
                        }
                    }
                    return 1;
                }
            });
        }

    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    public void setLoadMoreListener(RecyclerView recyclerView, final LoadMoreListener loadMoreListener) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i("WapRecyclerViewAdapter", "[addOnScrollListener] 滑动监听..." );
                if (isScrollToBottom(recyclerView) && loadMoreListener != null) {
                    loadMoreListener.loadMore();
                }
            }
        });
    }

    /**
     * computeVerticalScrollExtent 显示区域的高度
     * computeVerticalScrollOffset  已经向下滚动的距离，为0时表示已处于顶部
     * computeVerticalScrollRange 整体的高度，注意是整体，包括在显示区域之外的
     *
     * @param recyclerView
     * @return
     */
    public boolean isScrollToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return false;
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            mOrientation = linearLayoutManager.getOrientation();
        }
        int height = recyclerView.getHeight();
        Log.i("WapRecyclerViewAdapter", "recyclerView 高度 : " + height);
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            return recyclerView.computeHorizontalScrollExtent() + recyclerView.computeHorizontalScrollOffset() >= recyclerView.computeHorizontalScrollRange();
        } else {
            int scrollExtent = recyclerView.computeVerticalScrollExtent();
            int scrollOffset = recyclerView.computeVerticalScrollOffset();
            int scrollRange = recyclerView.computeVerticalScrollRange();
            Log.i("WapRecyclerViewAdapter", "屏幕可见高度 : " + scrollExtent);
            Log.i("WapRecyclerViewAdapter", "向下滑动高度 : " + scrollOffset);
            Log.i("WapRecyclerViewAdapter", "整体全部高度 : " + scrollRange);
            return (scrollExtent + scrollOffset >= scrollRange) && scrollRange >= height;
        }
    }

    public interface LoadMoreListener {
        void loadMore();
    }
}
