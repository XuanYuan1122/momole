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
import com.moemoe.lalala.model.entity.ChatContentEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.GlideCircleTransform;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by yi on 2017/3/13.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> items;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ChatAdapter(Context context){
        this.context = context;
        items = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(Collection list){
        this.items.clear();
        this.items.addAll(list);
        notifyDataSetChanged();
    }

    public void addData(Collection list){
        int bfSize = getItemCount();
        this.items.addAll(list);
        notifyItemRangeInserted(bfSize,list.size());
    }

    public ArrayList<Object> getList(){
        return items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0){
            return new TimeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text_center,parent,false));
        }else if(viewType == 1){
            return new ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_normal,parent,false));
        }else if(viewType == 2){
            return new ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_user,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof TimeViewHolder){
            String time = (String) getItem(position);
            ((TimeViewHolder) holder).tvTime.setText(time);
        }else if(holder instanceof ChatViewHolder){
            ChatContentEntity entity = (ChatContentEntity) getItem(position);
            ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
            String path;
            if(entity.getUserIcon().startsWith("http")){
                path = entity.getUserIcon();
            }else {
                path = ApiService.URL_QINIU + entity.getUserIcon();
            }
            Glide.with(context)
                    .load(StringUtils.getUrl(context, path, DensityUtil.dip2px(context,40),DensityUtil.dip2px(context,40),false,true))
                    .bitmapTransform(new CropCircleTransformation(context))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .into(chatViewHolder.ivAvatar);
            if(entity.getContentType().equals("TEXT")){
                chatViewHolder.tvContent.setText(entity.getContent());
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(view,position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemLongClick(v,position);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if(item instanceof String){
            return 0;//时间
        }else if(item instanceof ChatContentEntity){
            if(((ChatContentEntity) item).getUserId().equals(PreferenceUtils.getUUid())){
                return 2;//自己
            }else {
                return 1;
            }
        }
        return 1;//他人
    }

    public Object getItem(int position){
        if(position >= 0 && position < items.size()){
            return items.get(position);
        }else {
            return null;
        }
    }

    private class ChatViewHolder extends RecyclerView.ViewHolder{

        ImageView ivAvatar;
        TextView tvContent;

        ChatViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    private class TimeViewHolder extends RecyclerView.ViewHolder{

        TextView tvTime;

        TimeViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
