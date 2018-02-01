package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moemoe.lalala.R;

/**
 *
 * Created by Haru on 2016/4/28 0028.
 */
public class GridDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;

    public GridDecoration(int spanCount) {
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(spanCount == 2){
            int x24 = view.getContext().getResources().getDimensionPixelSize(R.dimen.x24);
            int x12 = view.getContext().getResources().getDimensionPixelSize(R.dimen.x12);
            if(parent.getChildLayoutPosition(view) % 2 == 0){
                outRect.left = x24;
                outRect.right = x12;
            }else {
                outRect.right = x24;
                outRect.left = x12;
            }
        }else if(spanCount == 3){
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
}
