package com.moemoe.lalala.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.Utils;
import com.app.common.util.DensityUtil;
import com.app.image.ImageOptions;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.Image;
import com.moemoe.lalala.data.NewDocBean;
import com.moemoe.lalala.data.NewDocType;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by yi on 2016/9/25.
 */

public class DocCoinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_TEXT = 1;
    private static final int TYPE_IMAGE = 2;

    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private ArrayList<NewDocBean.DocDetail> details;
    private LayoutInflater mLayoutInflater;

    public DocCoinAdapter(Context context, ArrayList<NewDocBean.DocDetail> docDetails){
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        details = docDetails;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_TEXT:
                return new DocRecyclerViewAdapter.TextHolder(mLayoutInflater.inflate(R.layout.item_new_doc_text,parent,false));
            case TYPE_IMAGE:
                return new DocRecyclerViewAdapter.ImageHolder(mLayoutInflater.inflate(R.layout.item_new_doc_image,parent,false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof TextHolder){
            createText((TextHolder) holder,position);
        }else if(holder instanceof ImageHolder){
            createImage((ImageHolder) holder,position);
        }
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public Object getItem(int position){
        return details.get(position).data;
    }

    @Override
    public int getItemViewType(int position) {
        String type = details.get(position - 1).type;
        return NewDocType.getType(type);
    }

    public static class TextHolder extends RecyclerView.ViewHolder{

        private TextView mTvText;

        public TextHolder(View itemView) {
            super(itemView);
            mTvText = (TextView) itemView.findViewById(R.id.tv_doc_content);
        }
    }

    private void createText(final TextHolder holder, int position){
        holder.mTvText.setText(((NewDocBean.DocText) getItem(position)).content);
        holder.mTvText.setText(StringUtils.getUrlClickableText(mContext, ((NewDocBean.DocText) getItem(position)).content));
        holder.mTvText.setMovementMethod(LinkMovementMethod.getInstance());
        holder.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                int pos = holder.getLayoutPosition();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });
    }

    public static class ImageHolder extends RecyclerView.ViewHolder{
        private ImageView mIvImage;

        public ImageHolder(View itemView) {
            super(itemView);
            mIvImage = (ImageView) itemView.findViewById(R.id.iv_doc_image);
        }
    }

    private void createImage(final ImageHolder holder, int position){
        Image image  = ((NewDocBean.DocImage) getItem(position)).image;
        final int[] wh = BitmapUtils.getDocIconSize(image.w, image.h, DensityUtil.getScreenWidth() - DensityUtil.dip2px(20));
        if(FileUtil.isGif(image.path)){
            ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
            layoutParams.width = wh[0];
            layoutParams.height = wh[1];
            holder.mIvImage.setLayoutParams(layoutParams);
            holder.mIvImage.requestLayout();
//            Glide.with(mContext)
//                    .load(image.real_path)
//                    .asGif()
//                    .override(wh[0], wh[1])
//                    .placeholder(R.drawable.ic_default_doc_l)
//                    .error(R.drawable.ic_default_doc_l)
//                    .into(holder.mIvImage);
            Utils.image().bind(holder.mIvImage,image.real_path,new ImageOptions.Builder()
                        .setIgnoreGif(false)
                        .setSize(wh[0],wh[1])
                        .setFailureDrawableId(R.drawable.ic_default_doc_l)
                        .setLoadingDrawableId(R.drawable.ic_default_doc_l)
                        .build());
        }else {
            ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
            layoutParams.width = wh[0];
            layoutParams.height = wh[1];
            holder.mIvImage.setLayoutParams(layoutParams);
            holder.mIvImage.requestLayout();
            Picasso.with(mContext)
                    .load(StringUtils.getUrl(mContext, image.path, wh[0], wh[1], true, true))
                    .resize(wh[0], wh[1])
                    .placeholder(R.drawable.ic_default_doc_l)
                    .error(R.drawable.ic_default_doc_l)
                    .config(Bitmap.Config.RGB_565)
                    .into(holder.mIvImage);
        }
        holder.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                int pos = holder.getLayoutPosition();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });
    }
}
