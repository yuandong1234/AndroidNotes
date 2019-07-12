package com.app.loadmore;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public abstract class LoadingFooter extends LinearLayout {

    public LoadingFooter(Context context) {
        this(context, null);
    }

    public LoadingFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    protected abstract void initView();

    protected abstract  void setFooterState(FooterState state);

    protected abstract FooterState  getFooterState();

}
