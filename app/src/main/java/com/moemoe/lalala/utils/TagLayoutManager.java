//package com.moemoe.lalala.utils;
//
//import android.graphics.Rect;
//import android.support.v7.widget.RecyclerView;
//import android.util.SparseArray;
//import android.view.View;
//import android.view.ViewGroup;
//
///**
// * Created by yi on 2017/8/15.
// */
//
//public class TagLayoutManager extends RecyclerView.LayoutManager{
//
//    private int mVerticalOffset;//竖直偏移量
//    private int mFirstVisiPos;//屏幕可见的第一个View的position
//    private int mLastVisiPos;//屏幕可见的最后一个View的position
//
//    private SparseArray<Rect> mItemRects;
//
//    public TagLayoutManager(){
//        setAutoMeasureEnabled(true);
//        mItemRects = new SparseArray<>();
//    }
//
//    @Override
//    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//    }
//
//    @Override
//    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        if(getItemCount() == 0){
//            detachAndScrapAttachedViews(recycler);
//            return;
//        }
//        if(getChildCount() == 0 && state.isPreLayout()){
//            return;
//        }
//        detachAndScrapAttachedViews(recycler);
//        //初始化
//        mVerticalOffset = 0;
//        mFirstVisiPos = 0;
//        mLastVisiPos = getItemCount();
//
//        fill(recycler,state);
//    }
//
//    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state){
//        fill(recycler,state,0);
//    }
//
//    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state,int dy){
//        int topOffset = getPaddingTop();//布局时的上偏移
//        int leftOffset = getPaddingLeft();//布局时的左偏移
//        int lineMaxHeight = 0;//每一行的最大高度
//        int minPos = mFirstVisiPos;
//        mLastVisiPos = getItemCount() - 1;
//        for (int i = minPos;i <= mLastVisiPos;i++){
//            View child = recycler.getViewForPosition(i);
//            addView(child);
//            measureChildWithMargins(child,0,0);
//            //计算宽度 包括margin
//            if(leftOffset + getDecoratedMeasurementHorizontal(child) <= getHorizontalSpace()){//还没满一行
//                layoutDecoratedWithMargins(child,
//                        leftOffset,
//                        topOffset,
//                        leftOffset + getDecoratedMeasurementHorizontal(child),
//                        topOffset + getDecoratedMeasurementVertical(child));
//
//                //增加偏移
//                leftOffset += getDecoratedMeasurementHorizontal(child);
//                lineMaxHeight = Math.max(lineMaxHeight,getDecoratedMeasurementVertical(child));
//            }else {//已满一行
//                //增加top
//                leftOffset = getPaddingLeft();
//                topOffset += lineMaxHeight;
//                lineMaxHeight = 0;
//
//                //新的一行要判断边界
//                if(topOffset - dy)
//            }
//        }
//    }
//
//    /**
//     * 获取view在水平方向上所占空间
//     * @param view
//     * @return
//     */
//    private int getDecoratedMeasurementHorizontal(View view){
//        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
//        return getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
//    }
//
//    /**
//     * 获取view在竖直方向所占的空间
//     * @param view
//     * @return
//     */
//    private int getDecoratedMeasurementVertical(View view){
//        final  RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
//        return getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
//    }
//
//    private int getVerticalSpace(){
//        return getHeight() - getPaddingTop() - getPaddingBottom();
//    }
//
//    private int getHorizontalSpace(){
//        return getWidth() - getPaddingLeft() - getPaddingRight();
//    }
//}
