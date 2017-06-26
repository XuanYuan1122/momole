package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moemoe.lalala.R;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2017/6/26.
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.ClickableViewHolder>{

    Context context;
    private ArrayList<T> list;
    private OnItemClickListener onItemClickListener;
    LayoutInflater mLayoutInflater;

    BaseRecyclerViewAdapter(Context context) {
        this(context,null);
    }

    BaseRecyclerViewAdapter(Context context, ArrayList<T> list) {
        this.context = context;
        if(list == null) {
            this.list = new ArrayList<>();
        }else {
            this.list = list;
        }
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ClickableViewHolder(mLayoutInflater.inflate(R.layout.item_empty,parent,false));
    }

    public void onBindViewHolder(final ClickableViewHolder holder, final int position) {
        holder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null) onItemClickListener.onItemClick(holder.getRootView(),position);
            }
        });
        holder.getRootView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener != null) onItemClickListener.onItemLongClick(holder.getRootView(),position);
                return false;
            }
        });
    }

    public T getItem(int position){
        return list.get(position);
    }

    public void setList(ArrayList<T> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(ArrayList<T> list){
        int bfSize = getItemCount();
        this.list.addAll(list);
        notifyItemRangeInserted(bfSize,getItemCount() - bfSize);
    }

    public ArrayList<T> getList(){ return list;}

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ClickableViewHolder extends RecyclerView.ViewHolder{

        private View rootView;

        public ClickableViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
        }

        View getRootView(){ return rootView;}

        @SuppressWarnings("unchecked")
        public <V extends View> V $(@IdRes int id){
            return (V)rootView.findViewById(id);
        }
    }

}
