package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moemoe.lalala.R;

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
            outRect.right = view.getContext().getResources().getDimensionPixelSize(R.dimen.x10);
        }else {
            outRect.left = view.getContext().getResources().getDimensionPixelSize(R.dimen.x8);
            outRect.right = view.getContext().getResources().getDimensionPixelSize(R.dimen.x8);
        }
    }
}
