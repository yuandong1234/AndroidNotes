package com.app.loadmore;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class WapRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_HEADER = 1;
    private static final int ITEM_TYPE_FOOTER = 2;
    private static final int ITEM_TYPE_NORMAL = 3;

    private View mHeadView;
    private View mFooterView;
    private RecyclerView.Adapter mRealAdapter;//实际的adapter
    private int mOrientation = -1;

    public void setHeadView(View headView) {
        this.mHeadView = headView;
    }

    public void setFooterView(View footerView) {
        this.mFooterView = footerView;
    }

    public RecyclerView.Adapter getRealAdapter() {
        return mRealAdapter;
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
                return ITEM_TYPE_NORMAL;
            }
        } else if (mHeadView != null) {
            if (position == 0) {
                return ITEM_TYPE_HEADER;
            } else {
                return ITEM_TYPE_NORMAL;
            }
        } else if (mFooterView != null) {
            if (position == getItemCount() - 1) {
                return ITEM_TYPE_FOOTER;
            } else {
                return ITEM_TYPE_NORMAL;
            }
        } else {
            return ITEM_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        if (mHeadView != null && mFooterView != null) {
            return mRealAdapter.getItemCount() + 2;
        } else if (mHeadView != null) {
            return mRealAdapter.getItemCount() + 1;
        } else if (mFooterView != null) {
            return mRealAdapter.getItemCount() + 1;
        }
        return mRealAdapter.getItemCount();
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
                if (isScrollToBottom(recyclerView) && loadMoreListener != null) {
                    loadMoreListener.loadMore();
                }
            }
        });
    }

    public boolean isScrollToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return false;
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            mOrientation = linearLayoutManager.getOrientation();
        }
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            return recyclerView.computeHorizontalScrollExtent() + recyclerView.computeHorizontalScrollOffset() >= recyclerView.computeHorizontalScrollRange();
        } else {
            return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange();
        }
    }

    public interface LoadMoreListener {
        void loadMore();
    }
}
