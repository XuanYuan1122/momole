package com.moemoe.lalala.view.widget.netamenu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.view.adapter.OnItemClickListener;

import java.util.ArrayList;

/**
 * Created by yi on 2017/6/6.
 */

public class MenuItemAdapter extends RecyclerView.Adapter {

    private int type;
    private ArrayList<MenuItem> list;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public MenuItemAdapter(Context context,int type,ArrayList<MenuItem> list){
        this.type = type;
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(type == 0){
            return new HorizontalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_horizontal_menu,parent,false));
        }else if (type == 1){
            return new VerticalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_vertical_menu,parent,false));
        }
        return new VerticalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_vertical_menu,parent,false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof HorizontalViewHolder){
            ((HorizontalViewHolder) holder).tvText.setText(list.get(position).getText());
            ((HorizontalViewHolder) holder).ivImg.setImageResource(list.get(position).getImgId());
        }else if(holder instanceof VerticalViewHolder){
            ((VerticalViewHolder) holder).tvText.setText(list.get(position).getText());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(holder.itemView,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private class HorizontalViewHolder extends RecyclerView.ViewHolder{

        ImageView ivImg;
        TextView tvText;

        HorizontalViewHolder(View itemView) {
            super(itemView);
            ivImg = (ImageView) itemView.findViewById(R.id.iv_menu);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);
        }
    }

    private class VerticalViewHolder extends RecyclerView.ViewHolder{

        TextView tvText;

        VerticalViewHolder(View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);
        }
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
