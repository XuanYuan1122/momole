package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moemoe.lalala.R;

/**
 *
 * Created by Haru on 2016/4/28 0028.
 */
public class FolderFeedTopDecoration extends RecyclerView.ItemDecoration {


    public FolderFeedTopDecoration() {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildLayoutPosition(view) == 1){
            outRect.left = (int)view.getContext().getResources().getDimension(R.dimen.x18);
            outRect.right = (int)view.getContext().getResources().getDimension(R.dimen.x18);
        }
    }
}
