package com.app.refresh.header;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.refresh.R;
import com.app.refresh.State;
import com.app.refresh.internal.LoadingLayout;
import com.app.refresh.internal.RotateImageView;

/**
 * Created by yuandong
 * on 2019/3/22 11:27.
 */
public class DefaultLoadingHeader extends LoadingLayout {

    private View headerView;
    private ImageView arrow;
    private TextView description;
    private RotateImageView progressBar;

    public DefaultLoadingHeader(Context context) {
        super(context);
    }

    @Override
    protected void initView(Context context) {
        setOrientation(VERTICAL);
        headerView = LayoutInflater.from(context).inflate(R.layout.layout_default_refresh_header, null);
        arrow = headerView.findViewById(R.id.arrow);
        description = headerView.findViewById(R.id.description);
        progressBar = headerView.findViewById(R.id.progress_bar);
        addView(headerView);
    }

    @Override
    public void setState(State state) {
        super.setState(state);
    }
}
