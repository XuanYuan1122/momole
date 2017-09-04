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
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ManHua2Entity;
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
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zlc.season.rxdownload.RxDownload;
import zlc.season.rxdownload.entity.DownloadStatus;

/**
 * Created by yi on 2017/8/20.
 */

public class FileManHua2ViewHolder extends ClickableViewHolder {

    ImageView cover;
    TextView title;
    TextView mark;
    ImageView select;

    public FileManHua2ViewHolder(View itemView) {
        super(itemView);
        cover = $(R.id.iv_cover);
        title = $(R.id.tv_title);
        mark = $(R.id.tv_mark);
        select = $(R.id.iv_select);
    }

    public void createItem(ManHua2Entity entity, boolean isSelect){
        select.setVisibility(isSelect?View.VISIBLE:View.GONE);
        select.setSelected(entity.isSelect());
        title.setText(entity.getFolderName());
        mark.setText(entity.getItems() + " P");
        int width = (DensityUtil.getScreenWidth(itemView.getContext()) - DensityUtil.dip2px(itemView.getContext(),42)) / 3;
        int height = DensityUtil.dip2px(itemView.getContext(),140);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,height);
        cover.setLayoutParams(lp);
        Glide.with(itemView.getContext())
                .load(StringUtils.getUrl(itemView.getContext(),entity.getCover(),width,height, false, true))
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(itemView.getContext(),width,height),new RoundedCornersTransformation(itemView.getContext(),DensityUtil.dip2px(itemView.getContext(),4),0))
                .into(cover);
//        iv.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                //TODO 跳转文件夹
//            }
//        });
    }

}
