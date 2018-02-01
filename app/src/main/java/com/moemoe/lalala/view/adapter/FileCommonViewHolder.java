package com.moemoe.lalala.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetTResultSubscriber;
import com.moemoe.lalala.model.entity.CommonFileEntity;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.Utils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;
import com.moemoe.lalala.view.widget.longimage.LongImageView;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2017/8/20.
 */

public class FileCommonViewHolder extends ClickableViewHolder {

    RelativeLayout root;
    ImageView cover;
    LongImageView longCover;
    TextView title;
    TextView extra;
    ImageView select;

    public FileCommonViewHolder(View itemView) {
        super(itemView);
        root = $(R.id.rl_root);
        cover = $(R.id.iv_cover);
        title = $(R.id.tv_name);
        extra = $(R.id.tv_size);
        select = $(R.id.iv_select);
        longCover = $(R.id.iv_long_image);
    }

    public void createItem(CommonFileEntity entity,boolean isSelect,boolean isBuy){
        select.setVisibility(isSelect?View.VISIBLE:View.GONE);
        select.setSelected(entity.isSelect());
        int size = (DensityUtil.getScreenWidth(itemView.getContext()) - (int)context.getResources().getDimension(R.dimen.x12)) /3;
        root.setLayoutParams(new RecyclerView.LayoutParams(size,size));
        cover.setLayoutParams(new RelativeLayout.LayoutParams(size,size));
        if(entity.getType().equals("image")){
            if(!isBuy){
                Glide.with(itemView.getContext())
                        .load(StringUtils.getUrl(itemView.getContext(),entity.getPath(),size,size,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .bitmapTransform(new CropSquareTransformation(itemView.getContext()))
                        .into(cover);
            }else {
                Glide.with(itemView.getContext())
                        .load(StringUtils.getUrl(itemView.getContext(),entity.getPath(),size,size,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .bitmapTransform(new CropSquareTransformation(itemView.getContext()),new BlurTransformation(context,10,4))
                        .into(cover);
            }
            title.setVisibility(View.GONE);
            extra.setVisibility(View.GONE);
        }else if(entity.getType().equals("music")){
            title.setVisibility(View.VISIBLE);
            extra.setVisibility(View.VISIBLE);
            title.setText(entity.getFileName());
            cover.setImageResource(R.drawable.bg_bag_music);
            if(entity.getAttr().has("timestamp")){
                extra.setText(StringUtils.getMinute(entity.getAttr().get("timestamp").getAsInt()));
            }
        }else if(entity.getType().equals("txt")){
            title.setVisibility(View.VISIBLE);
            extra.setVisibility(View.VISIBLE);
            title.setText(entity.getFileName());
            cover.setImageResource(R.drawable.bg_bag_word);
            if(entity.getAttr().has("size")){
                extra.setText(FileUtil.formatFileSizeToString(entity.getAttr().get("size").getAsLong()));
            }
        }else {
            Glide.with(itemView.getContext())
                    .load(R.drawable.bg_bag_unknow)
                    .override(size,size)
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .centerCrop()
                    .into(cover);
            title.setVisibility(View.VISIBLE);
            title.setText(entity.getFileName());
        }
    }

    public void createLinearItem(final CommonFileEntity entity, boolean isSelect, final RecyclerView.Adapter adapter, final int position,boolean isBuy){
        select.setVisibility(isSelect?View.VISIBLE:View.GONE);
        select.setSelected(entity.isSelect());
        if(entity.getType().equals("image")) {
            if(entity.getAttr().has("w") && entity.getAttr().has("h")){
                if(entity.getAttr().get("w").getAsInt() <= 0 || entity.getAttr().get("h").getAsInt() <= 0){
                    root.setVisibility(View.GONE);
                    root.setLayoutParams(new RecyclerView.LayoutParams(1,1));
                    cover.setLayoutParams(new RelativeLayout.LayoutParams(1,1));
                }else {
                    final int[] wh = Utils.getDocIconSize(entity.getAttr().get("w").getAsInt(), entity.getAttr().get("h").getAsInt(), DensityUtil.getScreenWidth(itemView.getContext()));
                    if(((float)(wh[1] / wh[0])) > 16.0f / 9.0f){
                        cover.setVisibility(View.GONE);
                        longCover.setVisibility(View.VISIBLE);
                        String temp = EncoderUtils.MD5(ApiService.URL_QINIU + entity.getPath()) + ".jpg";
                        final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
                        ViewGroup.LayoutParams layoutParams = longCover.getLayoutParams();
                        layoutParams.width = wh[0];
                        layoutParams.height = wh[1];
                        longCover.setLayoutParams(layoutParams);
                        longCover.requestLayout();
                        ViewGroup.LayoutParams layoutParams1 = itemView.getLayoutParams();
                        layoutParams1.width = wh[0];
                        layoutParams1.height = wh[1];
                        itemView.setLayoutParams(layoutParams1);
                        itemView.requestLayout();
                        if(longImage.exists()){
                            longCover.setImage(longImage.getAbsolutePath());
                        }else {
                            FileDownloader.getImpl().create(ApiService.URL_QINIU + entity.getPath())
                                    .setPath(StorageUtils.getGalleryDirPath() + temp)
                                    .setCallbackProgressTimes(1)
                                    .setListener(new FileDownloadListener() {
                                        @Override
                                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                        }

                                        @Override
                                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                        }

                                        @Override
                                        protected void completed(BaseDownloadTask task) {
                                            BitmapUtils.galleryAddPic(itemView.getContext(), longImage.getAbsolutePath());
                                            longCover.setImage(longImage.getAbsolutePath());
                                            adapter.notifyItemChanged(position);
                                        }

                                        @Override
                                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                        }

                                        @Override
                                        protected void error(BaseDownloadTask task, Throwable e) {

                                        }

                                        @Override
                                        protected void warn(BaseDownloadTask task) {

                                        }
                                    }).start();
                        }
                    }else {
                        cover.setVisibility(View.VISIBLE);
                        longCover.setVisibility(View.GONE);
                        if(FileUtil.isGif(entity.getPath())){
                            ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
                            layoutParams.width = wh[0];
                            layoutParams.height = wh[1];
                            cover.setLayoutParams(layoutParams);
                            cover.requestLayout();
                            ViewGroup.LayoutParams layoutParams1 = itemView.getLayoutParams();
                            layoutParams1.width = wh[0];
                            layoutParams1.height = wh[1];
                            itemView.setLayoutParams(layoutParams1);
                            itemView.requestLayout();
                            Glide.with(itemView.getContext())
                                    .load(ApiService.URL_QINIU + entity.getPath())
                                    .asGif()
                                    .override(wh[0], wh[1])
                                    .placeholder(R.drawable.bg_default_square)
                                    .error(R.drawable.bg_default_square)
                                    .into(cover);
                        }else {
                            ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
                            layoutParams.width = wh[0];
                            layoutParams.height = wh[1];
                            cover.setLayoutParams(layoutParams);
                            cover.requestLayout();
                            ViewGroup.LayoutParams layoutParams1 = itemView.getLayoutParams();
                            layoutParams1.width = wh[0];
                            layoutParams1.height = wh[1];
                            itemView.setLayoutParams(layoutParams1);
                            itemView.requestLayout();
                            Glide.with(itemView.getContext())
                                    .load(StringUtils.getUrl(itemView.getContext(),ApiService.URL_QINIU + entity.getPath(), wh[0], wh[1], true, true))
                                    .override(wh[0], wh[1])
                                    .placeholder(R.drawable.bg_default_square)
                                    .error(R.drawable.bg_default_square)
                                    .into(cover);
                        }
                    }
                }
            }else {
                root.setVisibility(View.GONE);
                root.setLayoutParams(new RecyclerView.LayoutParams(1,1));
                cover.setLayoutParams(new RelativeLayout.LayoutParams(1,1));
            }
        }
    }
}
