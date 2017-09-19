package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moemoe.lalala.R;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public class AlbumDecoration extends RecyclerView.ItemDecoration {


    public AlbumDecoration() {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = (int) view.getContext().getResources().getDimension(R.dimen.y36);
        if(parent.getChildLayoutPosition(view) % 2 == 0){
            outRect.left = (int) view.getContext().getResources().getDimension(R.dimen.x80);
        }else {
            outRect.left = (int) view.getContext().getResources().getDimension(R.dimen.x38);
        }
    }
}
