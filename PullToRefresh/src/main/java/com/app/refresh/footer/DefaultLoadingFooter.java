package com.app.refresh.footer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.app.refresh.R;
import com.app.refresh.State;
import com.app.refresh.internal.LoadingLayout;

public class DefaultLoadingFooter extends LoadingLayout {
    private View footerView;
    private TextView description;

    private State currentState = State.STATUS_REFRESH_FINISHED;
    /**
     * 记录上一次的状态是什么，避免进行重复操作
     */
    private State lastState = currentState;

    public DefaultLoadingFooter(Context context) {
        super(context);
    }

    @Override
    protected void initView(Context context) {
        setOrientation(VERTICAL);
        footerView = LayoutInflater.from(context).inflate(R.layout.layout_default_loading_footer, null);
        description =  footerView.findViewById(R.id.describe);
        addView(footerView);
    }


    @Override
    public void setState(State status) {
        currentState = status;
        if (currentState != lastState) {
            switch (status) {
                case STATUS_PULL_TO_LOADING:
                    description.setText(getContext().getResources().getString(R.string.pull_to_loading));
                    break;
                case STATUS_RELEASE_TO_LOADING:
                    description.setText(getContext().getResources().getString(R.string.realse_to_loading));
                    break;
                case STATUS_LOADING:
                    description.setText(getContext().getResources().getString(R.string.loading));
                    break;
                case STATUS_REFRESH_FINISHED:
                    description.setText(getContext().getResources().getString(R.string.load_finished));
                    break;
            }
        }
        lastState = currentState;
    }
}
