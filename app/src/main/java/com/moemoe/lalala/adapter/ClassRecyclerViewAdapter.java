package com.moemoe.lalala.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.common.util.DensityUtil;
import com.squareup.picasso.Picasso;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.FeaturedBean;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.NoDoubleClickListener;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/3/2 0002.
 */
public class ClassRecyclerViewAdapter extends RecyclerView.Adapter<ClassRecyclerViewAdapter.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ArrayList<FeaturedBean> mDocItems;
    private OnItemClickListener mOnItemClickListener;

    public ClassRecyclerViewAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDocItems = new ArrayList<>();
    }

    public void setData(ArrayList<FeaturedBean> docItems){
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
        FeaturedBean doc = getItem(i);
        myViewHolder.mTvTitle.setText(doc.name);
        Picasso.with(mContext)
                .load(StringUtils.getUrl(mContext, doc.bg.path, DensityUtil.dip2px(100), DensityUtil.dip2px(100), false, true))
                .resize(DensityUtil.dip2px(100), DensityUtil.dip2px(100))
                .placeholder(R.drawable.ic_default_transverse)
                .error(R.drawable.ic_default_transverse)
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

    public FeaturedBean getItem(int position){
        return mDocItems.get(position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mTvTitle;
        ImageView mIvPic;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mIvPic = (ImageView) itemView.findViewById(R.id.iv_image);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
