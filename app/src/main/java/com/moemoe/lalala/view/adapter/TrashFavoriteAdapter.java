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
import com.moemoe.lalala.model.entity.TrashEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by yi on 2016/12/14.
 */

public class TrashFavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<TrashEntity> entities;
    private String type;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public TrashFavoriteAdapter(Context context,String type){
        this.context = context;
        entities = new ArrayList<>();
        this.type = type;
    }

    public TrashEntity getItem(int position){
        return entities.get(position);
    }

    public void setData(ArrayList<TrashEntity> entities){
        //int bfSize = getItemCount();
        this.entities.clear();
        this.entities.addAll(entities);
        notifyDataSetChanged();
//        int afSize = getItemCount();
//        int btSize = afSize - bfSize;
//        if(btSize > 0){
//            notifyItemRangeChanged(0,bfSize);
//            notifyItemRangeInserted(bfSize,btSize);
//        }else {
//            notifyItemRangeChanged(0,afSize);
//            notifyItemRangeRemoved(afSize,btSize);
//        }
    }

    public void addData(ArrayList<TrashEntity> entities){
        int bfSize = getItemCount();
        this.entities.addAll(entities);
        notifyItemRangeInserted(bfSize,entities.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if("image".equals(type)){
            return new ImgViewHolder(LayoutInflater.from(context).inflate(R.layout.item_img_trash_favorite,parent,false));
        }else if("text".equals(type)){
            return new TextViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text_trash_favorite,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TrashEntity entity = entities.get(position);
        if("image".equals(type)){
            ImgViewHolder imgViewHolder = (ImgViewHolder) holder;
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + entity.getImage().getPath(), DensityUtil.dip2px(context,76),DensityUtil.dip2px(context,76),false,true))
                    .override(DensityUtil.dip2px(context,76),DensityUtil.dip2px(context,76))
                    .centerCrop()
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .into(imgViewHolder.img);
            imgViewHolder.title.setText(entity.getTitle());
        }else if("text".equals(type)){
            TextViewHolder textViewHolder = (TextViewHolder) holder;
            textViewHolder.title.setText(entity.getTitle());
            textViewHolder.content.setText(entity.getContent());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(view,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    class TextViewHolder extends RecyclerView.ViewHolder{

        TextView title,content;

        public TextViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    class ImgViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        ImageView img;

        public ImgViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            img = (ImageView) itemView.findViewById(R.id.iv_img);
        }
    }
}
