package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moemoe.lalala.R;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public class MapSelectItemDecoration extends RecyclerView.ItemDecoration {


    public MapSelectItemDecoration() {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = (int) view.getResources().getDimension(R.dimen.y24);
    }
}
