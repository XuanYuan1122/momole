package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/3/2 0002.
 */
public class ClassRecyclerViewAdapter extends RecyclerView.Adapter<ClassRecyclerViewAdapter.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ArrayList<FeaturedEntity> mDocItems;
    private OnItemClickListener mOnItemClickListener;

    public ClassRecyclerViewAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDocItems = new ArrayList<>();
    }

    public void setData(ArrayList<FeaturedEntity> docItems){
        this.mDocItems.clear();
        this.mDocItems.addAll(docItems);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(mLayoutInflater.inflate(R.layout.item_class_featured_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, int i) {
        FeaturedEntity doc = getItem(i);
        myViewHolder.mTvTitle.setText(doc.getName());
        Glide.with(mContext)
                .load(StringUtils.getUrl(mContext, ApiService.URL_QINIU + doc.getBg().getPath(), DensityUtil.dip2px(mContext,100), DensityUtil.dip2px(mContext,100), false, true))
                .override(DensityUtil.dip2px(mContext,100), DensityUtil.dip2px(mContext,100))
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .transform(new GlideRoundTransform(mContext,5))
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

    public FeaturedEntity getItem(int position){
        return mDocItems.get(position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mTvTitle;
        ImageView mIvPic;

        MyViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mIvPic = (ImageView) itemView.findViewById(R.id.iv_image);
        }

    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
