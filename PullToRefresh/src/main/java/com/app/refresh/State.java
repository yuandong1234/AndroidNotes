package com.app.refresh;

/**
 * Created by yuandong
 * on 2019/3/22 9:51.
 */
public enum State {
    STATUS_PULL_TO_REFRESH,//下拉状态
    STATUS_RELEASE_TO_REFRESH,//释放立即刷新状态
    STATUS_REFRESHING,//正在刷新状态
    STATUS_REFRESH_FINISHED,//刷新完成或未刷新状态
    STATUS_PULL_TO_LOADING,//上拉状态
    STATUS_RELEASE_TO_LOADING,//释放立即加载状态
    STATUS_LOADING//正在加载状态
}
