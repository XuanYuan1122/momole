package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moemoe.lalala.R;

/**
 *
 * Created by Haru on 2016/4/28 0028.
 */
public class FileItemDecoration extends RecyclerView.ItemDecoration {


    public FileItemDecoration() {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = view.getResources().getDimensionPixelSize(R.dimen.y6);
        int x2 =  view.getResources().getDimensionPixelSize(R.dimen.x2);
        int x4 =  view.getResources().getDimensionPixelSize(R.dimen.x4);
        if(parent.getChildLayoutPosition(view) % 3 == 0){
            outRect.left = 0;
            outRect.right = x4;
        }else if(parent.getChildLayoutPosition(view) % 3 == 1){
            outRect.left = x2;
            outRect.right = x2;
        }else if(parent.getChildLayoutPosition(view) % 3 == 2){
            outRect.left =  x4;
            outRect.right = 0;
        }
    }
}
