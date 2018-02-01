package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moemoe.lalala.R;

/**
 *
 * Created by Haru on 2016/4/28 0028.
 */
public class FeedAdminDecoration extends RecyclerView.ItemDecoration {


    public FeedAdminDecoration() {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildLayoutPosition(view) != 0){
            outRect.left = view.getContext().getResources().getDimensionPixelSize(R.dimen.x36);
        }else {
            outRect.left = 0;
        }
    }
}
