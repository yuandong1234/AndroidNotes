package com.yuong.notes.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yuong.notes.R;
import com.yuong.notes.utils.DensityUtil;
import com.yuong.notes.utils.ScreenUtil;

/**
 * 常见的弹框(居中)
 */
public class CommonDialog extends Dialog {

    private Context mContext;
    private int height, width;
    private boolean cancelTouchOut;
    private View view;


    private CommonDialog(Builder builder) {
        this(builder, 0);
    }

    private CommonDialog(Builder builder, int resStyle) {
        super(builder.mContext, resStyle);
        mContext = builder.mContext;
        height = builder.height;
        width = builder.width;
        cancelTouchOut = builder.cancelTouchOut;
        view = builder.view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        setCanceledOnTouchOutside(cancelTouchOut);
        setCancelable(cancelTouchOut);
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialog_common_window_anim); //设置窗口弹出动画
            WindowManager.LayoutParams lp = window.getAttributes();
            int screenHeight = ScreenUtil.getScreenHeight(mContext);//屏幕高度
            int screenWidth = ScreenUtil.getScreenWidth(mContext);//屏幕宽度
            if (height != 0) {
                lp.height = height;
            } else {
                lp.height = screenHeight;
            }
            if (width != 0) {
                lp.width = width;
            } else {
                lp.width = screenWidth;
            }
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);
        }
    }

    public static final class Builder {
        private Context mContext;
        private int height, width;
        private boolean cancelTouchOut;
        private View view;
        private int resStyle = -1;


        public Builder(Context context) {
            mContext = context;
        }

        public Builder view(View view) {
            this.view = view;
            return this;
        }

        public Builder view(int layoutID) {
            view = LayoutInflater.from(mContext).inflate(layoutID, null);
            return this;
        }

        /**
         * @param val 单位dp
         * @return
         */
        public Builder height(int val) {
            height = DensityUtil.dp2px(mContext, val);
            return this;
        }

        /**
         * @param val 单位dp
         * @return
         */
        public Builder width(int val) {
            width = DensityUtil.dp2px(mContext, val);
            return this;
        }

        public Builder heightDimenRes(@DimenRes int dimenRes) {
            height = mContext.getResources().getDimensionPixelOffset(dimenRes);
            return this;
        }

        public Builder widthDimenRes(@DimenRes int dimenRes) {
            width = mContext.getResources().getDimensionPixelOffset(dimenRes);
            return this;
        }

        public Builder style(int resStyle) {
            this.resStyle = resStyle;
            return this;
        }

        public Builder cancelTouchOut(boolean cancelTouchOut) {
            this.cancelTouchOut = cancelTouchOut;
            return this;
        }

        public Builder addViewOnclick(int viewRes, View.OnClickListener listener) {
            view.findViewById(viewRes).setOnClickListener(listener);
            return this;
        }


        public CommonDialog build() {
            if (resStyle != -1) {
                return new CommonDialog(this, resStyle);
            } else {
                return new CommonDialog(this);
            }
        }
    }
}
