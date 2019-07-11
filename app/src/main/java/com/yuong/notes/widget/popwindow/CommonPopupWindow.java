package com.yuong.notes.widget.popwindow;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class CommonPopupWindow extends PopupWindow {

    private Context mContext;
    private int layoutResId;
    private View contentView;
    private int mWidth, mHeight;
    private float bg_level;
    private int animationStyle;
    private boolean isTouchable;
    private boolean isFocusable;

    private CommonPopupWindow(Builder builder) {
        this.mContext = builder.mContext;
        this.layoutResId = builder.layoutResId;
        this.contentView = builder.contentView;
        this.mWidth = builder.mWidth;
        this.mHeight = builder.mHeight;
        this.bg_level = builder.bg_level;
        this.animationStyle = builder.animationStyle;
        this.isTouchable = builder.isTouchable;
        this.isFocusable = builder.isFocusable;
        init();
    }

    private void init() {
        View view = null;
        if (layoutResId != 0) {
            view = LayoutInflater.from(mContext).inflate(layoutResId, null);
        } else if (contentView != null) {
            view = contentView;
        }
        setContentView(view);

        if (mWidth == 0) {
            //如果没设置宽，默认是WRAP_CONTENT
            setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            setWidth(mWidth);
        }

        if (mHeight == 0) {
            //如果没设置高，默认是WRAP_CONTENT
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            setHeight(mHeight);
        }

        setBackgroundLevel(bg_level);
        if (animationStyle != 0) {
            setAnimationStyle(animationStyle);
        }
        setOutsideTouchable(isTouchable);
        setTouchable(true);

        /**
         *设置setFocusable为true时，setOutsideTouchable无论true还是false ，点击弹窗外部都会消失,点击返回键弹窗也会消失
         * 设置setFocusable为false时，setOutsideTouchable为true时 ，点击弹窗外部才会消失,点击返回键会关闭界面（除非重写onKeyDown事件自定义处理）
         */
        setFocusable(isFocusable);
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK && isShowing()) {
//                    dismiss();
//                    setBackgroundLevel(1.0f);
//                    return true;
//                }
//                return false;
//            }
//        });
    }


    private void setBackgroundLevel(float level) {
        Window mWindow = ((Activity) mContext).getWindow();
        WindowManager.LayoutParams params = mWindow.getAttributes();
        params.alpha = level;
        mWindow.setAttributes(params);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        setBackgroundLevel(1.0f);
    }

    public static class Builder {
        private Context mContext;
        private int layoutResId;
        private View contentView;
        private int mWidth, mHeight;
        private float bg_level = 1.0f;
        private int animationStyle;
        private boolean isTouchable = true;
        private boolean isFocusable;

        public Builder(Context context) {
            this.mContext = context;
        }

        /**
         * @param layoutResId 设置PopupWindow 布局ID
         * @return Builder
         */
        public Builder setView(int layoutResId) {
            this.layoutResId = layoutResId;
            return this;
        }

        /**
         * @param view 设置PopupWindow布局
         * @return Builder
         */
        public Builder setView(View view) {
            this.contentView = view;
            return this;
        }

        /**
         * 设置宽度 默认是wrap_content
         *
         * @param width 宽
         * @return Builder
         */
        public Builder setWidth(int width) {
            this.mWidth = width;
            return this;
        }

        /**
         * 设置高度 默认是wrap_content
         *
         * @param height 宽
         * @return Builder
         */
        public Builder setHeight(int height) {
            this.mHeight = height;
            return this;
        }

        /**
         * 设置背景灰色程度
         *
         * @param level 0.0f-1.0f
         * @return Builder
         */
        public Builder setBackgroundLevel(float level) {
            this.bg_level = level;
            return this;
        }

        /**
         * 设置动画
         *
         * @return Builder
         */
        public Builder setAnimationStyle(int animationStyle) {
            this.animationStyle = animationStyle;
            return this;
        }

        /**
         * 是否可点击Outside消失
         *
         * @param touchable 是否可点击
         * @return Builder
         */
        public Builder setOutsideTouchable(boolean touchable) {
            this.isTouchable = touchable;
            return this;
        }

        /**
         * 是否可点击Outside消失
         *
         * @param focusable 是否可点击
         * @return Builder
         */
        public Builder setFocusable(boolean focusable) {
            this.isFocusable = focusable;
            return this;
        }

        public CommonPopupWindow build() {
            return new CommonPopupWindow(this);
        }

    }
}
