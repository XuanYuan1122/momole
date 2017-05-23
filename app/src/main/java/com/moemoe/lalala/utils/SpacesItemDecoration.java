package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    private int getSpanCount(RecyclerView parent){
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    private int isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount){
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager){
            int spanSize = ((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanSize(pos);
            if(spanSize == spanCount){
                return 0;
            }else{
                if(pos > 0 && pos < childCount - 1){
                    int preSpanSize = ((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanSize(pos - 1);
                    if(preSpanSize == spanCount){
                        return 1;
                    }else{
                        //int firstVisibleItem = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                        // View view = parent.getChildAt(pos - firstVisibleItem);
                        View view = layoutManager.findViewByPosition(pos - 1);
                        if(view != null){
                            int rightMargin = getRightMargins(view);
                            if(rightMargin == space){
                                return 1;
                            }else {
                                return 2;
                            }
                        }else{
                            int preSpanSize1 = ((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanSize(pos + 1);
                            view = layoutManager.findViewByPosition(pos + 1);
                            int rightMargin = getRightMargins(view);
                            if(rightMargin == space && preSpanSize1 != spanCount){
                                return 1;
                            }else {
                                return 2;
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int itemPosition = parent.getChildAdapterPosition(view);
        View firstChild = ((ViewGroup)view).getChildAt(0);

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            if(firstChild instanceof ViewPager){

            }else if(firstChild instanceof ListView){

            }else if (firstChild instanceof RecyclerView){

            } else {
                int i = isLastColumn(parent,itemPosition,spanCount,childCount);
                if(i == 0){
                    setRightMargins(view,space);
                    setLeftMargins(view, space);
                }else if(i == 1){
                    setRightMargins(view, space / 2);
                    setLeftMargins(view, space);
                }else{
                    setRightMargins(view,space);
                    setLeftMargins(view,space / 2);
                }
            }
        }else if(layoutManager instanceof LinearLayoutManager){
            /*if(!isLastColumn(parent,itemPosition,spanCount,childCount)){
                outRect.right = space;
            }else{
                outRect.right = 0;
            }
            outRect.left = 0;*/
            if(itemPosition == childCount - 1){
                setRightMargins(view,0);
            }
        }

    }


    public void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public void setLeftMargins (View v, int l) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.leftMargin = l;
            v.requestLayout();
        }
    }

    public int getRightMargins (View v) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            return p.rightMargin;
        }
        return 0;
    }

    public void setRightMargins (View v, int r) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.rightMargin = r;
            v.requestLayout();
        }
    }

    public void setTopMargins (View v, int t) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.topMargin = t;
            v.requestLayout();
        }
    }

    public void setBottomMargins (View v, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.bottomMargin = b;
            v.requestLayout();
        }
    }

}
