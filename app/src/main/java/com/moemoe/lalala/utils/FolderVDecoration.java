package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public class FolderVDecoration extends RecyclerView.ItemDecoration {


    public FolderVDecoration() {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildLayoutPosition(view) == 0){
            outRect.left = 0;
            outRect.right = DensityUtil.dip2px(view.getContext(),5);
        }else {
            outRect.left = DensityUtil.dip2px(view.getContext(),4);
            outRect.right = DensityUtil.dip2px(view.getContext(),4);
        }
    }
}
