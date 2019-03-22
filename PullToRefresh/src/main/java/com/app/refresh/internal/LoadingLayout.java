package com.app.refresh.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.app.refresh.State;

/**
 * Created by yuandong
 * on 2019/3/22 9:52.
 */
public abstract class LoadingLayout  extends LinearLayout implements ILoadingLayout {

    public LoadingLayout(Context context) {
        this(context,null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    protected abstract void initView(Context context);


    @Override
    public void setState(State state) {

    }
}
