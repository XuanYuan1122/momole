package com.moemoe.lalala.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CommonFileEntity;
import com.moemoe.lalala.model.entity.FileXiaoShuoEntity;
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

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zlc.season.rxdownload.RxDownload;
import zlc.season.rxdownload.entity.DownloadStatus;

/**
 * Created by yi on 2017/8/20.
 */

public class FileXiaoShuoViewHolder extends ClickableViewHolder {

    ImageView cover;
    TextView title;
    TextView name;
    TextView num;
    TextView content;
    ImageView select;

    public FileXiaoShuoViewHolder(View itemView) {
        super(itemView);
        cover = $(R.id.iv_cover);
        title = $(R.id.tv_title);
        name = $(R.id.tv_user_name);
        num = $(R.id.tv_num);
        content = $(R.id.tv_content);
        select = $(R.id.iv_select);
    }

    public void createItem(FileXiaoShuoEntity entity, boolean isSelect){
        select.setVisibility(isSelect?View.VISIBLE:View.GONE);
        select.setSelected(entity.isSelect());
        Glide.with(itemView.getContext())
                .load(StringUtils.getUrl(itemView.getContext(),entity.getCover(),DensityUtil.dip2px(itemView.getContext(),56),DensityUtil.dip2px(itemView.getContext(),74),false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(itemView.getContext(),DensityUtil.dip2px(itemView.getContext(),56),DensityUtil.dip2px(itemView.getContext(),74)))
                .into(cover);
        title.setText(entity.getTitle());
        name.setText("UP " + entity.getUserName());
        num.setText("字数 " + entity.getNum());
        content.setText(entity.getContent());
    }
}
