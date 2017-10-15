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
import com.moemoe.lalala.model.entity.JuQingShowEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.StringUtils;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by yi on 2017/3/13.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<JuQingShowEntity> items;
    private Context context;

    public ChatAdapter(Context context){
        this.context = context;
        items = new ArrayList<>();
    }

    public void setData(ArrayList<JuQingShowEntity> list){
        this.items.clear();
        this.items.addAll(list);
        notifyDataSetChanged();
    }

    public void addData(ArrayList<JuQingShowEntity> list){
        int bfSize = getItemCount();
        this.items.addAll(list);
        notifyItemRangeInserted(bfSize,list.size());
    }

    public void addItem(JuQingShowEntity item){
        items.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public ArrayList<JuQingShowEntity> getList(){
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
        JuQingShowEntity entity = getItem(position);
        ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
        if(entity.getName().equals("me")){
            String path;
            if(entity.getPath().startsWith("http")){
                path = entity.getPath();
            }else {
                path = ApiService.URL_QINIU + entity.getPath();
            }
            Glide.with(context)
                    .load(StringUtils.getUrl(context, path, DensityUtil.dip2px(context,40),DensityUtil.dip2px(context,40),false,true))
                    .override(DensityUtil.dip2px(context,40),DensityUtil.dip2px(context,40))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(chatViewHolder.ivAvatar);
        }else {
            Glide.with(context)
                    .load(entity.getOtherPath())
                    .override(DensityUtil.dip2px(context,40),DensityUtil.dip2px(context,40))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(chatViewHolder.ivAvatar);
        }
        chatViewHolder.tvContent.setText(entity.getText());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        JuQingShowEntity item = getItem(position);
        if(item.getName().equals("me")){
            return 2;//自己
        }else {
            return 1;
        }
    }

    public JuQingShowEntity getItem(int position){
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
