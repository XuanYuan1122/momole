package com.moemoe.lalala.view.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DocDetailEntity;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.NewDocLabelAdapter;

import java.util.ArrayList;

/**
 * Created by Haru on 2015/12/17 0017.
 */
public class DocLabelView extends ViewGroup {

    private LabelItemClickListener mListener;
    private int mLineSpacing;
    private int mLabelSpacing;
    private int mChildViewHeight;
    private int mChildViewWidth;
    private int mChildViewMinWidth;
    private int mMaxLines;

    private NewDocLabelAdapter mAdapter;
    private SimpleLabelAdapter mSimpleAdapter;
    private DataChangeObserver mObserver;

    public DocLabelView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DocLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DocLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle){
        LabelConfig config = new LabelConfig(context,attrs);
        mLineSpacing = config.getLineSpacing();
        mLabelSpacing = config.getLabelSpacing();
        mChildViewHeight = config.getChildViewHeight();
        mChildViewWidth = config.getChildViewWidth();
        mChildViewMinWidth = config.getChildViewMinWidth();
        mMaxLines = config.getMaxLines();
    }

    public int getmLineSpacing() {
        return mLineSpacing;
    }

    public void setmLineSpacing(int mLineSpacing) {
        this.mLineSpacing = mLineSpacing;
    }

    public int getmLabelSpacing() {
        return mLabelSpacing;
    }

    public void setmLabelSpacing(int mLabelSpacing) {
        this.mLabelSpacing = mLabelSpacing;
    }

    public int getmChildViewHeight() {
        return mChildViewHeight;
    }

    public void setmChildViewHeight(int mChildViewHeight) {
        this.mChildViewHeight = mChildViewHeight;
    }

    public int getmChildViewWidth() {
        return mChildViewWidth;
    }

    public void setmChildViewWidth(int mChildViewWidth) {
        this.mChildViewWidth = mChildViewWidth;
    }

    public int getmChildViewMinWidth() {
        return mChildViewMinWidth;
    }

    public void setmChildViewMinWidth(int mChildViewMinWidth) {
        this.mChildViewMinWidth = mChildViewMinWidth;
    }

    public void setmMaxLines(int lines){this.mMaxLines = lines;}

    private void drawLayout(){
        if(mAdapter == null){
            return;
        }
        this.removeAllViews();
        for(int i = 0; i < mAdapter.getCount(); i++ ){
            View view = mAdapter.getView(i,null,null);
            final int position = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        mListener.itemClick(position);
                    }
                }
            });
            this.addView(view);
        }
    }

    private void drawLayoutSimple(){
        if(mSimpleAdapter == null){
            return;
        }
        this.removeAllViews();
        for(int i = 0; i < mSimpleAdapter.getCount(); i++ ){
            View view = mSimpleAdapter.getView(i,null,null);
            final int position = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        mListener.itemClick(position);
                    }
                }
            });
            this.addView(view);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mayHeight = 0;
        int mayWidth = resolveSize(0, widthMeasureSpec);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int childLeft = paddingLeft;
        int childTop = paddingTop;
        int lineHeight = 0;
        int curLine = 0;
        for(int i = 0; i < getChildCount(); i++){
            final View childView = getChildAt(i);
            LayoutParams params = childView.getLayoutParams();
            params.height = mChildViewHeight;
            params.width = mChildViewWidth;
            childView.measure(
                    getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, params.width),
                    getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, params.height)
            );
            int childHeight = childView.getMeasuredHeight();
            int childWidth = childView.getMeasuredWidth();

            lineHeight = Math.max(childHeight,lineHeight);

            if(childLeft + childWidth + paddingRight >= mayWidth){
                curLine++;
                if(curLine >=  mMaxLines){
                    break;
                }
                childLeft = paddingLeft;
                if(i == 0){
                    childTop = 0;
                }else{
                    childTop += mLineSpacing + childHeight;
                }
                lineHeight = childHeight;
            }

            childLeft += childWidth + mLabelSpacing;
        }

        mayHeight += childTop + lineHeight + paddingBottom;
        setMeasuredDimension(mayWidth, resolveSize(mayHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int childLeft = paddingLeft;
        int childTop = paddingTop;
        int lineHeight = 0;
        int curLine = 0;

        for(int i = 0; i < getChildCount();i++){
            final View childView = getChildAt(i);
            if(childView.getVisibility() == View.GONE){
                continue;
            }
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            lineHeight = Math.max(childHeight, lineHeight);
            if (childLeft + childWidth + paddingRight >= width) {
                curLine++;
                if(curLine >=  mMaxLines){
                    break;
                }
                childLeft = paddingLeft;
                if (i == 0) {
                    childTop = 0;
                } else {
                    childTop += mLineSpacing + lineHeight;
                }
                lineHeight = childHeight;
            }
            childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            childLeft += childWidth + mLabelSpacing;

        }


    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(this.getContext(),attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setItemClickListener(LabelItemClickListener listener){
        this.mListener = listener;
    }

    public interface LabelItemClickListener {
        void itemClick(int position);
    }

    public void setContentAndNumList(boolean needAdd,ArrayList<DocTagEntity> beans){
        setDocLabelAdapter(new NewDocLabelAdapter(getContext(), beans, needAdd));
    }

    public void notifyAdapter(){
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setDocLabelAdapter(NewDocLabelAdapter adapter){
        if(mAdapter == null){
            mAdapter = adapter;
            if(mObserver == null){
                mObserver = new DataChangeObserver();
                mAdapter.registerDataSetObserver(mObserver);
            }else{
                mAdapter.registerDataSetObserver(mObserver);
            }
            drawLayout();
        }
    }

    public void setChapter(ArrayList<DocDetailEntity.DocGroupLink.DocGroupLinkDetail> details, Context context){
        setSimpleAdapter(new SimpleLabelAdapter(0,context,details));
    }

    public void setLabels(ArrayList<String> tags, Context context){
        setSimpleAdapter(new SimpleLabelAdapter(tags,1,context));
    }

    public void setLabels(SimpleLabelAdapter adapter){
        setSimpleAdapter(adapter);
    }

    private void setSimpleAdapter(SimpleLabelAdapter adapter){
        if(mSimpleAdapter == null){
            mSimpleAdapter = adapter;
            if(mObserver == null){
                mObserver = new DataChangeObserver();
                mSimpleAdapter.registerDataSetObserver(mObserver);
            }else{
                mSimpleAdapter.registerDataSetObserver(mObserver);
            }
            drawLayoutSimple();
        }else {
            drawLayoutSimple();
        }
    }

    public class LabelConfig{
        private static final int DEFAULT_LINE_SPACING = 7;//默认行间距
        private static final int DEFAULT_LABEL_SPACING = 7;//默认两个布局间距

        private int lineSpacing;
        private int labelSpacing;
        private int childViewWidth;
        private int childViewHeight;
        private int childViewMinWidth;
        private int maxLines;

        public LabelConfig(Context context, AttributeSet attrs){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DocLabelView);
            try {
                lineSpacing =  a.getDimensionPixelSize(R.styleable.DocLabelView_line_spacing, DEFAULT_LINE_SPACING);
                labelSpacing = a.getDimensionPixelSize(R.styleable.DocLabelView_label_spacing, DEFAULT_LABEL_SPACING);
                childViewHeight =  a.getLayoutDimension(R.styleable.DocLabelView_child_view_height, -2);
                childViewWidth =  a.getLayoutDimension(R.styleable.DocLabelView_child_view_width, -2);
                childViewMinWidth = a.getLayoutDimension(R.styleable.DocLabelView_child_view_min_width,-2);
                maxLines = a.getInteger(R.styleable.DocLabelView_line_limit,Integer.MAX_VALUE);
            }finally {
                a.recycle();
            }
        }

        public int getLineSpacing(){ return lineSpacing; }
        public void setLineSpacing(int lineSpacing){ this.lineSpacing = lineSpacing; }
        public int getLabelSpacing() { return labelSpacing; }
        public void setLabelSpacing(int labelSpacing) {this.labelSpacing = labelSpacing;}
        public int getChildViewHeight() { return childViewHeight; }
        public void setChildViewHeight(int childViewHeight) { this.childViewHeight = childViewHeight; }
        public int getChildViewWidth() { return childViewWidth; }
        public void setChildViewWidth(int childViewWidth) { this.childViewWidth = childViewWidth; }
        public int getChildViewMinWidth(){ return childViewMinWidth; }
        public void setChildViewMinWidth(int childViewMinWidth){ this.childViewMinWidth = childViewMinWidth; }
        public int getMaxLines(){return maxLines;}
        public void setMaxLines(int line){ this.maxLines = line;}
    }

    class DataChangeObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            DocLabelView.this.drawLayout();
            DocLabelView.this.drawLayoutSimple();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }

    public static class SimpleLabelAdapter extends BaseAdapter {

        private int[] mBackGround = { R.drawable.shape_rect_white_pink, R.drawable.shape_rect_white_green, R.drawable.shape_rect_white_orange, R.drawable.shape_rect_white_blue, R.drawable.shape_rect_white_yellow, R.drawable.shape_rect_white_purple, R.drawable.shape_rect_white_tab_blue};
        private int[] mTextColor= { R.color.pink_fb7ba2, R.color.green_93d856, R.color.orange_ed853e, R.color.blue_39d8d8, R.color.yellow_f2cc2c, R.color.purple_cd8add, R.color.blue_4fc3f7};

        private ArrayList<String> mTags;
        private ArrayList<String> mContent;
        private int mType;//0:动画集数 1:标签

        //private int mChapterNum;
        ArrayList<DocDetailEntity.DocGroupLink.DocGroupLinkDetail> mDetails;
        private Context mContext;
        public SimpleLabelAdapter(int type) {
            mType = 0;
        }

        public SimpleLabelAdapter(int type, Context context, ArrayList<DocDetailEntity.DocGroupLink.DocGroupLinkDetail> details) {
            mType = type;
           // mChapterNum = num;
            mDetails = details;
            mContext = context;
        }

        public SimpleLabelAdapter(int type, ArrayList<String> content, Context context){
            mType = type;
            mContent = content;
            mContext =context;
            //mChapterNum = 0;
        }

        public SimpleLabelAdapter(ArrayList<String> tags, int type, Context context){
            mType = type;
            mContext = context;
            mTags = tags;

        }

        @Override
        public int getCount() {
            return mTags == null ? mDetails.size() : mTags.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tvNum;
            if(convertView == null){
                if(mType == 0){
                    convertView = View.inflate(mContext, R.layout.item_chapter,null);
                    tvNum = (TextView) convertView.findViewById(R.id.tv_chapter_num);
                }else{
                    convertView = View.inflate(mContext, R.layout.item_calender_type1_tag,null);
                    tvNum = (TextView) convertView.findViewById(R.id.tv_tag);
                }
                convertView.setTag(tvNum);
            }else{
                tvNum = (TextView) convertView.getTag();
            }
            if(mType == 0){
                tvNum.setText(mDetails.get(position).getName());
            }else{
                tvNum.setText(mTags.get(position));
                int index = StringUtils.getHashOfString(mTags.get(position), mTextColor.length);
                tvNum.setTextColor(mContext.getResources().getColor(mTextColor[index]));
                tvNum.setBackgroundResource(mBackGround[index]);
            }

            return convertView;
        }
    }
}
