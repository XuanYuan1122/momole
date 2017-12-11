package com.moemoe.lalala.view.widget.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.WallBlock;
import com.moemoe.lalala.utils.CommonUtils;
import com.moemoe.lalala.utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/7/5 0005.
 */
public class DraggableLayout extends ViewGroup {

    final int INVALID_POSITION = -1;
    /**
     * unit size of viewgroup, width and height is default 4
     */
    private int mUnitWidthNum = 4;
    private int mUnitHeightNum = 4;
    /**
     * calculate by UnitSize and real size of device
     */
    private int mUnitWidth;
    private int mUnitHeight;
    /**
     * save the details of child views
     */
    private ArrayList<WallBlock> listViews;
    /**
     * position of clicked, used to calculate the view pressed
     */
    private int mClickX;
    private int mClickY;
    /**
     * a imageview created to move with fingure when a subview in
     * freesizedraggablelayout pressed
     */
    private ImageView mDragImageView;
    /**
     * some params aux to get the real position of ImageView which need to draw
     */
    private int mPoint2ItemLeft;
    private int mPoint2ItemTop;
    private int mOffset2Left;
    private int mOffset2Top;
    private int mStatusHeight;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    /**
     * padding of each child vie
     */
    private int mViewPadding = 5;
    /**
     * index of pressed DetailView in listView
     */
    private int mPressedItem = INVALID_POSITION;
    /**
     * group change flag
     */
    private boolean mGroupChangeEnable = true;
    private boolean mPress = false;
    private DataChangeObserver mObserver;
    private DragAdapter mAdapter;
    private DragItemClickListener mListener;

    public DraggableLayout(Context context) {
        this(context, null);
    }

    public DraggableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraggableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = CommonUtils.getStatusHeight(getContext());
        listViews = new ArrayList<>();
        mViewPadding = (int)context.getResources().getDimension(R.dimen.x6);
    }

    public interface DragItemClickListener {
        void itemClick(View v, int position, WallBlock wallBlock);
    }

    public void setItemClickListener(DragItemClickListener listener){
        this.mListener = listener;
    }

    public void setDragList(Context context, ArrayList<WallBlock> listViews){
        if(mAdapter == null){
            setDragAdapter(new DragAdapter(context,listViews));
        }else {
            setList(listViews);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    public void addList(ArrayList<WallBlock> listViews){
        this.listViews.addAll(listViews);
        mAdapter.notifyDataSetChanged();
    }

    public void setList(ArrayList<WallBlock> listViews){
        this.listViews.clear();
        this.removeAllViews();
        this.listViews.addAll(listViews);
        mAdapter.notifyDataSetChanged();
    }

    public void setDragAdapter(DragAdapter adapter){
        if(mAdapter == null){
            mAdapter = adapter;
            if(mObserver == null){
                mObserver = new DataChangeObserver();
                mAdapter.registerDataSetObserver(mObserver);
            }else {
                mAdapter.registerDataSetObserver(mObserver);
            }
            drawLayout();
        }
    }

    private void drawLayout(){
        if(mAdapter == null) return;
        //this.removeAllViews();
        for(int i = 0;i < mAdapter.getCount();i++){
            View view = mAdapter.getView(i,null,null);
            final int position = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        WallBlock wallBlock = listViews.get(position);
                        mListener.itemClick(v,position,wallBlock);
                    }
                }
            });
            if(i >= getChildCount()){
                this.addView(view);
            }
        }
    }

    class DataChangeObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            DraggableLayout.this.drawLayout();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }



    /**
     * create a image for the pressed view
     *
     * @param v
     * @param x
     * @param y
     */
    private void createPressImageView(View v, int x, int y) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

        mWindowLayoutParams.x = x - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = y - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowLayoutParams.alpha = 0.5f;
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        //create the bitmap of view
        mDragImageView = new ImageView(getContext());
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        mDragImageView.setImageBitmap(bitmap);
        v.destroyDrawingCache();

        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }

    /**
     * set the list of view in freesizedraggablelayout
     *
     * @param list
     */
//    public void setList(List<DragDetailView> list) {
//        listViews = list;
//        removeAllViews();
////        for (DetailView v : listViews) {
////            addView(v);
////        }
//        for(int i = 0;i<listViews.size();i++){
//            DragDetailView v = listViews.get(i);
//            addView(v);
//
//            v.setParent(this);
//            v.fillImageView(null);
//        }
//    }

//    public void changePositionImg(int fromPosition, int toPosition){
//        DragDetailView itemView = listViews.get(fromPosition);
//        DragDetailView toView = listViews.get(toPosition);
//        changePositionInList(fromPosition, toPosition);
//        bringChildToFront(itemView);
//        bringChildToFront(toView);
//        itemView.changePositionImg(toView);
//    }


    /**
     * set the width of viewgroup
     *
     * @param i
     */
    public void setUnitWidthNum(int i) {
        mUnitWidthNum = i;
    }

    /**
     * set the height of viewgroup
     *
     * @param i
     */
    public void setUnitHeightNum(int i) {
        mUnitHeightNum = i;
    }


    /**
     * change position_data of DetailView. It's very important
     * cause if a onLayout is called then freeseizedraggablelayout
     * will redraw all items according to their point member.
     *
     * @param i
     * @param j
     */
