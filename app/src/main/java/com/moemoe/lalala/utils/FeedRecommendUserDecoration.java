package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moemoe.lalala.R;

/**
 *
 * Created by Haru on 2016/4/28 0028.
 */
public class FeedRecommendUserDecoration extends RecyclerView.ItemDecoration {


    public FeedRecommendUserDecoration() {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = outRect.bottom = (int) view.getResources().getDimension(R.dimen.y24);
        outRect.right = 0;
        if(parent.getChildLayoutPosition(view) != 0){
            outRect.left = (int) view.getResources().getDimension(R.dimen.x16);
        }else {
            outRect.left = (int) view.getResources().getDimension(R.dimen.x24);
        }
    }
}
