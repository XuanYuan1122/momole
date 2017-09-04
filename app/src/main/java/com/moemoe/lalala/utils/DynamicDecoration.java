package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public class DynamicDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public DynamicDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildLayoutPosition(view) == 0){

        }else if(parent.getChildLayoutPosition(view) % 2 == 0){
            outRect.right = 3 * space;
            outRect.left = space / 2;
        }else {
            outRect.right = space / 2;
            outRect.left = 3 * space;
        }
    }
}
