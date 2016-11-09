package com.moemoe.lalala.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.common.util.DensityUtil;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.CalendarDayItem;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/3/2 0002.
 */
public class CalendarRecyclerViewAdapter extends RecyclerView.Adapter<CalendarRecyclerViewAdapter.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ArrayList<CalendarDayItem.CalendarDoc> mDocItems;
    private OnItemClickListener mOnItemClickListener;

    public CalendarRecyclerViewAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDocItems = new ArrayList<>();
    }

    public void setData(ArrayList<CalendarDayItem.CalendarDoc> docItems){
        this.mDocItems.clear();
        this.mDocItems.addAll(docItems);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type3_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, int i) {
        CalendarDayItem.CalendarDoc doc = getItem(i);
        myViewHolder.mTvTitle.setText(doc.title);
        myViewHolder.mTvTime.setText(doc.content);
        if(!TextUtils.isEmpty(doc.mark)){
            myViewHolder.mTvTag.setVisibility(View.VISIBLE);
            myViewHolder.mTvTag.setText(doc.mark);
        }else {
            myViewHolder.mTvTag.setVisibility(View.GONE);
        }
//        Utils.image().bind(myViewHolder.mIvPic, StringUtils.getUrl(mContext, doc.icon.path,DensityUtil.dip2px(134),DensityUtil.dip2px(180),false,false),
//                new ImageOptions.Builder()
//                        .setSize(DensityUtil.dip2px(134), DensityUtil.dip2px(180))
//                        .setCrop(true)
//                        .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                        .setLoadingDrawableId(R.drawable.ic_default_transverse)
//                        .setFailureDrawableId(R.drawable.ic_default_transverse)
//                        .build()
//                    );
        Picasso.with(mContext)
                .load(StringUtils.getUrl(mContext, doc.icon.path,DensityUtil.dip2px(134),DensityUtil.dip2px(180),false,false))
                .resize(DensityUtil.dip2px(134), DensityUtil.dip2px(180))
                .placeholder(R.drawable.ic_default_transverse)
                .error(R.drawable.ic_default_transverse)
                .config(Bitmap.Config.RGB_565)
                .into(myViewHolder.mIvPic);
        myViewHolder.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                int pos = myViewHolder.getLayoutPosition();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(myViewHolder.itemView, pos);
                }
            }
        });
        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = myViewHolder.getLayoutPosition();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemLongClick(myViewHolder.itemView, pos);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDocItems.size();
    }

    public CalendarDayItem.CalendarDoc getItem(int position){
        return mDocItems.get(position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mTvTitle;
        TextView mTvTime;
        TextView mTvTag;
        ImageView mIvPic;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvTag = (TextView) itemView.findViewById(R.id.tv_tag);
            mIvPic = (ImageView) itemView.findViewById(R.id.iv_image);
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
