package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moemoe.lalala.R;

/**
 *
 * Created by Haru on 2016/4/28 0028.
 */
public class FolderFeedDecoration extends RecyclerView.ItemDecoration {


    public FolderFeedDecoration() {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildLayoutPosition(view) != 0){
            if(parent.getChildLayoutPosition(view) == 1 || parent.getChildLayoutPosition(view) == 2){
                outRect.top = 0;
            }else {
                outRect.top = (int)view.getContext().getResources().getDimension(R.dimen.y48);
            }
            if(parent.getChildLayoutPosition(view) % 2 != 0){
                outRect.left = (int)view.getContext().getResources().getDimension(R.dimen.x24);
                outRect.right = (int)view.getContext().getResources().getDimension(R.dimen.x11);
            }else {
                outRect.left = (int)view.getContext().getResources().getDimension(R.dimen.x11);
                outRect.right = (int)view.getContext().getResources().getDimension(R.dimen.x24);
            }
        }
    }
}