//    private void changePositionInList(int i, int j) {
//        DragDetailView f = listViews.get(i);
//        DragDetailView t = listViews.get(j);
//        listViews.remove(i);
//        listViews.add(i,t);
//        listViews.remove(j);
//        listViews.add(j,f);
//    }

    /**
     * get the clicked view's index in listView
     *
     * @param p
     * @return
     */
//    private int getClickedItem(Point p) {
//        int i = INVALID_POSITION;
//        for (DragDetailView view : listViews) {
//            View v = view;
//            Rect rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
//            if (rect.contains(p.x, p.y)) {
//                i = listViews.indexOf(view);
//                break;
//            }
//        }
//        return i;
//    }

    /**
     * set the padding of subviews in layout, default is 5
     *
     * @param i
     */
    public void setsubViewPadding(int i) {
        mViewPadding = i;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, widthMeasureSpec);
        //set the unit size
        mUnitWidth = MeasureSpec.getSize(widthMeasureSpec) / mUnitWidthNum;
        mUnitHeight = mUnitWidth * 3 / 4;
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int width = resolveSizeAndState(maxWidth, widthMeasureSpec, 0);
        int height = 0;
        if(listViews != null && listViews.size() > 0){
            WallBlock wallBlock = listViews.get(listViews.size() - 1);
            height = (wallBlock.getLtY() + wallBlock.getH()) * mUnitHeight;
        }
        setMeasuredDimension(width, height);
    }

    public void childOnLayout(DragDetailView dvView){
        dvView.setTranslationX(0);
        dvView.setTranslationY(0);
        int iL = dvView.getPoint().x * mUnitWidth;
        int iT = dvView.getPoint().y * mUnitHeight;
        int iR = iL + dvView.getWidthNum() * mUnitWidth;
        int iB = iT + dvView.getHeightNum() * mUnitHeight;
        ViewGroup.LayoutParams lp = dvView.getLayoutParams();
        lp.width = dvView.getWidthNum() * mUnitWidth;
        lp.height = dvView.getHeightNum() * mUnitHeight;
        dvView.setLayoutParams(lp);
        dvView.layout(iL + mViewPadding, iT + mViewPadding, iR - mViewPadding, iB - mViewPadding);
        dvView.setScaleX(1.0f);
        dvView.setScaleY(1.0f);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int iChildCount = getChildCount();
        for (int i = 0; i < iChildCount; ++i) {
            //set child view's layout with padding
            View vChild = getChildAt(i);
            DragDetailView dvView = (DragDetailView) vChild;
            int iL = dvView.getPoint().x * mUnitWidth;
            int iT = dvView.getPoint().y * mUnitHeight;
            int iR = iL + dvView.getWidthNum() * mUnitWidth;
            int iB = iT + dvView.getHeightNum() * mUnitHeight;

            ViewGroup.LayoutParams lp = dvView.getLayoutParams();
            lp.width = dvView.getWidthNum() * mUnitWidth - 2 * mViewPadding;
            lp.height = dvView.getHeightNum() * mUnitHeight - 2 * mViewPadding;
            dvView.setLayoutParams(lp);
            dvView.setPosition(i);
            Point p = new Point();
            p.x = iL + mViewPadding;
            p.y = iT + mViewPadding;
            dvView.setChangePoint(p);
            dvView.setHasSetChangePosition(true);


            vChild.layout(iL + mViewPadding, iT + mViewPadding, iR - mViewPadding, iB - mViewPadding);
            dvView.fillImageView();
        }
    }

    /**
     * set if group change is allowed, it's allow default
     * @param b
     */
    private void setGroupChangeEnable(Boolean b) {
        mGroupChangeEnable = b;
    }

    public class DragAdapter extends BaseAdapter {
        private Context context;

        public DragAdapter(Context context, ArrayList<WallBlock> list){
            this.context = context;
            listViews.addAll(list);
        }

        @Override
        public int getCount() {
            return listViews.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DragDetailView view;
            if(convertView == null){
                view = new DragDetailView(context);
                convertView = view;
            }else {
                view = (DragDetailView) convertView;
            }
            WallBlock wallBlock = listViews.get(position);
            view.setPoint(new Point(wallBlock.getLtX(), wallBlock.getLtY()));
            view.setHeightNum(wallBlock.getH());
            view.setWidthNum(wallBlock.getW());
            view.setImgPath(wallBlock.getBg().getPath());
            view.setPosition(position);
            view.setName(wallBlock.getName());
            return convertView;
        }
    }

}
