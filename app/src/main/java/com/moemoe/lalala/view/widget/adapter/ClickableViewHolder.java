package com.moemoe.lalala.view.widget.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by yi on 2017/7/3.
 */

public class ClickableViewHolder extends RecyclerView.ViewHolder{

    protected Context context;

    public ClickableViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V $(@IdRes int id){
        return (V)itemView.findViewById(id);
    }

    public ClickableViewHolder setText(@IdRes int viewId, CharSequence value){
        TextView view = $(viewId);
        view.setText(value);
        return this;
    }

    public ClickableViewHolder setText(@IdRes int viewId, @StringRes int strId){
        TextView view = $(viewId);
        view.setText(strId);
        return this;
    }

    public ClickableViewHolder setImageResource(@IdRes int viewId, @DrawableRes int imageResId) {
        ImageView view = $(viewId);
        view.setImageResource(imageResId);
        return this;
    }

    public ClickableViewHolder setVisible(@IdRes int viewId, boolean visible) {
        View view = $(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public ClickableViewHolder setBackgroundDrawable(@IdRes int viewId, Drawable drawable){
        View view = $(viewId);
        view.setBackgroundDrawable(drawable);
        return this;
    }
}
