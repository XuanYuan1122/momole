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
import com.moemoe.lalala.model.entity.BookInfo;
import com.moemoe.lalala.model.entity.VideoInfo;
import com.moemoe.lalala.utils.MusicLoader;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/9/25.
 */

public class SelectItemAdapter extends RecyclerView.Adapter<SelectItemAdapter.MyViewHolder>{

    private LayoutInflater mLayoutInflater;
    private ArrayList<Object> paths;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private int selectSize;

    public SelectItemAdapter(Context context){
        mLayoutInflater = LayoutInflater.from(context);
        paths = new ArrayList<>();
        mContext = context;
        selectSize = 9;
    }

    public void setSelectSize(int selectSize) {
        this.selectSize = selectSize;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
        void onAllDelete();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setData(ArrayList<Object> paths){
        this.paths = paths;
        notifyDataSetChanged();
    }

    @Override
    public SelectItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mLayoutInflater.inflate(R.layout.item_comment_img,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if(position == paths.size()){
            Glide.with(mContext)
                    .load(R.drawable.ic_add_photo)
                    .override((int)mContext.getResources().getDimension(R.dimen.y230),(int)mContext.getResources().getDimension(R.dimen.y230))
                    .into(holder.mIvImg);
            holder.mIvDel.setVisibility(View.GONE);
        }else {
            holder.mIvDel.setVisibility(View.VISIBLE);
            final Object path = paths.get(position);
            if(path instanceof MusicLoader.MusicInfo){
                Glide.with(mContext)
                        .load(R.drawable.ic_music_choice)
                        .centerCrop()
                        .override((int)mContext.getResources().getDimension(R.dimen.y230),(int)mContext.getResources().getDimension(R.dimen.y230))
                        .into(holder.mIvImg);
                holder.mTvTitle.setText(((MusicLoader.MusicInfo) path).getTitle());
            }else if(path instanceof BookInfo){
                Glide.with(mContext)
                        .load(R.drawable.ic_word_choice)
                        .centerCrop()
                        .override((int)mContext.getResources().getDimension(R.dimen.y230),(int)mContext.getResources().getDimension(R.dimen.y230))
                        .into(holder.mIvImg);
                holder.mTvTitle.setText(((BookInfo) path).getTitle());
            }else if(path instanceof VideoInfo){
                Glide.with(mContext)
                        .load(R.drawable.ic_video_choice)
                        .centerCrop()
                        .override((int)mContext.getResources().getDimension(R.dimen.y230),(int)mContext.getResources().getDimension(R.dimen.y230))
                        .into(holder.mIvImg);
                holder.mTvTitle.setText(((VideoInfo) path).getName());
            }else {
                Glide.with(mContext)
                        .load(path)
                        .centerCrop()
                        .override((int)mContext.getResources().getDimension(R.dimen.y230),(int)mContext.getResources().getDimension(R.dimen.y230))
                        .into(holder.mIvImg);
                holder.mTvTitle.setText("");
            }
            final Object del = paths.get(position);
            holder.mIvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paths.remove(del);
                    notifyDataSetChanged();
                    if (paths.size() == 0){
                        if(mOnItemClickListener != null){
                            mOnItemClickListener.onAllDelete();
                        }
                    }
                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(paths.size() < selectSize){
            return paths.size() + 1;
        }
        return paths.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvImg;
        ImageView mIvDel;
        TextView mTvTitle;

        MyViewHolder(View itemView) {
            super(itemView);
            mIvImg = (ImageView) itemView.findViewById(R.id.iv_img);
            mIvDel = (ImageView) itemView.findViewById(R.id.iv_del_img);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
