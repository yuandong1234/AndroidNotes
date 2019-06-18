package com.yuong.notes.widget.banner.holder;


public interface BannerHolderCreator<VH extends BannerViewHolder> {
    /**
     * 创建ViewHolder
     * @return
     */
     VH createViewHolder();
}
