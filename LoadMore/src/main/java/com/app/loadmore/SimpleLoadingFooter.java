package com.app.loadmore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.app.loadmore.FooterState.*;


public class SimpleLoadingFooter extends LoadingFooter {

    private ProgressBar progressBar;
    private TextView tv_state;
    private FooterState footerState = NORMAL;

    public SimpleLoadingFooter(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.layout_recyclerview_footer, null);
        progressBar = footer.findViewById(R.id.progressbar);
        tv_state = footer.findViewById(R.id.tv_state);
        removeAllViews();
        addView(footer);
    }

    @Override
    protected FooterState getFooterState() {
        return footerState;
    }

    @Override
    protected void setFooterState(FooterState state) {
        if (footerState == state) {
            return;
        }
        switch (state) {
            case NORMAL:
                progressBar.setVisibility(View.VISIBLE);
                tv_state.setText(getResources().getString(R.string.waprecyclerview_loading));
                break;
            case LOADING:
                progressBar.setVisibility(View.VISIBLE);
                tv_state.setText(getResources().getString(R.string.waprecyclerview_loading));
                break;
            case END:
                progressBar.setVisibility(View.GONE);
                tv_state.setText(getResources().getString(R.string.waprecyclerview_end));
                break;
        }
        footerState = state;
    }
}
