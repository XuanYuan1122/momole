package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moemoe.lalala.R;

/**
 *
 * Created by Haru on 2016/4/28 0028.
 */
public class FolderDecoration extends RecyclerView.ItemDecoration {


    public FolderDecoration() {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = view.getContext().getResources().getDimensionPixelSize(R.dimen.y24);
        int x24 = view.getContext().getResources().getDimensionPixelSize(R.dimen.x24);
        int x4 = view.getContext().getResources().getDimensionPixelSize(R.dimen.x4);
        int x14 = view.getContext().getResources().getDimensionPixelSize(R.dimen.x14);
        if(parent.getChildLayoutPosition(view) % 3 == 0){
            outRect.left = x24;
            outRect.right = x4;
        }else if(parent.getChildLayoutPosition(view) % 3 == 1){
            outRect.left = x14;
            outRect.right = x14;
        }else if(parent.getChildLayoutPosition(view) % 3 == 2){
            outRect.left = x4;
            outRect.right = x24;
        }
    }
}
