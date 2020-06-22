package com.yuong.notes.camera;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.yuong.notes.R;

import java.util.List;

public class PictureSelectorHelper {

    public static void openAlbum(Activity activity) {
        PictureWindowAnimationStyle windowAnimationStyle = new PictureWindowAnimationStyle();
        windowAnimationStyle.ofAllAnimation(R.anim.picture_anim_up_in, R.anim.picture_anim_down_out);
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())// 全部 .PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isUseCustomCamera(false)// 是否使用自定义相机
                .setPictureWindowAnimationStyle(windowAnimationStyle)// 自定义相册启动退出动画
                .imageSpanCount(3)// 每行显示个数
                .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
                .isSingleDirectReturn(true)// 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isPreviewImage(true)// 是否可预览图片
                //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
                .isCamera(false)// 是否显示拍照按钮
                .isCompress(true)// 是否压缩
                //.compressQuality(80)// 图片压缩后输出质量 0~ 100
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                //.compressSavePath(getPath())//压缩图片保存地址
//                .minimumCompressSize(1024)// 小于多少kb的图片不压缩
                .renameCompressFile(System.currentTimeMillis() +".jpg")
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        LocalMedia media = result.get(0);
                        Log.e("PictureSelectorHelper", "media :" + media.getPath());
                        Log.e("PictureSelectorHelper", "media :" + media.getCompressPath());
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

}
