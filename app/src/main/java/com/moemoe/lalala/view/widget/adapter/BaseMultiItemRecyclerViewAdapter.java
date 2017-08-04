package com.moemoe.lalala.view.widget.adapter;

import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yi on 2017/7/3.
 */

public abstract class BaseMultiItemRecyclerViewAdapter<T, K extends ClickableViewHolder> extends BaseRecyclerViewAdapter<T, K> {

    public static final int TYPE_NOT_FOUND = -404;
    private SparseIntArray layouts;

    public BaseMultiItemRecyclerViewAdapter(ArrayList<T> list) {
        super(list);
    }

    @Override
    protected void convert(K helper, T item, int position) {

    }

    @Override
    protected K onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, getLayoutId(viewType));
    }

    protected int getLayoutId(int viewType) {
        return layouts.get(viewType,TYPE_NOT_FOUND);
    }

    protected void addItemType(int type, @LayoutRes int layoutResId){
        if(layouts == null){
            layouts = new SparseIntArray();
        }
        layouts.put(type,layoutResId);
    }
}
