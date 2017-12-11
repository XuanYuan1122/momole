package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moemoe.lalala.R;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public class FolderDecoration extends RecyclerView.ItemDecoration {


    public FolderDecoration() {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = (int)view.getContext().getResources().getDimension(R.dimen.y36);
        if(parent.getChildLayoutPosition(view) % 3 == 0){
            outRect.left = 0;
            outRect.right = (int)view.getContext().getResources().getDimension(R.dimen.x10);
        }else if(parent.getChildLayoutPosition(view) % 3 == 1){
            outRect.left = (int)view.getContext().getResources().getDimension(R.dimen.x8);
            outRect.right = (int)view.getContext().getResources().getDimension(R.dimen.x8);
        }else if(parent.getChildLayoutPosition(view) % 3 == 2){
            outRect.left = (int)view.getContext().getResources().getDimension(R.dimen.x10);
            outRect.right = 0;
        }
    }
}
