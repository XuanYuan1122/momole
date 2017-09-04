package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public class FolderDecoration extends RecyclerView.ItemDecoration {


    public FolderDecoration() {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = DensityUtil.dip2px(view.getContext(),18);
        if(parent.getChildLayoutPosition(view) % 3 == 0){
            outRect.left = 0;
            outRect.right = DensityUtil.dip2px(view.getContext(),5);
        }else if(parent.getChildLayoutPosition(view) % 3 == 1){
            outRect.left = DensityUtil.dip2px(view.getContext(),4);
            outRect.right = DensityUtil.dip2px(view.getContext(),4);
        }else if(parent.getChildLayoutPosition(view) % 3 == 2){
            outRect.left = DensityUtil.dip2px(view.getContext(),5);
            outRect.right = 0;
        }
    }
}
